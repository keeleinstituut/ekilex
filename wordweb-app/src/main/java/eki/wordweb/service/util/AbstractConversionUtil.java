package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.ComplexityType;
import eki.wordweb.data.LangType;
import eki.wordweb.data.SourceLinkType;
import eki.wordweb.data.TypeMeaningWord;
import eki.wordweb.data.TypeSourceLink;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordTypeData;

public abstract class AbstractConversionUtil implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	protected ClassifierUtil classifierUtil;

	public void removeTempPlaceholder(List<TypeMeaningWord> meaningWords) {
		if (CollectionUtils.isEmpty(meaningWords)) {
			return;
		}
		meaningWords.forEach(meaningWord -> {
			String word = StringUtils.remove(meaningWord.getWord(), TEMP_CONVERSION_PLACEHOLDER);
			String wordPrese = StringUtils.remove(meaningWord.getWordPrese(), TEMP_CONVERSION_PLACEHOLDER);
			meaningWord.setWord(word);
			meaningWord.setWordPrese(wordPrese);
		});
	}

	public void setWordTypeFlags(WordTypeData wordTypeData) {

		boolean isPrefixoid = false;
		boolean isSuffixoid = false;
		boolean isAbbreviationWord = false;
		boolean isForeignWord = false;
		List<String> wordTypeCodes = wordTypeData.getWordTypeCodes();
		if (CollectionUtils.isNotEmpty(wordTypeCodes)) {
			isPrefixoid = wordTypeCodes.contains(WORD_TYPE_CODE_PREFIXOID);
			isSuffixoid = wordTypeCodes.contains(WORD_TYPE_CODE_SUFFIXOID);
			isAbbreviationWord = CollectionUtils.containsAny(wordTypeCodes, Arrays.asList(WORD_TYPE_CODES_ABBREVIATION));
			isForeignWord = CollectionUtils.containsAny(wordTypeCodes, Arrays.asList(WORD_TYPE_CODES_FOREIGN));
		}
		wordTypeData.setPrefixoid(isPrefixoid);
		wordTypeData.setSuffixoid(isSuffixoid);
		wordTypeData.setAbbreviationWord(isAbbreviationWord);
		wordTypeData.setForeignWord(isForeignWord);

		if (wordTypeData instanceof Word) {
			Word word = (Word) wordTypeData;
			List<TypeMeaningWord> meaningWords = word.getMeaningWords();
			if (CollectionUtils.isNotEmpty(meaningWords)) {
				for (TypeMeaningWord meaningWord : meaningWords) {
					setWordTypeFlags(meaningWord);
				}
			}
		}
	}

	protected <T extends ComplexityType> List<T> filterSimpleOnly(List<T> list, Complexity lexComplexity) {
		if ((lexComplexity != null) && Complexity.SIMPLE.equals(lexComplexity)) {
			return filter(list, lexComplexity);
		}
		return list;
	}

	protected <T extends ComplexityType> List<T> filterPreferred(List<T> list, Complexity lexComplexity) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		} else if (Complexity.DETAIL.equals(lexComplexity)) {
			List<Complexity> preferredComplexityHierarchy = Arrays.asList(PREFERRED_COMPLEXITY_HIERARCHY);
			List<Complexity> providedComplexities = list.stream()
					.map(ComplexityType::getComplexity)
					.filter(complexity -> complexity != null)
					.distinct()
					.sorted((complexity1, complexity2) -> preferredComplexityHierarchy.indexOf(complexity1) - preferredComplexityHierarchy.indexOf(complexity2))
					.collect(Collectors.toList());
			Complexity suggestedLexComplexity = providedComplexities.get(0);
			return filter(list, suggestedLexComplexity);
		}
		return filter(list, lexComplexity);
	}

	protected <T extends ComplexityType> List<T> filter(List<T> list, Complexity lexComplexity) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		if (lexComplexity == null) {
			return list;
		}
		return list.stream().filter(elem -> isComplexityMatch(elem.getComplexity(), lexComplexity)).collect(Collectors.toList());
	}

	protected boolean isComplexityMatch(Complexity dataComplexity, Complexity lexComplexity) {
		if (dataComplexity == null) {
			return true;
		}
		if (Complexity.DEFAULT.equals(dataComplexity)) {
			return true;
		}
		return StringUtils.startsWith(dataComplexity.name(), lexComplexity.name());
	}

	protected <T extends LangType> List<T> filter(List<T> list, String wordLang, List<String> destinLangs) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		if (CollectionUtils.isEmpty(destinLangs)) {
			return list;
		}
		return list.stream().filter(elem -> isLangFilterMatch(wordLang, elem.getLang(), destinLangs)).collect(Collectors.toList());
	}

	protected boolean isLangFilterMatch(String wordLang, String dataLang, List<String> destinLangs) {
		if (CollectionUtils.isEmpty(destinLangs)) {
			return true;
		}
		if (destinLangs.contains(DESTIN_LANG_ALL)) {
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

	protected <T extends SourceLinkType> void applySourceLinks(List<T> list, List<TypeSourceLink> allSourceLinks) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		if (CollectionUtils.isEmpty(allSourceLinks)) {
			return;
		}
		Map<Long, List<TypeSourceLink>> sourceLinkMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allSourceLinks)) {
			sourceLinkMap = allSourceLinks.stream().collect(Collectors.groupingBy(TypeSourceLink::getOwnerId));
		}
		for (T entity : list) {
			Long ownerId = entity.getOwnerId();
			List<TypeSourceLink> sourceLinks = sourceLinkMap.get(ownerId);
			convertUrlsToHrefs(sourceLinks);
			entity.setSourceLinks(sourceLinks);
		}
	}

	protected void convertUrlsToHrefs(List<TypeSourceLink> sourceLinks) {

		if (CollectionUtils.isEmpty(sourceLinks)) {
			return;
		}
		final String[] urlPrefixes = new String[] {"http://", "https://"};
		for (TypeSourceLink sourceLink : sourceLinks) {
			List<String> originalSourceProps = sourceLink.getSourceProps();
			List<String> convertedSourceProps = new ArrayList<>();
			if (CollectionUtils.isEmpty(originalSourceProps)) {
				continue;
			}
			for (String sourceProp : originalSourceProps) {
				if (StringUtils.containsAny(sourceProp, urlPrefixes)) {
					StringBuffer convertedSourcePropBuf = new StringBuffer();
					String processingSubstr = new String(sourceProp);
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
						convertedSourcePropBuf.append(preLinkSubstr);
						convertedSourcePropBuf.append("<a ");
						convertedSourcePropBuf.append("href=\"");
						convertedSourcePropBuf.append(url);
						convertedSourcePropBuf.append("\" target=\"_blank\">");
						convertedSourcePropBuf.append(url);
						convertedSourcePropBuf.append("</a>");
					}
					if (urlEndIndex != -1) {
						convertedSourcePropBuf.append(processingSubstr);
					}
					sourceProp = convertedSourcePropBuf.toString();
				}
				convertedSourceProps.add(sourceProp);
			}
			sourceLink.setSourceProps(convertedSourceProps);
		}
	}
}
