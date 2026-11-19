package movva.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import movva.modelo.Cliente;
import movva.modelo.Locacao;
import movva.modelo.Locadora;
import movva.modelo.Veiculo;
import movva.modelo.VeiculoFactory;
import movva.util.Formatador;

/**
 * Interface de texto do sistema. Só conversa com o usuário (lê e mostra);
 * todas as regras de negócio ficam na Locadora e nas classes do modelo.
 */
public class MenuConsole {

	private final Locadora locadora;
	private final Scanner scanner;

	public MenuConsole(Locadora locadora) {
		this.locadora = locadora;
		this.scanner = new Scanner(System.in);
	}

	public void iniciar() {
		System.out.println("==========================================");
		System.out.println("     MOVVA - Locadora de Veículos");
		System.out.println("==========================================");
		try {
			int opcao;
			do {
				exibirMenuPrincipal();
				opcao = lerInteiro("Opção: ", 0, 3);
				switch (opcao) {
				case 1:
					menuVeiculos();
					break;
				case 2:
					menuClientes();
					break;
				case 3:
					menuLocacoes();
					break;
				default:
					break;
				}
			} while (opcao != 0);
		} catch (NoSuchElementException e) {
			// A entrada foi encerrada (ex.: Ctrl+Z no Windows): sai do sistema normalmente.
		}
		System.out.println("\nAté logo! Obrigado por usar a Movva.");
	}

	private void exibirMenuPrincipal() {
		titulo("MENU PRINCIPAL");
		System.out.println("1 - Veículos");
		System.out.println("2 - Clientes");
		System.out.println("3 - Locações");
		System.out.println("0 - Sair");
	}

	// =====================================================================
	// VEÍCULOS
	// =====================================================================

	private void menuVeiculos() {
		int opcao;
		do {
			titulo("VEÍCULOS");
			System.out.println("1 - Cadastrar veículo");
			System.out.println("2 - Listar todos");
			System.out.println("3 - Listar disponíveis");
			System.out.println("4 - Buscar por id");
			System.out.println("5 - Buscar por placa");
			System.out.println("6 - Alterar diária base");
			System.out.println("0 - Voltar");
			opcao = lerInteiro("Opção: ", 0, 6);
			try {
				switch (opcao) {
				case 1:
					cadastrarVeiculo();
					break;
				case 2:
					listar(locadora.getVeiculos(), "Nenhum veículo cadastrado.");
					break;
				case 3:
					listar(locadora.getVeiculosDisponiveis(), "Nenhum veículo disponível no momento.");
					break;
				case 4:
					mostrarResultadoBusca(locadora.buscarVeiculo(lerInteiro("Id do veículo: ")));
					break;
				case 5:
					mostrarResultadoBusca(locadora.buscarVeiculo(lerTexto("Placa: ")));
					break;
				case 6:
					alterarDiariaBase();
					break;
				default:
					break;
				}
			} catch (IllegalArgumentException | IllegalStateException e) {
				erro(e.getMessage());
			}
			if (opcao != 0) {
				pausar();
			}
		} while (opcao != 0);
	}

	private void cadastrarVeiculo() {
		System.out.println("Tipos disponíveis:");
		List<String> tipos = VeiculoFactory.TIPOS;
		for (int i = 0; i < tipos.size(); i++) {
			System.out.println("  " + (i + 1) + " - " + tipos.get(i));
		}
		String tipo = tipos.get(lerInteiro("Tipo: ", 1, tipos.size()) - 1);

		String placa = lerPlaca();
		if (locadora.buscarVeiculo(placa) != null) {
			throw new IllegalArgumentException("Já existe um veículo com a placa " + placa + ".");
		}
		String marca = lerTexto("Marca: ");
		String modelo = lerTexto("Modelo: ");
		int ano = lerInteiro("Ano: ");
		BigDecimal diaria = lerDecimal("Diária base (R$): ");

		// Campos específicos de cada tipo.
		int portasOuCilindradas;
		int autonomiaKm = 0;
		if (tipo.equals(VeiculoFactory.MOTO)) {
			portasOuCilindradas = lerInteiro("Cilindradas (cc): ");
		} else {
			portasOuCilindradas = lerInteiro("Quantidade de portas: ");
		}
		if (tipo.equals(VeiculoFactory.ELETRICO)) {
			autonomiaKm = lerInteiro("Autonomia (km): ");
		}

		// Factory Method: o menu não precisa saber qual subclasse instanciar.
		Veiculo veiculo = VeiculoFactory.criar(tipo, placa, marca, modelo, ano, diaria, portasOuCilindradas,
				autonomiaKm);
		locadora.cadastrarVeiculo(veiculo);
		sucesso("Veículo cadastrado: " + veiculo);
	}

