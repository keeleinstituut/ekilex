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
			List<OsWord> meaningWords = word.getMeaningWords();
			List<OsLexemeMeaning> lexemeMeanings = word.getLexemeMeanings();
			String meaningWordsWrapup = null;
			String definitionsWrapup = null;

			if (CollectionUtils.isNotEmpty(meaningWords)) {
				meaningWordsWrapup = meaningWords.stream()
						.map(OsWord::getValuePrese)
						.collect(Collectors.joining(", "));
			}
			if (CollectionUtils.isNotEmpty(lexemeMeanings)) {
				definitionsWrapup = lexemeMeanings.stream()
						.map(OsLexemeMeaning::getMeaning)
						.filter(meaning -> meaning.getDefinition() != null)
						.map(OsMeaning::getDefinition)
						.map(OsDefinition::getValuePrese)
						.collect(Collectors.joining(", "));
			}

			String searchUri = webUtil.composeOsSearchUri(wordValue, homonymNr);
			boolean selected = StringUtils.equals(searchValue, wordValue)
					&& homonymNr.equals(selectedHomonymNr)
					&& !alreadySelected;
			if (selected) {
				alreadySelected = true;
			}

			word.setMeaningWordsWrapup(meaningWordsWrapup);
			word.setDefinitionsWrapup(definitionsWrapup);
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
