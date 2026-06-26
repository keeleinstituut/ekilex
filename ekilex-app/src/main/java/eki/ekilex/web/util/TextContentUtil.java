package eki.ekilex.web.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import eki.ekilex.service.TextContentService;

@Component
public class TextContentUtil {

	@Autowired
	private TextContentService textContentService;

	public String getText(String textName) {

		Locale locale = LocaleContextHolder.getLocale();
		String lang = locale.getISO3Language();

		return textContentService.getText(textName, lang);
	}
}
