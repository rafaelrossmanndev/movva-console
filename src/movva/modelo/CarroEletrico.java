package movva.modelo;

import java.math.BigDecimal;

// Terceiro nível da herança: Veiculo -> Carro -> CarroEletrico.
public class CarroEletrico extends Carro {

	private static final BigDecimal TAXA_RECARGA = new BigDecimal("25.00");

	private int autonomiaKm;

	// Cadastro novo (id definido depois pela Locadora).
	public CarroEletrico(String placa, String marca, String modelo, int ano, BigDecimal valorDiaria,
			int quantidadePortas, int autonomiaKm) {
		this(0, placa, marca, modelo, ano, valorDiaria, quantidadePortas, autonomiaKm);
	}

	// Sobrecarga: restauração com id conhecido (ex.: CSV).
	public CarroEletrico(int id, String placa, String marca, String modelo, int ano, BigDecimal valorDiaria,
			int quantidadePortas, int autonomiaKm) {
		super(id, placa, marca, modelo, ano, valorDiaria, quantidadePortas);
		if (autonomiaKm <= 0) {
			throw new IllegalArgumentException("Autonomia deve ser maior que zero.");
		}
		this.autonomiaKm = autonomiaKm;
	}

	// Reaproveita a regra de Carro (portas) e soma a taxa fixa de recarga.
	@Override
	public BigDecimal calcularDiaria() {
		return arredondar(super.calcularDiaria().add(TAXA_RECARGA));
	}

	@Override
	public String getTipo() {
		return "ELETRICO";
	}

	public int getAutonomiaKm() {
		return autonomiaKm;
	}

	@Override
	public String toString() {
		return super.toString() + " | autonomia " + autonomiaKm + " km";
	}

}
