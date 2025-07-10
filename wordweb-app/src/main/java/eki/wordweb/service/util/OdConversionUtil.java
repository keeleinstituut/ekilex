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
import eki.wordweb.data.od.OdDefinition;
import eki.wordweb.data.od.OdLexemeMeaning;
import eki.wordweb.data.od.OdLexemeWord;
import eki.wordweb.data.od.OdMeaning;
import eki.wordweb.data.od.OdWord;
import eki.wordweb.data.od.OdWordRelationGroup;
import eki.wordweb.web.util.WebUtil;

@Component
public class OdConversionUtil implements GlobalConstant {

	@Autowired
	private WebUtil webUtil;

	public void applyGenericConversions(List<OdWord> words, Integer selectedHomonymNr) {

		if (CollectionUtils.isEmpty(words)) {
			return;
		}

		Integer homonymNr = 1;

		for (OdWord word : words) {

			String wordValue = word.getValue();
			List<OdLexemeMeaning> lexemeMeanings = word.getLexemeMeanings();

			for (OdLexemeMeaning lexemeMeaning : lexemeMeanings) {

				OdMeaning meaning = lexemeMeaning.getMeaning();
				OdDefinition definition = meaning.getDefinition();
				List<OdLexemeWord> lexemeWords = meaning.getLexemeWords();

				if (definition == null) {
					continue;
				}
				word.setDefinitionsWrapup(definition.getValuePrese());
				if (CollectionUtils.isNotEmpty(lexemeWords)) {
					String meaningWordsWrapup = lexemeWords.stream()
							.map(OdLexemeWord::getValuePrese)
							.collect(Collectors.joining(", "));
					word.setMeaningWordsWrapup(meaningWordsWrapup);
				}
			}

			String searchUri = webUtil.composeOdSearchUri(wordValue, homonymNr);
			boolean selected = homonymNr.equals(selectedHomonymNr);

			word.setSearchUri(searchUri);
			word.setHomonymNr(homonymNr);
			word.setSelected(selected);

			setWordTypeFlags(word);

			homonymNr++;
		}

		boolean isNotHomonymSelected = words.stream().noneMatch(OdWord::isSelected);
		if (isNotHomonymSelected) {
			OdWord firstWord = words.get(0);
			firstWord.setSelected(true);
		}
	}

	public void applyWordRelationConversions(OdWord word) {

		List<OdWordRelationGroup> wordRelationGroups = word.getWordRelationGroups();
		List<OdWordRelationGroup> primaryWordRelationGroups = null;
		List<OdWordRelationGroup> secondaryWordRelationGroups = null;
		final List<String> primaryWordRelTypeCodes = Arrays.asList(PRIMARY_OD_WORD_REL_TYPE_CODES);

		if (CollectionUtils.isEmpty(wordRelationGroups)) {
			return;
		}

		for (OdWordRelationGroup wordRelationGroup : wordRelationGroups) {

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

	public void setWordTypeFlags(OdWord word) {

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

	public Long getSelectedWordId(List<OdWord> words) {

		if (CollectionUtils.isEmpty(words)) {
			return null;
		}
		OdWord word = words.stream()
				.filter(OdWord::isSelected)
				.findFirst()
				.orElse(null);
		Long wordId;
		if (word == null) {
			OdWord firstWord = words.get(0);
			wordId = firstWord.getWordId();
		} else {
			wordId = word.getWordId();
		}
		return wordId;
	}

	public void setContentExistsFlags(OdWord selectedWord) {

		List<OdLexemeMeaning> lexemeMeanings = selectedWord.getLexemeMeanings();
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
