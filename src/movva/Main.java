package movva;

import java.math.BigDecimal;

import movva.modelo.Cliente;
import movva.modelo.Locadora;
import movva.modelo.VeiculoFactory;
import movva.ui.MenuConsole;

public class Main {

	public static void main(String[] args) {
		Locadora locadora = new Locadora();
		carregarDadosDeExemplo(locadora); // temporário: será substituído pela leitura dos CSV (Rodada 7)

		MenuConsole menu = new MenuConsole(locadora);
		menu.iniciar();
	}

	private static void carregarDadosDeExemplo(Locadora locadora) {
		locadora.cadastrarVeiculo(VeiculoFactory.criar("CARRO", "ABC1234", "Fiat", "Argo", 2023, new BigDecimal("150"), 4, 0));
		locadora.cadastrarVeiculo(VeiculoFactory.criar("CARRO", "DEF5678", "VW", "Up", 2021, new BigDecimal("120"), 2, 0));
		locadora.cadastrarVeiculo(VeiculoFactory.criar("MOTO", "GHI1J23", "Honda", "CG 160", 2024, new BigDecimal("80"), 160, 0));
		locadora.cadastrarVeiculo(VeiculoFactory.criar("ELETRICO", "MNO8P90", "BYD", "Dolphin", 2025, new BigDecimal("150"), 4, 400));
		locadora.cadastrarCliente(new Cliente("Ana Souza", "123.456.789-09", "(51) 99999-0000"));
		locadora.cadastrarCliente(new Cliente("Bruno Lima", "987.654.321-00", "(51) 98888-1111"));
	}

}
