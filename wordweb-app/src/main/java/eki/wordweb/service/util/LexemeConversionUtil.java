package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.constant.ReferenceType;
import eki.common.data.Classifier;
import eki.common.data.OrderedMap;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.SourceLink;
import eki.wordweb.data.TypeDefinition;
import eki.wordweb.data.TypeGovernment;
import eki.wordweb.data.TypeGrammar;
import eki.wordweb.data.TypeLexemeRelation;
import eki.wordweb.data.TypeMeaningRelation;
import eki.wordweb.data.TypeMeaningWord;
import eki.wordweb.data.TypePublicNote;
import eki.wordweb.data.TypeUsage;

@Component
public class LexemeConversionUtil extends AbstractConversionUtil {

	private static final char RAW_VALUE_ELEMENTS_SEPARATOR = '|';

	public void enrich(
			DatasetType datasetType,
			String wordLang,
			List<Lexeme> lexemes,
			List<LexemeMeaningTuple> lexemeMeaningTuples,
			List<String> allRelatedWordValues,
			Map<String, Long> langOrderByMap,
			DataFilter dataFilter,
			String displayLang) {

		List<String> destinLangs = dataFilter.getDestinLangs();
		Complexity lexComplexity;
		if (DatasetType.TERM.equals(datasetType)) {
			lexComplexity = null;
		} else {
			lexComplexity = dataFilter.getLexComplexity();
		}

		Map<Long, Lexeme> lexemeMap = new HashMap<>();

		for (Lexeme lexeme : lexemes) {

			lexemeMap.put(lexeme.getLexemeId(), lexeme);
			populateLexeme(lexeme, lexComplexity, displayLang);
			populateUsages(lexeme, wordLang, destinLangs, lexComplexity, displayLang);
			populateRelatedLexemes(lexeme, lexComplexity, displayLang);
			populateMeaningWords(lexeme, langOrderByMap, wordLang, destinLangs, lexComplexity, displayLang);
			filterMeaningWords(lexeme, allRelatedWordValues);
		}

		for (LexemeMeaningTuple tuple : lexemeMeaningTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);
			if (lexeme == null) {
				continue;
			}
			populateMeaning(lexeme, tuple, langOrderByMap, wordLang, destinLangs, lexComplexity, displayLang);
			populateRelatedMeanings(lexeme, tuple, displayLang);
		}

