package movva.modelo;

import java.math.BigDecimal;

public class Carro extends Veiculo {

	private static final BigDecimal ACRESCIMO_QUATRO_PORTAS = new BigDecimal("1.10");

	private int quantidadePortas;

	// Cadastro novo (id definido depois pela Locadora).
	public Carro(String placa, String marca, String modelo, int ano, BigDecimal valorDiaria, int quantidadePortas) {
		this(0, placa, marca, modelo, ano, valorDiaria, quantidadePortas);
	}

	// Sobrecarga: restauração com id conhecido (ex.: CSV).
	public Carro(int id, String placa, String marca, String modelo, int ano, BigDecimal valorDiaria,
			int quantidadePortas) {
		super(id, placa, marca, modelo, ano, valorDiaria);
		if (quantidadePortas < 2 || quantidadePortas > 5) {
			throw new IllegalArgumentException("Quantidade de portas deve estar entre 2 e 5.");
		}
		this.quantidadePortas = quantidadePortas;
	}

	// Regra: carros de 4 portas ou mais custam 10% a mais que a diária base.
	@Override
	public BigDecimal calcularDiaria() {
		BigDecimal base = getValorDiariaBase();
		if (quantidadePortas >= 4) {
			return arredondar(base.multiply(ACRESCIMO_QUATRO_PORTAS));
		}
		return arredondar(base);
	}

	@Override
	public String getTipo() {
		return "CARRO";
	}

	public int getQuantidadePortas() {
		return quantidadePortas;
	}

	@Override
	public String toString() {
		return super.toString() + " | " + quantidadePortas + " portas";
	}

}
