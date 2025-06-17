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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.DatasetType;
import eki.common.constant.SynonymType;
import eki.common.data.Classifier;
import eki.common.data.OrderedMap;
import eki.common.service.TextDecorationService;
import eki.wordweb.data.Definition;
import eki.wordweb.data.Government;
import eki.wordweb.data.Grammar;
import eki.wordweb.data.LexemeRelation;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Meaning;
import eki.wordweb.data.MeaningImage;
import eki.wordweb.data.MeaningMedia;
import eki.wordweb.data.MeaningRelation;
import eki.wordweb.data.MeaningWord;
import eki.wordweb.data.Note;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.SourceLink;
import eki.wordweb.data.Usage;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordTypeData;

@Component
public class LexemeConversionUtil extends AbstractConversionUtil {

	@Autowired
	private TextDecorationService textDecorationService;

	public void sortLexLexemes(List<LexemeWord> lexemeWords) {

		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}
		Collections.sort(lexemeWords, Comparator
				.comparing(LexemeWord::getDatasetOrderBy)
				.thenComparing(LexemeWord::getLevel1)
				.thenComparing(LexemeWord::getLevel2));
	}

	public void sortTermLexemesDefault(List<LexemeWord> lexemeWords) {

		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}

		Collections.sort(lexemeWords, Comparator.comparing(LexemeWord::getLexemeOrderBy));
	}

	public void sortTermLexemes(List<LexemeWord> lexemeWords, Word headword) {

		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}

		Comparator<LexemeWord> datasetNameComparator = new Comparator<LexemeWord>() {

			@Override
			public int compare(LexemeWord lexemeWord1, LexemeWord lexemeWord2) {

				String datasetCode1 = lexemeWord1.getDatasetCode();
				String datasetName1 = lexemeWord1.getDatasetName();
				String datasetCode2 = lexemeWord2.getDatasetCode();
				String datasetName2 = lexemeWord2.getDatasetName();

				if (StringUtils.equals(DATASET_ESTERM, datasetCode1) && StringUtils.equals(DATASET_ESTERM, datasetCode2)) {
					return 0;
				}
				if (StringUtils.equals(DATASET_ESTERM, datasetCode1)) {
					return -1;
				}
				if (StringUtils.equals(DATASET_ESTERM, datasetCode2)) {
					return 1;
				}
				return StringUtils.compare(datasetName1, datasetName2);
			}

		};

		Comparator<LexemeWord> headwordValueStateComparator = new Comparator<LexemeWord>() {

			@Override
			public int compare(LexemeWord lexemeWord1, LexemeWord lexemeWord2) {

				if (headword == null) {
					return 0;
				}

				String headwordValue = headword.getValue();
				List<MeaningWord> meaningWords1 = lexemeWord1.getMeaningWords();
				boolean isPreferred1 = false;
				if (CollectionUtils.isNotEmpty(meaningWords1)) {
					isPreferred1 = meaningWords1.stream()
							.anyMatch(mw -> StringUtils.equals(headwordValue, mw.getValue())
									&& StringUtils.equals(VALUE_STATE_CODE_MOST_PREFERRED, mw.getMwLexemeValueStateCode()));
				}
				List<MeaningWord> meaningWords2 = lexemeWord2.getMeaningWords();
				boolean isPreferred2 = false;
				if (CollectionUtils.isNotEmpty(meaningWords2)) {
					isPreferred2 = meaningWords2.stream()
							.anyMatch(mw -> StringUtils.equals(headwordValue, mw.getValue())
									&& StringUtils.equals(VALUE_STATE_CODE_MOST_PREFERRED, mw.getMwLexemeValueStateCode()));
				}
				if (isPreferred1 && isPreferred2) {
					return 0;
				}
				if (isPreferred1 && !isPreferred2) {
					return -1;
				}
				if (!isPreferred1 && isPreferred2) {
					return 1;
				}
				return 0;
			}
		};

		Collections.sort(lexemeWords,
				datasetNameComparator
						.thenComparing(headwordValueStateComparator)
						.thenComparing(Comparator.comparing(LexemeWord::getLexemeOrderBy)));
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
				&& CollectionUtils.isEmpty(lexemeWord.getDomains())
				&& CollectionUtils.isEmpty(lexemeWord.getSourceLangFullSynonyms())
				&& CollectionUtils.isEmpty(lexemeWord.getSourceLangNearSynonyms())
				&& CollectionUtils.isEmpty(lexemeWord.getDestinLangSynonyms())
				&& CollectionUtils.isEmpty(lexemeWord.getUsages())
				&& CollectionUtils.isEmpty(lexemeWord.getRegisters())
				&& CollectionUtils.isEmpty(lexemeWord.getGovernments())
				&& CollectionUtils.isEmpty(lexemeWord.getCollocPosGroups())
				&& CollectionUtils.isEmpty(lexemeWord.getRelatedLexemes());
	}

	public List<LexemeWord> arrangeHierarchy(Long wordId, List<LexemeWord> allLexemes) {

		List<LexemeWord> lexemeWords = allLexemes.stream()
				.filter(lexeme -> lexeme.getWordId().equals(wordId))
				.collect(Collectors.toList());
		Map<Long, List<LexemeWord>> lexemesByMeanings = allLexemes.stream()
				.collect(Collectors.groupingBy(LexemeWord::getMeaningId));

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

		for (LexemeWord lexemeWord : lexemeWords) {

			populateLexeme(lexemeWord, wordLang, searchContext, displayLang);
			populateLexemeNotes(lexemeWord, wordLang, searchContext, displayLang, langOrderByMap);
			populateUsages(lexemeWord, wordLang, searchContext, displayLang);
			populateRelatedLexemes(lexemeWord, searchContext, displayLang);
		}
	}

	public void composeMeanings(
			String wordLang,
			List<LexemeWord> lexemeWords,
			List<Meaning> meanings,
			List<String> allRelatedWordValues,
			Map<String, Long> langOrderByMap,
			SearchContext searchContext,
			String displayLang) {

		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}

		Map<Long, Meaning> lexemeMeaningMap = meanings.stream().collect(Collectors.toMap(Meaning::getLexemeId, meaning -> meaning));

		for (LexemeWord lexemeWord : lexemeWords) {

			Long lexemeId = lexemeWord.getLexemeId();
			Meaning meaning = lexemeMeaningMap.get(lexemeId);
			populateMeaningWordsAndSynonyms(lexemeWord, wordLang, meaning, langOrderByMap, searchContext, displayLang);
			filterMeaningWords(lexemeWord, allRelatedWordValues);
			populateMeaning(lexemeWord, wordLang, meaning, langOrderByMap, searchContext, displayLang);
			populateRelatedMeanings(lexemeWord, wordLang, meaning, langOrderByMap, searchContext, displayLang);
			setValueStateFlags(lexemeWord, wordLang);
			setWordTypeFlags(lexemeWord);
			populateMeaningLexemes(lexemeWord, wordLang, searchContext, langOrderByMap);
		}
	}

	private void populateLexeme(
			LexemeWord lexemeWord,
			String wordLang,
			SearchContext searchContext,
			String displayLang) {

		lexemeWord.setSourceLangFullSynonyms(new ArrayList<>());
		lexemeWord.setSourceLangNearSynonyms(new ArrayList<>());
		lexemeWord.setSourceLangSynonymWordIds(new ArrayList<>());
		lexemeWord.setDestinLangSynonyms(new ArrayList<>());
		lexemeWord.setCollocPosGroups(new ArrayList<>());

		List<SourceLink> lexemeSourceLinks = lexemeWord.getLexemeSourceLinks();
		List<Grammar> grammars = lexemeWord.getGrammars();
		List<Government> governments = lexemeWord.getGovernments();

		convertUrlsToHrefs(lexemeSourceLinks);
		lexemeWord.setGrammars(filter(grammars, searchContext));
		lexemeWord.setGovernments(filter(governments, searchContext));

		classifierUtil.applyClassifiers(lexemeWord, displayLang);
		classifierUtil.applyClassifiers((WordTypeData) lexemeWord, displayLang);
	}

	private void populateLexemeNotes(
			LexemeWord lexemeWord,
			String wordLang,
			SearchContext searchContext,
			String displayLang,
			Map<String, Long> langOrderByMap) {

		List<String> destinLangs = searchContext.getDestinLangs();
		List<Note> notes = lexemeWord.getLexemeNotes();
		if (CollectionUtils.isEmpty(notes)) {
			return;
		}

		notes = filter(notes, searchContext);
		notes = filter(notes, wordLang, destinLangs);
		convertContainingSourceLinkUrlsToHrefs(notes);
		Map<String, List<Note>> notesByLangUnordered = notes.stream().collect(Collectors.groupingBy(Note::getLang));
		Map<String, List<Note>> notesByLangOrdered = composeOrderedMap(notesByLangUnordered, langOrderByMap);

		lexemeWord.setLexemeNotes(notes);
		lexemeWord.setLexemeNotesByLang(notesByLangOrdered);
	}

	private void populateUsages(
			LexemeWord lexemeWord,
			String wordLang,
			SearchContext searchContext,
			String displayLang) {

		List<String> destinLangs = searchContext.getDestinLangs();
		List<Usage> usages = lexemeWord.getUsages();
		if (CollectionUtils.isEmpty(usages)) {
			return;
		}

		usages = filter(usages, wordLang, destinLangs);
		usages = filter(usages, searchContext);
		convertContainingSourceLinkUrlsToHrefs(usages);
		lexemeWord.setUsages(usages);

		usages.forEach(usage -> {
			// based on reasonable expectation that all translations are in fact in rus
			if (!isDestinLangAlsoRus(destinLangs)) {
				usage.setUsageTranslationValues(null);
			}
			String usageLang = usage.getLang();
			boolean isPutOnSpeaker = StringUtils.equals(usageLang, DESTIN_LANG_EST);
			usage.setPutOnSpeaker(isPutOnSpeaker);
		});
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

	private void populateRelatedLexemes(LexemeWord lexemeWord, SearchContext searchContext, String displayLang) {

		List<LexemeRelation> relatedLexemes = lexemeWord.getRelatedLexemes();
		if (CollectionUtils.isEmpty(relatedLexemes)) {
			return;
		}
		relatedLexemes = filter(relatedLexemes, searchContext);
		lexemeWord.setRelatedLexemes(relatedLexemes);
		for (LexemeRelation lexemeRelation : relatedLexemes) {
			classifierUtil.applyClassifiers(lexemeRelation, displayLang);
			setWordTypeFlags(lexemeRelation);
		}
		Map<Classifier, List<LexemeRelation>> relatedLexemesByType = relatedLexemes.stream()
				.collect(Collectors.groupingBy(LexemeRelation::getLexRelType));
		lexemeWord.setRelatedLexemesByType(relatedLexemesByType);
	}

	private void populateMeaningLexemes(LexemeWord lexemeWord, String wordLang, SearchContext searchContext, Map<String, Long> langOrderByMap) {

		List<String> destinLangs = searchContext.getDestinLangs();
		Map<String, List<LexemeWord>> meaningLexemesByLangOrdered = new HashMap<>();
		List<LexemeWord> meaningLexemes = lexemeWord.getMeaningLexemes();

		if (CollectionUtils.isNotEmpty(meaningLexemes)) {

			meaningLexemes = filter(meaningLexemes, wordLang, destinLangs);
			lexemeWord.setMeaningLexemes(meaningLexemes);
			sortTermLexemesDefault(meaningLexemes);
			setValueStateFlags(meaningLexemes, wordLang);
			Map<String, List<LexemeWord>> meaningLexemesByLangUnordered = meaningLexemes.stream().collect(Collectors.groupingBy(LexemeWord::getLang));
			meaningLexemesByLangOrdered = composeOrderedMap(meaningLexemesByLangUnordered, langOrderByMap);
		}
		lexemeWord.setMeaningLexemesByLang(meaningLexemesByLangOrdered);
	}

	private void populateMeaningWordsAndSynonyms(
			LexemeWord lexemeWord,
			String wordLang,
			Meaning meaning,
			Map<String, Long> langOrderByMap,
			SearchContext searchContext,
			String displayLang) {

		List<String> destinLangs = searchContext.getDestinLangs();
		List<MeaningWord> meaningWords = lexemeWord.getMeaningWords();
		if (CollectionUtils.isNotEmpty(meaningWords)) {
			if (DatasetType.LEX.equals(lexemeWord.getDatasetType())) {
				meaningWords = meaningWords.stream()
						.filter(meaningWord -> !meaningWord.getWordId().equals(lexemeWord.getWordId()))
						.collect(Collectors.toList());
			}
			meaningWords = filter(meaningWords, wordLang, destinLangs);
			meaningWords = filter(meaningWords, searchContext);

			for (MeaningWord meaningWord : meaningWords) {

				meaningWord.setType(SynonymType.MEANING_WORD);
				classifierUtil.applyClassifiers(meaningWord, displayLang);
				setWordTypeFlags(meaningWord);
				boolean additionalDataExists = (meaningWord.getAspect() != null)
						|| (meaningWord.getMwLexemeValueState() != null)
						|| CollectionUtils.isNotEmpty(meaningWord.getWordTypes())
						|| CollectionUtils.isNotEmpty(meaningWord.getMwLexemeRegisters())
						|| CollectionUtils.isNotEmpty(meaningWord.getMwLexemeGovernments());
				meaningWord.setAdditionalDataExists(additionalDataExists);

				if (DatasetType.LEX.equals(lexemeWord.getDatasetType()) && StringUtils.equals(wordLang, meaningWord.getLang())) {
					lexemeWord.getSourceLangFullSynonyms().add(meaningWord);
					lexemeWord.getSourceLangSynonymWordIds().add(meaningWord.getWordId());
				} else {
					lexemeWord.getDestinLangSynonyms().add(meaningWord);
				}
			}
			lexemeWord.setMeaningWords(meaningWords);
		}

		List<MeaningRelation> meaningRelations = meaning.getRelatedMeanings();

		List<MeaningRelation> synMeaningRelations = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(meaningRelations)) {
			synMeaningRelations = meaningRelations.stream()
					.filter(relation -> StringUtils.equals(MEANING_REL_TYPE_CODE_SIMILAR, relation.getMeaningRelTypeCode()))
					.collect(Collectors.toList());
			synMeaningRelations = filter(synMeaningRelations, wordLang, destinLangs);
			synMeaningRelations = filter(synMeaningRelations, searchContext);
		}
		if (CollectionUtils.isNotEmpty(synMeaningRelations)) {
			Map<String, MeaningWord> synMeaningRelsMap = new OrderedMap<>();

			for (MeaningRelation meaningRelation : synMeaningRelations) {

				String wordValue = meaningRelation.getValue();
				MeaningWord synMeaningRel = synMeaningRelsMap.get(wordValue);

				if (synMeaningRel == null) {
					synMeaningRel = new MeaningWord();
					synMeaningRel.setType(SynonymType.MEANING_REL);
					synMeaningRel.setMeaningId(meaningRelation.getMeaningId());
					synMeaningRel.setMwLexemeWeight(meaningRelation.getWeight());
					synMeaningRel.setWordId(meaningRelation.getWordId());
					synMeaningRel.setValue(wordValue);
					synMeaningRel.setValuePrese(meaningRelation.getValuePrese());
					synMeaningRel.setHomonymNr(meaningRelation.getHomonymNr());
					synMeaningRel.setLang(meaningRelation.getLang());
					synMeaningRel.setWordTypeCodes(meaningRelation.getWordTypeCodes());
					synMeaningRel.setAspectCode(meaningRelation.getAspectCode());
					setWordTypeFlags(synMeaningRel);
					synMeaningRel.setMwLexemeValueStateCode(meaningRelation.getLexValueStateCode());
					synMeaningRel.setMwLexemeRegisterCodes(new ArrayList<>());
					synMeaningRel.setMwLexemeGovernmentValues(new ArrayList<>());
					synMeaningRel.setWwUnif(meaningRelation.isWwUnif());
					synMeaningRel.setWwLite(meaningRelation.isWwLite());
					synMeaningRel.setWwOd(meaningRelation.isWwOd());
					synMeaningRelsMap.put(wordValue, synMeaningRel);
				}

				List<String> lexRegisterCodes = meaningRelation.getLexRegisterCodes();
				List<String> existingLexRegisterCodes = synMeaningRel.getMwLexemeRegisterCodes();
				if (lexRegisterCodes != null) {
					lexRegisterCodes.forEach(regCode -> {
						if (!existingLexRegisterCodes.contains(regCode)) {
							existingLexRegisterCodes.add(regCode);
						}
					});
				}

				List<String> lexGovernmentValues = meaningRelation.getLexGovernmentValues();
				List<String> existingLexGovernmentValues = synMeaningRel.getMwLexemeGovernmentValues();
				if (lexGovernmentValues != null) {
					lexGovernmentValues.forEach(govValue -> {
						if (!existingLexGovernmentValues.contains(govValue)) {
							existingLexGovernmentValues.add(govValue);
						}
					});
				}

				classifierUtil.applyClassifiers(synMeaningRel, displayLang);
				boolean additionalDataExists = (synMeaningRel.getAspect() != null)
						|| (synMeaningRel.getMwLexemeValueState() != null)
						|| CollectionUtils.isNotEmpty(synMeaningRel.getWordTypes())
						|| CollectionUtils.isNotEmpty(synMeaningRel.getMwLexemeRegisters())
						|| CollectionUtils.isNotEmpty(synMeaningRel.getMwLexemeGovernments());
				synMeaningRel.setAdditionalDataExists(additionalDataExists);
			}

			for (MeaningWord meaningRelSyn : synMeaningRelsMap.values()) {
				if (DatasetType.LEX.equals(lexemeWord.getDatasetType()) && StringUtils.equals(wordLang, meaningRelSyn.getLang())) {
					lexemeWord.getSourceLangNearSynonyms().add(meaningRelSyn);
					lexemeWord.getSourceLangSynonymWordIds().add(meaningRelSyn.getWordId());
				}
			}
		}

		List<MeaningRelation> nearSynMeaningRelations = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(meaningRelations)) {

			nearSynMeaningRelations = meaningRelations.stream()
					.filter(relation -> StringUtils.equalsAny(relation.getMeaningRelTypeCode(), MEANING_REL_TYPE_CODE_NARROW, MEANING_REL_TYPE_CODE_WIDE))
					.collect(Collectors.toList());
			nearSynMeaningRelations = filter(nearSynMeaningRelations, wordLang, destinLangs);
			nearSynMeaningRelations = filter(nearSynMeaningRelations, searchContext);
		}

		List<MeaningWord> destinLangNearSynonyms = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(nearSynMeaningRelations)) {

			List<MeaningRelation> destinLangNearSynMeaningRels = nearSynMeaningRelations.stream()
					.filter(meaningRel -> !StringUtils.equals(wordLang, meaningRel.getLang()))
					.collect(Collectors.toList());

			Map<Long, List<MeaningRelation>> sourceLangNearSynMeaningRelsMap = nearSynMeaningRelations.stream()
					.filter(meaningRel -> StringUtils.equals(wordLang, meaningRel.getLang()))
					.collect(Collectors.groupingBy(MeaningRelation::getMeaningId));

			for (MeaningRelation destinLangNearSynMeaningRel : destinLangNearSynMeaningRels) {

				Long meaningId = destinLangNearSynMeaningRel.getMeaningId();

				MeaningWord destinLangNearSyn = new MeaningWord();
				destinLangNearSyn.setType(SynonymType.INEXACT_SYN_MEANING_REL);
				destinLangNearSyn.setMeaningId(meaningId);
				destinLangNearSyn.setWordId(destinLangNearSynMeaningRel.getWordId());
				destinLangNearSyn.setValue(destinLangNearSynMeaningRel.getValue());
				destinLangNearSyn.setValuePrese(destinLangNearSynMeaningRel.getValuePrese());
				destinLangNearSyn.setHomonymNr(destinLangNearSynMeaningRel.getHomonymNr());
				destinLangNearSyn.setLang(destinLangNearSynMeaningRel.getLang());
				destinLangNearSyn.setWordTypeCodes(destinLangNearSynMeaningRel.getWordTypeCodes());
				destinLangNearSyn.setAspectCode(destinLangNearSynMeaningRel.getAspectCode());
				destinLangNearSyn.setMwLexemeRegisterCodes(destinLangNearSynMeaningRel.getLexRegisterCodes());
				destinLangNearSyn.setMwLexemeGovernmentValues(destinLangNearSynMeaningRel.getLexGovernmentValues());
				destinLangNearSyn.setWwUnif(destinLangNearSynMeaningRel.isWwUnif());
				destinLangNearSyn.setWwLite(destinLangNearSynMeaningRel.isWwLite());
				destinLangNearSyn.setWwOd(destinLangNearSynMeaningRel.isWwOd());
				setWordTypeFlags(destinLangNearSyn);
				destinLangNearSyn.setMwLexemeValueStateCode(destinLangNearSynMeaningRel.getLexValueStateCode());

				classifierUtil.applyClassifiers(destinLangNearSyn, displayLang);
				boolean additionalDataExists = (destinLangNearSyn.getAspect() != null)
						|| (destinLangNearSyn.getMwLexemeValueState() != null)
						|| CollectionUtils.isNotEmpty(destinLangNearSyn.getWordTypes())
						|| CollectionUtils.isNotEmpty(destinLangNearSyn.getMwLexemeRegisters())
						|| CollectionUtils.isNotEmpty(destinLangNearSyn.getMwLexemeGovernments());
				destinLangNearSyn.setAdditionalDataExists(additionalDataExists);

				List<MeaningRelation> sourceLangNearSynMeaningRels = sourceLangNearSynMeaningRelsMap.get(meaningId);
				if (CollectionUtils.isEmpty(sourceLangNearSynMeaningRels)) {
					destinLangNearSyn.setNearSynDefinitionValue(destinLangNearSynMeaningRel.getNearSynDefinitionValue());
				}

				destinLangNearSynonyms.add(destinLangNearSyn);
			}

			for (MeaningWord destinLangNearSynonym : destinLangNearSynonyms) {
				if (DatasetType.LEX.equals(lexemeWord.getDatasetType())) {
					lexemeWord.getDestinLangSynonyms().add(destinLangNearSynonym);
				}
			}
		}

		Map<String, List<MeaningWord>> destinLangSynonymsByLangOrdered = new HashMap<>();
		List<MeaningWord> destinLangSynonyms = lexemeWord.getDestinLangSynonyms();
		if (CollectionUtils.isNotEmpty(destinLangSynonyms)) {
			Map<String, List<MeaningWord>> destinLangSynonymsByLangUnordered = destinLangSynonyms.stream()
					.collect(Collectors.groupingBy(MeaningWord::getLang));
			destinLangSynonymsByLangOrdered = composeOrderedMap(destinLangSynonymsByLangUnordered, langOrderByMap);
		}
		lexemeWord.setDestinLangSynonymsByLang(destinLangSynonymsByLangOrdered);
	}

	private void populateMeaning(
			LexemeWord lexemeWord,
			String wordLang,
			Meaning meaning,
			Map<String, Long> langOrderByMap,
			SearchContext searchContext,
			String displayLang) {

		List<String> destinLangs = searchContext.getDestinLangs();
		List<Definition> definitions = meaning.getDefinitions();

		if (CollectionUtils.isNotEmpty(definitions)) {
			definitions = filter(definitions, wordLang, destinLangs);
			definitions = filter(definitions, searchContext);
			lexemeWord.setDefinitions(definitions);
			Map<String, List<Definition>> definitionsByLangUnordered = definitions.stream().collect(Collectors.groupingBy(Definition::getLang));
			Map<String, List<Definition>> definitionsByLangOrdered = composeOrderedMap(definitionsByLangUnordered, langOrderByMap);
			lexemeWord.setDefinitionsByLang(definitionsByLangOrdered);
			convertContainingSourceLinkUrlsToHrefs(definitions);
			definitions.forEach(definition -> {
				List<Note> notes = definition.getNotes();
				List<SourceLink> sourceLinks = definition.getSourceLinks();
				boolean notesExists = CollectionUtils.isNotEmpty(notes);
				boolean sourceLinksExists = CollectionUtils.isNotEmpty(sourceLinks);
				boolean subDataExists = notesExists || sourceLinksExists;
				definition.setSubDataExists(subDataExists);
				convertContainingSourceLinkUrlsToHrefs(notes);
			});
		}

		populateMeaningNotes(meaning, lexemeWord, wordLang, searchContext, displayLang, langOrderByMap);

		List<MeaningImage> imageFiles = meaning.getMeaningImages();
		List<MeaningImage> filteredImageFiles = filter(imageFiles, searchContext);
		List<MeaningMedia> mediaFiles = meaning.getMeaningMedias();
		List<MeaningMedia> filteredMediaFiles = filter(mediaFiles, searchContext);

		lexemeWord.setMeaningManualEventOn(meaning.getMeaningManualEventOn());
		lexemeWord.setMeaningLastActivityEventOn(meaning.getMeaningLastActivityEventOn());
		lexemeWord.setMeaningImages(filteredImageFiles);
		lexemeWord.setMeaningMedias(filteredMediaFiles);
		lexemeWord.setSemanticTypes(meaning.getSemanticTypes());

		if (searchContext.isWwLite()) {
			lexemeWord.setLearnerComments(meaning.getLearnerComments());
		}
		classifierUtil.applyClassifiers(meaning, lexemeWord, displayLang);
	}

	private void populateMeaningNotes(
			Meaning meaning,
			LexemeWord lexemeWord,
			String wordLang,
			SearchContext searchContext,
			String displayLang,
			Map<String, Long> langOrderByMap) {

		List<String> destinLangs = searchContext.getDestinLangs();
		List<Note> meaningNotes = meaning.getMeaningNotes();
		if (CollectionUtils.isEmpty(meaningNotes)) {
			return;
		}

		meaningNotes = filter(meaningNotes, searchContext);
		meaningNotes = filter(meaningNotes, wordLang, destinLangs);
		convertContainingSourceLinkUrlsToHrefs(meaningNotes);
		Map<String, List<Note>> notesByLangUnordered = meaningNotes.stream().collect(Collectors.groupingBy(Note::getLang));
		Map<String, List<Note>> notesByLangOrdered = composeOrderedMap(notesByLangUnordered, langOrderByMap);

		lexemeWord.setMeaningNotes(meaningNotes);
		lexemeWord.setMeaningNotesByLang(notesByLangOrdered);
	}

	private void populateRelatedMeanings(
			LexemeWord lexemeWord,
			String wordLang,
			Meaning tuple,
			Map<String, Long> langOrderByMap,
			SearchContext searchContext,
			String displayLang) {

		if (CollectionUtils.isNotEmpty(lexemeWord.getRelatedMeanings())) {
			return;
		}
		List<MeaningRelation> relatedMeanings = tuple.getRelatedMeanings();
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			relatedMeanings = relatedMeanings.stream()
					.filter(relation -> !StringUtils.containsAny(relation.getMeaningRelTypeCode(), DISABLED_MEANING_RELATION_TYPE_CODES))
					.collect(Collectors.toList());
		}
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			relatedMeanings = filter(relatedMeanings, searchContext);
		}
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			for (MeaningRelation meaningRelation : relatedMeanings) {
				classifierUtil.applyClassifiers(meaningRelation, displayLang);
				setWordTypeFlags(meaningRelation);
			}
		}
		lexemeWord.setRelatedMeanings(relatedMeanings);
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			Map<Classifier, List<MeaningRelation>> relatedMeaningsByType = relatedMeanings.stream()
					.sorted((relation1, relation2) -> compareLangOrderby(relation1, relation2, wordLang, langOrderByMap))
					.collect(Collectors.groupingBy(MeaningRelation::getMeaningRelType,
							Collectors.groupingBy(MeaningRelation::getMeaningId, Collectors.toList())))
					.entrySet().stream()
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> {
						List<MeaningRelation> meaningRelations = new ArrayList<>();
						entry.getValue().values().forEach(list -> meaningRelations.add(list.get(0)));
						return meaningRelations;
					}));
			lexemeWord.setRelatedMeaningsByType(relatedMeaningsByType);
		}
	}

	private int compareLangOrderby(
			MeaningRelation relation1,
			MeaningRelation relation2,
			String sourceLang,
			Map<String, Long> langOrderByMap) {

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

		List<MeaningWord> meaningWords = lexemeWord.getMeaningWords();
		if (CollectionUtils.isEmpty(meaningWords)) {
			return;
		}
		if (CollectionUtils.isNotEmpty(allRelatedWordValues)) {
			meaningWords = meaningWords.stream().filter(meaningWord -> !allRelatedWordValues.contains(meaningWord.getValue())).collect(Collectors.toList());
		}
		List<LexemeRelation> relatedLexemes = lexemeWord.getRelatedLexemes();
		if (CollectionUtils.isNotEmpty(relatedLexemes)) {
			List<String> relatedLexemeWordValues = relatedLexemes.stream()
					.map(LexemeRelation::getValue)
					.distinct()
					.collect(Collectors.toList());
			meaningWords = meaningWords.stream()
					.filter(meaningWord -> !relatedLexemeWordValues.contains(meaningWord.getValue()))
					.collect(Collectors.toList());
		}
		lexemeWord.setMeaningWords(meaningWords);
	}

	private void setValueStateFlags(List<LexemeWord> lexemeWords, String wordLang) {
		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}
		for (LexemeWord lexemeWord : lexemeWords) {
			setValueStateFlags(lexemeWord, wordLang);
		}
	}

	private void setValueStateFlags(LexemeWord lexemeWord, String wordLang) {

		String valueStateCode = lexemeWord.getValueStateCode();
		List<MeaningWord> meaningWords = lexemeWord.getMeaningWords();
		DatasetType datasetType = lexemeWord.getDatasetType();

		if (StringUtils.equals(valueStateCode, VALUE_STATE_CODE_INCORRECT) && CollectionUtils.isNotEmpty(meaningWords)) {
			lexemeWord.setCorrectMeaningWord(meaningWords.get(0));
		}

		if (DatasetType.TERM.equals(datasetType)) {
			if (StringUtils.equals(valueStateCode, VALUE_STATE_CODE_LEAST_PREFERRED)) {
				MeaningWord preferredTermMeaningWord = meaningWords.stream()
						.filter(meaningWord -> StringUtils.equals(VALUE_STATE_CODE_MOST_PREFERRED, meaningWord.getMwLexemeValueStateCode())
								&& StringUtils.equals(wordLang, meaningWord.getLang()))
						.findFirst().orElse(null);
				lexemeWord.setPreferredTermMeaningWord(preferredTermMeaningWord);
			}
		}

		boolean valueStatePreferred = StringUtils.equals(valueStateCode, VALUE_STATE_CODE_MOST_PREFERRED);
		boolean valueStateWarning = StringUtils.equalsAny(valueStateCode, VALUE_STATE_CODE_LEAST_PREFERRED, VALUE_STATE_CODE_FORMER);
		boolean valueStatePriority = StringUtils.equalsAny(valueStateCode, VALUE_STATE_CODE_MOST_PREFERRED, VALUE_STATE_CODE_LEAST_PREFERRED, VALUE_STATE_CODE_FORMER);
		lexemeWord.setValueStatePreferred(valueStatePreferred);
		lexemeWord.setValueStateWarning(valueStateWarning);
		lexemeWord.setValueStatePriority(valueStatePriority);
	}

	// good code. keeping just in case
	private String getOversizeValuePreseCut(String valuePrese, int oversizeLimit) {

		final int oversizeLimitForMarkupBuffering = Double.valueOf(oversizeLimit * 1.5).intValue();
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		int valuePreseLength = StringUtils.length(valuePrese);
		boolean isOversizeValue = StringUtils.length(value) > oversizeLimit;
		boolean containsMarkup = StringUtils.contains(valuePrese, GENERIC_EKI_MARKUP_OPENING_PREFIX);
		String valuePreseCut;

		if (isOversizeValue) {
			if (containsMarkup) {
				valuePreseCut = StringUtils.substring(valuePrese, 0, oversizeLimitForMarkupBuffering);
				valuePreseCut = getAllMarkupClosedCut(valuePreseCut);
			} else {
				valuePreseCut = StringUtils.substring(valuePrese, 0, oversizeLimit);
			}

			int valuePreseCutLength = StringUtils.length(valuePreseCut);
			if (valuePreseLength > valuePreseCutLength) {
				return valuePreseCut;
			}
		}

		return null;
	}

	private String getAllMarkupClosedCut(String valuePrese) {

		boolean containsUnclosedMarkup = containsUnclosedMarkup(valuePrese);

		while (containsUnclosedMarkup) {
			valuePrese = StringUtils.substringBeforeLast(valuePrese, GENERIC_EKI_MARKUP_OPENING_PREFIX);
			containsUnclosedMarkup = containsUnclosedMarkup(valuePrese);
		}

		return valuePrese;
	}

	private boolean containsUnclosedMarkup(String valuePrese) {

		int markupOpeningCount = StringUtils.countMatches(valuePrese, GENERIC_EKI_MARKUP_OPENING_PREFIX);
		int markupClosingCount = StringUtils.countMatches(valuePrese, GENERIC_EKI_MARKUP_CLOSING_PREFIX);
		return markupOpeningCount > markupClosingCount;
	}

}
