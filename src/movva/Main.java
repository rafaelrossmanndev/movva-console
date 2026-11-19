package movva;

import movva.modelo.Locadora;
import movva.persistencia.PersistenciaCSV;
import movva.persistencia.Persistivel;
import movva.ui.MenuConsole;

/**
 * Ponto de entrada: monta os objetos e "injeta" as dependências.
 * Para trocar CSV por outro formato, bastaria mudar a linha da persistência.
 */
public class Main {

	public static void main(String[] args) {
		Locadora locadora = new Locadora();
		Persistivel persistencia = new PersistenciaCSV("dados");

		MenuConsole menu = new MenuConsole(locadora, persistencia);
		menu.iniciar();
	}

}
