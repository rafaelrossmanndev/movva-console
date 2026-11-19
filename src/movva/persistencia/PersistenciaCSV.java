package movva.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import movva.modelo.Carro;
import movva.modelo.CarroEletrico;
import movva.modelo.Cliente;
import movva.modelo.Locacao;
import movva.modelo.Locadora;
import movva.modelo.Moto;
import movva.modelo.StatusLocacao;
import movva.modelo.Veiculo;
import movva.modelo.VeiculoFactory;

/**
 * Salva e carrega o estado da Locadora em três arquivos-texto CSV (separador ";"):
 * veiculos.csv, clientes.csv e locacoes.csv. Datas no formato ISO (aaaa-mm-dd).
 */
public class PersistenciaCSV implements Persistivel {

	private static final String SEPARADOR = ";";
	private static final String ARQ_VEICULOS = "veiculos.csv";
	private static final String ARQ_CLIENTES = "clientes.csv";
	private static final String ARQ_LOCACOES = "locacoes.csv";

	private static final String CAB_VEICULOS = "tipo;id;placa;marca;modelo;ano;valorDiariaBase;portasOuCilindradas;autonomiaKm";
	private static final String CAB_CLIENTES = "id;nome;cpf;telefone";
	private static final String CAB_LOCACOES = "id;clienteId;veiculoId;dataRetirada;dataPrevistaDevolucao;dataEfetivaDevolucao;valorDiariaContratada;valorFinal;status";

	private final String diretorio;

	public PersistenciaCSV(String diretorio) {
		this.diretorio = diretorio;
	}

	// =====================================================================
	// SALVAR
	// =====================================================================

	@Override
	public void salvar(Locadora locadora) throws IOException {
		File pasta = new File(diretorio);
		if (!pasta.exists() && !pasta.mkdirs()) {
			throw new IOException("Não foi possível criar a pasta " + pasta.getAbsolutePath());
		}
		salvarVeiculos(locadora);
		salvarClientes(locadora);
		salvarLocacoes(locadora);
	}

	private void salvarVeiculos(Locadora locadora) throws IOException {
		// try-with-resources: o arquivo é fechado automaticamente, mesmo se ocorrer erro.
		try (BufferedWriter escritor = abrirEscrita(ARQ_VEICULOS)) {
			escritor.write(CAB_VEICULOS);
			escritor.newLine();
			for (Veiculo v : locadora.getVeiculos()) {
				int portasOuCilindradas = 0;
				int autonomiaKm = 0;
				if (v instanceof Moto) {
					portasOuCilindradas = ((Moto) v).getCilindradas();
				} else if (v instanceof Carro) { // também verdadeiro para CarroEletrico (é um Carro)
					portasOuCilindradas = ((Carro) v).getQuantidadePortas();
				}
				if (v instanceof CarroEletrico) {
					autonomiaKm = ((CarroEletrico) v).getAutonomiaKm();
				}
				escritor.write(juntar(v.getTipo(), v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAno(),
						v.getValorDiariaBase().toPlainString(), portasOuCilindradas, autonomiaKm));
				escritor.newLine();
			}
		}
	}

	private void salvarClientes(Locadora locadora) throws IOException {
		try (BufferedWriter escritor = abrirEscrita(ARQ_CLIENTES)) {
			escritor.write(CAB_CLIENTES);
			escritor.newLine();
			for (Cliente c : locadora.getClientes()) {
				escritor.write(juntar(c.getId(), c.getNome(), c.getCpf(), c.getTelefone()));
				escritor.newLine();
			}
		}
	}

	private void salvarLocacoes(Locadora locadora) throws IOException {
		try (BufferedWriter escritor = abrirEscrita(ARQ_LOCACOES)) {
			escritor.write(CAB_LOCACOES);
			escritor.newLine();
			for (Locacao l : locadora.getLocacoes()) {
				escritor.write(juntar(l.getId(), l.getCliente().getId(), l.getVeiculo().getId(),
						l.getDataRetirada(), l.getDataPrevistaDevolucao(), l.getDataEfetivaDevolucao(),
						l.getValorDiariaContratada().toPlainString(),
						l.getValorFinal() == null ? null : l.getValorFinal().toPlainString(),
						l.getStatus()));
				escritor.newLine();
			}
		}
	}

	private BufferedWriter abrirEscrita(String nomeArquivo) throws IOException {
		return new BufferedWriter(new FileWriter(new File(diretorio, nomeArquivo), StandardCharsets.UTF_8));
	}

	// Monta uma linha CSV. Valores nulos viram campo vazio; ";" no texto é trocado por ",".
	private String juntar(Object... campos) {
		StringBuilder linha = new StringBuilder();
		for (int i = 0; i < campos.length; i++) {
			if (i > 0) {
				linha.append(SEPARADOR);
			}
			if (campos[i] != null) {
				linha.append(campos[i].toString().replace(SEPARADOR, ",").replace("\n", " "));
			}
		}
		return linha.toString();
	}

