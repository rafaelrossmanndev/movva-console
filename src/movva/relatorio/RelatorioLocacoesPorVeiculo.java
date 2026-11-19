package movva.relatorio;

import movva.modelo.Locacao;
import movva.modelo.Locadora;
import movva.modelo.Veiculo;

public class RelatorioLocacoesPorVeiculo implements Relatorio {

	@Override
	public String gerar(Locadora locadora) {
		StringBuilder sb = new StringBuilder();
		sb.append("=== LOCAÇÕES POR VEÍCULO ===\n");

		if (locadora.getVeiculos().isEmpty()) {
			sb.append("Nenhum veículo cadastrado.\n");
			return sb.toString();
		}

		for (Veiculo v : locadora.getVeiculos()) {
			String situacao = locadora.veiculoDisponivel(v) ? "disponível" : "LOCADO";
			sb.append(String.format("#%d %s %s (%s) - %s%n", v.getId(), v.getMarca(), v.getModelo(), v.getPlaca(), situacao));

			boolean possuiLocacao = false;
			for (Locacao l : locadora.getLocacoes()) {
				if (l.getVeiculo() == v) {
					sb.append("   ").append(l).append('\n');
					possuiLocacao = true;
				}
			}
			if (!possuiLocacao) {
				sb.append("   (nenhuma locação)\n");
			}
		}
		return sb.toString();
	}

}
