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

	public void applyGenericConversions(List<OsWord> words, String searchValue, Integer selectedHomonymNr) {

		if (CollectionUtils.isEmpty(words)) {
			return;
		}

		boolean alreadySelected = false;

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
			boolean selected = StringUtils.equals(searchValue, wordValue)
					&& homonymNr.equals(selectedHomonymNr)
					&& !alreadySelected;
			if (selected) {
				alreadySelected = true;
			}

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
		List<OsWordRelationGroup> primary1WordRelationGroups = null;
		List<OsWordRelationGroup> primary2WordRelationGroups = null;
		List<OsWordRelationGroup> secondaryWordRelationGroups = null;
		final List<String> primary1WordRelTypeCodes = Arrays.asList(OS_PRIMARY1_WORD_REL_TYPE_CODES);
		final List<String> primary2WordRelTypeCodes = Arrays.asList(OS_PRIMARY2_WORD_REL_TYPE_CODES);

		if (CollectionUtils.isEmpty(wordRelationGroups)) {
			return;
		}

		for (OsWordRelationGroup wordRelationGroup : wordRelationGroups) {

			String wordRelTypeCode = wordRelationGroup.getWordRelTypeCode();
			if (primary1WordRelTypeCodes.contains(wordRelTypeCode)) {
				if (primary1WordRelationGroups == null) {
					primary1WordRelationGroups = new ArrayList<>();
				}
				primary1WordRelationGroups.add(wordRelationGroup);
			} else if (primary2WordRelTypeCodes.contains(wordRelTypeCode)) {
				if (primary2WordRelationGroups == null) {
					primary2WordRelationGroups = new ArrayList<>();
				}
				primary2WordRelationGroups.add(wordRelationGroup);
			} else {
				if (secondaryWordRelationGroups == null) {
					secondaryWordRelationGroups = new ArrayList<>();
				}
				secondaryWordRelationGroups.add(wordRelationGroup);
			}
		}

		word.setPrimary1WordRelationGroups(primary1WordRelationGroups);
		word.setPrimary2WordRelationGroups(primary2WordRelationGroups);
		word.setSecondaryWordRelationGroups(secondaryWordRelationGroups);
	}

	public void setWordTypeFlags(OsWord word) {

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
