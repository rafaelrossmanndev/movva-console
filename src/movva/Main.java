package movva;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import movva.modelo.Veiculo;
import movva.modelo.VeiculoFactory;

public class Main {

	public static void main(String[] args) {
		System.out.println("=== Movva - Locadora de Veículos ===");

		// --- Teste temporário da Rodada 3 (será substituído pelo menu) ---
		// Simula dados que chegariam "como texto" (do menu ou de uma linha do CSV).
		String[][] entradas = {
				{ "CARRO", "ABC1234", "Fiat", "Argo", "2023", "150.00", "4", "0" },
				{ "moto", "GHI1J23", "Honda", "CG 160", "2024", "80.00", "160", "0" },
				{ " Eletrico ", "MNO8P90", "BYD", "Dolphin", "2025", "150.00", "4", "400" },
		};

		// O Main não importa Carro, Moto nem CarroEletrico: só conhece Veiculo e a fábrica.
		List<Veiculo> frota = new ArrayList<>();
		for (String[] e : entradas) {
			Veiculo v = VeiculoFactory.criar(e[0], e[1], e[2], e[3], Integer.parseInt(e[4]),
					new BigDecimal(e[5]), Integer.parseInt(e[6]), Integer.parseInt(e[7]));
			frota.add(v);
		}

		System.out.println("\n--- Veículos criados pela fábrica ---");
		for (Veiculo v : frota) {
			System.out.println("Classe real: " + v.getClass().getSimpleName());
			System.out.println("  " + v);
		}

		System.out.println("\n--- Tipo inválido ---");
		try {
			VeiculoFactory.criar("CAMINHAO", "XYZ9876", "Volvo", "FH", 2020, new BigDecimal("500"), 2, 0);
		} catch (IllegalArgumentException ex) {
			System.out.println("Erro esperado: " + ex.getMessage());
		}
	}

}
