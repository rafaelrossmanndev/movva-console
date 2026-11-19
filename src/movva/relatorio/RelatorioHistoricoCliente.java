package movva.relatorio;

import java.math.BigDecimal;

import movva.modelo.Cliente;
import movva.modelo.Locacao;
import movva.modelo.Locadora;
import movva.util.Formatador;

// Diferente dos outros relatórios, este precisa de um parâmetro (o cliente),
// recebido no construtor. A assinatura de gerar() continua a mesma da interface.
public class RelatorioHistoricoCliente implements Relatorio {

	private final Cliente cliente;

	public RelatorioHistoricoCliente(Cliente cliente) {
		if (cliente == null) {
			throw new IllegalArgumentException("Informe o cliente do histórico.");
		}
		this.cliente = cliente;
	}

	@Override
	public String gerar(Locadora locadora) {
		StringBuilder sb = new StringBuilder();
		sb.append("=== HISTÓRICO DO CLIENTE ===\n");
		sb.append(cliente).append('\n');

		int quantidade = 0;
		BigDecimal totalPago = BigDecimal.ZERO;
		for (Locacao l : locadora.getLocacoes()) {
			if (l.getCliente() == cliente) {
				sb.append("   ").append(l).append('\n');
				quantidade++;
				if (!l.estaAtiva()) {
					totalPago = totalPago.add(l.getValorFinal());
				}
			}
		}

		if (quantidade == 0) {
			sb.append("   Nenhuma locação registrada para este cliente.\n");
		}
		sb.append("Total de locações: ").append(quantidade)
				.append(" | Total pago: ").append(Formatador.moeda(totalPago)).append('\n');
		return sb.toString();
	}

}
