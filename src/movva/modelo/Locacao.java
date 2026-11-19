package movva.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import movva.util.Formatador;

public class Locacao {

	private static final BigDecimal MULTA_POR_DIA_ATRASO = new BigDecimal("0.20");

	private int id;
	private Cliente cliente;
	private Veiculo veiculo;
	private LocalDate dataRetirada;
	private LocalDate dataPrevistaDevolucao;
	private LocalDate dataEfetivaDevolucao;
	private BigDecimal valorDiariaContratada;
	private BigDecimal valorFinal;
	private StatusLocacao status;

	// Nova locação: visibilidade de pacote, pois só a Locadora cria locações (composição).
	Locacao(Cliente cliente, Veiculo veiculo, LocalDate dataRetirada, LocalDate dataPrevistaDevolucao) {
		this(0, cliente, veiculo, dataRetirada, dataPrevistaDevolucao, null,
				veiculo == null ? null : veiculo.calcularDiaria(), null, StatusLocacao.ATIVA);
	}

	// Sobrecarga: restaura uma locação com todos os dados já conhecidos (ex.: CSV).
	public Locacao(int id, Cliente cliente, Veiculo veiculo, LocalDate dataRetirada,
			LocalDate dataPrevistaDevolucao, LocalDate dataEfetivaDevolucao, BigDecimal valorDiariaContratada,
			BigDecimal valorFinal, StatusLocacao status) {
		if (cliente == null || veiculo == null) {
			throw new IllegalArgumentException("Locação precisa de cliente e veículo.");
		}
		if (dataRetirada == null || dataPrevistaDevolucao == null) {
			throw new IllegalArgumentException("Datas de retirada e previsão de devolução são obrigatórias.");
		}
		if (dataPrevistaDevolucao.isBefore(dataRetirada)) {
			throw new IllegalArgumentException("A devolução prevista não pode ser antes da retirada.");
		}
		if (status == null || valorDiariaContratada == null) {
			throw new IllegalArgumentException("Status e diária contratada são obrigatórios.");
		}
		if (status == StatusLocacao.FINALIZADA && (dataEfetivaDevolucao == null || valorFinal == null)) {
			throw new IllegalArgumentException("Locação finalizada precisa de data de devolução e valor final.");
		}
		this.id = id;
		this.cliente = cliente;
		this.veiculo = veiculo;
		this.dataRetirada = dataRetirada;
		this.dataPrevistaDevolucao = dataPrevistaDevolucao;
		this.dataEfetivaDevolucao = dataEfetivaDevolucao;
		this.valorDiariaContratada = valorDiariaContratada;
		this.valorFinal = valorFinal;
		this.status = status;
	}

	public boolean estaAtiva() {
		return status == StatusLocacao.ATIVA;
	}

	// Mínimo de 1 diária, mesmo que a devolução prevista seja no mesmo dia.
	public long calcularDiasPrevistos() {
		return Math.max(1, ChronoUnit.DAYS.between(dataRetirada, dataPrevistaDevolucao));
	}

	public BigDecimal calcularValorPrevisto() {
		return arredondar(valorDiariaContratada.multiply(BigDecimal.valueOf(calcularDiasPrevistos())));
	}

	/**
	 * Encerra a locação. Cobra os dias efetivamente usados (mínimo 1) e,
	 * para cada dia de atraso, uma multa de 20% da diária contratada.
	 */
	public void finalizar(LocalDate dataEfetiva) {
		if (!estaAtiva()) {
			throw new IllegalStateException("Locação #" + id + " já foi finalizada.");
		}
		if (dataEfetiva == null || dataEfetiva.isBefore(dataRetirada)) {
			throw new IllegalArgumentException("A devolução não pode ser antes da retirada ("
					+ Formatador.data(dataRetirada) + ").");
		}
		long diasUsados = Math.max(1, ChronoUnit.DAYS.between(dataRetirada, dataEfetiva));
		long diasAtraso = Math.max(0, ChronoUnit.DAYS.between(dataPrevistaDevolucao, dataEfetiva));

		BigDecimal valorDias = valorDiariaContratada.multiply(BigDecimal.valueOf(diasUsados));
		BigDecimal multa = valorDiariaContratada.multiply(MULTA_POR_DIA_ATRASO)
				.multiply(BigDecimal.valueOf(diasAtraso));

		this.dataEfetivaDevolucao = dataEfetiva;
		this.valorFinal = arredondar(valorDias.add(multa));
		this.status = StatusLocacao.FINALIZADA;
	}

	public long calcularDiasAtraso() {
		if (dataEfetivaDevolucao == null) {
			return 0;
		}
		return Math.max(0, ChronoUnit.DAYS.between(dataPrevistaDevolucao, dataEfetivaDevolucao));
	}

	// Visibilidade de pacote: só a Locadora pode definir o id.
	void atribuirId(int id) {
		this.id = id;
	}

	private static BigDecimal arredondar(BigDecimal valor) {
		return valor.setScale(2, RoundingMode.HALF_UP);
	}

	public int getId() {
		return id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public LocalDate getDataRetirada() {
		return dataRetirada;
	}

	public LocalDate getDataPrevistaDevolucao() {
		return dataPrevistaDevolucao;
	}

	public LocalDate getDataEfetivaDevolucao() {
		return dataEfetivaDevolucao;
	}

	public BigDecimal getValorDiariaContratada() {
		return valorDiariaContratada;
	}

	public BigDecimal getValorFinal() {
		return valorFinal;
	}

	public StatusLocacao getStatus() {
		return status;
	}

	@Override
	public String toString() {
		String texto = String.format("Locação #%d [%s] | %s | %s %s (%s) | %s a %s | Diária %s",
				id, status, cliente.getNome(), veiculo.getMarca(), veiculo.getModelo(), veiculo.getPlaca(),
				Formatador.data(dataRetirada), Formatador.data(dataPrevistaDevolucao),
				Formatador.moeda(valorDiariaContratada));
		if (estaAtiva()) {
			return texto + " | Previsto " + Formatador.moeda(calcularValorPrevisto());
		}
		return texto + " | Devolvido em " + Formatador.data(dataEfetivaDevolucao)
				+ " (" + calcularDiasAtraso() + " dia(s) de atraso) | Total " + Formatador.moeda(valorFinal);
	}

}
