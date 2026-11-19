package movva.relatorio;

import movva.modelo.Locadora;

/**
 * Padrão de projeto Strategy (comportamental).
 *
 * Cada implementação encapsula um "algoritmo" diferente de relatório.
 * Quem usa (o MenuConsole) conhece apenas esta interface e chama gerar(),
 * sem saber qual relatório concreto está executando.
 */
public interface Relatorio {

	String gerar(Locadora locadora);

}
