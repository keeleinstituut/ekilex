package eki.ekilex.web.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.TextDecoration;
import eki.ekilex.data.DecoratedWordType;
import eki.ekilex.service.CommonDataService;

@Component
public class ViewUtil implements InitializingBean {

	private static final String LEXEME_WEIGHT_PATTERN = "#.##";

	private static final int DEFINITION_MAX_CHARS = 100;

	private Map<String, String> languageIso2Map = null;

	private DecimalFormat lexemeWeightFormat;

	@Autowired
	private CommonDataService commonDataService;

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

	public String getMarkupHtml(DecoratedWordType word) {

		String wordPrese = new String(word.getWordValuePrese());
		if (word.isSuffixoid()) {
			wordPrese = "-" + wordPrese;
		} else if (word.isPrefixoid()) {
			wordPrese = wordPrese + "-";
		}
		StringBuilder htmlBuf = new StringBuilder();
		htmlBuf.append("<span>");
		String foreignMarkupCode = TextDecoration.FOREIGN.getCode();
		if (word.isForeign() && !StringUtils.contains(wordPrese, foreignMarkupCode)) {
			htmlBuf.append('<');
			htmlBuf.append(foreignMarkupCode);
			htmlBuf.append('>');
			htmlBuf.append(wordPrese);
			htmlBuf.append("</");
			htmlBuf.append(foreignMarkupCode);
			htmlBuf.append('>');
		} else {
			htmlBuf.append(wordPrese);
		}
		htmlBuf.append("</span>");
		return htmlBuf.toString();
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

}
