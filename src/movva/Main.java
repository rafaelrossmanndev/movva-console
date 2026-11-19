package movva;

import java.math.BigDecimal;
import java.time.LocalDate;

import movva.modelo.Cliente;
import movva.modelo.Locacao;
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

		Cliente ana = locadora.buscarCliente(1);
		Cliente bruno = locadora.buscarCliente(2);
		Locacao l1 = locadora.realizarLocacao(ana, locadora.buscarVeiculo(1), LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 5));
		locadora.registrarDevolucao(l1, LocalDate.of(2026, 11, 7)); // 2 dias de atraso
		Locacao l2 = locadora.realizarLocacao(bruno, locadora.buscarVeiculo(3), LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 6));
		locadora.registrarDevolucao(l2, LocalDate.of(2026, 11, 6)); // no prazo
		Locacao l3 = locadora.realizarLocacao(bruno, locadora.buscarVeiculo(1), LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
		locadora.registrarDevolucao(l3, LocalDate.of(2026, 11, 12));
		locadora.realizarLocacao(ana, locadora.buscarVeiculo(4), LocalDate.of(2026, 11, 18), LocalDate.of(2026, 11, 22)); // ativa
	}

}
