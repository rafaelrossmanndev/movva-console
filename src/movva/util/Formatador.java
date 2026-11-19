package movva.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Classe utilitária: concentra a formatação de valores e datas exibidos no console.
public final class Formatador {

	private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
	public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private Formatador() {
		// não deve ser instanciada
	}

	public static String moeda(BigDecimal valor) {
		return String.format(PT_BR, "R$ %,.2f", valor);
	}

	public static String data(LocalDate data) {
		return data == null ? "-" : data.format(FORMATO_DATA);
	}

}
