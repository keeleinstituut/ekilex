package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.RelationStatus;
import eki.common.data.Classifier;
import eki.wordweb.data.Definition;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.MeaningWord;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordGroup;
import eki.wordweb.data.WordRelation;
import eki.wordweb.data.WordRelationGroup;
import eki.wordweb.data.WordRelationsTuple;

@Component
public class WordConversionUtil extends AbstractConversionUtil {

	public void composeHomonymWrapups(List<Word> words, SearchContext searchContext) {

		for (Word word : words) {

			String wordLang = word.getLang();
			List<Definition> definitions = word.getDefinitions();
			List<MeaningWord> meaningWords = word.getMeaningWords();
			Long definitionMeaningId = null;

			if (CollectionUtils.isNotEmpty(definitions)) {
				List<Definition> primaryDefinitions = definitions.stream()
						.filter(definition -> isPublishingTargetMatch(definition, searchContext))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(primaryDefinitions)) {
					Definition firstDefinition = primaryDefinitions.get(0);
					if (StringUtils.isNotBlank(firstDefinition.getValue())) {
						Long lexemeId = firstDefinition.getLexemeId();
						definitionMeaningId = firstDefinition.getMeaningId();
						List<String> definitionValues = primaryDefinitions.stream()
								.filter(definition -> definition.getLexemeId().equals(lexemeId))
								.map(Definition::getValue)
								.collect(Collectors.toList());
						String definitionsWrapup = StringUtils.join(definitionValues, ", ");
						word.setDefinitionsWrapup(definitionsWrapup);
					}
				}
			}

			if (definitionMeaningId != null && CollectionUtils.isNotEmpty(meaningWords)) {
				List<MeaningWord> primaryMeaningWords = meaningWords.stream()
						.filter(meaningWord -> isPublishingTargetMatch(meaningWord, searchContext))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(primaryMeaningWords)) {
					Long meaningWordMeaningId = definitionMeaningId;
					List<String> meaningWordValues = primaryMeaningWords.stream()
							.filter(meaningWord -> meaningWord.getMeaningId().equals(meaningWordMeaningId))
							.filter(meaningWord -> StringUtils.equals(wordLang, meaningWord.getLang()))
							.map(meaningWord -> {
								if (meaningWord.isPrefixoid()) {
									return meaningWord.getValue() + "-";
								} else if (meaningWord.isSuffixoid()) {
									return "-" + meaningWord.getValue();
								} else {
									return meaningWord.getValue();
								}
							})
							.distinct()
							.collect(Collectors.toList());
					String meaningWordsWrapup = StringUtils.join(meaningWordValues, ", ");
					word.setMeaningWordsWrapup(meaningWordsWrapup);
				}
			}
		}
	}

	public void selectHomonymWithLang(List<Word> words, Integer homonymNr, String lang) {

		if (CollectionUtils.isEmpty(words)) {
			return;
		}
		Word firstWord = words.get(0);
		if (homonymNr == null) {
			firstWord.setSelected(true);
		} else if (StringUtils.isBlank(lang)) {
			firstWord.setSelected(true);
		} else {
			boolean isSelected = false;
			for (Word word : words) {

				Integer wordHomonymNr = word.getHomonymNr();
				String wordLang = word.getLang();
				isSelected = homonymNr.equals(wordHomonymNr) && StringUtils.equals(lang, wordLang);
				word.setSelected(isSelected);

				if (isSelected) {
					break;
				}
			}
			if (!isSelected) {
				firstWord.setSelected(true);
			}
		}
	}

	public void composeWordRelations(
			Word word,
			WordRelationsTuple wordRelationsTuple,
			Map<String, Long> langOrderByMap,
			SearchContext searchContext,
			Locale displayLocale,
			String displayLang) {

		if (wordRelationsTuple == null) {
			return;
		}

		String wordLang = word.getLang();
		List<String> destinLangs = searchContext.getDestinLangs();

		word.setWordGroups(new ArrayList<>());
		word.setRelatedWords(new ArrayList<>());
		word.setPrimaryRelatedWordTypeGroups(new ArrayList<>());
		word.setSecondaryRelatedWordTypeGroups(new ArrayList<>());

		List<Classifier> wordRelTypes = classifierUtil.getClassifiers(ClassifierName.WORD_REL_TYPE, displayLang);
		List<Classifier> aspects = classifierUtil.getClassifiers(ClassifierName.ASPECT, displayLang);
		List<RelationStatus> relationStatusOrder = Arrays.asList(RelationStatus.PROCESSED, RelationStatus.UNDEFINED, RelationStatus.DELETED);

		List<WordRelation> wordRelations = wordRelationsTuple.getRelatedWords();
		Map<String, List<WordRelation>> wordRelationsMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(wordRelations)) {
			wordRelations = filter(wordRelations, wordLang, destinLangs);
			wordRelations = wordRelations.stream()
					.filter(relation -> !RelationStatus.DELETED.equals(relation.getRelationStatus()))
					.filter(relation -> isPublishingTargetMatch(relation, searchContext))
					.sorted((relation1, relation2) -> {
						if (relation1.getRelationStatus().equals(relation2.getRelationStatus())) {
							return (int) (relation1.getOrderBy() - relation2.getOrderBy());
						}
						int relationStatusOrder1 = relationStatusOrder.indexOf(relation1.getRelationStatus());
						int relationStatusOrder2 = relationStatusOrder.indexOf(relation2.getRelationStatus());
						return relationStatusOrder1 - relationStatusOrder2;
					})
					.collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(wordRelations)) {
				String alternativeWord = null;
				for (WordRelation wordRelation : wordRelations) {
					classifierUtil.applyClassifiers(wordRelation, displayLang);
					setWordTypeFlags(wordRelation);
					if (StringUtils.equals(wordRelation.getWordRelTypeCode(), WORD_REL_TYPE_CODE_DERIVATIVE_BASE)) {
						alternativeWord = wordRelation.getValue();
					}
				}
				word.setAlternativeWord(alternativeWord);
				word.getRelatedWords().addAll(wordRelations);
				wordRelationsMap = wordRelations.stream().collect(Collectors.groupingBy(WordRelation::getWordRelTypeCode));
			}
		}

		for (Classifier wordRelType : wordRelTypes) {
			String wordRelTypeCode = wordRelType.getCode();
			List<WordRelation> relatedWordsOfType = wordRelationsMap.get(wordRelTypeCode);
			List<WordRelationGroup> wordRelationGroups;
			if (ArrayUtils.contains(PRIMARY_WORD_REL_TYPE_CODES, wordRelTypeCode)) {
				wordRelationGroups = word.getPrimaryRelatedWordTypeGroups();
				handleWordRelType(word, wordRelType, relatedWordsOfType, wordRelationGroups, langOrderByMap, displayLocale);
			} else if (CollectionUtils.isNotEmpty(relatedWordsOfType)) {
				wordRelationGroups = word.getSecondaryRelatedWordTypeGroups();
				handleWordRelType(word, wordRelType, relatedWordsOfType, wordRelationGroups, langOrderByMap, displayLocale);
			}
		}

		List<WordRelation> allWordGroupMembers = wordRelationsTuple.getWordGroupMembers();
		if (CollectionUtils.isNotEmpty(allWordGroupMembers)) {
			allWordGroupMembers = filter(allWordGroupMembers, wordLang, destinLangs);
			List<String> aspectCodeOrder = aspects.stream().map(Classifier::getCode).collect(Collectors.toList());
			Map<Long, List<WordRelation>> wordGroupMap = allWordGroupMembers.stream().collect(Collectors.groupingBy(WordRelation::getWordGroupId));
			List<Long> wordGroupIds = new ArrayList<>(wordGroupMap.keySet());
			Collections.sort(wordGroupIds);
			for (Long wordGroupId : wordGroupIds) {
				List<WordRelation> wordGroupMembers = wordGroupMap.get(wordGroupId);
				wordGroupMembers = wordGroupMembers.stream()
						.filter(member -> isPublishingTargetMatch(member, searchContext))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(wordGroupMembers)) {
					for (WordRelation wordGroupMember : wordGroupMembers) {
						classifierUtil.applyClassifiers(wordGroupMember, displayLang);
						setWordTypeFlags(wordGroupMember);
					}
					WordRelation firstWordGroupMember = wordGroupMembers.get(0);
					String groupWordRelTypeCode = firstWordGroupMember.getWordRelTypeCode();
					Classifier groupWordRelType = firstWordGroupMember.getWordRelType();
					if (StringUtils.equals(WORD_REL_TYPE_CODE_ASCPECTS, groupWordRelTypeCode)) {
						groupWordRelType = classifierUtil.reValue(groupWordRelType, "classifier.word_rel_type.aspect", displayLocale);
						wordGroupMembers.sort((WordRelation rel1, WordRelation rel2) -> {
							String aspectCode1 = rel1.getAspectCode();
							String aspectCode2 = rel2.getAspectCode();
							if (StringUtils.isBlank(aspectCode1) || StringUtils.isBlank(aspectCode2)) {
								return 0;
							}
							int aspectOrder1 = aspectCodeOrder.indexOf(aspectCode1);
							int aspectOrder2 = aspectCodeOrder.indexOf(aspectCode2);
							return aspectOrder1 - aspectOrder2;
						});
					}
					WordGroup wordGroup = new WordGroup();
					wordGroup.setWordGroupId(wordGroupId);
					wordGroup.setWordRelTypeCode(groupWordRelTypeCode);
					wordGroup.setWordRelType(groupWordRelType);
					wordGroup.setWordGroupMembers(wordGroupMembers);
					word.getWordGroups().add(wordGroup);
				}
			}
		}

		boolean wordRelationsExist = CollectionUtils.isNotEmpty(word.getSecondaryRelatedWordTypeGroups()) || CollectionUtils.isNotEmpty(word.getWordGroups());
		word.setWordRelationsExist(wordRelationsExist);
	}

	private void handleWordRelType(
			Word word,
			Classifier wordRelType,
			List<WordRelation> wordRelations,
			List<WordRelationGroup> wordRelationGroups,
			Map<String, Long> langOrderByMap,
			Locale displayLocale) {

		WordRelationGroup wordRelationGroup;
		if (StringUtils.equals(WORD_REL_TYPE_CODE_RAW, wordRelType.getCode())) {
			List<WordRelation> wordRelationSyns = null;
			List<WordRelation> wordRelationMatches = null;
			if (CollectionUtils.isNotEmpty(wordRelations)) {
				Map<Boolean, List<WordRelation>> wordRelationSynOrMatchMap = wordRelations.stream()
						.collect(Collectors.groupingBy(wordRelation -> StringUtils.equals(word.getLang(), wordRelation.getLang())));
				wordRelationSyns = wordRelationSynOrMatchMap.get(Boolean.TRUE);
				wordRelationMatches = wordRelationSynOrMatchMap.get(Boolean.FALSE);
			}
			// raw rel syn group
			Classifier wordRelTypeSyn = classifierUtil.reValue(wordRelType, "classifier.word_rel_type.raw.syn", displayLocale);
			wordRelationGroup = new WordRelationGroup();
			wordRelationGroup.setWordRelType(wordRelTypeSyn);
			appendRelatedWordTypeGroup(wordRelationGroup, wordRelationGroups, wordRelationSyns, null);

			// raw rel match group w lang grouping
			Classifier wordRelTypeMatch = classifierUtil.reValue(wordRelType, "classifier.word_rel_type.raw.match", displayLocale);
			wordRelationGroup = new WordRelationGroup();
			wordRelationGroup.setWordRelType(wordRelTypeMatch);
			wordRelationGroup.setCollapsible(true);
			appendRelatedWordTypeGroup(wordRelationGroup, wordRelationGroups, wordRelationMatches, langOrderByMap);
		} else {
			wordRelationGroup = new WordRelationGroup();
			wordRelationGroup.setWordRelType(wordRelType);
			appendRelatedWordTypeGroup(wordRelationGroup, wordRelationGroups, wordRelations, null);
		}
	}

	private void appendRelatedWordTypeGroup(
			WordRelationGroup wordRelationGroup,
			List<WordRelationGroup> wordRelationGroups,
			List<WordRelation> relatedWordsOfType,
			Map<String, Long> langOrderByMap) {

		if (CollectionUtils.isEmpty(relatedWordsOfType)) {
			wordRelationGroup.setEmpty(true);
		} else if (MapUtils.isEmpty(langOrderByMap)) {
			wordRelationGroup.setRelatedWords(relatedWordsOfType);
			wordRelationGroup.setAsList(true);
		} else {
			Map<String, List<WordRelation>> relatedWordsByLangUnordered = relatedWordsOfType.stream().collect(Collectors.groupingBy(WordRelation::getLang));
			Map<String, List<WordRelation>> relatedWordsByLangOrdered = composeOrderedMap(relatedWordsByLangUnordered, langOrderByMap);
			wordRelationGroup.setRelatedWordsByLang(relatedWordsByLangOrdered);
			wordRelationGroup.setAsMap(true);
		}
		wordRelationGroups.add(wordRelationGroup);
	}

	public List<String> collectAllRelatedWords(Word word) {
		List<WordRelation> relatedWords = word.getRelatedWords();
		List<String> allRelatedWordValues = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(relatedWords)) {
			List<String> relatedWordValues = relatedWords.stream().map(WordRelation::getValue).distinct().collect(Collectors.toList());
			allRelatedWordValues.addAll(relatedWordValues);
		}
		List<WordGroup> wordGroups = word.getWordGroups();
		if (CollectionUtils.isNotEmpty(wordGroups)) {
			for (WordGroup wordGroup : wordGroups) {
				List<String> relatedWordValues = wordGroup.getWordGroupMembers().stream().map(WordRelation::getValue).distinct().collect(Collectors.toList());
				allRelatedWordValues.addAll(relatedWordValues);
			}
		}
		return allRelatedWordValues;
	}

	public void composeCommon(Word word, List<LexemeWord> lexLexemes, List<LexemeWord> termLexemes) {

		List<Classifier> summarisedPoses = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(lexLexemes)) {
			List<Classifier> lexSummarisedPoses = lexLexemes.stream()
					.filter(lexeme -> CollectionUtils.isNotEmpty(lexeme.getPoses()))
					.map(LexemeWord::getPoses)
					.flatMap(List::stream)
					.collect(Collectors.toList());
			summarisedPoses.addAll(lexSummarisedPoses);
		}

		if (CollectionUtils.isNotEmpty(termLexemes)) {
			List<Classifier> termSummarisedPoses = termLexemes.stream()
					.filter(lexeme -> CollectionUtils.isNotEmpty(lexeme.getPoses()))
					.map(LexemeWord::getPoses)
					.flatMap(List::stream)
					.collect(Collectors.toList());
			summarisedPoses.addAll(termSummarisedPoses);
		}

		summarisedPoses = summarisedPoses.stream().distinct().collect(Collectors.toList());
		List<String> summarisedPosCodes = summarisedPoses.stream().map(Classifier::getCode).collect(Collectors.toList());

		boolean isShowLexPoses = false;
		boolean isShowTermPoses = false;

		if (CollectionUtils.isNotEmpty(lexLexemes)) {

			if (CollectionUtils.isNotEmpty(summarisedPosCodes)) {
				isShowLexPoses = lexLexemes.stream()
						.anyMatch(lexeme -> (lexeme.getPosCodes() == null) || !CollectionUtils.isEqualCollection(lexeme.getPosCodes(), summarisedPosCodes));
			}

			for (LexemeWord lexemeWord : lexLexemes) {

				boolean isShowPoses = isShowLexPoses && (CollectionUtils.isNotEmpty(lexemeWord.getPoses()));
				boolean isShowSection1 = isShowPoses
						|| CollectionUtils.isNotEmpty(lexemeWord.getGrammars())
						|| (lexemeWord.getValueState() != null);
				boolean isShowSection2 = CollectionUtils.isNotEmpty(lexemeWord.getRelatedLexemes())
						|| CollectionUtils.isNotEmpty(lexemeWord.getRelatedMeanings())
						|| CollectionUtils.isNotEmpty(lexemeWord.getLearnerComments())
						|| CollectionUtils.isNotEmpty(lexemeWord.getLexemeNotes())
						|| CollectionUtils.isNotEmpty(lexemeWord.getMeaningNotes())
						|| CollectionUtils.isNotEmpty(lexemeWord.getLexemeSourceLinks());
				boolean isShowSection3 = CollectionUtils.isNotEmpty(lexemeWord.getGovernments())
						|| CollectionUtils.isNotEmpty(lexemeWord.getUsages())
						|| CollectionUtils.isNotEmpty(lexemeWord.getMeaningImages());
				lexemeWord.setShowSection1(isShowSection1);
				lexemeWord.setShowSection2(isShowSection2);
				lexemeWord.setShowSection3(isShowSection3);
				lexemeWord.setShowPoses(isShowPoses);
			}
		}

		if (CollectionUtils.isNotEmpty(termLexemes)) {

			if (CollectionUtils.isNotEmpty(summarisedPosCodes)) {
				isShowTermPoses = termLexemes.stream()
						.anyMatch(lexeme -> (lexeme.getPosCodes() == null) || !CollectionUtils.isEqualCollection(lexeme.getPosCodes(), summarisedPosCodes));
			}

			for (LexemeWord lexemeWord : termLexemes) {

				String wordValue = lexemeWord.getValue();
				String wordLang = lexemeWord.getLang();
				List<LexemeWord> meaningLexemes = lexemeWord.getMeaningLexemes();
				for (LexemeWord meaningLexeme : meaningLexemes) {
					String meaningLexemeWordValue = meaningLexeme.getValue();
					String meaningLexemeWordlang = meaningLexeme.getLang();
					if (StringUtils.equals(meaningLexemeWordValue, wordValue) && StringUtils.equals(meaningLexemeWordlang, wordLang)) {
						meaningLexeme.setShowWordDataAsHidden(false);
						meaningLexeme.setShowPoses(isShowTermPoses);
					} else {
						boolean isShowWordDataAsHidden = CollectionUtils.isNotEmpty(meaningLexeme.getPoses()) || meaningLexeme.getGender() != null;
						meaningLexeme.setShowWordDataAsHidden(isShowWordDataAsHidden);
						meaningLexeme.setShowPoses(true);
					}
				}
			}
		}

		word.setSummarisedPoses(summarisedPoses);
	}

	public void filterWordRelationsBySynonyms(Word word, List<LexemeWord> lexemeWords) {

		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}
		List<WordRelationGroup> primaryRelatedWordTypeGroups = word.getPrimaryRelatedWordTypeGroups();
		if (CollectionUtils.isEmpty(primaryRelatedWordTypeGroups)) {
			return;
		}
		List<Long> sourceLangSynonymWordIds = lexemeWords.stream()
				.map(LexemeWord::getSourceLangSynonymWordIds)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(sourceLangSynonymWordIds)) {
			return;
		}
		for (WordRelationGroup wordRelationGroup : primaryRelatedWordTypeGroups) {
			Classifier wordRelType = wordRelationGroup.getWordRelType();
			List<WordRelation> relatedWords = wordRelationGroup.getRelatedWords();
			if (CollectionUtils.isEmpty(relatedWords)) {
				continue;
			}
			if (StringUtils.equals(WORD_REL_TYPE_CODE_RAW, wordRelType.getCode())) {
				relatedWords = relatedWords.stream()
						.filter(relatedWord -> !sourceLangSynonymWordIds.contains(relatedWord.getWordId()))
						.collect(Collectors.toList());
				wordRelationGroup.setRelatedWords(relatedWords);
			}
		}
	}
}
