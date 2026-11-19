package movva.relatorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import movva.modelo.Locacao;
import movva.modelo.Locadora;
import movva.modelo.Veiculo;

public class RelatorioVeiculosMaisAlugados implements Relatorio {

	@Override
	public String gerar(Locadora locadora) {
		// Conta quantas vezes cada veículo foi locado.
		Map<Veiculo, Integer> contagem = new HashMap<>();
		for (Locacao l : locadora.getLocacoes()) {
			contagem.put(l.getVeiculo(), contagem.getOrDefault(l.getVeiculo(), 0) + 1);
		}

		// Ordena do mais alugado para o menos alugado.
		List<Veiculo> ranking = new ArrayList<>(contagem.keySet());
		ranking.sort((a, b) -> Integer.compare(contagem.get(b), contagem.get(a)));

		StringBuilder sb = new StringBuilder();
		sb.append("=== VEÍCULOS MAIS ALUGADOS ===\n");
		if (ranking.isEmpty()) {
			sb.append("Nenhuma locação registrada ainda.\n");
			return sb.toString();
		}

		int posicao = 1;
		for (Veiculo v : ranking) {
			int vezes = contagem.get(v);
			sb.append(String.format("%dº %-8s %s %s (%s) - %d locação(ões)%n",
					posicao++, v.getTipo(), v.getMarca(), v.getModelo(), v.getPlaca(), vezes));
		}
		return sb.toString();
	}

}
