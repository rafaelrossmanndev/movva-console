package movva.relatorio;

import java.util.LinkedHashMap;
import java.util.Map;

import movva.modelo.Locadora;
import movva.modelo.Veiculo;

public class RelatorioResumoGeral implements Relatorio {

	@Override
	public String gerar(Locadora locadora) {
		int totalVeiculos = locadora.getVeiculos().size();
		int disponiveis = locadora.getVeiculosDisponiveis().size();
		int ativas = locadora.getLocacoesAtivas().size();
		int totalLocacoes = locadora.getLocacoes().size();

		// Conta a frota por tipo; getTipo() é polimórfico (cada subclasse responde o seu).
		Map<String, Integer> frotaPorTipo = new LinkedHashMap<>();
		for (Veiculo v : locadora.getVeiculos()) {
			frotaPorTipo.put(v.getTipo(), frotaPorTipo.getOrDefault(v.getTipo(), 0) + 1);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("=== RESUMO GERAL ===\n");
		sb.append("Veículos na frota: ").append(totalVeiculos).append('\n');
		for (Map.Entry<String, Integer> item : frotaPorTipo.entrySet()) {
			sb.append("   - ").append(item.getKey()).append(": ").append(item.getValue()).append('\n');
		}
		sb.append("Disponíveis: ").append(disponiveis).append(" | Locados: ").append(totalVeiculos - disponiveis).append('\n');
		sb.append("Clientes cadastrados: ").append(locadora.getClientes().size()).append('\n');
		sb.append("Locações: ").append(totalLocacoes)
				.append(" (ativas: ").append(ativas)
				.append(", finalizadas: ").append(totalLocacoes - ativas).append(")\n");
		return sb.toString();
	}

}
