package eki.ekilex.sandbox;

import java.util.Locale;

// try out anything
public class TestBench {

	public static void main(String[] args) throws Exception {

		Locale locale = new Locale("ru");
		String lang = locale.getISO3Language();
		System.out.println(lang);
	}

}
