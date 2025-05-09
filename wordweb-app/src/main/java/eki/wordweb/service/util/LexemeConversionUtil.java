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

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.constant.SynonymType;
import eki.common.data.Classifier;
import eki.common.data.OrderedMap;
import eki.common.service.TextDecorationService;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Meaning;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordTypeData;
import eki.wordweb.data.type.TypeDefinition;
import eki.wordweb.data.type.TypeFreeform;
import eki.wordweb.data.type.TypeLexemeRelation;
import eki.wordweb.data.type.TypeMeaningRelation;
import eki.wordweb.data.type.TypeMeaningWord;
import eki.wordweb.data.type.TypeMediaFile;
import eki.wordweb.data.type.TypeNote;
import eki.wordweb.data.type.TypeSourceLink;
import eki.wordweb.data.type.TypeUsage;

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

				String headwordValue = headword.getWord();
				List<TypeMeaningWord> meaningWords1 = lexemeWord1.getMeaningWords();
				boolean isPreferred1 = meaningWords1.stream()
						.anyMatch(mw -> StringUtils.equals(headwordValue, mw.getWord())
								&& StringUtils.equals(VALUE_STATE_CODE_MOST_PREFERRED, mw.getMwLexValueStateCode()));
				List<TypeMeaningWord> meaningWords2 = lexemeWord2.getMeaningWords();
				boolean isPreferred2 = meaningWords2.stream()
						.anyMatch(mw -> StringUtils.equals(headwordValue, mw.getWord())
								&& StringUtils.equals(VALUE_STATE_CODE_MOST_PREFERRED, mw.getMwLexValueStateCode()));

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
				&& CollectionUtils.isEmpty(lexemeWord.getUsages())
				&& CollectionUtils.isEmpty(lexemeWord.getSourceLangFullSynonyms())
				&& CollectionUtils.isEmpty(lexemeWord.getSourceLangNearSynonyms())
				&& CollectionUtils.isEmpty(lexemeWord.getDestinLangSynonyms())
				&& CollectionUtils.isEmpty(lexemeWord.getRelatedLexemes())
				&& CollectionUtils.isEmpty(lexemeWord.getDomains())
				&& CollectionUtils.isEmpty(lexemeWord.getCollocPosGroups());
		//not much of a content?
		//&& CollectionUtils.isEmpty(lexeme.getRegisters())
		//&& CollectionUtils.isEmpty(lexeme.getGovernments())
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

		List<String> destinLangs = searchContext.getDestinLangs();
		Complexity lexComplexity = searchContext.getLexComplexity();

		for (LexemeWord lexemeWord : lexemeWords) {

			populateLexeme(lexemeWord, wordLang, destinLangs, lexComplexity, displayLang);
			populateLexemeNotes(lexemeWord, wordLang, destinLangs, lexComplexity, displayLang, langOrderByMap);
			populateUsages(lexemeWord, wordLang, destinLangs, lexComplexity, displayLang);
			populateRelatedLexemes(lexemeWord, lexComplexity, displayLang);
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

		List<String> destinLangs = searchContext.getDestinLangs();
		Complexity lexComplexity = searchContext.getLexComplexity();

		for (LexemeWord lexemeWord : lexemeWords) {

			Long lexemeId = lexemeWord.getLexemeId();
			Meaning meaning = lexemeMeaningMap.get(lexemeId);
			populateMeaningWordsAndSynonyms(lexemeWord, wordLang, meaning, destinLangs, langOrderByMap, lexComplexity, displayLang);
			filterMeaningWords(lexemeWord, allRelatedWordValues);
			populateMeaning(lexemeWord, wordLang, meaning, destinLangs, langOrderByMap, lexComplexity, displayLang);
			populateRelatedMeanings(lexemeWord, wordLang, meaning, langOrderByMap, lexComplexity, displayLang);
			setValueStateFlags(lexemeWord, wordLang);
			setWordTypeFlags(lexemeWord);
			populateMeaningLexemes(lexemeWord, wordLang, destinLangs, langOrderByMap);
		}
	}

	private void populateLexeme(
			LexemeWord lexemeWord,
			String wordLang,
			List<String> destinLangs,
			Complexity lexComplexity,
			String displayLang) {

		lexemeWord.setSourceLangFullSynonyms(new ArrayList<>());
		lexemeWord.setSourceLangNearSynonyms(new ArrayList<>());
		lexemeWord.setSourceLangSynonymWordIds(new ArrayList<>());
		lexemeWord.setDestinLangSynonyms(new ArrayList<>());
		lexemeWord.setCollocPosGroups(new ArrayList<>());

		List<TypeSourceLink> lexemeSourceLinks = lexemeWord.getLexemeSourceLinks();
		List<TypeFreeform> grammars = lexemeWord.getGrammars();
		List<TypeFreeform> governments = lexemeWord.getGovernments();

		convertUrlsToHrefs(lexemeSourceLinks);
		lexemeWord.setGrammars(filter(grammars, lexComplexity));
		lexemeWord.setGovernments(filter(governments, lexComplexity));

		classifierUtil.applyClassifiers(lexemeWord, displayLang);
		classifierUtil.applyClassifiers((WordTypeData) lexemeWord, displayLang);
	}

	private void populateLexemeNotes(
			LexemeWord lexemeWord,
			String wordLang,
			List<String> destinLangs,
			Complexity lexComplexity,
			String displayLang,
			Map<String, Long> langOrderByMap) {

		List<TypeNote> notes = lexemeWord.getLexemeNotes();
		if (CollectionUtils.isEmpty(notes)) {
			return;
		}

		notes = filter(notes, lexComplexity);
		notes = filter(notes, wordLang, destinLangs);
		convertContainingSourceLinkUrlsToHrefs(notes);
		Map<String, List<TypeNote>> notesByLangUnordered = notes.stream().collect(Collectors.groupingBy(TypeNote::getLang));
		Map<String, List<TypeNote>> notesByLangOrdered = composeOrderedMap(notesByLangUnordered, langOrderByMap);

		lexemeWord.setLexemeNotes(notes);
		lexemeWord.setLexemeNotesByLang(notesByLangOrdered);

		notes.forEach(note -> {
			String valuePrese = note.getValuePrese();
			String valuePreseCut = getOversizeValuePreseCut(valuePrese, NOTE_OVERSIZE_LIMIT);
			note.setValuePreseCut(valuePreseCut);
		});
	}

	private void populateMeaningNotes(
			Meaning meaning,
			LexemeWord lexemeWord,
			String wordLang,
			List<String> destinLangs,
			Complexity lexComplexity,
			String displayLang,
			Map<String, Long> langOrderByMap) {

		List<TypeNote> notes = meaning.getNotes();
		if (CollectionUtils.isEmpty(notes)) {
			return;
		}

		notes = filter(notes, lexComplexity);
		notes = filter(notes, wordLang, destinLangs);
		convertContainingSourceLinkUrlsToHrefs(notes);
		Map<String, List<TypeNote>> notesByLangUnordered = notes.stream().collect(Collectors.groupingBy(TypeNote::getLang));
		Map<String, List<TypeNote>> notesByLangOrdered = composeOrderedMap(notesByLangUnordered, langOrderByMap);

		lexemeWord.setMeaningNotes(notes);
		lexemeWord.setMeaningNotesByLang(notesByLangOrdered);

		notes.forEach(note -> {
			String valuePrese = note.getValuePrese();
			String valuePreseCut = getOversizeValuePreseCut(valuePrese, NOTE_OVERSIZE_LIMIT);
			note.setValuePreseCut(valuePreseCut);
		});
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

		usages = filter(usages, wordLang, destinLangs);
		usages = filter(usages, lexComplexity);
		convertContainingSourceLinkUrlsToHrefs(usages);
		lexemeWord.setUsages(usages);

		usages.forEach(usage -> {
			// based on reasonable expectation that all translations are in fact in rus
			if (!isDestinLangAlsoRus(destinLangs)) {
				usage.setUsageTranslations(null);
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
		Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType = relatedLexemes.stream()
				.collect(Collectors.groupingBy(TypeLexemeRelation::getLexRelType));
		lexemeWord.setRelatedLexemesByType(relatedLexemesByType);
	}

	private void populateMeaningLexemes(LexemeWord lexemeWord, String wordLang, List<String> destinLangs, Map<String, Long> langOrderByMap) {

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
			List<String> destinLangs,
			Map<String, Long> langOrderByMap,
			Complexity lexComplexity,
			String displayLang) {

		List<TypeMeaningWord> meaningWords = lexemeWord.getMeaningWords();
		if (CollectionUtils.isNotEmpty(meaningWords)) {
			if (DatasetType.LEX.equals(lexemeWord.getDatasetType())) {
				meaningWords = meaningWords.stream()
						.filter(meaningWord -> !meaningWord.getWordId().equals(lexemeWord.getWordId()))
						.collect(Collectors.toList());
			}
			meaningWords = filter(meaningWords, wordLang, destinLangs);
			meaningWords = filter(meaningWords, lexComplexity);

			for (TypeMeaningWord meaningWord : meaningWords) {

				meaningWord.setType(SynonymType.MEANING_WORD);
				classifierUtil.applyClassifiers(meaningWord, displayLang);
				setWordTypeFlags(meaningWord);
				boolean additionalDataExists = (meaningWord.getAspect() != null)
						|| (meaningWord.getMwLexValueState() != null)
						|| CollectionUtils.isNotEmpty(meaningWord.getWordTypes())
						|| CollectionUtils.isNotEmpty(meaningWord.getMwLexRegisters())
						|| CollectionUtils.isNotEmpty(meaningWord.getMwLexGovernments());
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

		List<TypeMeaningRelation> meaningRelations = meaning.getRelatedMeanings();

		List<TypeMeaningRelation> synMeaningRelations = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(meaningRelations)) {
			synMeaningRelations = meaningRelations.stream()
					.filter(relation -> StringUtils.equals(MEANING_REL_TYPE_CODE_SIMILAR, relation.getMeaningRelTypeCode()))
					.collect(Collectors.toList());
			synMeaningRelations = filter(synMeaningRelations, wordLang, destinLangs);
			synMeaningRelations = filter(synMeaningRelations, lexComplexity);
		}
		if (CollectionUtils.isNotEmpty(synMeaningRelations)) {
			Map<String, TypeMeaningWord> synMeaningRelsMap = new OrderedMap<>();

			for (TypeMeaningRelation meaningRelation : synMeaningRelations) {

				String wordValue = meaningRelation.getWord();
				TypeMeaningWord synMeaningRel = synMeaningRelsMap.get(wordValue);

				if (synMeaningRel == null) {
					synMeaningRel = new TypeMeaningWord();
					synMeaningRel.setType(SynonymType.MEANING_REL);
					synMeaningRel.setMeaningId(meaningRelation.getMeaningId());
					synMeaningRel.setMwLexWeight(meaningRelation.getWeight());
					synMeaningRel.setWordId(meaningRelation.getWordId());
					synMeaningRel.setWord(wordValue);
					synMeaningRel.setWordPrese(meaningRelation.getWordPrese());
					synMeaningRel.setHomonymNr(meaningRelation.getHomonymNr());
					synMeaningRel.setLang(meaningRelation.getLang());
					synMeaningRel.setWordTypeCodes(meaningRelation.getWordTypeCodes());
					synMeaningRel.setAspectCode(meaningRelation.getAspectCode());
					setWordTypeFlags(synMeaningRel);
					synMeaningRel.setMwLexComplexity(meaningRelation.getComplexity());
					if (CollectionUtils.isNotEmpty(meaningRelation.getLexValueStateCodes())) {
						synMeaningRel.setMwLexValueStateCode(meaningRelation.getLexValueStateCodes().get(0));
					}
					synMeaningRel.setMwLexRegisterCodes(new ArrayList<>());
					synMeaningRel.setMwLexGovernmentValues(new ArrayList<>());
					synMeaningRelsMap.put(wordValue, synMeaningRel);
				}

				List<String> lexRegisterCodes = meaningRelation.getLexRegisterCodes();
				List<String> existingLexRegisterCodes = synMeaningRel.getMwLexRegisterCodes();
				if (lexRegisterCodes != null) {
					lexRegisterCodes.forEach(regCode -> {
						if (!existingLexRegisterCodes.contains(regCode)) {
							existingLexRegisterCodes.add(regCode);
						}
					});
				}

				List<String> lexGovernmentValues = meaningRelation.getLexGovernmentValues();
				List<String> existingLexGovernmentValues = synMeaningRel.getMwLexGovernmentValues();
				if (lexGovernmentValues != null) {
					lexGovernmentValues.forEach(govValue -> {
						if (!existingLexGovernmentValues.contains(govValue)) {
							existingLexGovernmentValues.add(govValue);
						}
					});
				}

				classifierUtil.applyClassifiers(synMeaningRel, displayLang);
				boolean additionalDataExists = (synMeaningRel.getAspect() != null)
						|| (synMeaningRel.getMwLexValueState() != null)
						|| CollectionUtils.isNotEmpty(synMeaningRel.getWordTypes())
						|| CollectionUtils.isNotEmpty(synMeaningRel.getMwLexRegisters())
						|| CollectionUtils.isNotEmpty(synMeaningRel.getMwLexGovernments());
				synMeaningRel.setAdditionalDataExists(additionalDataExists);
			}

			for (TypeMeaningWord meaningRelSyn : synMeaningRelsMap.values()) {
				if (DatasetType.LEX.equals(lexemeWord.getDatasetType()) && StringUtils.equals(wordLang, meaningRelSyn.getLang())) {
					lexemeWord.getSourceLangNearSynonyms().add(meaningRelSyn);
					lexemeWord.getSourceLangSynonymWordIds().add(meaningRelSyn.getWordId());
				}
			}
		}

		List<TypeMeaningRelation> nearSynMeaningRelations = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(meaningRelations)) {

			nearSynMeaningRelations = meaningRelations.stream()
					.filter(relation -> StringUtils.equalsAny(relation.getMeaningRelTypeCode(), MEANING_REL_TYPE_CODE_NARROW, MEANING_REL_TYPE_CODE_WIDE))
					.collect(Collectors.toList());
			nearSynMeaningRelations = filter(nearSynMeaningRelations, wordLang, destinLangs);
			nearSynMeaningRelations = filter(nearSynMeaningRelations, lexComplexity);
		}

		List<TypeMeaningWord> destinLangNearSynonyms = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(nearSynMeaningRelations)) {

			List<TypeMeaningRelation> destinLangNearSynMeaningRels = nearSynMeaningRelations.stream()
					.filter(meaningRel -> !StringUtils.equals(wordLang, meaningRel.getLang()))
					.collect(Collectors.toList());

			Map<Long, List<TypeMeaningRelation>> sourceLangNearSynMeaningRelsMap = nearSynMeaningRelations.stream()
					.filter(meaningRel -> StringUtils.equals(wordLang, meaningRel.getLang()))
					.collect(Collectors.groupingBy(TypeMeaningRelation::getMeaningId));

			for (TypeMeaningRelation destinLangNearSynMeaningRel : destinLangNearSynMeaningRels) {

				Long meaningId = destinLangNearSynMeaningRel.getMeaningId();

				TypeMeaningWord destinLangNearSyn = new TypeMeaningWord();
				destinLangNearSyn.setType(SynonymType.INEXACT_SYN_MEANING_REL);
				destinLangNearSyn.setMeaningId(meaningId);
				destinLangNearSyn.setWordId(destinLangNearSynMeaningRel.getWordId());
				destinLangNearSyn.setWord(destinLangNearSynMeaningRel.getWord());
				destinLangNearSyn.setWordPrese(destinLangNearSynMeaningRel.getWordPrese());
				destinLangNearSyn.setHomonymNr(destinLangNearSynMeaningRel.getHomonymNr());
				destinLangNearSyn.setLang(destinLangNearSynMeaningRel.getLang());
				destinLangNearSyn.setWordTypeCodes(destinLangNearSynMeaningRel.getWordTypeCodes());
				destinLangNearSyn.setAspectCode(destinLangNearSynMeaningRel.getAspectCode());
				destinLangNearSyn.setMwLexComplexity(destinLangNearSynMeaningRel.getComplexity());
				destinLangNearSyn.setMwLexRegisterCodes(destinLangNearSynMeaningRel.getLexRegisterCodes());
				destinLangNearSyn.setMwLexGovernmentValues(destinLangNearSynMeaningRel.getLexGovernmentValues());
				setWordTypeFlags(destinLangNearSyn);
				if (CollectionUtils.isNotEmpty(destinLangNearSynMeaningRel.getLexValueStateCodes())) {
					destinLangNearSyn.setMwLexValueStateCode(destinLangNearSynMeaningRel.getLexValueStateCodes().get(0));
				}

				classifierUtil.applyClassifiers(destinLangNearSyn, displayLang);
				boolean additionalDataExists = (destinLangNearSyn.getAspect() != null)
						|| (destinLangNearSyn.getMwLexValueState() != null)
						|| CollectionUtils.isNotEmpty(destinLangNearSyn.getWordTypes())
						|| CollectionUtils.isNotEmpty(destinLangNearSyn.getMwLexRegisters())
						|| CollectionUtils.isNotEmpty(destinLangNearSyn.getMwLexGovernments());
				destinLangNearSyn.setAdditionalDataExists(additionalDataExists);

				List<TypeMeaningRelation> sourceLangNearSynMeaningRels = sourceLangNearSynMeaningRelsMap.get(meaningId);
				if (CollectionUtils.isEmpty(sourceLangNearSynMeaningRels)) {
					destinLangNearSyn.setInexactSynMeaningDefinition(destinLangNearSynMeaningRel.getInexactSynDef());
				}

				destinLangNearSynonyms.add(destinLangNearSyn);
			}

			for (TypeMeaningWord destinLangNearSynonym : destinLangNearSynonyms) {
				if (DatasetType.LEX.equals(lexemeWord.getDatasetType())) {
					lexemeWord.getDestinLangSynonyms().add(destinLangNearSynonym);
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

	private void populateMeaning(
			LexemeWord lexemeWord,
			String wordLang,
			Meaning meaning,
			List<String> destinLangs,
			Map<String, Long> langOrderByMap,
			Complexity lexComplexity,
			String displayLang) {

		List<TypeDefinition> definitions = meaning.getDefinitions();

		if (CollectionUtils.isNotEmpty(definitions)) {
			definitions = filter(definitions, wordLang, destinLangs);
			definitions = filter(definitions, lexComplexity);
			lexemeWord.setDefinitions(definitions);
			Map<String, List<TypeDefinition>> definitionsByLangUnordered = definitions.stream().collect(Collectors.groupingBy(TypeDefinition::getLang));
			Map<String, List<TypeDefinition>> definitionsByLangOrdered = composeOrderedMap(definitionsByLangUnordered, langOrderByMap);
			lexemeWord.setDefinitionsByLang(definitionsByLangOrdered);
			convertContainingSourceLinkUrlsToHrefs(definitions);
			definitions.forEach(definition -> {
				List<TypeNote> notes = definition.getNotes();
				List<TypeSourceLink> sourceLinks = definition.getSourceLinks();
				String valuePrese = definition.getValuePrese();
				String valuePreseCut = getOversizeValuePreseCut(valuePrese, DEFINITION_OVERSIZE_LIMIT);
				boolean notesExists = CollectionUtils.isNotEmpty(notes);
				boolean sourceLinksExists = CollectionUtils.isNotEmpty(sourceLinks);
				boolean subDataExists = notesExists || sourceLinksExists;
				definition.setSubDataExists(subDataExists);
				definition.setValuePreseCut(valuePreseCut);
				convertContainingSourceLinkUrlsToHrefs(notes);
			});
		}

		populateMeaningNotes(meaning, lexemeWord, wordLang, destinLangs, lexComplexity, displayLang, langOrderByMap);

		List<TypeMediaFile> imageFiles = meaning.getMeaningImages();
		List<TypeMediaFile> filteredImageFiles = filter(imageFiles, lexComplexity);
		List<TypeMediaFile> mediaFiles = meaning.getMediaFiles();
		List<TypeMediaFile> filteredMediaFiles = filter(mediaFiles, lexComplexity);

		lexemeWord.setMeaningManualEventOn(meaning.getMeaningManualEventOn());
		lexemeWord.setMeaningLastActivityEventOn(meaning.getMeaningLastActivityEventOn());
		lexemeWord.setMeaningImages(filteredImageFiles);
		lexemeWord.setMediaFiles(filteredMediaFiles);
		lexemeWord.setSystematicPolysemyPatterns(meaning.getSystematicPolysemyPatterns());
		lexemeWord.setSemanticTypes(meaning.getSemanticTypes());

		if (Complexity.SIMPLE.equals(lexComplexity)) {
			lexemeWord.setLearnerComments(meaning.getLearnerComments());
		}
		classifierUtil.applyClassifiers(meaning, lexemeWord, displayLang);
	}

	private void populateRelatedMeanings(
			LexemeWord lexemeWord,
			String wordLang,
			Meaning tuple,
			Map<String, Long> langOrderByMap,
			Complexity lexComplexity,
			String displayLang) {

		if (CollectionUtils.isNotEmpty(lexemeWord.getRelatedMeanings())) {
			return;
		}
		List<TypeMeaningRelation> relatedMeanings = tuple.getRelatedMeanings();
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			relatedMeanings = relatedMeanings.stream()
					.filter(relation -> !StringUtils.containsAny(relation.getMeaningRelTypeCode(), DISABLED_MEANING_RELATION_TYPE_CODES))
					.collect(Collectors.toList());
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

	private int compareLangOrderby(
			TypeMeaningRelation relation1,
			TypeMeaningRelation relation2,
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
		List<TypeMeaningWord> meaningWords = lexemeWord.getMeaningWords();
		DatasetType datasetType = lexemeWord.getDatasetType();

		if (StringUtils.equals(valueStateCode, VALUE_STATE_CODE_INCORRECT) && CollectionUtils.isNotEmpty(meaningWords)) {
			lexemeWord.setCorrectMeaningWord(meaningWords.get(0));
		}

		if (DatasetType.TERM.equals(datasetType)) {
			if (StringUtils.equals(valueStateCode, VALUE_STATE_CODE_LEAST_PREFERRED)) {
				TypeMeaningWord preferredTermMeaningWord = meaningWords.stream()
						.filter(meaningWord -> StringUtils.equals(VALUE_STATE_CODE_MOST_PREFERRED, meaningWord.getMwLexValueStateCode()) && StringUtils.equals(wordLang, meaningWord.getLang()))
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
