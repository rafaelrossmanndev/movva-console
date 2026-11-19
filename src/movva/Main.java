package movva;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import movva.modelo.Carro;
import movva.modelo.CarroEletrico;
import movva.modelo.Moto;
import movva.modelo.Veiculo;

public class Main {

	public static void main(String[] args) {
		System.out.println("=== Movva - Locadora de Veículos ===");

		// --- Teste temporário da Rodada 2 (será substituído pelo menu) ---
		BigDecimal base150 = new BigDecimal("150.00");
		BigDecimal base80 = new BigDecimal("80.00");

		// A lista é do tipo da superclasse, mas guarda objetos de várias subclasses.
		List<Veiculo> frota = new ArrayList<>();
		frota.add(new Carro("ABC1234", "Fiat", "Argo", 2023, base150, 4));
		frota.add(new Carro("DEF5678", "VW", "Up", 2021, base150, 2));
		frota.add(new Moto("GHI1J23", "Honda", "CG 160", 2024, base80, 160));
		frota.add(new Moto("JKL4567", "Yamaha", "MT-07", 2022, base80, 689));
		frota.add(new CarroEletrico("MNO8P90", "BYD", "Dolphin", 2025, base150, 4, 400));

		System.out.println("\n--- Frota (cada objeto calcula a própria diária) ---");
		for (Veiculo v : frota) {
			System.out.println(v); // toString() e calcularDiaria() resolvidos em tempo de execução
		}

		System.out.println("\n--- Alterando a diária base do elétrico para R$ 200,00 ---");
		Veiculo eletrico = frota.get(4);
		eletrico.alterarDiariaBase(new BigDecimal("200"));
		System.out.println(eletrico);

		System.out.println("\n--- Validações ---");
		try {
			new Carro("XYZ-99", "Fiat", "Mobi", 2020, base150, 4);
		} catch (IllegalArgumentException e) {
			System.out.println("Erro esperado: " + e.getMessage());
		}
		try {
			new Moto("QRS1234", "Honda", "Biz", 2020, BigDecimal.ZERO, 110);
		} catch (IllegalArgumentException e) {
			System.out.println("Erro esperado: " + e.getMessage());
		}
	}

}
