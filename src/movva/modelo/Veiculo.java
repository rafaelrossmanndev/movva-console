package movva.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import movva.util.Formatador;

// Classe abstrata: não existe "um veículo genérico" na locadora, só Carro, Moto ou CarroEletrico.
public abstract class Veiculo {

	private int id;
	private String placa;
	private String marca;
	private String modelo;
	private int ano;
	private BigDecimal valorDiaria;

	protected Veiculo(int id, String placa, String marca, String modelo, int ano, BigDecimal valorDiaria) {
		this.id = id;
		this.placa = normalizarPlaca(placa);
		this.marca = textoObrigatorio(marca, "Marca");
		this.modelo = textoObrigatorio(modelo, "Modelo");
		this.ano = validarAno(ano);
		this.valorDiaria = validarValor(valorDiaria);
	}

	// Cada tipo de veículo calcula sua diária de um jeito (polimorfismo).
	public abstract BigDecimal calcularDiaria();

	// Código do tipo, usado na listagem, nos relatórios e no CSV.
	public abstract String getTipo();

	public void alterarDiariaBase(BigDecimal novoValor) {
		this.valorDiaria = validarValor(novoValor);
	}

	// Visibilidade de pacote: só a Locadora pode definir o id.
	void atribuirId(int id) {
		this.id = id;
	}

	// Utilitário para as subclasses: padroniza valores monetários com 2 casas.
	protected static BigDecimal arredondar(BigDecimal valor) {
		return valor.setScale(2, RoundingMode.HALF_UP);
	}

	public static String normalizarPlaca(String placa) {
		if (placa == null) {
			throw new IllegalArgumentException("Placa é obrigatória.");
		}
		String normalizada = placa.replaceAll("[\\s-]", "").toUpperCase();
		// Aceita o padrão antigo (ABC1234) e o Mercosul (ABC1D23).
		if (!normalizada.matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")) {
			throw new IllegalArgumentException("Placa inválida: use o formato ABC1234 ou ABC1D23.");
		}
		return normalizada;
	}

	private static String textoObrigatorio(String valor, String campo) {
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException(campo + " é obrigatório(a).");
		}
		return valor.trim();
	}

	private static int validarAno(int ano) {
		int limite = LocalDate.now().getYear() + 1;
		if (ano < 1990 || ano > limite) {
			throw new IllegalArgumentException("Ano deve estar entre 1990 e " + limite + ".");
		}
		return ano;
	}

	private static BigDecimal validarValor(BigDecimal valor) {
		if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Valor da diária deve ser maior que zero.");
		}
		return arredondar(valor);
	}

	public int getId() {
		return id;
	}

	public String getPlaca() {
		return placa;
	}

	public String getMarca() {
		return marca;
	}

	public String getModelo() {
		return modelo;
	}

	public int getAno() {
		return ano;
	}

	public BigDecimal getValorDiariaBase() {
		return valorDiaria;
	}

	@Override
	public String toString() {
		return String.format("#%d | %-8s | %s %s %d | Placa %s | Base %s | Diária %s",
				id, getTipo(), marca, modelo, ano, placa,
				Formatador.moeda(valorDiaria), Formatador.moeda(calcularDiaria()));
	}

}
