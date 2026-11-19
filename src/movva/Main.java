package movva;

import java.math.BigDecimal;
import java.time.LocalDate;

import movva.modelo.Cliente;
import movva.modelo.Locacao;
import movva.modelo.Locadora;
import movva.modelo.Veiculo;
import movva.modelo.VeiculoFactory;

public class Main {

	public static void main(String[] args) {
		System.out.println("=== Movva - Locadora de Veículos ===");

		// --- Teste temporário da Rodada 4 (será substituído pelo menu) ---
		Locadora locadora = new Locadora();

		locadora.cadastrarVeiculo(VeiculoFactory.criar("CARRO", "ABC1234", "Fiat", "Argo", 2023, new BigDecimal("150"), 4, 0));
		locadora.cadastrarVeiculo(VeiculoFactory.criar("MOTO", "GHI1J23", "Honda", "CG 160", 2024, new BigDecimal("80"), 160, 0));
		locadora.cadastrarVeiculo(VeiculoFactory.criar("ELETRICO", "MNO8P90", "BYD", "Dolphin", 2025, new BigDecimal("150"), 4, 400));
		locadora.cadastrarCliente(new Cliente("Ana Souza", "123.456.789-09", "(51) 99999-0000"));
		locadora.cadastrarCliente(new Cliente("Bruno Lima", "987.654.321-00", "(51) 98888-1111"));

		System.out.println("\n1) Cadastros (ids gerados pela Locadora)");
		locadora.getVeiculos().forEach(System.out::println);
		locadora.getClientes().forEach(System.out::println);

		System.out.println("\n2) Duplicidades");
		tentar(() -> locadora.cadastrarVeiculo(VeiculoFactory.criar("CARRO", "abc-1234", "VW", "Gol", 2020, new BigDecimal("100"), 4, 0)));
		tentar(() -> locadora.cadastrarCliente(new Cliente("Outra Ana", "12345678909", "(51) 90000-0000")));

		System.out.println("\n3) Sobrecarga de buscarVeiculo");
		System.out.println("buscarVeiculo(2)          -> " + locadora.buscarVeiculo(2).getModelo());
		System.out.println("buscarVeiculo(\"abc-1234\") -> " + locadora.buscarVeiculo("abc-1234").getModelo());

		System.out.println("\n4) Locação do Argo por 4 dias (01/11 a 05/11)");
		Cliente ana = locadora.buscarCliente(1);
		Veiculo argo = locadora.buscarVeiculo("ABC1234");
		Locacao locacao = locadora.realizarLocacao(ana, argo, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 5));
		System.out.println(locacao);
		System.out.println("Argo disponível? " + locadora.veiculoDisponivel(argo));

		System.out.println("\n5) Tentar locar o mesmo veículo");
		Cliente bruno = locadora.buscarCliente(2);
		tentar(() -> locadora.realizarLocacao(bruno, argo, LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 3)));

		System.out.println("\n6) Diária base sobe para R$ 300 -> a locação mantém a diária contratada");
		argo.alterarDiariaBase(new BigDecimal("300"));
		System.out.println("Diária atual do Argo: " + argo.calcularDiaria());
		System.out.println(locacao);

		System.out.println("\n7) Devolução em 07/11 (2 dias de atraso)");
		locadora.registrarDevolucao(locacao, LocalDate.of(2026, 11, 7));
		System.out.println(locacao);
		System.out.println("Argo disponível? " + locadora.veiculoDisponivel(argo));

		System.out.println("\n8) Devolver de novo");
		tentar(() -> locadora.registrarDevolucao(locacao, LocalDate.of(2026, 11, 8)));
	}

	// Executa uma ação que deve falhar e mostra a mensagem do erro.
	private static void tentar(Runnable acao) {
		try {
			acao.run();
			System.out.println("(nenhum erro?)");
		} catch (IllegalArgumentException | IllegalStateException e) {
			System.out.println("Erro esperado: " + e.getMessage());
		}
	}

}
