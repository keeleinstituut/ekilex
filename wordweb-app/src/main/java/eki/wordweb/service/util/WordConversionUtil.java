package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.Complexity;
import eki.common.data.Classifier;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.TypeDefinition;
import eki.wordweb.data.TypeMeaningWord;
import eki.wordweb.data.TypeWordRelation;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordGroup;
import eki.wordweb.data.WordRelationGroup;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.data.WordTypeData;

@Component
public class WordConversionUtil extends AbstractConversionUtil {

	public void composeHomonymWrapups(List<Word> words, DataFilter dataFilter) {

		Complexity lexComplexity = dataFilter.getLexComplexity();
		//TODO destinLang & datasetCodes?

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

	public void composeWordRelations(Word word, List<WordRelationTuple> wordRelationTuples, Complexity lexComplexity, String displayLang) {

		if (CollectionUtils.isEmpty(wordRelationTuples)) {
			return;
		}
		List<Classifier> wordRelTypes = classifierUtil.getClassifiers(ClassifierName.WORD_REL_TYPE, displayLang);
		List<String> wordRelTypeCodes = wordRelTypes.stream().map(Classifier::getCode).collect(Collectors.toList());
		word.setWordGroups(new ArrayList<>());
		word.setRelatedWords(new ArrayList<>());
		for (WordRelationTuple tuple : wordRelationTuples) {
			List<TypeWordRelation> relatedWords = tuple.getRelatedWords();
			if (CollectionUtils.isNotEmpty(relatedWords) && (lexComplexity != null)) {
				relatedWords = relatedWords.stream()
						.filter(relation -> ArrayUtils.contains(relation.getLexComplexities(), lexComplexity))
						.collect(Collectors.toList());
			}
			if (CollectionUtils.isNotEmpty(relatedWords)) {
				word.getRelatedWords().addAll(relatedWords);
			}
			if (CollectionUtils.isNotEmpty(relatedWords)) {
				for (TypeWordRelation wordRelation : relatedWords) {
					classifierUtil.applyClassifiers(wordRelation, displayLang);
					setWordTypeFlags(wordRelation);
				}
				word.setLimitedRelatedWordTypeGroups(new ArrayList<>());
				word.setRelatedWordTypeGroups(new ArrayList<>());
				Map<String, List<TypeWordRelation>> relatedWordsMap = relatedWords.stream().collect(Collectors.groupingBy(TypeWordRelation::getWordRelTypeCode));
				for (String wordRelTypeCode : wordRelTypeCodes) {
					List<TypeWordRelation> relatedWordsOfType = relatedWordsMap.get(wordRelTypeCode);
					if (CollectionUtils.isNotEmpty(relatedWordsOfType)) {
						Classifier wordRelType = relatedWordsOfType.get(0).getWordRelType();
						WordRelationGroup wordRelationGroup = new WordRelationGroup();
						wordRelationGroup.setWordRelType(wordRelType);
						wordRelationGroup.setRelatedWords(relatedWordsOfType);
						word.getRelatedWordTypeGroups().add(wordRelationGroup);
					}
				}
				int limitedRelatedWordCounter = 0;
				for (WordRelationGroup wordRelationGroup : word.getRelatedWordTypeGroups()) {
					if (limitedRelatedWordCounter >= WORD_RELATIONS_DISPLAY_LIMIT) {
						break;
					}
					List<TypeWordRelation> relatedWordsOfType = wordRelationGroup.getRelatedWords();
					int maxLimit = Math.min(relatedWordsOfType.size(), WORD_RELATIONS_DISPLAY_LIMIT - limitedRelatedWordCounter);
					List<TypeWordRelation> limitedRelatedWordsOfType = relatedWordsOfType.subList(0, maxLimit);
					if (CollectionUtils.isNotEmpty(limitedRelatedWordsOfType)) {
						WordRelationGroup limitedWordRelationGroup = new WordRelationGroup();
						limitedWordRelationGroup.setWordRelType(wordRelationGroup.getWordRelType());
						limitedWordRelationGroup.setRelatedWords(limitedRelatedWordsOfType);
						word.getLimitedRelatedWordTypeGroups().add(limitedWordRelationGroup);
						limitedRelatedWordCounter += limitedRelatedWordsOfType.size();
					}
				}
			}
			List<TypeWordRelation> wordGroupMembers = tuple.getWordGroupMembers();
			if (CollectionUtils.isNotEmpty(wordGroupMembers) && (lexComplexity != null)) {
				wordGroupMembers = wordGroupMembers.stream()
						.filter(member -> ArrayUtils.contains(member.getLexComplexities(), lexComplexity))
						.collect(Collectors.toList());
			}
			if (CollectionUtils.isNotEmpty(wordGroupMembers)) {
				for (TypeWordRelation wordGroupMember : wordGroupMembers) {
					classifierUtil.applyClassifiers(wordGroupMember, displayLang);
					setWordTypeFlags(wordGroupMember);
				}
				WordGroup wordGroup = new WordGroup();
				wordGroup.setWordGroupId(tuple.getWordGroupId());
				wordGroup.setWordRelTypeCode(tuple.getWordRelTypeCode());
				wordGroup.setWordGroupMembers(wordGroupMembers);
				classifierUtil.applyClassifiers(wordGroup, displayLang);
				word.getWordGroups().add(wordGroup);
			}
		}
		boolean wordRelationsExist = CollectionUtils.isNotEmpty(word.getRelatedWords()) || CollectionUtils.isNotEmpty(word.getWordGroups());
		word.setWordRelationsExist(wordRelationsExist);
		boolean isMoreWordRelations = CollectionUtils.size(word.getRelatedWords()) > WORD_RELATIONS_DISPLAY_LIMIT;
		word.setMoreWordRelations(isMoreWordRelations);
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

		List<Classifier> summarisedPoses = new ArrayList<>();
		for (Lexeme lexeme : lexemes) {
			if (CollectionUtils.isNotEmpty(lexeme.getPoses())) {
				summarisedPoses.addAll(lexeme.getPoses());
			}
		}
		summarisedPoses = summarisedPoses.stream().distinct().collect(Collectors.toList());
		boolean isSinglePos = CollectionUtils.size(summarisedPoses) == 1;
		word.setSummarisedPoses(summarisedPoses);
		word.setSinglePos(isSinglePos);
	}

}
