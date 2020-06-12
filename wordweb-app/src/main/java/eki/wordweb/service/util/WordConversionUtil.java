package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.data.Classifier;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.TypeDefinition;
import eki.wordweb.data.TypeMeaningWord;
import eki.wordweb.data.TypeWordRelation;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordGroup;
import eki.wordweb.data.WordRelationGroup;
import eki.wordweb.data.WordRelationsTuple;
import eki.wordweb.data.WordTypeData;

@Component
public class WordConversionUtil extends AbstractConversionUtil {

	public void composeHomonymWrapups(List<Word> words, DataFilter dataFilter) {

		Complexity lexComplexity = dataFilter.getLexComplexity();

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

	public void composeWordRelations(Word word, WordRelationsTuple wordRelationsTuple, Map<String, Long> langOrderByMap, Complexity lexComplexity, String displayLang) {

		if (wordRelationsTuple == null) {
			return;
		}
		word.setWordGroups(new ArrayList<>());
		word.setRelatedWords(new ArrayList<>());
		word.setRelatedWordTypeGroups(new ArrayList<>());

		List<Classifier> wordRelTypes = classifierUtil.getClassifiers(ClassifierName.WORD_REL_TYPE, displayLang);
		List<String> wordRelTypeCodes = wordRelTypes.stream().map(Classifier::getCode).collect(Collectors.toList());
		List<Complexity> combinedLexComplexity = Arrays.asList(lexComplexity, Complexity.ANY);

		List<TypeWordRelation> relatedWords = wordRelationsTuple.getRelatedWords();
		if (CollectionUtils.isNotEmpty(relatedWords)) {
			relatedWords = relatedWords.stream()
					.filter(relation -> CollectionUtils.isNotEmpty(CollectionUtils.intersection(relation.getLexComplexities(), combinedLexComplexity)))
					.collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(relatedWords)) {
				word.getRelatedWords().addAll(relatedWords);
				for (TypeWordRelation wordRelation : relatedWords) {
					classifierUtil.applyClassifiers(wordRelation, displayLang);
					setWordTypeFlags(wordRelation);
				}
				Map<String, List<TypeWordRelation>> relatedWordsMap = relatedWords.stream().collect(Collectors.groupingBy(TypeWordRelation::getWordRelTypeCode));
				for (String wordRelTypeCode : wordRelTypeCodes) {
					List<TypeWordRelation> relatedWordsOfType = relatedWordsMap.get(wordRelTypeCode);
					if (CollectionUtils.isNotEmpty(relatedWordsOfType)) {
						Classifier wordRelType = relatedWordsOfType.get(0).getWordRelType();
						if (StringUtils.equals(WORD_REL_TYPE_CODE_RAW, wordRelTypeCode)) {
							Map<Boolean, List<TypeWordRelation>> wordRelationSynOrMatchMap = relatedWordsOfType.stream()
									.collect(Collectors.groupingBy(wordRelation -> StringUtils.equals(word.getLang(), wordRelation.getLang())));
							List<TypeWordRelation> relatedWordsAsSyn = wordRelationSynOrMatchMap.get(Boolean.TRUE);
							Classifier wordRelTypeSyn = classifierUtil.reValue(wordRelType, "classifier.word_rel_type.raw.syn");
							appendRelatedWordTypeGroup(word, wordRelTypeSyn, relatedWordsAsSyn, null);
							List<TypeWordRelation> relatedWordsAsMatch = wordRelationSynOrMatchMap.get(Boolean.FALSE);
							Classifier wordRelTypeMatch = classifierUtil.reValue(wordRelType, "classifier.word_rel_type.raw.match");
							appendRelatedWordTypeGroup(word, wordRelTypeMatch, relatedWordsAsMatch, langOrderByMap);
						} else {
							appendRelatedWordTypeGroup(word, wordRelType, relatedWordsOfType, null);
						}
					}
				}
			}
		}
		List<TypeWordRelation> allWordGroupMembers = wordRelationsTuple.getWordGroupMembers();
		if (CollectionUtils.isNotEmpty(allWordGroupMembers)) {
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
					WordGroup wordGroup = new WordGroup();
					wordGroup.setWordGroupId(wordGroupId);
					wordGroup.setWordRelTypeCode(firstWordGroupMember.getWordRelTypeCode());
					wordGroup.setWordGroupMembers(wordGroupMembers);
					classifierUtil.applyClassifiers(wordGroup, displayLang);
					word.getWordGroups().add(wordGroup);
				}
			}
		}

		boolean wordRelationsExist = CollectionUtils.isNotEmpty(word.getRelatedWords()) || CollectionUtils.isNotEmpty(word.getWordGroups());
		word.setWordRelationsExist(wordRelationsExist);
	}

	private void appendRelatedWordTypeGroup(Word word, Classifier wordRelType, List<TypeWordRelation> relatedWordsOfType, Map<String, Long> langOrderByMap) {
		if (CollectionUtils.isEmpty(relatedWordsOfType)) {
			return;
		}
		WordRelationGroup wordRelationGroup = new WordRelationGroup();
		wordRelationGroup.setWordRelType(wordRelType);

		if (MapUtils.isNotEmpty(langOrderByMap)) {
			Map<String, List<TypeWordRelation>> relatedWordsByLangUnordered = relatedWordsOfType.stream().collect(Collectors.groupingBy(TypeWordRelation::getLang));
			Map<String, List<TypeWordRelation>> relatedWordsByLangOrdered = composeOrderedMap(relatedWordsByLangUnordered, langOrderByMap);
			wordRelationGroup.setRelatedWordsByLang(relatedWordsByLangOrdered);
			wordRelationGroup.setAsMap(true);
		} else {
			wordRelationGroup.setRelatedWords(relatedWordsOfType);	
			wordRelationGroup.setAsList(true);
		}
		word.getRelatedWordTypeGroups().add(wordRelationGroup);
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

	public void composeCommon(Word word, List<Lexeme> lexemes) {

		List<Classifier> summarisedPoses = lexemes.stream()
				.filter(lexeme -> CollectionUtils.isNotEmpty(lexeme.getPoses()))
				.map(Lexeme::getPoses)
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList());
		boolean isSinglePos = CollectionUtils.size(summarisedPoses) == 1;
		for (Lexeme lexeme : lexemes) {
			boolean isShowSection1 = DatasetType.TERM.equals(lexeme.getDatasetType())
					|| (CollectionUtils.isNotEmpty(lexeme.getPoses()) && !isSinglePos)
					|| CollectionUtils.isNotEmpty(lexeme.getGrammars());
			boolean isShowSection2 = CollectionUtils.isNotEmpty(lexeme.getRelatedLexemes())
					|| CollectionUtils.isNotEmpty(lexeme.getRelatedMeanings())
					|| CollectionUtils.isNotEmpty(lexeme.getAdviceNotes())
					|| CollectionUtils.isNotEmpty(lexeme.getLearnerComments())
					|| CollectionUtils.isNotEmpty(lexeme.getLexemePublicNotes())
					|| CollectionUtils.isNotEmpty(lexeme.getMeaningPublicNotes())
					|| CollectionUtils.isNotEmpty(lexeme.getLexemeSourceLinks());
			boolean isShowSection3 = CollectionUtils.isNotEmpty(lexeme.getGovernments())
					|| CollectionUtils.isNotEmpty(lexeme.getUsages())
					|| CollectionUtils.isNotEmpty(lexeme.getImageFiles());
			lexeme.setShowSection1(isShowSection1);
			lexeme.setShowSection2(isShowSection2);
			lexeme.setShowSection3(isShowSection3);
		}
		word.setSummarisedPoses(summarisedPoses);
		word.setSinglePos(isSinglePos);
	}
}