	// =====================================================================
	// CARREGAR
	// =====================================================================

	@Override
	public void carregar(Locadora locadora) throws IOException {
		// A ordem importa: locações referenciam clientes e veículos pelo id.
		carregarVeiculos(locadora);
		carregarClientes(locadora);
		carregarLocacoes(locadora);
	}

	private void carregarVeiculos(Locadora locadora) throws IOException {
		File arquivo = new File(diretorio, ARQ_VEICULOS);
		if (!arquivo.exists()) {
			return; // primeira execução: ainda não há dados
		}
		try (BufferedReader leitor = abrirLeitura(arquivo)) {
			leitor.readLine(); // descarta a linha de cabeçalho
			String linha;
			int numero = 1;
			while ((linha = leitor.readLine()) != null) {
				numero++;
				if (linha.isBlank()) {
					continue;
				}
				try {
					String[] c = linha.split(SEPARADOR, -1);
					// Factory Method: a fábrica decide qual subclasse criar a partir da coluna "tipo".
					Veiculo v = VeiculoFactory.criar(c[0], Integer.parseInt(c[1]), c[2], c[3], c[4],
							Integer.parseInt(c[5]), new BigDecimal(c[6]), Integer.parseInt(c[7]), Integer.parseInt(c[8]));
					locadora.cadastrarVeiculo(v);
				} catch (RuntimeException e) {
					throw erroDeLinha(arquivo, numero, e);
				}
			}
		}
	}

	private void carregarClientes(Locadora locadora) throws IOException {
		File arquivo = new File(diretorio, ARQ_CLIENTES);
		if (!arquivo.exists()) {
			return;
		}
		try (BufferedReader leitor = abrirLeitura(arquivo)) {
			leitor.readLine(); // descarta a linha de cabeçalho
			String linha;
			int numero = 1;
			while ((linha = leitor.readLine()) != null) {
				numero++;
				if (linha.isBlank()) {
					continue;
				}
				try {
					String[] c = linha.split(SEPARADOR, -1);
					locadora.cadastrarCliente(new Cliente(Integer.parseInt(c[0]), c[1], c[2], c[3]));
				} catch (RuntimeException e) {
					throw erroDeLinha(arquivo, numero, e);
				}
			}
		}
	}

	private void carregarLocacoes(Locadora locadora) throws IOException {
		File arquivo = new File(diretorio, ARQ_LOCACOES);
		if (!arquivo.exists()) {
			return;
		}
		try (BufferedReader leitor = abrirLeitura(arquivo)) {
			leitor.readLine(); // descarta a linha de cabeçalho
			String linha;
			int numero = 1;
			while ((linha = leitor.readLine()) != null) {
				numero++;
				if (linha.isBlank()) {
					continue;
				}
				try {
					String[] c = linha.split(SEPARADOR, -1);
					Cliente cliente = locadora.buscarCliente(Integer.parseInt(c[1]));
					Veiculo veiculo = locadora.buscarVeiculo(Integer.parseInt(c[2]));
					Locacao l = new Locacao(Integer.parseInt(c[0]), cliente, veiculo,
							LocalDate.parse(c[3]), LocalDate.parse(c[4]), lerDataOpcional(c[5]),
							new BigDecimal(c[6]), lerDecimalOpcional(c[7]), StatusLocacao.valueOf(c[8]));
					locadora.restaurarLocacao(l);
				} catch (RuntimeException e) {
					throw erroDeLinha(arquivo, numero, e);
				}
			}
		}
	}

	private BufferedReader abrirLeitura(File arquivo) throws IOException {
		return new BufferedReader(new FileReader(arquivo, StandardCharsets.UTF_8));
	}

	private LocalDate lerDataOpcional(String texto) {
		return texto.isBlank() ? null : LocalDate.parse(texto);
	}

	private BigDecimal lerDecimalOpcional(String texto) {
		return texto.isBlank() ? null : new BigDecimal(texto);
	}

	// Converte qualquer falha de leitura em IOException indicando arquivo e linha.
	private IOException erroDeLinha(File arquivo, int numeroLinha, RuntimeException causa) {
		String motivo;
		if (causa instanceof NumberFormatException) {
			motivo = "valor numérico inválido";
		} else if (causa instanceof DateTimeParseException) {
			motivo = "data inválida (esperado aaaa-mm-dd)";
		} else if (causa instanceof ArrayIndexOutOfBoundsException) {
			motivo = "quantidade de colunas incorreta";
		} else {
			motivo = causa.getMessage(); // mensagens das regras do modelo (já em português)
		}
		return new IOException(arquivo.getName() + ", linha " + numeroLinha + ": " + motivo, causa);
	}

}
