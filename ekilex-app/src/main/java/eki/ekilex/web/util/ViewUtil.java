package eki.ekilex.web.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.TextDecoration;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.DecoratedWordType;
import eki.ekilex.service.CommonDataService;

@Component
public class ViewUtil implements InitializingBean {

	private static final String WEIGHT_PATTERN = "#.##";

	private Map<String, String> languageIso2Map = null;

	private DecimalFormat weightFormat;

	@Autowired
	private CommonDataService commonDataService;

	@Override
	public void afterPropertiesSet() {
		weightFormat = new DecimalFormat(WEIGHT_PATTERN);
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

	public String getWordValueMarkup(DecoratedWordType word) {

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

	public String getFormattedWeight(Float weight) {
		return weightFormat.format(weight);
	}

	public String composeFunction(String functionName, Object... args) {
		String functionSignature = functionName + "(" + StringUtils.join(args, ',') + ")";
		return functionSignature;
	}

	public String getLexRegisterTooltipHtml(List<String> lexRegisterCodes) {

		StringBuilder htmlBuf = new StringBuilder();
		if (CollectionUtils.isEmpty(lexRegisterCodes) || StringUtils.isEmpty(lexRegisterCodes.get(0))) {
			htmlBuf.append("register puudub");
		} else {
			boolean multipleRegisters = lexRegisterCodes.size() > 1;
			if (multipleRegisters) {
				int definitionsCount = 1;
				htmlBuf.append("<p style='text-align:left'>");
				for (String lexRegisterCode : lexRegisterCodes) {
					htmlBuf.append(definitionsCount++);
					htmlBuf.append(". ");
					htmlBuf.append(lexRegisterCode);
					htmlBuf.append("<br>");
				}
				htmlBuf.append("</p>");
			} else {
				htmlBuf.append(lexRegisterCodes.get(0));
			}
		}
		return htmlBuf.toString();
	}

	public String getClassifierValue(String code, List<Classifier> classifiers) {
		Optional<Classifier> classifier = classifiers.stream().filter(c -> c.getCode().equals(code)).findFirst();
		return classifier.isPresent() ? classifier.get().getValue() : code;
	}

	public boolean enumEquals(Enum<?> enum1, Enum<?> enum2) {
		if (enum1 == null) {
			return false;
		}
		if (enum2 == null) {
			return false;
		}
		return StringUtils.equals(enum1.name(), enum2.name());
	}
}
