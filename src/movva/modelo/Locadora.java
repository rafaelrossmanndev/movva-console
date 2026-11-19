package movva.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe central do domínio: guarda a frota, os clientes e as locações,
 * e garante as regras de negócio da Movva.
 *
 * - Veiculo e Cliente: agregação (existem por si só e são apenas cadastrados aqui).
 * - Locacao: composição (só é criada pela Locadora e não existe fora dela).
 */
public class Locadora {

	private List<Veiculo> veiculos = new ArrayList<>();
	private List<Cliente> clientes = new ArrayList<>();
	private List<Locacao> locacoes = new ArrayList<>();
	private int proximoIdVeiculo = 1;
	private int proximoIdCliente = 1;
	private int proximoIdLocacao = 1;

	// ---------- Veículos ----------

	public void cadastrarVeiculo(Veiculo veiculo) {
		if (veiculo == null) {
			throw new IllegalArgumentException("Veículo inválido.");
		}
		if (placaJaCadastrada(veiculo.getPlaca())) {
			throw new IllegalArgumentException("Já existe um veículo com a placa " + veiculo.getPlaca() + ".");
		}
		if (veiculo.getId() == 0) {
			veiculo.atribuirId(proximoIdVeiculo++);
		} else {
			if (buscarVeiculo(veiculo.getId()) != null) {
				throw new IllegalArgumentException("Já existe um veículo com id " + veiculo.getId() + ".");
			}
			proximoIdVeiculo = Math.max(proximoIdVeiculo, veiculo.getId() + 1);
		}
		veiculos.add(veiculo);
	}

	public Veiculo buscarVeiculo(int id) {
		for (Veiculo v : veiculos) {
			if (v.getId() == id) {
				return v;
			}
		}
		return null;
	}

	// Sobrecarga: mesmo nome, parâmetro de outro tipo.
	public Veiculo buscarVeiculo(String placa) {
		if (placa == null) {
			return null;
		}
		String procurada = placa.replaceAll("[\\s-]", "").toUpperCase();
		for (Veiculo v : veiculos) {
			if (v.getPlaca().equals(procurada)) {
				return v;
			}
		}
		return null;
	}

	public boolean veiculoDisponivel(Veiculo veiculo) {
		for (Locacao l : locacoes) {
			if (l.estaAtiva() && l.getVeiculo() == veiculo) {
				return false;
			}
		}
		return true;
	}

	public List<Veiculo> getVeiculosDisponiveis() {
		List<Veiculo> disponiveis = new ArrayList<>();
		for (Veiculo v : veiculos) {
			if (veiculoDisponivel(v)) {
				disponiveis.add(v);
			}
		}
		return disponiveis;
	}

	private boolean placaJaCadastrada(String placa) {
		return buscarVeiculo(placa) != null;
	}

	// ---------- Clientes ----------

	public void cadastrarCliente(Cliente cliente) {
		if (cliente == null) {
			throw new IllegalArgumentException("Cliente inválido.");
		}
		if (cpfJaCadastrado(cliente.getCpf())) {
			throw new IllegalArgumentException("Já existe um cliente com o CPF " + cliente.getCpfFormatado() + ".");
		}
		if (cliente.getId() == 0) {
			cliente.atribuirId(proximoIdCliente++);
		} else {
			if (buscarCliente(cliente.getId()) != null) {
				throw new IllegalArgumentException("Já existe um cliente com id " + cliente.getId() + ".");
			}
			proximoIdCliente = Math.max(proximoIdCliente, cliente.getId() + 1);
		}
		clientes.add(cliente);
	}

	public Cliente buscarCliente(int id) {
		for (Cliente c : clientes) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}

	private boolean cpfJaCadastrado(String cpf) {
		for (Cliente c : clientes) {
			if (c.getCpf().equals(cpf)) {
				return true;
			}
		}
		return false;
	}

	// ---------- Locações ----------

	public Locacao realizarLocacao(Cliente cliente, Veiculo veiculo, LocalDate retirada, LocalDate previsao) {
		if (!clientes.contains(cliente)) {
			throw new IllegalArgumentException("Cliente não cadastrado na locadora.");
		}
		if (!veiculos.contains(veiculo)) {
			throw new IllegalArgumentException("Veículo não cadastrado na locadora.");
		}
		if (!veiculoDisponivel(veiculo)) {
			throw new IllegalStateException("O veículo " + veiculo.getPlaca() + " já está locado.");
		}
		Locacao locacao = new Locacao(cliente, veiculo, retirada, previsao);
		locacao.atribuirId(proximoIdLocacao++);
		locacoes.add(locacao);
		return locacao;
	}

	public void registrarDevolucao(Locacao locacao, LocalDate dataEfetiva) {
		if (locacao == null || !locacoes.contains(locacao)) {
			throw new IllegalArgumentException("Locação não encontrada.");
		}
		locacao.finalizar(dataEfetiva);
	}

	// Usado pela persistência: adiciona uma locação já existente sem recalcular valores.
	public void restaurarLocacao(Locacao locacao) {
		if (locacao == null) {
			throw new IllegalArgumentException("Locação inválida.");
		}
		if (buscarLocacao(locacao.getId()) != null) {
			throw new IllegalArgumentException("Já existe uma locação com id " + locacao.getId() + ".");
		}
		if (!clientes.contains(locacao.getCliente()) || !veiculos.contains(locacao.getVeiculo())) {
			throw new IllegalArgumentException("Locação #" + locacao.getId() + " referencia cliente ou veículo inexistente.");
		}
		if (locacao.estaAtiva() && !veiculoDisponivel(locacao.getVeiculo())) {
			throw new IllegalStateException("Veículo da locação #" + locacao.getId() + " já está em outra locação ativa.");
		}
		locacoes.add(locacao);
		proximoIdLocacao = Math.max(proximoIdLocacao, locacao.getId() + 1);
	}

	public Locacao buscarLocacao(int id) {
		for (Locacao l : locacoes) {
			if (l.getId() == id) {
				return l;
			}
		}
		return null;
	}

	public List<Locacao> getLocacoesAtivas() {
		List<Locacao> ativas = new ArrayList<>();
		for (Locacao l : locacoes) {
			if (l.estaAtiva()) {
				ativas.add(l);
			}
		}
		return ativas;
	}

	// ---------- Consultas (somente leitura) ----------

	public List<Veiculo> getVeiculos() {
		return Collections.unmodifiableList(veiculos);
	}

	public List<Cliente> getClientes() {
		return Collections.unmodifiableList(clientes);
	}

	public List<Locacao> getLocacoes() {
		return Collections.unmodifiableList(locacoes);
	}

}