	private void alterarDiariaBase() {
		Veiculo veiculo = locadora.buscarVeiculo(lerInteiro("Id do veículo: "));
		if (veiculo == null) {
			erro("Veículo não encontrado.");
			return;
		}
		System.out.println("Atual: " + veiculo);
		veiculo.alterarDiariaBase(lerDecimal("Nova diária base (R$): "));
		sucesso("Diária atualizada: " + veiculo);
	}

	// =====================================================================
	// CLIENTES
	// =====================================================================

	private void menuClientes() {
		int opcao;
		do {
			titulo("CLIENTES");
			System.out.println("1 - Cadastrar cliente");
			System.out.println("2 - Listar clientes");
			System.out.println("3 - Atualizar telefone");
			System.out.println("0 - Voltar");
			opcao = lerInteiro("Opção: ", 0, 3);
			try {
				switch (opcao) {
				case 1:
					cadastrarCliente();
					break;
				case 2:
					listar(locadora.getClientes(), "Nenhum cliente cadastrado.");
					break;
				case 3:
					atualizarTelefone();
					break;
				default:
					break;
				}
			} catch (IllegalArgumentException | IllegalStateException e) {
				erro(e.getMessage());
			}
			if (opcao != 0) {
				pausar();
			}
		} while (opcao != 0);
	}

	private void cadastrarCliente() {
		String nome = lerTexto("Nome: ");
		String cpf = lerTexto("CPF: ");
		String telefone = lerTexto("Telefone: ");
		Cliente cliente = new Cliente(nome, cpf, telefone);
		locadora.cadastrarCliente(cliente);
		sucesso("Cliente cadastrado: " + cliente);
	}

	private void atualizarTelefone() {
		listar(locadora.getClientes(), "Nenhum cliente cadastrado.");
		if (locadora.getClientes().isEmpty()) {
			return;
		}
		Cliente cliente = locadora.buscarCliente(lerInteiro("Id do cliente: "));
		if (cliente == null) {
			erro("Cliente não encontrado.");
			return;
		}
		cliente.atualizarTelefone(lerTexto("Novo telefone: "));
		sucesso("Telefone atualizado: " + cliente);
	}

	// =====================================================================
	// LOCAÇÕES
	// =====================================================================

	private void menuLocacoes() {
		int opcao;
		do {
			titulo("LOCAÇÕES");
			System.out.println("1 - Nova locação");
			System.out.println("2 - Registrar devolução");
			System.out.println("3 - Listar locações ativas");
			System.out.println("4 - Listar todas as locações");
			System.out.println("0 - Voltar");
			opcao = lerInteiro("Opção: ", 0, 4);
			try {
				switch (opcao) {
				case 1:
					novaLocacao();
					break;
				case 2:
					registrarDevolucao();
					break;
				case 3:
					listar(locadora.getLocacoesAtivas(), "Nenhuma locação ativa.");
					break;
				case 4:
					listar(locadora.getLocacoes(), "Nenhuma locação registrada.");
					break;
				default:
					break;
				}
			} catch (IllegalArgumentException | IllegalStateException e) {
				erro(e.getMessage());
			}
			if (opcao != 0) {
				pausar();
			}
		} while (opcao != 0);
	}

	private void novaLocacao() {
		if (locadora.getClientes().isEmpty()) {
			erro("Cadastre um cliente antes de fazer uma locação.");
			return;
		}
		List<Veiculo> disponiveis = locadora.getVeiculosDisponiveis();
		if (disponiveis.isEmpty()) {
			erro("Nenhum veículo disponível no momento.");
			return;
		}

		System.out.println("Clientes:");
		listar(locadora.getClientes(), "");
		Cliente cliente = locadora.buscarCliente(lerInteiro("Id do cliente: "));
		if (cliente == null) {
			erro("Cliente não encontrado.");
			return;
		}

		System.out.println("Veículos disponíveis:");
		listar(disponiveis, "");
		Veiculo veiculo = locadora.buscarVeiculo(lerInteiro("Id do veículo: "));
		if (veiculo == null) {
			erro("Veículo não encontrado.");
			return;
		}
		if (!locadora.veiculoDisponivel(veiculo)) {
			erro("O veículo " + veiculo.getPlaca() + " já está locado.");
			return;
		}

		LocalDate retirada = lerData("Data de retirada (dd/mm/aaaa) [Enter = hoje]: ", LocalDate.now());
		LocalDate previsao = lerData("Previsão de devolução (dd/mm/aaaa): ");

		Locacao locacao = locadora.realizarLocacao(cliente, veiculo, retirada, previsao);
		sucesso("Locação registrada!");
		System.out.println(locacao);
		System.out.println("Dias previstos: " + locacao.calcularDiasPrevistos());
	}

