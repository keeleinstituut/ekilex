package eki.ekilex.web.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import eki.ekilex.service.CommonDataService;

@Component
public class ViewUtil implements InitializingBean {

	private static final String LEXEME_WEIGHT_PATTERN = "#.##";

	private static final int DEFINITION_MAX_CHARS = 100;

	private Map<String, String> languageIso2Map = null;

	private DecimalFormat lexemeWeightFormat;

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private MessageSource messageSource;

	@Override
	public void afterPropertiesSet() {
		lexemeWeightFormat = new DecimalFormat(LEXEME_WEIGHT_PATTERN);
	}

	public String getLangIso2(String langIso3) {
		if (StringUtils.isBlank(langIso3)) {
			return "-";
		}
		if (languageIso2Map == null) {
			languageIso2Map = commonDataService.getLanguageIso2Map();
		}
		String langIso2 = languageIso2Map.get(langIso3);
		if (StringUtils.isBlank(langIso2)) {
			return "?";
		}
		return langIso2;
	}

	public String getFormattedLexemeWeight(Float lexemeWeight) {
		return lexemeWeightFormat.format(lexemeWeight);
	}

	public String getDefinitionTooltipHtml(List<String> definitions) {

		StringBuilder htmlBuf = new StringBuilder();
		if (CollectionUtils.isEmpty(definitions)) {
			htmlBuf.append("definitsioon puudub");
		} else {
			boolean countDefinitions = definitions.size() > 1;
			int definitionsCount = 1;
			htmlBuf.append("<p style='text-align:left'>");
			for (String definition : definitions) {
				if (countDefinitions) {
					htmlBuf.append(definitionsCount++);
					htmlBuf.append(". ");
				}
				htmlBuf.append(StringUtils.abbreviate(definition, DEFINITION_MAX_CHARS));
				htmlBuf.append("<br>");
			}
			htmlBuf.append("</p>");
		}
		return htmlBuf.toString();
	}

	public String getLayerText(String layerName) {

		Locale locale = LocaleContextHolder.getLocale();
		String messageKey = "layername.";
		if (StringUtils.isEmpty(layerName)) {
			messageKey += "none";
		} else {
			messageKey += layerName;
		}
		return messageSource.getMessage(messageKey, new Object[0], locale);
	}
}
