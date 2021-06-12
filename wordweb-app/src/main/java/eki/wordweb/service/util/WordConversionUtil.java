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
import eki.common.constant.Complexity;
import eki.common.constant.RelationStatus;
import eki.common.data.Classifier;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordGroup;
import eki.wordweb.data.WordRelationGroup;
import eki.wordweb.data.WordRelationsTuple;
import eki.wordweb.data.WordTypeData;
import eki.wordweb.data.type.TypeDefinition;
import eki.wordweb.data.type.TypeMeaningWord;
import eki.wordweb.data.type.TypeWordRelation;

@Component
public class WordConversionUtil extends AbstractConversionUtil {

	public void composeHomonymWrapups(List<Word> words, SearchContext searchContext) {

		Complexity lexComplexity = searchContext.getLexComplexity();

		for (Word word : words) {
			String wordLang = word.getLang();
			List<TypeMeaningWord> meaningWords = word.getMeaningWords();
			if (CollectionUtils.isNotEmpty(meaningWords)) {
				List<TypeMeaningWord> primaryMeaningWords = meaningWords.stream()
						.filter(meaningWord -> isComplexityMatch(meaningWord.getMwLexComplexity(), lexComplexity))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(primaryMeaningWords)) {
					TypeMeaningWord firstMeaningWord = primaryMeaningWords.get(0);
					if (StringUtils.isNotBlank(firstMeaningWord.getWord())) {
						Long lexemeId = firstMeaningWord.getLexemeId();
						List<String> meaningWordValues = primaryMeaningWords.stream()
								.filter(meaningWord -> meaningWord.getLexemeId().equals(lexemeId))
								.filter(meaningWord -> StringUtils.equals(wordLang, meaningWord.getLang()))
								.map(meaningWord -> {
									if (meaningWord.isPrefixoid()) {
										return meaningWord.getWord() + "-";
									} else if (meaningWord.isSuffixoid()) {
										return "-" + meaningWord.getWord();
									} else {
										return meaningWord.getWord();
									}
								})
								.distinct()
								.collect(Collectors.toList());
						String meaningWordsWrapup = StringUtils.join(meaningWordValues, ", ");
						word.setMeaningWordsWrapup(meaningWordsWrapup);
					}
				}
			}
			List<TypeDefinition> definitions = word.getDefinitions();
			if (CollectionUtils.isNotEmpty(definitions)) {
				List<TypeDefinition> primaryDefinitions = definitions.stream()
						.filter(definition -> isComplexityMatch(definition.getComplexity(), lexComplexity))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(primaryDefinitions)) {
					TypeDefinition firstDefinition = primaryDefinitions.get(0);
					if (StringUtils.isNotBlank(firstDefinition.getValue())) {
						Long lexemeId = firstDefinition.getLexemeId();
						List<String> definitionValues = primaryDefinitions.stream()
								.filter(definition -> definition.getLexemeId().equals(lexemeId))
								.map(TypeDefinition::getValue)
								.collect(Collectors.toList());
						String definitionsWrapup = StringUtils.join(definitionValues, ", ");
						word.setDefinitionsWrapup(definitionsWrapup);
					}
				}
			}
		}
	}

	public void setAffixoidFlags(List<? extends WordTypeData> words) {

		for (WordTypeData word : words) {
			setWordTypeFlags(word);
		}
	}

	public void selectHomonym(List<Word> words, Integer homonymNr) {

		if (CollectionUtils.isEmpty(words)) {
			return;
		}
		boolean homonymNrExists = words.stream().anyMatch(word -> word.getHomonymNr().equals(homonymNr));
		if (homonymNrExists) {
			for (Word word : words) {
				if (homonymNr.equals(word.getHomonymNr())) {
					word.setSelected(true);
					break;
				}
			}
		} else {
			words.get(0).setSelected(true);
		}
	}

	public void composeWordRelations(
			Word word,
			WordRelationsTuple wordRelationsTuple,
			Map<String, Long> langOrderByMap,
			Complexity lexComplexity,
			Locale displayLocale,
			String displayLang) {

		if (wordRelationsTuple == null) {
			return;
		}
		word.setWordGroups(new ArrayList<>());
		word.setRelatedWords(new ArrayList<>());
		word.setPrimaryRelatedWordTypeGroups(new ArrayList<>());
		word.setSecondaryRelatedWordTypeGroups(new ArrayList<>());

		List<Classifier> wordRelTypes = classifierUtil.getClassifiers(ClassifierName.WORD_REL_TYPE, displayLang);
		List<Classifier> aspects = classifierUtil.getClassifiers(ClassifierName.ASPECT, displayLang);
		List<Complexity> combinedLexComplexity = Arrays.asList(lexComplexity, Complexity.ANY);
		List<RelationStatus> relationStatusOrder = Arrays.asList(RelationStatus.PROCESSED, RelationStatus.UNDEFINED, RelationStatus.DELETED);

		List<TypeWordRelation> wordRelations = wordRelationsTuple.getRelatedWords();
		Map<String, List<TypeWordRelation>> wordRelationsMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(wordRelations)) {
			wordRelations = wordRelations.stream()
					.filter(relation -> !RelationStatus.DELETED.equals(relation.getRelationStatus()))
					.filter(relation -> CollectionUtils.isNotEmpty(CollectionUtils.intersection(relation.getLexComplexities(), combinedLexComplexity)))
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
				for (TypeWordRelation wordRelation : wordRelations) {
					classifierUtil.applyClassifiers(wordRelation, displayLang);
					setWordTypeFlags(wordRelation);
					if (StringUtils.equals(wordRelation.getWordRelTypeCode(), WORD_REL_TYPE_CODE_DERIVATIVE_BASE)) {
						alternativeWord = wordRelation.getWord();
					}
				}
				word.setAlternativeWord(alternativeWord);
				word.getRelatedWords().addAll(wordRelations);
				wordRelationsMap = wordRelations.stream().collect(Collectors.groupingBy(TypeWordRelation::getWordRelTypeCode));
			}
		}

		for (Classifier wordRelType : wordRelTypes) {
			String wordRelTypeCode = wordRelType.getCode();
			List<TypeWordRelation> relatedWordsOfType = wordRelationsMap.get(wordRelTypeCode);
			List<WordRelationGroup> wordRelationGroups;
			if (ArrayUtils.contains(PRIMARY_WORD_REL_TYPE_CODES, wordRelTypeCode)) {
				wordRelationGroups = word.getPrimaryRelatedWordTypeGroups();
				handleWordRelType(word, wordRelType, relatedWordsOfType, wordRelationGroups, langOrderByMap, displayLocale);
			} else if (CollectionUtils.isNotEmpty(relatedWordsOfType)) {
				wordRelationGroups = word.getSecondaryRelatedWordTypeGroups();
				handleWordRelType(word, wordRelType, relatedWordsOfType, wordRelationGroups, langOrderByMap, displayLocale);
			}
		}

		List<TypeWordRelation> allWordGroupMembers = wordRelationsTuple.getWordGroupMembers();
		if (CollectionUtils.isNotEmpty(allWordGroupMembers)) {
			List<String> aspectCodeOrder = aspects.stream().map(Classifier::getCode).collect(Collectors.toList());
			Map<Long, List<TypeWordRelation>> wordGroupMap = allWordGroupMembers.stream().collect(Collectors.groupingBy(TypeWordRelation::getWordGroupId));
			List<Long> wordGroupIds = new ArrayList<>(wordGroupMap.keySet());
			Collections.sort(wordGroupIds);
			for (Long wordGroupId : wordGroupIds) {
				List<TypeWordRelation> wordGroupMembers = wordGroupMap.get(wordGroupId);
				wordGroupMembers = wordGroupMembers.stream()
						.filter(member -> CollectionUtils.isNotEmpty(CollectionUtils.intersection(member.getLexComplexities(), combinedLexComplexity)))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(wordGroupMembers)) {
					for (TypeWordRelation wordGroupMember : wordGroupMembers) {
						classifierUtil.applyClassifiers(wordGroupMember, displayLang);
						setWordTypeFlags(wordGroupMember);
					}
					TypeWordRelation firstWordGroupMember = wordGroupMembers.get(0);
					String groupWordRelTypeCode = firstWordGroupMember.getWordRelTypeCode();
					Classifier groupWordRelType = firstWordGroupMember.getWordRelType();
					if (StringUtils.equals(WORD_REL_TYPE_CODE_ASCPECTS, groupWordRelTypeCode)) {
						groupWordRelType = classifierUtil.reValue(groupWordRelType, "classifier.word_rel_type.aspect", displayLocale);
						wordGroupMembers.sort((TypeWordRelation rel1, TypeWordRelation rel2) -> {
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
			List<TypeWordRelation> wordRelations,
			List<WordRelationGroup> wordRelationGroups,
			Map<String, Long> langOrderByMap,
			Locale displayLocale) {

		WordRelationGroup wordRelationGroup;
		if (StringUtils.equals(WORD_REL_TYPE_CODE_RAW, wordRelType.getCode())) {
			List<TypeWordRelation> wordRelationSyns = null;
			List<TypeWordRelation> wordRelationMatches = null;
			if (CollectionUtils.isNotEmpty(wordRelations)) {
				Map<Boolean, List<TypeWordRelation>> wordRelationSynOrMatchMap = wordRelations.stream()
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
			appendRelatedWordTypeGroup(wordRelationGroup, wordRelationGroups, wordRelationMatches, langOrderByMap);
		} else {
			wordRelationGroup = new WordRelationGroup();
			wordRelationGroup.setWordRelType(wordRelType);
			appendRelatedWordTypeGroup(wordRelationGroup, wordRelationGroups, wordRelations, null);
		}
	}

	private void appendRelatedWordTypeGroup(
			WordRelationGroup wordRelationGroup, List<WordRelationGroup> wordRelationGroups, List<TypeWordRelation> relatedWordsOfType, Map<String, Long> langOrderByMap) {

		if (CollectionUtils.isEmpty(relatedWordsOfType)) {
			wordRelationGroup.setEmpty(true);
		} else if (MapUtils.isEmpty(langOrderByMap)) {
			wordRelationGroup.setRelatedWords(relatedWordsOfType);
			wordRelationGroup.setAsList(true);
		} else {
			Map<String, List<TypeWordRelation>> relatedWordsByLangUnordered = relatedWordsOfType.stream().collect(Collectors.groupingBy(TypeWordRelation::getLang));
			Map<String, List<TypeWordRelation>> relatedWordsByLangOrdered = composeOrderedMap(relatedWordsByLangUnordered, langOrderByMap);
			wordRelationGroup.setRelatedWordsByLang(relatedWordsByLangOrdered);
			wordRelationGroup.setAsMap(true);
		}
		wordRelationGroups.add(wordRelationGroup);
	}

	public List<String> collectAllRelatedWords(Word word) {
		List<TypeWordRelation> relatedWords = word.getRelatedWords();
		List<String> allRelatedWordValues = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(relatedWords)) {
			List<String> relatedWordValues = relatedWords.stream().map(TypeWordRelation::getWord).distinct().collect(Collectors.toList());
			allRelatedWordValues.addAll(relatedWordValues);
		}
		List<WordGroup> wordGroups = word.getWordGroups();
		if (CollectionUtils.isNotEmpty(wordGroups)) {
			for (WordGroup wordGroup : wordGroups) {
				List<String> relatedWordValues = wordGroup.getWordGroupMembers().stream().map(TypeWordRelation::getWord).distinct().collect(Collectors.toList());
				allRelatedWordValues.addAll(relatedWordValues);
			}
		}
		return allRelatedWordValues;
	}

	public void composeCommon(Word word, List<LexemeWord> lexemeWords) {

		List<Classifier> summarisedPoses = lexemeWords.stream()
				.filter(lexeme -> CollectionUtils.isNotEmpty(lexeme.getPoses()))
				.map(LexemeWord::getPoses)
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList());
		boolean isSinglePos = CollectionUtils.size(summarisedPoses) == 1;
		for (LexemeWord lexemeWord : lexemeWords) {
			boolean isShowSection1 = CollectionUtils.isNotEmpty(lexemeWord.getGrammars())
					|| (CollectionUtils.isNotEmpty(lexemeWord.getPoses()) && !isSinglePos)
					|| (lexemeWord.getValueState() != null);
			boolean isShowSection2 = CollectionUtils.isNotEmpty(lexemeWord.getRelatedLexemes())
					|| CollectionUtils.isNotEmpty(lexemeWord.getRelatedMeanings())
					|| CollectionUtils.isNotEmpty(lexemeWord.getAdviceNotes())
					|| CollectionUtils.isNotEmpty(lexemeWord.getLearnerComments())
					|| CollectionUtils.isNotEmpty(lexemeWord.getLexemeNotes())
					|| CollectionUtils.isNotEmpty(lexemeWord.getMeaningNotes())
					|| CollectionUtils.isNotEmpty(lexemeWord.getLexemeSourceLinks());
			boolean isShowSection3 = CollectionUtils.isNotEmpty(lexemeWord.getGovernments())
					|| CollectionUtils.isNotEmpty(lexemeWord.getUsages())
					|| CollectionUtils.isNotEmpty(lexemeWord.getImageFiles());
			lexemeWord.setShowSection1(isShowSection1);
			lexemeWord.setShowSection2(isShowSection2);
			lexemeWord.setShowSection3(isShowSection3);
		}
		word.setSummarisedPoses(summarisedPoses);
		word.setSinglePos(isSinglePos);
	}
}