		for (Lexeme lexeme : lexemes) {
			boolean isEmptyLexeme = isEmptyLexeme(lexeme);
			lexeme.setEmptyLexeme(isEmptyLexeme);
		}
	}

	private void populateLexeme(Lexeme lexeme, Complexity lexComplexity, String displayLang) {

		if (DatasetType.LEX.equals(lexeme.getDatasetType())) {
			lexeme.setDatasetName(null);
		}
		lexeme.setSourceLangMeaningWords(new ArrayList<>());
		lexeme.setDestinLangMatchWords(new ArrayList<>());
		lexeme.setCollocationPosGroups(new ArrayList<>());

		List<TypePublicNote> publicNotes = lexeme.getPublicNotes();
		List<TypeGrammar> grammars = lexeme.getGrammars();
		List<TypeGovernment> governments = lexeme.getGovernments();

		lexeme.setPublicNotes(filter(publicNotes, lexComplexity));
		lexeme.setGrammars(filter(grammars, lexComplexity));
		lexeme.setGovernments(filter(governments, lexComplexity));

		classifierUtil.applyClassifiers(lexeme, displayLang);
	}

	private void populateUsages(Lexeme lexeme, String wordLang, List<String> destinLangs, Complexity lexComplexity, String displayLang) {
		List<TypeUsage> usages = lexeme.getUsages();
		if (CollectionUtils.isEmpty(usages)) {
			return;
		}
		usages = filter(usages, lexComplexity);
		usages = filter(usages, wordLang, destinLangs);
		lexeme.setUsages(usages);
		for (TypeUsage usage : usages) {
			usage.setUsageAuthors(new ArrayList<>());
			classifierUtil.applyClassifiers(usage, displayLang);
			List<String> usageAuthorsRaw = usage.getUsageAuthorsRaw();
			if (CollectionUtils.isNotEmpty(usageAuthorsRaw)) {
				for (String usageAuthorRaw : usageAuthorsRaw) {
					String[] usageAuthorElements = StringUtils.split(usageAuthorRaw, RAW_VALUE_ELEMENTS_SEPARATOR);
					String type = usageAuthorElements[0];
					String name = usageAuthorElements[1];
					boolean isTranslator = StringUtils.equalsIgnoreCase(ReferenceType.TRANSLATOR.name(), type);
					SourceLink usageAuthor = new SourceLink();
					usageAuthor.setType(type);
					usageAuthor.setName(name);
					usageAuthor.setTranslator(isTranslator);
					usage.getUsageAuthors().add(usageAuthor);
				}
			}
		}
		boolean isMoreUsages = CollectionUtils.size(usages) > TYPICAL_COLLECTIONS_DISPLAY_LIMIT;
		lexeme.setMoreUsages(isMoreUsages);
	}

	private void populateRelatedLexemes(Lexeme lexeme, Complexity lexComplexity, String displayLang) {
		List<TypeLexemeRelation> relatedLexemes = lexeme.getRelatedLexemes();
		if (CollectionUtils.isEmpty(relatedLexemes)) {
			return;
		}
		relatedLexemes = filter(relatedLexemes, lexComplexity);
		lexeme.setRelatedLexemes(relatedLexemes);
		for (TypeLexemeRelation lexemeRelation : relatedLexemes) {
			classifierUtil.applyClassifiers(lexemeRelation, displayLang);
		}
		Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType = relatedLexemes.stream().collect(Collectors.groupingBy(TypeLexemeRelation::getLexRelType));
		lexeme.setRelatedLexemesByType(relatedLexemesByType);
	}

	private void populateMeaningWords(Lexeme lexeme, Map<String, Long> langOrderByMap, String wordLang, List<String> destinLangs, Complexity lexComplexity, String displayLang) {

		List<TypeMeaningWord> meaningWords = lexeme.getMeaningWords();
		if (CollectionUtils.isEmpty(meaningWords)) {
			return;
		}
		List<TypeMeaningWord> filteredMeaningWords = new ArrayList<>();
		for (TypeMeaningWord meaningWord : meaningWords) {
			if (lexComplexity != null
					&& Complexity.SIMPLE.equals(lexComplexity)
					&& Complexity.SIMPLE.equals(meaningWord.getComplexity())) {
				continue;
			}
			String meaningWordLang = meaningWord.getLang();
			if (!isLangFilterMatch(wordLang, meaningWordLang, destinLangs)) {
				continue;
			}
			filteredMeaningWords.add(meaningWord);
			cleanEscapeSym(meaningWord.getMwLexGovernments());
			classifierUtil.applyClassifiers(meaningWord, displayLang);
			setWordTypeFlags(meaningWord);
			boolean additionalDataExists = (meaningWord.getAspect() != null)
					|| (meaningWord.getMwLexValueState() != null)
					|| CollectionUtils.isNotEmpty(meaningWord.getMwLexRegisters())
					|| CollectionUtils.isNotEmpty(meaningWord.getMwLexGovernments());
			meaningWord.setAdditionalDataExists(additionalDataExists);
			if (StringUtils.equals(wordLang, meaningWordLang)) {
				lexeme.getSourceLangMeaningWords().add(meaningWord);
			} else {
				lexeme.getDestinLangMatchWords().add(meaningWord);
			}
		}
		lexeme.setMeaningWords(filteredMeaningWords);

		Map<String, List<TypeMeaningWord>> destinLangMatchWordsByLangUnordered = lexeme.getDestinLangMatchWords().stream().collect(Collectors.groupingBy(TypeMeaningWord::getLang));
		Map<String, List<TypeMeaningWord>> destinLangMatchWordsByLangOrdered = composeOrderedMap(destinLangMatchWordsByLangUnordered, langOrderByMap);

		lexeme.setDestinLangMatchWordsByLang(destinLangMatchWordsByLangOrdered);
	}

	//masking syms added at aggregation because nested complex type array masking fail by postgres
	private void cleanEscapeSym(List<TypeGovernment> governments) {
		if (CollectionUtils.isEmpty(governments)) {
			return;
		}
		for (TypeGovernment government : governments) {
			String cleanValue = StringUtils.replaceChars(government.getValue(), '`', ' ');
			government.setValue(cleanValue);
		}
	}

	private void populateMeaning(
			Lexeme lexeme, LexemeMeaningTuple tuple,
			Map<String, Long> langOrderByMap, String wordLang, List<String> destinLangs, Complexity lexComplexity, String displayLang) {

		List<TypeDefinition> definitions = tuple.getDefinitions();

		if (CollectionUtils.isNotEmpty(definitions)) {
			definitions = filter(definitions, lexComplexity);
			definitions = filter(definitions, wordLang, destinLangs);
			lexeme.setDefinitions(definitions);
			Map<String, List<TypeDefinition>> definitionsByLangUnordered = definitions.stream().collect(Collectors.groupingBy(TypeDefinition::getLang));
			Map<String, List<TypeDefinition>> definitionsByLangOrdered = composeOrderedMap(definitionsByLangUnordered, langOrderByMap);
			lexeme.setDefinitionsByLang(definitionsByLangOrdered);
		}

		lexeme.setSystematicPolysemyPatterns(tuple.getSystematicPolysemyPatterns());
		lexeme.setSemanticTypes(tuple.getSemanticTypes());

		if (Complexity.SIMPLE.equals(lexComplexity)) {
			lexeme.setImageFiles(tuple.getImageFiles());
			lexeme.setLearnerComments(tuple.getLearnerComments());
		}
		classifierUtil.applyClassifiers(tuple, lexeme, displayLang);
	}

	private void populateRelatedMeanings(Lexeme lexeme, LexemeMeaningTuple tuple, String displayLang) {
		if (CollectionUtils.isNotEmpty(lexeme.getRelatedMeanings())) {
			return;
		}
		List<TypeMeaningRelation> relatedMeanings = tuple.getRelatedMeanings();
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			for (TypeMeaningRelation meaningRelation : relatedMeanings) {
				classifierUtil.applyClassifiers(meaningRelation, displayLang);
			}
		}
		lexeme.setRelatedMeanings(relatedMeanings);
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			Map<Classifier, List<TypeMeaningRelation>> relatedMeaningsByType = relatedMeanings.stream().collect(Collectors.groupingBy(TypeMeaningRelation::getMeaningRelType));
			lexeme.setRelatedMeaningsByType(relatedMeaningsByType);
		}
	}

	private void filterMeaningWords(Lexeme lexeme, List<String> allRelatedWordValues) {

		List<TypeMeaningWord> meaningWords = lexeme.getMeaningWords();
		if (CollectionUtils.isEmpty(meaningWords)) {
			return;
		}
		if (CollectionUtils.isNotEmpty(allRelatedWordValues)) {
			meaningWords = meaningWords.stream().filter(meaningWord -> !allRelatedWordValues.contains(meaningWord.getWord())).collect(Collectors.toList());
		}
		List<TypeLexemeRelation> relatedLexemes = lexeme.getRelatedLexemes();
		if (CollectionUtils.isNotEmpty(relatedLexemes)) {
			List<String> relatedLexemeWordValues = relatedLexemes.stream().map(TypeLexemeRelation::getWord).distinct().collect(Collectors.toList());
			meaningWords = meaningWords.stream().filter(meaningWord -> !relatedLexemeWordValues.contains(meaningWord.getWord())).collect(Collectors.toList());
		}
		lexeme.setMeaningWords(meaningWords);
	}

	private boolean isEmptyLexeme(Lexeme lexeme) {
		return CollectionUtils.isEmpty(lexeme.getDefinitions())
				&& CollectionUtils.isEmpty(lexeme.getRelatedLexemes())
				&& CollectionUtils.isEmpty(lexeme.getDomains())
				&& CollectionUtils.isEmpty(lexeme.getRegisters())
				&& CollectionUtils.isEmpty(lexeme.getGovernments())
				&& CollectionUtils.isEmpty(lexeme.getUsages())
				&& CollectionUtils.isEmpty(lexeme.getDestinLangMatchWords());
	}

	private <T> OrderedMap<String, List<T>> composeOrderedMap(Map<String, List<T>> langKeyUnorderedMap, Map<String, Long> langOrderByMap) {
		return langKeyUnorderedMap.entrySet().stream()
				.sorted((entry1, entry2) -> {
					Long orderBy1 = langOrderByMap.get(entry1.getKey());
					Long orderBy2 = langOrderByMap.get(entry2.getKey());
					if (orderBy1 == null) {
						return 0;
					}
					if (orderBy2 == null) {
						return 0;
					}
					return orderBy1.compareTo(orderBy2);
				})
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, OrderedMap::new));
	}
}
