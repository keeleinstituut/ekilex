package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.wordweb.data.os.OsDefinition;
import eki.wordweb.data.os.OsLexemeMeaning;
import eki.wordweb.data.os.OsLexemeWord;
import eki.wordweb.data.os.OsMeaning;
import eki.wordweb.data.os.OsWord;
import eki.wordweb.data.os.OsWordRelationGroup;
import eki.wordweb.web.util.WebUtil;

@Component
public class OsConversionUtil implements GlobalConstant {

	@Autowired
	private WebUtil webUtil;

	public void applyGenericConversions(List<OsWord> words, Integer selectedHomonymNr) {

		if (CollectionUtils.isEmpty(words)) {
			return;
		}

		for (OsWord word : words) {

			String wordValue = word.getValue();
			Integer homonymNr = word.getHomonymNr();
			List<OsLexemeMeaning> lexemeMeanings = word.getLexemeMeanings();

			for (OsLexemeMeaning lexemeMeaning : lexemeMeanings) {

				OsMeaning meaning = lexemeMeaning.getMeaning();
				OsDefinition definition = meaning.getDefinition();
				List<OsLexemeWord> lexemeWords = meaning.getLexemeWords();

				if (definition == null) {
					continue;
				}
				word.setDefinitionsWrapup(definition.getValuePrese());
				if (CollectionUtils.isNotEmpty(lexemeWords)) {
					String meaningWordsWrapup = lexemeWords.stream()
							.map(OsLexemeWord::getValuePrese)
							.collect(Collectors.joining(", "));
					word.setMeaningWordsWrapup(meaningWordsWrapup);
				}
			}

			String searchUri = webUtil.composeOsSearchUri(wordValue, homonymNr);
			boolean selected = homonymNr.equals(selectedHomonymNr);

			word.setSearchUri(searchUri);
			word.setHomonymNr(homonymNr);
			word.setSelected(selected);

			setWordTypeFlags(word);
		}

		boolean isNotHomonymSelected = words.stream().noneMatch(OsWord::isSelected);
		if (isNotHomonymSelected) {
			OsWord firstWord = words.get(0);
			firstWord.setSelected(true);
		}
	}

	public void applyWordRelationConversions(OsWord word) {

		List<OsWordRelationGroup> wordRelationGroups = word.getWordRelationGroups();
		List<OsWordRelationGroup> primaryWordRelationGroups = null;
		List<OsWordRelationGroup> secondaryWordRelationGroups = null;
		final List<String> primaryWordRelTypeCodes = Arrays.asList(PRIMARY_OS_WORD_REL_TYPE_CODES);

		if (CollectionUtils.isEmpty(wordRelationGroups)) {
			return;
		}

		for (OsWordRelationGroup wordRelationGroup : wordRelationGroups) {

			String wordRelTypeCode = wordRelationGroup.getWordRelTypeCode();
			if (primaryWordRelTypeCodes.contains(wordRelTypeCode)) {
				if (primaryWordRelationGroups == null) {
					primaryWordRelationGroups = new ArrayList<>();
				}
				primaryWordRelationGroups.add(wordRelationGroup);
			} else {
				if (secondaryWordRelationGroups == null) {
					secondaryWordRelationGroups = new ArrayList<>();
				}
				secondaryWordRelationGroups.add(wordRelationGroup);
			}
		}

		word.setPrimaryWordRelationGroups(primaryWordRelationGroups);
		word.setSecondaryWordRelationGroups(secondaryWordRelationGroups);
	}

	public void setWordTypeFlags(OsWord word) {

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
	}

	public Long getSelectedWordId(List<OsWord> words) {

		if (CollectionUtils.isEmpty(words)) {
			return null;
		}
		OsWord word = words.stream()
				.filter(OsWord::isSelected)
				.findFirst()
				.orElse(null);
		Long wordId;
		if (word == null) {
			OsWord firstWord = words.get(0);
			wordId = firstWord.getWordId();
		} else {
			wordId = word.getWordId();
		}
		return wordId;
	}

	public void setContentExistsFlags(OsWord selectedWord) {

		List<OsLexemeMeaning> lexemeMeanings = selectedWord.getLexemeMeanings();
		if (CollectionUtils.isEmpty(lexemeMeanings)) {
			return;
		}
		boolean lexemeMeaningsContentExist = lexemeMeanings.stream()
				.anyMatch(lexemeMeaning -> StringUtils.isNotBlank(lexemeMeaning.getValueStateCode())
						|| CollectionUtils.isNotEmpty(lexemeMeaning.getRegisterCodes())
						|| (lexemeMeaning.getMeaning().getDefinition() != null)
						|| CollectionUtils.isNotEmpty(lexemeMeaning.getMeaning().getLexemeWords()));
		selectedWord.setLexemeMeaningsContentExist(lexemeMeaningsContentExist);
	}
}
