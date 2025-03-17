package eki.wordweb.service.util;

import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LanguageContext {

	private static final String DEFAULT_UILANG_CODE = "et";

	private static final String[] SUPPORTED_UILANG_CODES = {"et", "en", "ru"};

	public Locale getDisplayLocale() {
		Locale locale = LocaleContextHolder.getLocale();
		String uiLangCode = locale.getLanguage();
		if (!ArrayUtils.contains(SUPPORTED_UILANG_CODES, uiLangCode)) {
			locale = new Locale(DEFAULT_UILANG_CODE);
			LocaleContextHolder.setLocale(locale);
		}
		return locale;
	}

	public String getDisplayLang() {
		return getDisplayLocale().getISO3Language();
	}
}
