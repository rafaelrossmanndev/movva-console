package movva;

import movva.modelo.Cliente;

public class Main {

	public static void main(String[] args) {
		System.out.println("=== Movva - Locadora de Veículos ===");

		// --- Teste temporário da Rodada 1 (será substituído pelo menu) ---
		Cliente ana = new Cliente("Ana Souza", "123.456.789-09", "(51) 99999-0000");
		Cliente bruno = new Cliente(7, "Bruno Lima", "98765432100", "(51) 98888-1111");
		System.out.println(ana);
		System.out.println(bruno);

		bruno.atualizarTelefone("(51) 97777-2222");
		System.out.println("Após atualizar telefone: " + bruno);

		try {
			new Cliente("Carlos", "123", "(51) 90000-0000");
		} catch (IllegalArgumentException e) {
			System.out.println("Erro esperado: " + e.getMessage());
		}

		try {
			ana.atualizarTelefone("   ");
		} catch (IllegalArgumentException e) {
			System.out.println("Erro esperado: " + e.getMessage());
		}
	}

}
