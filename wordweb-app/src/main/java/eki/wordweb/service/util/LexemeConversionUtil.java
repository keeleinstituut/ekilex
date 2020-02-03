package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.constant.ReferenceOwner;
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
import eki.wordweb.data.TypeSourceLink;
import eki.wordweb.data.TypeUsage;

@Component
public class LexemeConversionUtil extends AbstractConversionUtil {

	private static final char RAW_VALUE_ELEMENTS_SEPARATOR = '|';

	public List<Lexeme> filterLexemes(List<Lexeme> lexemes, Complexity lexComplexity) {
		return filterSimpleOnly(lexemes, lexComplexity);
	}

	public void compose(
			DatasetType datasetType,
			String wordLang,
			List<Lexeme> lexemes,
			Map<Long, List<TypeSourceLink>> lexemeSourceLinkMap,
			Map<Long, List<TypeSourceLink>> freeformSourceLinkMap,
			Map<Long, LexemeMeaningTuple> lexemeMeaningTupleMap,
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

		for (Lexeme lexeme : lexemes) {

			Long lexemeId = lexeme.getLexemeId();
			populateLexeme(lexeme, lexemeSourceLinkMap, lexComplexity, displayLang);
			populateUsages(lexeme, wordLang, destinLangs, lexComplexity, displayLang);
			populateRelatedLexemes(lexeme, lexComplexity, displayLang);
			populateMeaningWords(lexeme, langOrderByMap, wordLang, destinLangs, lexComplexity, displayLang);
			filterMeaningWords(lexeme, allRelatedWordValues);
			LexemeMeaningTuple lexemeMeaningTuple = lexemeMeaningTupleMap.get(lexemeId);
			populateMeaning(lexeme, lexemeMeaningTuple, langOrderByMap, wordLang, destinLangs, lexComplexity, displayLang);
			populateRelatedMeanings(lexeme, lexemeMeaningTuple, displayLang);

			boolean isEmptyLexeme = isEmptyLexeme(lexeme);
			lexeme.setEmptyLexeme(isEmptyLexeme);
		}
	}

	private void populateLexeme(Lexeme lexeme, Map<Long, List<TypeSourceLink>> lexemeSourceLinkMap, Complexity lexComplexity, String displayLang) {

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

		List<TypeSourceLink> lexemeSourceLinks = lexemeSourceLinkMap.get(lexeme.getLexemeId());
		lexeme.setLexemeSourceLinks(lexemeSourceLinks);

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
			// TODO based on reasonable expectation that all translations are in fact in rus
			if (!isDestinLangAlsoRus(destinLangs)) {
				usage.setUsageTranslations(null);
			}
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

	private boolean isDestinLangAlsoRus(List<String> destinLangs) {
		if (CollectionUtils.isEmpty(destinLangs)) {
			return true;
		}
		if (destinLangs.contains(DESTIN_LANG_ALL)) {
			return true;
		}
		return destinLangs.contains(DESTIN_LANG_RUS);
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
		meaningWords = filter(meaningWords, wordLang, destinLangs);
		meaningWords = filterSimpleOnly(meaningWords, lexComplexity);

		for (TypeMeaningWord meaningWord : meaningWords) {
			String meaningWordLang = meaningWord.getLang();
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
		lexeme.setMeaningWords(meaningWords);

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
			List<TypeSourceLink> allDefinitionSourceLinks = tuple.getDefinitionSourceLinks();
			if (CollectionUtils.isNotEmpty(allDefinitionSourceLinks)) {
				Map<Long, List<TypeSourceLink>> definitionSourceLinksMap = allDefinitionSourceLinks.stream()
						.filter(sourceLink -> ReferenceOwner.DEFINITION.equals(sourceLink.getRefOwner()))
						.collect(Collectors.groupingBy(TypeSourceLink::getOwnerId));
				definitions.forEach(definition -> {
					Long definitionId = definition.getDefinitionId();
					List<TypeSourceLink> sourceLinks = definitionSourceLinksMap.get(definitionId);
					definition.setSourceLinks(sourceLinks);
				});
			}
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
				&& CollectionUtils.isEmpty(lexeme.getUsages())
				&& CollectionUtils.isEmpty(lexeme.getSourceLangMeaningWords())
				&& CollectionUtils.isEmpty(lexeme.getDestinLangMatchWords())
				&& CollectionUtils.isEmpty(lexeme.getRelatedLexemes())
				&& CollectionUtils.isEmpty(lexeme.getDomains())
				;
		//not much of a content?
		//&& CollectionUtils.isEmpty(lexeme.getRegisters()) 
		//&& CollectionUtils.isEmpty(lexeme.getGovernments())
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
