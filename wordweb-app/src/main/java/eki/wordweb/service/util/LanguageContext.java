package eki.wordweb.service.util;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LanguageContext {

	public Locale getDisplayLocale() {
		Locale locale = LocaleContextHolder.getLocale();
		return locale;
	}

	public String getDisplayLang() {
		return getDisplayLocale().getISO3Language();
	}
}
