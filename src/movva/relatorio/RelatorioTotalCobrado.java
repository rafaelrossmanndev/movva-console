package movva.relatorio;

import java.math.BigDecimal;
import java.math.RoundingMode;

import movva.modelo.Locacao;
import movva.modelo.Locadora;
import movva.util.Formatador;

public class RelatorioTotalCobrado implements Relatorio {

	@Override
	public String gerar(Locadora locadora) {
		BigDecimal faturado = BigDecimal.ZERO;
		BigDecimal emAberto = BigDecimal.ZERO;
		int finalizadas = 0;
		int ativas = 0;

		for (Locacao l : locadora.getLocacoes()) {
			if (l.estaAtiva()) {
				emAberto = emAberto.add(l.calcularValorPrevisto());
				ativas++;
			} else {
				faturado = faturado.add(l.getValorFinal());
				finalizadas++;
			}
		}

		BigDecimal ticketMedio = finalizadas == 0
				? BigDecimal.ZERO
				: faturado.divide(BigDecimal.valueOf(finalizadas), 2, RoundingMode.HALF_UP);

		StringBuilder sb = new StringBuilder();
		sb.append("=== TOTAL COBRADO ===\n");
		sb.append("Faturado (").append(finalizadas).append(" locação(ões) finalizada(s)): ")
				.append(Formatador.moeda(faturado)).append('\n');
		sb.append("Ticket médio por locação: ").append(Formatador.moeda(ticketMedio)).append('\n');
		sb.append("Previsto em aberto (").append(ativas).append(" locação(ões) ativa(s)): ")
				.append(Formatador.moeda(emAberto)).append('\n');
		return sb.toString();
	}

}
