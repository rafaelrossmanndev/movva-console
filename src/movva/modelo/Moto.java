package movva.modelo;

import java.math.BigDecimal;

public class Moto extends Veiculo {

	private static final int LIMITE_BAIXA_CILINDRADA = 300;
	private static final BigDecimal DESCONTO_BAIXA_CILINDRADA = new BigDecimal("0.85");

	private int cilindradas;

	// Cadastro novo (id definido depois pela Locadora).
	public Moto(String placa, String marca, String modelo, int ano, BigDecimal valorDiaria, int cilindradas) {
		this(0, placa, marca, modelo, ano, valorDiaria, cilindradas);
	}

	// Sobrecarga: restauração com id conhecido (ex.: CSV).
	public Moto(int id, String placa, String marca, String modelo, int ano, BigDecimal valorDiaria,
			int cilindradas) {
		super(id, placa, marca, modelo, ano, valorDiaria);
		if (cilindradas <= 0) {
			throw new IllegalArgumentException("Cilindradas deve ser maior que zero.");
		}
		this.cilindradas = cilindradas;
	}

	// Regra: motos de até 300 cc têm 15% de desconto sobre a diária base.
	@Override
	public BigDecimal calcularDiaria() {
		BigDecimal base = getValorDiariaBase();
		if (cilindradas <= LIMITE_BAIXA_CILINDRADA) {
			return arredondar(base.multiply(DESCONTO_BAIXA_CILINDRADA));
		}
		return arredondar(base);
	}

	@Override
	public String getTipo() {
		return "MOTO";
	}

	public int getCilindradas() {
		return cilindradas;
	}

	@Override
	public String toString() {
		return super.toString() + " | " + cilindradas + " cc";
	}

}
