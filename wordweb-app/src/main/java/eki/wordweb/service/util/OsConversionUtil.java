package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.wordweb.data.os.OsDefinition;
import eki.wordweb.data.os.OsLexemeMeaning;
import eki.wordweb.data.os.OsLexemeWord;
import eki.wordweb.data.os.OsMeaning;
import eki.wordweb.data.os.OsWord;
import eki.wordweb.data.os.OsWordRelationGroup;
import eki.wordweb.data.os.WordEkiRecommendation;
import eki.wordweb.web.util.WebUtil;

@Component
public class OsConversionUtil implements GlobalConstant {

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private WebUtil webUtil;

	public void applyAllConversions(List<OsWord> words) {

		if (CollectionUtils.isEmpty(words)) {
			return;
		}

		for (OsWord word : words) {

			classifierUtil.applyOsClassifiers(word, LANGUAGE_CODE_EST);
			setWordTypeFlags(word);
			applyWordRelationConversions(word);
			removeEkiRecommendationLinks(word);
			setContentExistsFlags(word);
			applySearchUri(word);
			applyWrapups(word);
		}
	}

	public void makeHomonymSearchSelection(List<OsWord> words, String searchValue, Integer selectedHomonymNr) {

		if (CollectionUtils.isEmpty(words)) {
			return;
		}

		OsWord selectedWord = words.stream()
				.filter(word -> StringUtils.equals(word.getValue(), searchValue)
						&& word.getHomonymNr().equals(selectedHomonymNr))
				.findFirst()
				.orElse(null);

		if (selectedWord == null) {
			selectedWord = words.get(0);
		}
		selectedWord.setSelected(true);
	}

	public void makeCompoundSearchSelection(List<OsWord> words) {

		if (CollectionUtils.isEmpty(words)) {
			return;
		}

		words.forEach(word -> word.setSelected(true));
	}

	private void applySearchUri(OsWord word) {

		AppData appData = appDataHolder.getAppData();
		String baseUrl = appData.getBaseUrl();
		String wordValue = word.getValue();
		Integer homonymNr = word.getHomonymNr();
		String searchUri = webUtil.composeOsSearchUri(wordValue, homonymNr);
		String searchUrl = baseUrl + searchUri;
		word.setSearchUri(searchUri);
		word.setSearchUrl(searchUrl);
	}

	private void applyWrapups(OsWord word) {

		List<OsLexemeMeaning> lexemeMeanings = word.getLexemeMeanings();
		String meaningWordsWrapup = null;
		String definitionsWrapup = null;

		if (CollectionUtils.isNotEmpty(lexemeMeanings)) {
			OsMeaning firstMeaning = lexemeMeanings.get(0).getMeaning();
			OsDefinition definition = firstMeaning.getDefinition();
			if (definition != null) {
				definitionsWrapup = definition.getValuePrese();
			}
			List<OsLexemeWord> lexemeWords = firstMeaning.getLexemeWords();
			if (CollectionUtils.isNotEmpty(lexemeWords)) {
				meaningWordsWrapup = lexemeWords.stream()
						.map(OsLexemeWord::getValuePrese)
						.collect(Collectors.joining(", "));
			}
		}

		word.setMeaningWordsWrapup(meaningWordsWrapup);
		word.setDefinitionsWrapup(definitionsWrapup);
	}

	private void applyWordRelationConversions(OsWord word) {

		List<OsWordRelationGroup> wordRelationGroups = word.getWordRelationGroups();
		List<OsWordRelationGroup> title1WordRelationGroups = null;
		List<OsWordRelationGroup> title2WordRelationGroups = null;
		List<OsWordRelationGroup> title3WordRelationGroups = null;
		List<OsWordRelationGroup> secondaryWordRelationGroups = null;
		final List<String> title1WordRelTypeCodes = Arrays.asList(OS_TITLE_1_WORD_REL_TYPE_CODES);
		final List<String> title2WordRelTypeCodes = Arrays.asList(OS_TITLE_2_WORD_REL_TYPE_CODES);
		final List<String> title3WordRelTypeCodes = Arrays.asList(OS_TITLE_3_WORD_REL_TYPE_CODES);

		if (CollectionUtils.isEmpty(wordRelationGroups)) {
			return;
		}

		for (OsWordRelationGroup wordRelationGroup : wordRelationGroups) {

			String wordRelTypeCode = wordRelationGroup.getWordRelTypeCode();
			if (title1WordRelTypeCodes.contains(wordRelTypeCode)) {
				if (title1WordRelationGroups == null) {
					title1WordRelationGroups = new ArrayList<>();
				}
				title1WordRelationGroups.add(wordRelationGroup);
			} else if (title2WordRelTypeCodes.contains(wordRelTypeCode)) {
				if (title2WordRelationGroups == null) {
					title2WordRelationGroups = new ArrayList<>();
				}
				title2WordRelationGroups.add(wordRelationGroup);
			} else if (title3WordRelTypeCodes.contains(wordRelTypeCode)) {
				if (title3WordRelationGroups == null) {
					title3WordRelationGroups = new ArrayList<>();
				}
				title3WordRelationGroups.add(wordRelationGroup);
			} else {
				if (secondaryWordRelationGroups == null) {
					secondaryWordRelationGroups = new ArrayList<>();
				}
				secondaryWordRelationGroups.add(wordRelationGroup);
			}
		}

		word.setTitle1WordRelationGroups(title1WordRelationGroups);
		word.setTitle2WordRelationGroups(title2WordRelationGroups);
		word.setTitle3WordRelationGroups(title3WordRelationGroups);
		word.setSecondaryWordRelationGroups(secondaryWordRelationGroups);
	}

