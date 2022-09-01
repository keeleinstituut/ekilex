package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import eki.common.constant.SynonymType;
import eki.common.data.Classifier;
import eki.common.data.OrderedMap;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Meaning;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.WordTypeData;
import eki.wordweb.data.type.TypeDefinition;
import eki.wordweb.data.type.TypeFreeform;
import eki.wordweb.data.type.TypeLexemeRelation;
import eki.wordweb.data.type.TypeMeaningRelation;
import eki.wordweb.data.type.TypeMeaningWord;
import eki.wordweb.data.type.TypeMediaFile;
import eki.wordweb.data.type.TypeSourceLink;
import eki.wordweb.data.type.TypeUsage;

@Component
public class LexemeConversionUtil extends AbstractConversionUtil {

	public void sortLexemes(List<LexemeWord> lexemeWords, DatasetType datasetType) {
		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}
		if (datasetType == null) {
			return;
		}
		if (DatasetType.LEX.equals(datasetType)) {
			Collections.sort(lexemeWords, Comparator
					.comparing(LexemeWord::getDatasetOrderBy)
					.thenComparing(LexemeWord::getLevel1)
					.thenComparing(LexemeWord::getLevel2));
		} else if (DatasetType.TERM.equals(datasetType)) {
			Collections.sort(lexemeWords, Comparator
					.comparing(LexemeWord::getDatasetOrderBy)
					.thenComparing(LexemeWord::getLexemeOrderBy));
		}
	}

	public void flagEmptyLexemes(List<LexemeWord> lexemeWords) {
		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}
		lexemeWords.forEach(lexeme -> {
			boolean isEmptyLexeme = isEmptyLexeme(lexeme);
			lexeme.setEmptyLexeme(isEmptyLexeme);
		});
	}

	private boolean isEmptyLexeme(LexemeWord lexemeWord) {
		return CollectionUtils.isEmpty(lexemeWord.getDefinitions())
				&& CollectionUtils.isEmpty(lexemeWord.getUsages())
				&& CollectionUtils.isEmpty(lexemeWord.getSourceLangSynonyms())
				&& CollectionUtils.isEmpty(lexemeWord.getDestinLangSynonyms())
				&& CollectionUtils.isEmpty(lexemeWord.getRelatedLexemes())
				&& CollectionUtils.isEmpty(lexemeWord.getDomains())
				&& CollectionUtils.isEmpty(lexemeWord.getCollocationPosGroups());
		//not much of a content?
		//&& CollectionUtils.isEmpty(lexeme.getRegisters())
		//&& CollectionUtils.isEmpty(lexeme.getGovernments())
	}

	public List<LexemeWord> arrangeHierarchy(Long wordId, List<LexemeWord> allLexemes) {

		List<LexemeWord> lexemeWords = allLexemes.stream().filter(lexeme -> lexeme.getWordId().equals(wordId)).collect(Collectors.toList());
		Map<Long, List<LexemeWord>> lexemesByMeanings = allLexemes.stream().collect(Collectors.groupingBy(LexemeWord::getMeaningId));
		for (LexemeWord lexemeWord : lexemeWords) {
			Long meaningId = lexemeWord.getMeaningId();
			List<LexemeWord> meaningLexemes = lexemesByMeanings.get(meaningId);
			lexemeWord.setMeaningLexemes(meaningLexemes);
		}
		return lexemeWords;
	}

	public void composeLexemes(
			String wordLang,
			List<LexemeWord> lexemeWords,
			Map<String, Long> langOrderByMap,
			SearchContext searchContext,
			String displayLang) {

		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}

		List<String> destinLangs = searchContext.getDestinLangs();
		Complexity lexComplexity = searchContext.getLexComplexity();

		for (LexemeWord lexemeWord : lexemeWords) {
			populateLexeme(lexemeWord, langOrderByMap, lexComplexity, displayLang);
			populateNotes(lexemeWord, langOrderByMap, lexComplexity);
			populateUsages(lexemeWord, wordLang, destinLangs, lexComplexity, displayLang);
			populateRelatedLexemes(lexemeWord, lexComplexity, displayLang);
		}
	}

	public void composeMeanings(
			String wordLang,
			List<LexemeWord> lexemeWords,
			Map<Long, Meaning> lexemeMeaningMap,
			List<String> allRelatedWordValues,
			Map<String, Long> langOrderByMap,
			SearchContext searchContext,
			String displayLang) {

		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}

		List<String> destinLangs = searchContext.getDestinLangs();
		Complexity lexComplexity = searchContext.getLexComplexity();

		for (LexemeWord lexemeWord : lexemeWords) {
			Long lexemeId = lexemeWord.getLexemeId();
			Meaning meaning = lexemeMeaningMap.get(lexemeId);
			populateMeaningWordsAndSynonyms(lexemeWord, wordLang, meaning, langOrderByMap, destinLangs, lexComplexity, displayLang);
			filterMeaningWords(lexemeWord, allRelatedWordValues);
			populateMeaning(lexemeWord, wordLang, meaning, langOrderByMap, destinLangs, lexComplexity, displayLang);
			populateRelatedMeanings(lexemeWord, wordLang, meaning, langOrderByMap, lexComplexity, displayLang);
			setValueStateFlags(lexemeWord, wordLang);
			populateMeaningLexemes(lexemeWord, langOrderByMap);
			setWordTypeFlags(lexemeWord);
		}
	}

	private void populateLexeme(LexemeWord lexemeWord, Map<String, Long> langOrderByMap, Complexity lexComplexity, String displayLang) {

		lexemeWord.setSourceLangSynonyms(new ArrayList<>());
		lexemeWord.setDestinLangSynonyms(new ArrayList<>());
		lexemeWord.setCollocationPosGroups(new ArrayList<>());

		List<TypeFreeform> notes = lexemeWord.getLexemeNotes();
		List<TypeFreeform> grammars = lexemeWord.getGrammars();
		List<TypeFreeform> governments = lexemeWord.getGovernments();

		lexemeWord.setLexemeNotes(filter(notes, lexComplexity));
		lexemeWord.setGrammars(filter(grammars, lexComplexity));
		lexemeWord.setGovernments(filter(governments, lexComplexity));

		convertUrlsToHrefs(lexemeWord.getLexemeSourceLinks());

		classifierUtil.applyClassifiers(lexemeWord, displayLang);
		classifierUtil.applyClassifiers((WordTypeData) lexemeWord, displayLang);
	}

	private void populateNotes(LexemeWord lexemeWord, Map<String, Long> langOrderByMap, Complexity lexComplexity) {

		List<TypeFreeform> notes = lexemeWord.getLexemeNotes();
		if (CollectionUtils.isEmpty(notes)) {
			return;
		}
		List<TypeSourceLink> lexemeFreeformSourceLinks = lexemeWord.getLexemeFreeformSourceLinks();
		applySourceLinks(notes, lexemeFreeformSourceLinks);
		Map<String, List<TypeFreeform>> notesByLangUnordered = notes.stream().collect(Collectors.groupingBy(TypeFreeform::getLang));
		Map<String, List<TypeFreeform>> notesByLangOrdered = composeOrderedMap(notesByLangUnordered, langOrderByMap);
		lexemeWord.setLexemeNotesByLang(notesByLangOrdered);
	}

	private void populateUsages(
			LexemeWord lexemeWord,
			String wordLang,
			List<String> destinLangs,
			Complexity lexComplexity,
			String displayLang) {

		List<TypeUsage> usages = lexemeWord.getUsages();
		if (CollectionUtils.isEmpty(usages)) {
			return;
		}

		List<TypeSourceLink> lexemeFreeformSourceLinks = lexemeWord.getLexemeFreeformSourceLinks();
		convertUrlsToHrefs(lexemeFreeformSourceLinks);
		Map<Long, List<TypeSourceLink>> lexemeFreeformSourceLinkMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(lexemeFreeformSourceLinks)) {
			lexemeFreeformSourceLinkMap = lexemeFreeformSourceLinks.stream().collect(Collectors.groupingBy(TypeSourceLink::getOwnerId));
		}

		usages = filter(usages, wordLang, destinLangs);
		usages = filter(usages, lexComplexity);
		lexemeWord.setUsages(usages);

		for (TypeUsage usage : usages) {
			// TODO based on reasonable expectation that all translations are in fact in rus
			if (!isDestinLangAlsoRus(destinLangs)) {
				usage.setUsageTranslations(null);
			}
			classifierUtil.applyClassifiers(usage, displayLang);
			Long usageId = usage.getUsageId();
			List<TypeSourceLink> usageSourceLinks = lexemeFreeformSourceLinkMap.get(usageId);
			if (CollectionUtils.isNotEmpty(usageSourceLinks)) {
				usageSourceLinks.forEach(sourceLink -> {
					boolean isTranslator = ReferenceType.TRANSLATOR.equals(sourceLink.getType());
					sourceLink.setTranslator(isTranslator);
				});
			}
			usage.setSourceLinks(usageSourceLinks);
			boolean isPutOnSpeaker = StringUtils.equals(usage.getUsageLang(), DESTIN_LANG_EST);
			usage.setPutOnSpeaker(isPutOnSpeaker);
		}
		boolean isMoreUsages = CollectionUtils.size(usages) > TYPICAL_COLLECTIONS_DISPLAY_LIMIT;
		lexemeWord.setMoreUsages(isMoreUsages);
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

	private void populateRelatedLexemes(LexemeWord lexemeWord, Complexity lexComplexity, String displayLang) {
		List<TypeLexemeRelation> relatedLexemes = lexemeWord.getRelatedLexemes();
		if (CollectionUtils.isEmpty(relatedLexemes)) {
			return;
		}
		relatedLexemes = filter(relatedLexemes, lexComplexity);
		lexemeWord.setRelatedLexemes(relatedLexemes);
		for (TypeLexemeRelation lexemeRelation : relatedLexemes) {
			classifierUtil.applyClassifiers(lexemeRelation, displayLang);
			setWordTypeFlags(lexemeRelation);
		}
		Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType = relatedLexemes.stream().collect(Collectors.groupingBy(TypeLexemeRelation::getLexRelType));
		lexemeWord.setRelatedLexemesByType(relatedLexemesByType);
	}

	private void populateMeaningLexemes(LexemeWord lexemeWord, Map<String, Long> langOrderByMap) {

		Map<String, List<LexemeWord>> meaningLexemesByLangOrdered = new HashMap<>();
		List<LexemeWord> meaningLexemes = lexemeWord.getMeaningLexemes();
		if (CollectionUtils.isNotEmpty(meaningLexemes)) {
			sortLexemes(meaningLexemes, DatasetType.TERM);
			Map<String, List<LexemeWord>> meaningLexemesByLangUnordered = meaningLexemes.stream().collect(Collectors.groupingBy(LexemeWord::getLang));
			meaningLexemesByLangOrdered = composeOrderedMap(meaningLexemesByLangUnordered, langOrderByMap);
		}
		lexemeWord.setMeaningLexemesByLang(meaningLexemesByLangOrdered);
	}

	private void populateMeaningWordsAndSynonyms(LexemeWord lexemeWord, String wordLang, Meaning meaning, Map<String, Long> langOrderByMap, List<String> destinLangs, Complexity lexComplexity, String displayLang) {

		List<TypeMeaningWord> meaningWords = lexemeWord.getMeaningWords();
		if (CollectionUtils.isNotEmpty(meaningWords)) {
			if (DatasetType.LEX.equals(lexemeWord.getDatasetType())) {
				meaningWords = meaningWords.stream().filter(meaningWord -> !meaningWord.getWordId().equals(lexemeWord.getWordId())).collect(Collectors.toList());
			}
			meaningWords = filter(meaningWords, wordLang, destinLangs);
			meaningWords = filter(meaningWords, lexComplexity);

			for (TypeMeaningWord meaningWord : meaningWords) {
				meaningWord.setType(SynonymType.MEANING_WORD);

				cleanEscapeSym(meaningWord.getMwLexGovernments());
				classifierUtil.applyClassifiers(meaningWord, displayLang);
				setWordTypeFlags(meaningWord);
				boolean additionalDataExists = (meaningWord.getAspect() != null)
						|| (meaningWord.getMwLexValueState() != null)
						|| CollectionUtils.isNotEmpty(meaningWord.getMwLexRegisters())
						|| CollectionUtils.isNotEmpty(meaningWord.getMwLexGovernments());
				meaningWord.setAdditionalDataExists(additionalDataExists);

				if (DatasetType.LEX.equals(lexemeWord.getDatasetType()) && StringUtils.equals(wordLang, meaningWord.getLang())) {
					lexemeWord.getSourceLangSynonyms().add(meaningWord);
				} else {
					lexemeWord.getDestinLangSynonyms().add(meaningWord);
				}
			}
			lexemeWord.setMeaningWords(meaningWords);
		}

		List<TypeMeaningRelation> meaningRelations = meaning.getRelatedMeanings();
		if (CollectionUtils.isNotEmpty(meaningRelations)) {
			meaningRelations = meaningRelations.stream().filter(relation -> StringUtils.equals(MEANING_REL_TYPE_CODE_SIMILAR, relation.getMeaningRelTypeCode())).collect(Collectors.toList());
			meaningRelations = filter(meaningRelations, wordLang, destinLangs);
			meaningRelations = filter(meaningRelations, lexComplexity);
		}
		if (CollectionUtils.isNotEmpty(meaningRelations)) {
			Map<String, TypeMeaningWord> meaningRelSynsMap = new OrderedMap<>();

			for (TypeMeaningRelation meaningRelation : meaningRelations) {
				String wordValue = meaningRelation.getWord();
				TypeMeaningWord meaningRelSyn = meaningRelSynsMap.get(wordValue);
				if (meaningRelSyn == null) {
					meaningRelSyn = new TypeMeaningWord();
					meaningRelSyn.setType(SynonymType.MEANING_REL);
					meaningRelSyn.setMeaningId(meaningRelation.getMeaningId());
					meaningRelSyn.setMwLexWeight(meaningRelation.getWeight());
					meaningRelSyn.setWordId(meaningRelation.getWordId());
					meaningRelSyn.setWord(wordValue);
					meaningRelSyn.setWordPrese(meaningRelation.getWordPrese());
					meaningRelSyn.setHomonymNr(meaningRelation.getHomonymNr());
					meaningRelSyn.setLang(meaningRelation.getLang());
					meaningRelSyn.setWordTypeCodes(meaningRelation.getWordTypeCodes());
					meaningRelSyn.setAspectCode(meaningRelation.getAspectCode());
					setWordTypeFlags(meaningRelSyn);
					meaningRelSyn.setMwLexComplexity(meaningRelation.getComplexity());
					if (CollectionUtils.isNotEmpty(meaningRelation.getLexValueStateCodes())){
						meaningRelSyn.setMwLexValueStateCode(meaningRelation.getLexValueStateCodes().get(0));
					}
					meaningRelSyn.setMwLexRegisterCodes(new ArrayList<>());
					meaningRelSyn.setMwLexGovernmentValues(new ArrayList<>());
					meaningRelSynsMap.put(wordValue, meaningRelSyn);
				}

				List<String> lexRegisterCodes = meaningRelation.getLexRegisterCodes();
				List<String> existingLexRegisterCodes = meaningRelSyn.getMwLexRegisterCodes();
				if (lexRegisterCodes != null) {
					lexRegisterCodes.forEach(regCode -> {
						if (!existingLexRegisterCodes.contains(regCode)) {
							existingLexRegisterCodes.add(regCode);
						}
					});
				}

				List<String> lexGovernmentValues = meaningRelation.getLexGovernmentValues();
				List<String> existingLexGovernmentValues = meaningRelSyn.getMwLexGovernmentValues();
				if (lexGovernmentValues != null) {
					lexGovernmentValues.forEach(govValue -> {
						if (!existingLexGovernmentValues.contains(govValue)) {
							existingLexGovernmentValues.add(govValue);
						}
					});
				}

				cleanEscapeSym(meaningRelSyn.getMwLexGovernments());
				classifierUtil.applyClassifiers(meaningRelSyn, displayLang);
				boolean additionalDataExists = (meaningRelSyn.getAspect() != null)
						|| (meaningRelSyn.getMwLexValueState() != null)
						|| CollectionUtils.isNotEmpty(meaningRelSyn.getMwLexRegisters())
						|| CollectionUtils.isNotEmpty(meaningRelSyn.getMwLexGovernments());
				meaningRelSyn.setAdditionalDataExists(additionalDataExists);
			}

			for (TypeMeaningWord meaningRelSyn : meaningRelSynsMap.values()) {
				if (DatasetType.LEX.equals(lexemeWord.getDatasetType()) && StringUtils.equals(wordLang, meaningRelSyn.getLang())) {
					lexemeWord.getSourceLangSynonyms().add(meaningRelSyn);
				}
			}
		}

		Map<String, List<TypeMeaningWord>> destinLangSynonymsByLangOrdered = new HashMap<>();
		List<TypeMeaningWord> destinLangSynonyms = lexemeWord.getDestinLangSynonyms();
		if (CollectionUtils.isNotEmpty(destinLangSynonyms)) {
			Map<String, List<TypeMeaningWord>> destinLangSynonymsByLangUnordered = destinLangSynonyms.stream().collect(Collectors.groupingBy(TypeMeaningWord::getLang));
			destinLangSynonymsByLangOrdered = composeOrderedMap(destinLangSynonymsByLangUnordered, langOrderByMap);
		}
		lexemeWord.setDestinLangSynonymsByLang(destinLangSynonymsByLangOrdered);
	}

	//masking syms added at aggregation because nested complex type array masking fail by postgres
	private void cleanEscapeSym(List<TypeFreeform> governments) {
		if (CollectionUtils.isEmpty(governments)) {
			return;
		}
		for (TypeFreeform government : governments) {
			String cleanValue = StringUtils.replaceChars(government.getValue(), TEMP_CONVERSION_PLACEHOLDER, ' ');
			government.setValue(cleanValue);
		}
	}

	private void populateMeaning(
			LexemeWord lexemeWord, String wordLang,
			Meaning tuple, Map<String, Long> langOrderByMap, List<String> destinLangs, Complexity lexComplexity, String displayLang) {

		final int definitionValueOversizeLimitForMarkupBuffering = Double.valueOf(DEFINITION_OVERSIZE_LIMIT * 1.5).intValue();

		List<TypeDefinition> definitions = tuple.getDefinitions();
		List<TypeSourceLink> allDefinitionSourceLinks = tuple.getDefinitionSourceLinks();
		List<TypeSourceLink> meaningFreeformSourceLinks = tuple.getFreeformSourceLinks();

		if (CollectionUtils.isNotEmpty(definitions)) {
			definitions = filter(definitions, wordLang, destinLangs);
			definitions = filter(definitions, lexComplexity);
			applySourceLinks(definitions, allDefinitionSourceLinks);
			lexemeWord.setDefinitions(definitions);
			Map<String, List<TypeDefinition>> definitionsByLangUnordered = definitions.stream().collect(Collectors.groupingBy(TypeDefinition::getLang));
			Map<String, List<TypeDefinition>> definitionsByLangOrdered = composeOrderedMap(definitionsByLangUnordered, langOrderByMap);
			lexemeWord.setDefinitionsByLang(definitionsByLangOrdered);
			definitions.forEach(definition -> {
				String definitionValue = definition.getValue();
				String definitionValuePrese = definition.getValuePrese();
				boolean isOversizeValue = StringUtils.length(definitionValue) > DEFINITION_OVERSIZE_LIMIT;
				if (isOversizeValue) {
					String definitionValuePreseCut;
					if (StringUtils.contains(definitionValuePrese, GENERIC_EKI_MARKUP_PREFIX)) {
						String definitionValuePreseResizedForMarkup = StringUtils.substring(definitionValuePrese, 0, definitionValueOversizeLimitForMarkupBuffering);
						definitionValuePreseCut = StringUtils.substringBeforeLast(definitionValuePreseResizedForMarkup, GENERIC_EKI_MARKUP_PREFIX);
					} else {
						definitionValuePreseCut = StringUtils.substring(definitionValuePrese, 0, DEFINITION_OVERSIZE_LIMIT);
					}
					definition.setValuePrese(definitionValuePreseCut);
				}
				boolean subDataExists = CollectionUtils.isNotEmpty(definition.getNotes()) || CollectionUtils.isNotEmpty(definition.getSourceLinks());
				definition.setSubDataExists(subDataExists);
				definition.setOversizeValue(isOversizeValue);
			});
		}

		List<TypeFreeform> notes = tuple.getNotes();
		applySourceLinks(notes, meaningFreeformSourceLinks);
		Map<String, List<TypeFreeform>> notesByLangOrdered = null;
		if (CollectionUtils.isNotEmpty(notes)) {
			Map<String, List<TypeFreeform>> notesByLangUnordered = notes.stream().collect(Collectors.groupingBy(TypeFreeform::getLang));
			notesByLangOrdered = composeOrderedMap(notesByLangUnordered, langOrderByMap);
		}
		List<TypeMediaFile> imageFiles = tuple.getImageFiles();
		List<TypeMediaFile> filteredImageFiles = filter(imageFiles, lexComplexity);
		applySourceLinks(filteredImageFiles, meaningFreeformSourceLinks);
		List<TypeMediaFile> mediaFiles = tuple.getMediaFiles();
		List<TypeMediaFile> filteredMediaFiles = filter(mediaFiles, lexComplexity);

		lexemeWord.setMeaningManualEventOn(tuple.getMeaningManualEventOn());
		lexemeWord.setMeaningLastActivityEventOn(tuple.getMeaningLastActivityEventOn());
		lexemeWord.setMeaningNotes(notes);
		lexemeWord.setMeaningNotesByLang(notesByLangOrdered);
		lexemeWord.setImageFiles(filteredImageFiles);
		lexemeWord.setMediaFiles(filteredMediaFiles);
		lexemeWord.setSystematicPolysemyPatterns(tuple.getSystematicPolysemyPatterns());
		lexemeWord.setSemanticTypes(tuple.getSemanticTypes());

		if (Complexity.SIMPLE.equals(lexComplexity)) {
			lexemeWord.setLearnerComments(tuple.getLearnerComments());
		}
		classifierUtil.applyClassifiers(tuple, lexemeWord, displayLang);
	}

	private void populateRelatedMeanings(LexemeWord lexemeWord, String wordLang, Meaning tuple, Map<String, Long> langOrderByMap, Complexity lexComplexity, String displayLang) {

		if (CollectionUtils.isNotEmpty(lexemeWord.getRelatedMeanings())) {
			return;
		}
		List<TypeMeaningRelation> relatedMeanings = tuple.getRelatedMeanings();
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			relatedMeanings = relatedMeanings.stream().filter(relation -> !StringUtils.equals(MEANING_REL_TYPE_CODE_SIMILAR, relation.getMeaningRelTypeCode())).collect(Collectors.toList());
		}
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			relatedMeanings = filter(relatedMeanings, lexComplexity);
		}
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			for (TypeMeaningRelation meaningRelation : relatedMeanings) {
				classifierUtil.applyClassifiers(meaningRelation, displayLang);
				setWordTypeFlags(meaningRelation);
			}
		}
		lexemeWord.setRelatedMeanings(relatedMeanings);
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			Map<Classifier, List<TypeMeaningRelation>> relatedMeaningsByType = relatedMeanings.stream()
					.sorted((relation1, relation2) -> compareLangOrderby(relation1, relation2, wordLang, langOrderByMap))
					.collect(Collectors.groupingBy(TypeMeaningRelation::getMeaningRelType,
							Collectors.groupingBy(TypeMeaningRelation::getMeaningId, Collectors.toList())))
					.entrySet().stream()
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> {
						List<TypeMeaningRelation> meaningRelations = new ArrayList<>();
						entry.getValue().values().forEach(list -> meaningRelations.add(list.get(0)));
						return meaningRelations;
					}));
			lexemeWord.setRelatedMeaningsByType(relatedMeaningsByType);
		}
	}

	private int compareLangOrderby(TypeMeaningRelation relation1, TypeMeaningRelation relation2, String sourceLang, Map<String, Long> langOrderByMap) {

		String lang1 = relation1.getLang();
		String lang2 = relation2.getLang();

		if (StringUtils.equals(sourceLang, lang1) && StringUtils.equals(sourceLang, lang2)) {
			return 0;
		}
		if (StringUtils.equals(sourceLang, lang1)) {
			return -1;
		}
		if (StringUtils.equals(sourceLang, lang2)) {
			return 1;
		}
		Long lang1OrderBy = langOrderByMap.get(lang1);
		Long lang2OrderBy = langOrderByMap.get(lang2);
		return (int) (lang1OrderBy - lang2OrderBy);
	}

	private void filterMeaningWords(LexemeWord lexemeWord, List<String> allRelatedWordValues) {

		List<TypeMeaningWord> meaningWords = lexemeWord.getMeaningWords();
		if (CollectionUtils.isEmpty(meaningWords)) {
			return;
		}
		if (CollectionUtils.isNotEmpty(allRelatedWordValues)) {
			meaningWords = meaningWords.stream().filter(meaningWord -> !allRelatedWordValues.contains(meaningWord.getWord())).collect(Collectors.toList());
		}
		List<TypeLexemeRelation> relatedLexemes = lexemeWord.getRelatedLexemes();
		if (CollectionUtils.isNotEmpty(relatedLexemes)) {
			List<String> relatedLexemeWordValues = relatedLexemes.stream().map(TypeLexemeRelation::getWord).distinct().collect(Collectors.toList());
			meaningWords = meaningWords.stream().filter(meaningWord -> !relatedLexemeWordValues.contains(meaningWord.getWord())).collect(Collectors.toList());
		}
		lexemeWord.setMeaningWords(meaningWords);
	}

	private void setValueStateFlags(LexemeWord lexemeWord, String wordLang) {

		String valueStateCode = lexemeWord.getValueStateCode();
		List<TypeMeaningWord> meaningWords = lexemeWord.getMeaningWords();
		DatasetType datasetType = lexemeWord.getDatasetType();

		if (StringUtils.equals(valueStateCode, VALUE_STATE_INCORRECT) && CollectionUtils.isNotEmpty(meaningWords)) {
			lexemeWord.setCorrectMeaningWord(meaningWords.get(0));
		}

		if (DatasetType.TERM.equals(datasetType)) {
			if (StringUtils.equals(valueStateCode, VALUE_STATE_LEAST_PREFERRED)) {
				TypeMeaningWord preferredTermMeaningWord = meaningWords.stream()
						.filter(meaningWord -> StringUtils.equals(VALUE_STATE_MOST_PREFERRED, meaningWord.getMwLexValueStateCode()) && StringUtils.equals(wordLang, meaningWord.getLang()))
						.findFirst().orElse(null);
				lexemeWord.setPreferredTermMeaningWord(preferredTermMeaningWord);
			}
		}
	}

}
