package eki.wordweb.service.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.common.data.OrderedMap;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.LangType;
import eki.wordweb.data.SourceLinkType;
import eki.wordweb.data.WordTypeData;
import eki.wordweb.data.type.TypeSourceLink;

public abstract class AbstractConversionUtil implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	protected ClassifierUtil classifierUtil;

	public void setWordTypeFlags(List<? extends WordTypeData> words) {

		for (WordTypeData word : words) {
			setWordTypeFlags(word);
		}
	}

	public void setWordTypeFlags(WordTypeData wordTypeData) {

		boolean isPrefixoid = false;
		boolean isSuffixoid = false;
		boolean isAbbreviationWord = false;
		boolean isForeignWord = false;
		boolean isIncorrectWordForm = false;
		List<String> wordTypeCodes = wordTypeData.getWordTypeCodes();
		if (CollectionUtils.isNotEmpty(wordTypeCodes)) {
			isPrefixoid = wordTypeCodes.contains(WORD_TYPE_CODE_PREFIXOID);
			isSuffixoid = wordTypeCodes.contains(WORD_TYPE_CODE_SUFFIXOID);
			isAbbreviationWord = CollectionUtils.containsAny(wordTypeCodes, Arrays.asList(WORD_TYPE_CODES_ABBREVIATION));
			isForeignWord = CollectionUtils.containsAny(wordTypeCodes, Arrays.asList(WORD_TYPE_CODES_FOREIGN));
			isIncorrectWordForm = wordTypeCodes.contains(WORD_TYPE_CODE_INCORRECT_WORD_FORM);
		}
		wordTypeData.setPrefixoid(isPrefixoid);
		wordTypeData.setSuffixoid(isSuffixoid);
		wordTypeData.setAbbreviationWord(isAbbreviationWord);
		wordTypeData.setForeignWord(isForeignWord);
		wordTypeData.setIncorrectWordForm(isIncorrectWordForm);
	}

	protected <T> OrderedMap<String, List<T>> composeOrderedMap(Map<String, List<T>> langKeyUnorderedMap, Map<String, Long> langOrderByMap) {
		return langKeyUnorderedMap.entrySet().stream()
				.sorted((entry1, entry2) -> {
					Long orderBy1 = langOrderByMap.get(entry1.getKey());
					Long orderBy2 = langOrderByMap.get(entry2.getKey());
					if (orderBy1 == null) {
						return 0;
					}
					if (orderBy2 == null) {
						return 0;
					}
					return orderBy1.compareTo(orderBy2);
				})
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, OrderedMap::new));
	}

	protected <T extends ComplexityType> List<T> filter(List<T> list, Complexity lexComplexity) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		if (lexComplexity == null) {
			return list;
		}
		return list.stream()
				.filter(elem -> isComplexityMatch(elem.getComplexity(), lexComplexity))
				.collect(Collectors.toList());
	}

	protected boolean isComplexityMatch(Complexity dataComplexity, Complexity lexComplexity) {
		if (dataComplexity == null) {
			return true;
		}
		if (Complexity.ANY.equals(dataComplexity)) {
			return true;
		}
		return dataComplexity.equals(lexComplexity);
	}

	protected <T extends LangType> List<T> filter(List<T> list, String wordLang, List<String> destinLangs) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		if (CollectionUtils.isEmpty(destinLangs)) {
			return list;
		}
		return list.stream()
				.filter(elem -> isLangFilterMatch(wordLang, elem.getLang(), destinLangs))
				.collect(Collectors.toList());
	}

	protected boolean isLangFilterMatch(String wordLang, String dataLang, List<String> destinLangs) {
		if (CollectionUtils.isEmpty(destinLangs)) {
			return true;
		}
		if (destinLangs.contains(DESTIN_LANG_ALL)) {
			return true;
		}
		// forcing estonian content
		if (StringUtils.equals(DESTIN_LANG_EST, dataLang)) {
			return true;
		}
		if (StringUtils.equals(lANGUAGE_CODE_MUL, dataLang)) {
			return true;
		}
		if (StringUtils.equals(wordLang, dataLang)) {
			return true;
		}
		boolean isDataLangSupportedFilterLang = ArrayUtils.contains(SUPPORTED_DESTIN_LANGS, dataLang);
		if (isDataLangSupportedFilterLang && destinLangs.contains(dataLang)) {
			return true;
		} else if (!isDataLangSupportedFilterLang && destinLangs.contains(DESTIN_LANG_OTHER)) {
			return true;
		}
		return false;
	}

	protected void convertContainingSourceLinkUrlsToHrefs(List<? extends SourceLinkType> sourceLinkTypes) {
		if (CollectionUtils.isEmpty(sourceLinkTypes)) {
			return;
		}
		for (SourceLinkType sourceLinkType : sourceLinkTypes) {
			List<TypeSourceLink> sourceLinks = sourceLinkType.getSourceLinks();
			convertUrlsToHrefs(sourceLinks);
		}
	}

	protected void convertUrlsToHrefs(List<TypeSourceLink> sourceLinks) {

		if (CollectionUtils.isEmpty(sourceLinks)) {
			return;
		}
		final String[] urlPrefixes = new String[] {"http://", "https://"};
		for (TypeSourceLink sourceLink : sourceLinks) {
			String valuePrese = sourceLink.getSourceValuePrese();
			if (StringUtils.contains(valuePrese, "<a href=")) {
				continue;
			}
			if (StringUtils.containsAny(valuePrese, urlPrefixes)) {
				StringBuffer convertedValuePreseBuf = new StringBuffer();
				String processingSubstr = new String(valuePrese);
				int urlStartIndex;
				int urlEndIndex = 0;
				while ((urlStartIndex = StringUtils.indexOfAny(processingSubstr, urlPrefixes)) != -1) {
					String preLinkSubstr = StringUtils.substring(processingSubstr, 0, urlStartIndex);
					processingSubstr = StringUtils.substring(processingSubstr, urlStartIndex);
					urlEndIndex = StringUtils.indexOfAny(processingSubstr, ' ', ']');
					String url;
					if (urlEndIndex != -1) {
						url = StringUtils.substring(processingSubstr, 0, urlEndIndex);
					} else {
						url = new String(processingSubstr);
					}
					processingSubstr = StringUtils.substring(processingSubstr, urlEndIndex);
					convertedValuePreseBuf.append(preLinkSubstr);
					convertedValuePreseBuf.append("<a href=\"");
					convertedValuePreseBuf.append(url);
					convertedValuePreseBuf.append("\" target=\"_blank\">");
					convertedValuePreseBuf.append(url);
					convertedValuePreseBuf.append("</a>");
				}
				if (urlEndIndex != -1) {
					convertedValuePreseBuf.append(processingSubstr);
				}
				String convertedValuePrese = convertedValuePreseBuf.toString();
				sourceLink.setSourceValuePrese(convertedValuePrese);
			}
		}
	}
}
