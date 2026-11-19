package movva.modelo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Padrão de projeto Factory Method (criacional).
 *
 * Retorna uma instância dentre várias subclasses possíveis de Veiculo,
 * dependendo do tipo informado. Assim, quem cria veículos (menu e persistência)
 * depende apenas da abstração Veiculo, e não de Carro, Moto ou CarroEletrico.
 * Este é o único ponto do sistema que conhece as classes concretas.
 */
public final class VeiculoFactory {

	public static final String CARRO = "CARRO";
	public static final String MOTO = "MOTO";
	public static final String ELETRICO = "ELETRICO";

	public static final List<String> TIPOS = List.of(CARRO, MOTO, ELETRICO);

	private VeiculoFactory() {
		// classe utilitária: não deve ser instanciada
	}

	/**
	 * Cria um veículo novo (sem id; a Locadora atribui depois).
	 *
	 * @param portasOuCilindradas quantidade de portas (CARRO/ELETRICO) ou cilindradas (MOTO)
	 * @param autonomiaKm         usado apenas por ELETRICO (ignorado nos demais)
	 */
	public static Veiculo criar(String tipo, String placa, String marca, String modelo, int ano,
			BigDecimal valorDiaria, int portasOuCilindradas, int autonomiaKm) {
		return criar(tipo, 0, placa, marca, modelo, ano, valorDiaria, portasOuCilindradas, autonomiaKm);
	}

	// Sobrecarga: cria um veículo com id conhecido (usado ao carregar o CSV).
	public static Veiculo criar(String tipo, int id, String placa, String marca, String modelo, int ano,
			BigDecimal valorDiaria, int portasOuCilindradas, int autonomiaKm) {
		switch (normalizarTipo(tipo)) {
		case CARRO:
			return new Carro(id, placa, marca, modelo, ano, valorDiaria, portasOuCilindradas);
		case MOTO:
			return new Moto(id, placa, marca, modelo, ano, valorDiaria, portasOuCilindradas);
		case ELETRICO:
			return new CarroEletrico(id, placa, marca, modelo, ano, valorDiaria, portasOuCilindradas, autonomiaKm);
		default:
			throw new IllegalArgumentException("Tipo de veículo desconhecido: " + tipo + ". Use " + TIPOS + ".");
		}
	}

	private static String normalizarTipo(String tipo) {
		return tipo == null ? "" : tipo.trim().toUpperCase();
	}

}
