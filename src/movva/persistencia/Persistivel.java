package movva.persistencia;

import java.io.IOException;

import movva.modelo.Locadora;

/**
 * Contrato de persistência. O MenuConsole depende apenas desta interface;
 * a implementação concreta (CSV, hoje) é injetada pelo Main (Injeção de Dependência).
 *
 * IOException é uma exceção checked: quem chama é obrigado a tratá-la.
 */
public interface Persistivel {

	void salvar(Locadora locadora) throws IOException;

	void carregar(Locadora locadora) throws IOException;

}