	private void registrarDevolucao() {
		List<Locacao> ativas = locadora.getLocacoesAtivas();
		if (ativas.isEmpty()) {
			erro("Nenhuma locação ativa para devolver.");
			return;
		}
		listar(ativas, "");
		Locacao locacao = locadora.buscarLocacao(lerInteiro("Id da locação: "));
		if (locacao == null) {
			erro("Locação não encontrada.");
			return;
		}
		LocalDate data = lerData("Data da devolução (dd/mm/aaaa) [Enter = hoje]: ", LocalDate.now());
		locadora.registrarDevolucao(locacao, data);
		sucesso("Devolução registrada!");
		System.out.println(locacao);
		if (locacao.calcularDiasAtraso() > 0) {
			System.out.println("Atenção: " + locacao.calcularDiasAtraso()
					+ " dia(s) de atraso, multa de 20% da diária por dia incluída no total.");
		}
		System.out.println("Total a pagar: " + Formatador.moeda(locacao.getValorFinal()));
	}

	// =====================================================================
	// SAÍDA (helpers de exibição)
	// =====================================================================

	// Aceita lista de qualquer tipo: cada item se exibe pelo próprio toString() (polimorfismo).
	private void listar(List<?> itens, String mensagemSeVazia) {
		if (itens.isEmpty()) {
			if (!mensagemSeVazia.isEmpty()) {
				System.out.println(mensagemSeVazia);
			}
			return;
		}
		for (Object item : itens) {
			System.out.println("  " + item);
		}
	}

	private void mostrarResultadoBusca(Object encontrado) {
		if (encontrado == null) {
			erro("Nenhum registro encontrado.");
		} else {
			System.out.println("  " + encontrado);
		}
	}

	private void titulo(String texto) {
		System.out.println();
		System.out.println("---------- " + texto + " ----------");
	}

	private void sucesso(String mensagem) {
		System.out.println("[OK] " + mensagem);
	}

	private void erro(String mensagem) {
		System.out.println("[ERRO] " + mensagem);
	}

	private void pausar() {
		System.out.print("\nPressione Enter para continuar...");
		scanner.nextLine();
	}

	// =====================================================================
	// ENTRADA (leitura segura: repete até o valor ser válido)
	// =====================================================================

	private String lerTexto(String rotulo) {
		while (true) {
			System.out.print(rotulo);
			String texto = scanner.nextLine().trim();
			if (!texto.isEmpty()) {
				return texto;
			}
			erro("Campo obrigatório.");
		}
	}

	private int lerInteiro(String rotulo) {
		while (true) {
			String texto = lerTexto(rotulo);
			try {
				return Integer.parseInt(texto);
			} catch (NumberFormatException e) {
				erro("Digite um número inteiro.");
			}
		}
	}

	// Sobrecarga: mesma leitura, mas restrita a um intervalo (usada nas opções de menu).
	private int lerInteiro(String rotulo, int minimo, int maximo) {
		while (true) {
			int valor = lerInteiro(rotulo);
			if (valor >= minimo && valor <= maximo) {
				return valor;
			}
			erro("Escolha um valor entre " + minimo + " e " + maximo + ".");
		}
	}

	private BigDecimal lerDecimal(String rotulo) {
		while (true) {
			String texto = lerTexto(rotulo).replace(",", ".");
			try {
				return new BigDecimal(texto);
			} catch (NumberFormatException e) {
				erro("Digite um valor numérico (ex.: 150,00).");
			}
		}
	}

	private String lerPlaca() {
		while (true) {
			try {
				return Veiculo.normalizarPlaca(lerTexto("Placa: "));
			} catch (IllegalArgumentException e) {
				erro(e.getMessage());
			}
		}
	}

	private LocalDate lerData(String rotulo) {
		while (true) {
			try {
				return LocalDate.parse(lerTexto(rotulo), Formatador.FORMATO_DATA);
			} catch (DateTimeParseException e) {
				erro("Data inválida. Use o formato dd/mm/aaaa.");
			}
		}
	}

	// Sobrecarga: se o usuário apertar Enter sem digitar nada, usa a data padrão.
	private LocalDate lerData(String rotulo, LocalDate padrao) {
		while (true) {
			System.out.print(rotulo);
			String texto = scanner.nextLine().trim();
			if (texto.isEmpty()) {
				return padrao;
			}
			try {
				return LocalDate.parse(texto, Formatador.FORMATO_DATA);
			} catch (DateTimeParseException e) {
				erro("Data inválida. Use o formato dd/mm/aaaa.");
			}
		}
	}

}
