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
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Synonym;
import eki.wordweb.data.TypeDefinition;
import eki.wordweb.data.TypeFreeform;
import eki.wordweb.data.TypeLexemeRelation;
import eki.wordweb.data.TypeMeaningRelation;
import eki.wordweb.data.TypeMeaningWord;
import eki.wordweb.data.TypeMediaFile;
import eki.wordweb.data.TypeSourceLink;
import eki.wordweb.data.TypeUsage;

@Component
public class LexemeConversionUtil extends AbstractConversionUtil {

	public void sortLexemes(List<Lexeme> lexemes, DatasetType datasetType) {
		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}
		if (datasetType == null) {
			return;
		}
		if (DatasetType.LEX.equals(datasetType)) {
			Collections.sort(lexemes, Comparator
					.comparing(Lexeme::getDatasetOrderBy)
					.thenComparing(Lexeme::getLevel1)
					.thenComparing(Lexeme::getLevel2));
		} else if (DatasetType.TERM.equals(datasetType)) {
			Collections.sort(lexemes, Comparator
					.comparing(Lexeme::getDatasetOrderBy)
					.thenComparing(Lexeme::getValueStateOrderBy, Comparator.nullsLast(Comparator.naturalOrder()))
					.thenComparing(Lexeme::getLexemeOrderBy));
		}
	}

	public void flagEmptyLexemes(List<Lexeme> lexemes) {
		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}
		lexemes.forEach(lexeme -> {
			boolean isEmptyLexeme = isEmptyLexeme(lexeme);
			lexeme.setEmptyLexeme(isEmptyLexeme);
		});
	}

	private boolean isEmptyLexeme(Lexeme lexeme) {
		return CollectionUtils.isEmpty(lexeme.getDefinitions())
				&& CollectionUtils.isEmpty(lexeme.getUsages())
				&& CollectionUtils.isEmpty(lexeme.getSourceLangSynonyms())
				&& CollectionUtils.isEmpty(lexeme.getDestinLangSynonyms())
				&& CollectionUtils.isEmpty(lexeme.getRelatedLexemes())
				&& CollectionUtils.isEmpty(lexeme.getDomains())
				&& CollectionUtils.isEmpty(lexeme.getCollocationPosGroups());
		//not much of a content?
		//&& CollectionUtils.isEmpty(lexeme.getRegisters()) 
		//&& CollectionUtils.isEmpty(lexeme.getGovernments())
	}

	public void compose(
			String wordLang,
			List<Lexeme> lexemes,
			Map<Long, LexemeMeaningTuple> lexemeMeaningTupleMap,
			List<String> allRelatedWordValues,
			Map<String, Long> langOrderByMap,
			DataFilter dataFilter,
			String displayLang) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		List<String> destinLangs = dataFilter.getDestinLangs();
		Complexity lexComplexity = dataFilter.getLexComplexity();

		for (Lexeme lexeme : lexemes) {

			Long lexemeId = lexeme.getLexemeId();
			populateLexeme(lexeme, langOrderByMap, lexComplexity, displayLang);
			populateUsages(lexeme, wordLang, destinLangs, lexComplexity, displayLang);
			populateRelatedLexemes(lexeme, lexComplexity, displayLang);
			LexemeMeaningTuple lexemeMeaningTuple = lexemeMeaningTupleMap.get(lexemeId);
			populateSynonyms(lexeme, wordLang, lexemeMeaningTuple, langOrderByMap, destinLangs, lexComplexity, displayLang);
			populateMeaningWords(lexeme, wordLang, destinLangs, lexComplexity, displayLang);
			filterMeaningWords(lexeme, allRelatedWordValues);
			populateMeaning(lexeme, wordLang, lexemeMeaningTuple, langOrderByMap, destinLangs, lexComplexity, displayLang);
			populateRelatedMeanings(lexeme, wordLang, lexemeMeaningTuple, langOrderByMap, lexComplexity, displayLang);
			setValueStateFlags(lexeme, wordLang);
		}
	}

	private void populateLexeme(Lexeme lexeme, Map<String, Long> langOrderByMap, Complexity lexComplexity, String displayLang) {

		lexeme.setSourceLangSynonyms(new ArrayList<>());
		lexeme.setDestinLangSynonyms(new ArrayList<>());
		lexeme.setCollocationPosGroups(new ArrayList<>());

		List<TypeFreeform> notes = lexeme.getLexemeNotes();
		List<TypeFreeform> grammars = lexeme.getGrammars();
		List<TypeFreeform> governments = lexeme.getGovernments();

		List<TypeFreeform> filteredNotes = filter(notes, lexComplexity);
		Map<String, List<TypeFreeform>> notesByLangOrdered = null;
		if (CollectionUtils.isNotEmpty(filteredNotes)) {
			Map<String, List<TypeFreeform>> notesByLangUnordered = filteredNotes.stream().collect(Collectors.groupingBy(TypeFreeform::getLang));
			notesByLangOrdered = composeOrderedMap(notesByLangUnordered, langOrderByMap);
		}

		lexeme.setLexemeNotes(filteredNotes);
		lexeme.setLexemeNotesByLang(notesByLangOrdered);
		lexeme.setGrammars(filter(grammars, lexComplexity));
		lexeme.setGovernments(filter(governments, lexComplexity));

		convertUrlsToHrefs(lexeme.getLexemeSourceLinks());

		classifierUtil.applyClassifiers(lexeme, displayLang);
	}

	private void populateUsages(
			Lexeme lexeme,
			String wordLang,
			List<String> destinLangs,
			Complexity lexComplexity,
			String displayLang) {

		List<TypeUsage> usages = lexeme.getUsages();
		if (CollectionUtils.isEmpty(usages)) {
			return;
		}

		List<TypeSourceLink> lexemeFreeformSourceLinks = lexeme.getLexemeFreeformSourceLinks();
		convertUrlsToHrefs(lexemeFreeformSourceLinks);
		Map<Long, List<TypeSourceLink>> lexemeFreeformSourceLinkMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(lexemeFreeformSourceLinks)) {
			lexemeFreeformSourceLinkMap = lexemeFreeformSourceLinks.stream().collect(Collectors.groupingBy(TypeSourceLink::getOwnerId));
		}

		usages = filter(usages, wordLang, destinLangs);
		usages = filter(usages, lexComplexity);
		lexeme.setUsages(usages);

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
			setWordTypeFlags(lexemeRelation);
		}
		Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType = relatedLexemes.stream().collect(Collectors.groupingBy(TypeLexemeRelation::getLexRelType));
		lexeme.setRelatedLexemesByType(relatedLexemesByType);
	}

	private void populateSynonyms(Lexeme lexeme, String wordLang, LexemeMeaningTuple lexemeMeaningTuple, Map<String, Long> langOrderByMap, List<String> destinLangs, Complexity lexComplexity, String displayLang) {

		List<TypeMeaningWord> meaningWords = lexeme.getMeaningWords();
		if (CollectionUtils.isNotEmpty(meaningWords)) {
			if (DatasetType.LEX.equals(lexeme.getDatasetType())) {
				meaningWords = meaningWords.stream().filter(meaningWord -> !meaningWord.getWordId().equals(lexeme.getWordId())).collect(Collectors.toList());
			}
			meaningWords = filter(meaningWords, wordLang, destinLangs);
			meaningWords = filter(meaningWords, lexComplexity);

			for (TypeMeaningWord meaningWord : meaningWords) {
				Synonym meaningWordSyn = new Synonym();
				meaningWordSyn.setType(SynonymType.MEANING_WORD);
				meaningWordSyn.setMeaningId(meaningWord.getMeaningId());
				meaningWordSyn.setWeight(meaningWord.getMwLexWeight());
				meaningWordSyn.setLexemeId(meaningWord.getMwLexemeId());
				meaningWordSyn.setWordId(meaningWord.getWordId());
				meaningWordSyn.setWord(meaningWord.getWord());
				meaningWordSyn.setWordPrese(meaningWord.getWordPrese());
				meaningWordSyn.setHomonymNr(meaningWord.getHomonymNr());
				meaningWordSyn.setLang(meaningWord.getLang());
				meaningWordSyn.setWordTypeCodes(meaningWord.getWordTypeCodes());
				meaningWordSyn.setLexRegisterCodes(meaningWord.getMwLexRegisterCodes());
				meaningWordSyn.setLexGovernments(meaningWord.getMwLexGovernments());
				meaningWordSyn.setComplexity(meaningWord.getMwLexComplexity());
				meaningWordSyn.setLexValueStateCode(meaningWord.getMwLexValueStateCode());
				meaningWordSyn.setAspectCode(meaningWord.getAspectCode());

				cleanEscapeSym(meaningWordSyn.getLexGovernments());
				setWordTypeFlags(meaningWordSyn);
				classifierUtil.applyClassifiers(meaningWordSyn, displayLang);

				boolean additionalDataExists = (meaningWordSyn.getAspect() != null)
						|| (meaningWordSyn.getLexValueState() != null)
						|| CollectionUtils.isNotEmpty(meaningWordSyn.getLexRegisters())
						|| CollectionUtils.isNotEmpty(meaningWordSyn.getLexGovernments());
				meaningWordSyn.setAdditionalDataExists(additionalDataExists);

				if (DatasetType.LEX.equals(lexeme.getDatasetType()) && StringUtils.equals(wordLang, meaningWordSyn.getLang())) {
					lexeme.getSourceLangSynonyms().add(meaningWordSyn);
				} else {
					lexeme.getDestinLangSynonyms().add(meaningWordSyn);
				}
			}
		}

		List<TypeMeaningRelation> meaningRelations = lexemeMeaningTuple.getRelatedMeanings();
		if (CollectionUtils.isNotEmpty(meaningRelations)) {
			meaningRelations.removeIf(relation -> !StringUtils.equals(MEANING_REL_TYPE_CODE_SIMILAR, relation.getMeaningRelTypeCode()));
			meaningRelations = filter(meaningRelations, wordLang, destinLangs);
			meaningRelations = filter(meaningRelations, lexComplexity);
		}
		if (CollectionUtils.isNotEmpty(meaningRelations)) {
			for (TypeMeaningRelation meaningRelation : meaningRelations) {
				Synonym meaningRelSyn = new Synonym();
				meaningRelSyn.setType(SynonymType.MEANING_REL);
				meaningRelSyn.setMeaningId(meaningRelation.getMeaningId());
				meaningRelSyn.setWeight(meaningRelation.getWeight());
				meaningRelSyn.setWordId(meaningRelation.getWordId());
				meaningRelSyn.setWord(meaningRelation.getWord());
				meaningRelSyn.setWordPrese(meaningRelation.getWordPrese());
				meaningRelSyn.setHomonymNr(meaningRelation.getHomonymNr());
				meaningRelSyn.setLang(meaningRelation.getLang());
				meaningRelSyn.setWordTypeCodes(meaningRelation.getWordTypeCodes());
				meaningRelSyn.setLexRegisterCodes(meaningRelation.getLexRegisterCodes());
				meaningRelSyn.setLexGovernmentValues(meaningRelation.getLexGovernmentValues());
				meaningRelSyn.setComplexity(meaningRelation.getComplexity());
				if (CollectionUtils.isNotEmpty(meaningRelation.getLexValueStateCodes())){
					meaningRelSyn.setLexValueStateCode(meaningRelation.getLexValueStateCodes().get(0));
				}
				meaningRelSyn.setAspectCode(meaningRelation.getAspectCode());

				cleanEscapeSym(meaningRelSyn.getLexGovernments());
				setWordTypeFlags(meaningRelSyn);
				classifierUtil.applyClassifiers(meaningRelSyn, displayLang);

				boolean additionalDataExists = (meaningRelSyn.getAspect() != null)
						|| (meaningRelSyn.getLexValueState() != null)
						|| CollectionUtils.isNotEmpty(meaningRelSyn.getLexRegisters())
						|| CollectionUtils.isNotEmpty(meaningRelSyn.getLexGovernments());
				meaningRelSyn.setAdditionalDataExists(additionalDataExists);

				if (DatasetType.LEX.equals(lexeme.getDatasetType()) && StringUtils.equals(wordLang, meaningRelSyn.getLang())) {
					lexeme.getSourceLangSynonyms().add(meaningRelSyn);
				} else {
					lexeme.getDestinLangSynonyms().add(meaningRelSyn);
				}
			}
		}

		Map<String, List<Synonym>> destinLangSynonymsByLangOrdered = new HashMap<>();
		List<Synonym> destinLangSynonyms = lexeme.getDestinLangSynonyms();
		if (CollectionUtils.isNotEmpty(destinLangSynonyms)) {
			Map<String, List<Synonym>> destinLangSynonymsByLangUnordered = destinLangSynonyms.stream().collect(Collectors.groupingBy(Synonym::getLang));
			destinLangSynonymsByLangOrdered = composeOrderedMap(destinLangSynonymsByLangUnordered, langOrderByMap);
		}
		lexeme.setDestinLangSynonymsByLang(destinLangSynonymsByLangOrdered);
	}

	private void populateMeaningWords(Lexeme lexeme, String wordLang, List<String> destinLangs, Complexity lexComplexity, String displayLang) {

		List<TypeMeaningWord> meaningWords = lexeme.getMeaningWords();
		if (CollectionUtils.isEmpty(meaningWords)) {
			return;
		}
		if (DatasetType.LEX.equals(lexeme.getDatasetType())) {
			meaningWords = meaningWords.stream().filter(meaningWord -> !meaningWord.getWordId().equals(lexeme.getWordId())).collect(Collectors.toList());
		}
		meaningWords = filter(meaningWords, wordLang, destinLangs);
		meaningWords = filter(meaningWords, lexComplexity);

		for (TypeMeaningWord meaningWord : meaningWords) {
			cleanEscapeSym(meaningWord.getMwLexGovernments());
			classifierUtil.applyClassifiers(meaningWord, displayLang);
			setWordTypeFlags(meaningWord);
			boolean additionalDataExists = (meaningWord.getAspect() != null)
					|| (meaningWord.getMwLexValueState() != null)
					|| CollectionUtils.isNotEmpty(meaningWord.getMwLexRegisters())
					|| CollectionUtils.isNotEmpty(meaningWord.getMwLexGovernments());
			meaningWord.setAdditionalDataExists(additionalDataExists);
		}
		lexeme.setMeaningWords(meaningWords);
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
			Lexeme lexeme, String wordLang,
			LexemeMeaningTuple tuple, Map<String, Long> langOrderByMap, List<String> destinLangs, Complexity lexComplexity, String displayLang) {

		final int definitionValueOversizeLimitForMarkupBuffering = new Float(DEFINITION_OVERSIZE_LIMIT * 1.5).intValue();

		List<TypeDefinition> definitions = tuple.getDefinitions();
		List<TypeSourceLink> allDefinitionSourceLinks = tuple.getDefinitionSourceLinks();
		List<TypeSourceLink> meaningFreeformSourceLinks = tuple.getFreeformSourceLinks();

		if (CollectionUtils.isNotEmpty(definitions)) {
			definitions = filter(definitions, wordLang, destinLangs);
			definitions = filter(definitions, lexComplexity);
			applySourceLinks(definitions, allDefinitionSourceLinks);
			lexeme.setDefinitions(definitions);
			Map<String, List<TypeDefinition>> definitionsByLangUnordered = definitions.stream().collect(Collectors.groupingBy(TypeDefinition::getLang));
			Map<String, List<TypeDefinition>> definitionsByLangOrdered = composeOrderedMap(definitionsByLangUnordered, langOrderByMap);
			lexeme.setDefinitionsByLang(definitionsByLangOrdered);
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
		lexeme.setMeaningNotes(notes);
		lexeme.setMeaningNotesByLang(notesByLangOrdered);
		lexeme.setImageFiles(filteredImageFiles);
		lexeme.setMediaFiles(filteredMediaFiles);
		lexeme.setSystematicPolysemyPatterns(tuple.getSystematicPolysemyPatterns());
		lexeme.setSemanticTypes(tuple.getSemanticTypes());

		if (Complexity.SIMPLE.equals(lexComplexity)) {
			lexeme.setLearnerComments(tuple.getLearnerComments());
		}
		classifierUtil.applyClassifiers(tuple, lexeme, displayLang);
	}

	private void populateRelatedMeanings(Lexeme lexeme, String wordLang, LexemeMeaningTuple tuple, Map<String, Long> langOrderByMap, Complexity lexComplexity, String displayLang) {

		if (CollectionUtils.isNotEmpty(lexeme.getRelatedMeanings())) {
			return;
		}
		List<TypeMeaningRelation> relatedMeanings = tuple.getRelatedMeanings();
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			relatedMeanings.removeIf(relation -> StringUtils.equals(MEANING_REL_TYPE_CODE_SIMILAR, relation.getMeaningRelTypeCode()));
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
		lexeme.setRelatedMeanings(relatedMeanings);
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
			lexeme.setRelatedMeaningsByType(relatedMeaningsByType);
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

	private void setValueStateFlags(Lexeme lexeme, String wordLang) {

		DatasetType datasetType = lexeme.getDatasetType();
		if (DatasetType.TERM.equals(datasetType)) {
			List<TypeMeaningWord> meaningWords = lexeme.getMeaningWords();
			String valueStateCode = lexeme.getValueStateCode();
			if (StringUtils.equals(valueStateCode, VALUE_STATE_LEAST_PREFERRED)) {
				TypeMeaningWord preferredTermMeaningWord = meaningWords.stream()
						.filter(meaningWord -> StringUtils.equals(VALUE_STATE_MOST_PREFERRED, meaningWord.getMwLexValueStateCode()) && StringUtils.equals(wordLang, meaningWord.getLang()))
						.findFirst().orElse(null);
				lexeme.setPreferredTermMeaningWord(preferredTermMeaningWord);
			}
		}
	}

}