	private void removeEkiRecommendationLinks(OsWord word) {

		if (word == null) {
			return;
		}

		List<WordEkiRecommendation> wordEkiRecommendations = word.getWordEkiRecommendations();
		if (CollectionUtils.isEmpty(wordEkiRecommendations)) {
			return;
		}

		final Pattern extLinkPattern = Pattern.compile("<ext-link[^>]*>|<\\/ext-link>");
		final String keepMatchContainingValue = "/eki.ee/teatmik/";

		for (WordEkiRecommendation ekiRecommendation : wordEkiRecommendations) {

			String ekiRecommendationValuePrese = ekiRecommendation.getValuePrese();
			String ekiRecommendationValuePreseClean = cleanByPattern(extLinkPattern, ekiRecommendationValuePrese, keepMatchContainingValue);
			ekiRecommendation.setValuePrese(ekiRecommendationValuePreseClean);
		}
	}

	private String cleanByPattern(Pattern pattern, String text, String keepMatchContainingValue) {

		StringBuffer textBuf = new StringBuffer();
		Matcher matcher = pattern.matcher(text);
		int textLength = text.length();
		int textStart = 0;
		int matchStart;
		int matchEnd;
		String cleanFragment;
		String matchFragment;

		while (matcher.find()) {

			matchStart = matcher.start();
			matchEnd = matcher.end();
			cleanFragment = StringUtils.substring(text, textStart, matchStart);
			matchFragment = matcher.group(matcher.groupCount());
			textBuf.append(cleanFragment);
			if (StringUtils.contains(matchFragment, keepMatchContainingValue)) {
				textBuf.append(matchFragment);
			}
			textStart = matchEnd;
		}
		if (textStart < textLength) {

			cleanFragment = StringUtils.substring(text, textStart, textLength);
			textBuf.append(cleanFragment);
		}
		return textBuf.toString();
	}

	private void setWordTypeFlags(OsWord word) {

		if (word == null) {
			return;
		}

		boolean isPrefixoid = false;
		boolean isSuffixoid = false;
		boolean isAbbreviationWord = false;
		boolean isForeignWord = false;
		List<String> wordTypeCodes = word.getWordTypeCodes();
		if (CollectionUtils.isNotEmpty(wordTypeCodes)) {
			isPrefixoid = wordTypeCodes.contains(WORD_TYPE_CODE_PREFIXOID);
			isSuffixoid = wordTypeCodes.contains(WORD_TYPE_CODE_SUFFIXOID);
			isForeignWord = CollectionUtils.containsAny(wordTypeCodes, Arrays.asList(WORD_TYPE_CODES_FOREIGN));
			isAbbreviationWord = CollectionUtils.containsAny(wordTypeCodes, Arrays.asList(WORD_TYPE_CODES_ABBREVIATION));
		}
		word.setPrefixoid(isPrefixoid);
		word.setSuffixoid(isSuffixoid);
		word.setAbbreviationWord(isAbbreviationWord);
		word.setForeignWord(isForeignWord);

		List<OsLexemeMeaning> lexemeMeanings = word.getLexemeMeanings();
		if (CollectionUtils.isNotEmpty(lexemeMeanings)) {
			for (OsLexemeMeaning lexemeMeaning : lexemeMeanings) {
				OsMeaning meaning = lexemeMeaning.getMeaning();
				List<OsLexemeWord> lexemeWords = meaning.getLexemeWords();
				if (CollectionUtils.isNotEmpty(lexemeWords)) {
					for (OsLexemeWord lexemeWord : lexemeWords) {
						setWordTypeFlags(lexemeWord);
					}
				}
			}
		}
	}

	private void setContentExistsFlags(OsWord selectedWord) {

		List<OsLexemeMeaning> lexemeMeanings = selectedWord.getLexemeMeanings();
		if (CollectionUtils.isEmpty(lexemeMeanings)) {
			return;
		}
		boolean lexemeMeaningsContentExist = lexemeMeanings.stream()
				.anyMatch(lexemeMeaning -> StringUtils.isNotBlank(lexemeMeaning.getValueStateCode())
						|| CollectionUtils.isNotEmpty(lexemeMeaning.getRegisters())
						|| (lexemeMeaning.getMeaning().getDefinition() != null)
						|| CollectionUtils.isNotEmpty(lexemeMeaning.getMeaning().getLexemeWords()));
		selectedWord.setLexemeMeaningsContentExist(lexemeMeaningsContentExist);
	}
}
