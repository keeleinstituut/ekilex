package eki.ekilex.web.interceptor;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.ekilex.data.ClassifierSelect;

@Component
public class TermViewUtil {

	private static final int LANGUAGES_WRAPUP_LIMIT = 4;

	public String composeLanguagesOrderWrapup(List<ClassifierSelect> languagesOrder) {
		List<String> languageValues = languagesOrder.stream().filter(lang -> lang.isSelected()).map(lang -> lang.getValue()).collect(Collectors.toList());
		String languagesOrderWrapup;
		if (languageValues.size() > LANGUAGES_WRAPUP_LIMIT) {
			languagesOrderWrapup = StringUtils.join(languageValues.toArray(new String[0]), ", ", 0, LANGUAGES_WRAPUP_LIMIT) + " ...";
		} else {
			languagesOrderWrapup = StringUtils.join(languageValues, ", ");
		}
		return languagesOrderWrapup;
	}

	public boolean isSelectedLang(String langCode, List<ClassifierSelect> languagesOrder) {
		boolean match = languagesOrder.stream().anyMatch(lang -> StringUtils.equals(langCode, lang.getCode()) && lang.isSelected());
		return match;
	}
}
