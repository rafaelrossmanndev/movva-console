package movva.modelo;

public class Cliente {

	private int id;
	private String nome;
	private String cpf;
	private String telefone;

	// Construtor usado no cadastro: o id ainda não existe e será atribuído pela Locadora.
	public Cliente(String nome, String cpf, String telefone) {
		this(0, nome, cpf, telefone);
	}

	// Sobrecarga: construtor usado ao restaurar um cliente que já tem id (ex.: lido do CSV).
	public Cliente(int id, String nome, String cpf, String telefone) {
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("Nome do cliente é obrigatório.");
		}
		this.id = id;
		this.nome = nome.trim();
		this.cpf = normalizarCpf(cpf);
		this.telefone = validarTelefone(telefone);
	}

	public void atualizarTelefone(String novoTelefone) {
		this.telefone = validarTelefone(novoTelefone);
	}

	// Visibilidade de pacote: só a Locadora (mesmo pacote) pode definir o id.
	void atribuirId(int id) {
		this.id = id;
	}

	private static String normalizarCpf(String cpf) {
		if (cpf == null) {
			throw new IllegalArgumentException("CPF é obrigatório.");
		}
		String somenteDigitos = cpf.replaceAll("\\D", "");
		if (somenteDigitos.length() != 11) {
			throw new IllegalArgumentException("CPF deve ter 11 dígitos.");
		}
		return somenteDigitos;
	}

	private static String validarTelefone(String telefone) {
		if (telefone == null || telefone.isBlank()) {
			throw new IllegalArgumentException("Telefone é obrigatório.");
		}
		return telefone.trim();
	}

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getCpf() {
		return cpf;
	}

	public String getCpfFormatado() {
		return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
	}

	public String getTelefone() {
		return telefone;
	}

	@Override
	public String toString() {
		return String.format("#%d | %s | CPF %s | Tel %s", id, nome, getCpfFormatado(), telefone);
	}

}
