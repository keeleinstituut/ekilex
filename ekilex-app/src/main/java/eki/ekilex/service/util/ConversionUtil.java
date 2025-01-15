package eki.ekilex.service.util;

import static java.util.stream.Collectors.groupingBy;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.SynonymType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Form;
import eki.ekilex.data.InexactSynonym;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.LexemeTag;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.Note;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.SourcePropertyTuple;
import eki.ekilex.data.SynWord;
import eki.ekilex.data.Synonym;
import eki.ekilex.data.SynonymLangGroup;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.TypeTermMeaningWord;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymRel;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordGroup;
import eki.ekilex.data.WordRelation;
import eki.ekilex.data.WordRelationDetails;

@Component
public class ConversionUtil implements GlobalConstant {

	@Autowired
	private MessageSource messageSource;

	public void cleanTermMeanings(List<TermMeaning> termMeanings) {

		termMeanings.forEach(termMeaning -> {
			List<TypeTermMeaningWord> meaningWords = termMeaning.getMeaningWords().stream()
					.filter(meaningWord -> meaningWord.getWordId() != null)
					.distinct()
					.collect(Collectors.toList());
			boolean meaningWordsExist = CollectionUtils.isNotEmpty(meaningWords);
			termMeaning.setMeaningWords(meaningWords);
			termMeaning.setMeaningWordsExist(meaningWordsExist);
		});
	}

	public Classifier classifierFromIdString(String idString) {
		JsonParser jsonParser = JsonParserFactory.getJsonParser();
		Map<String, Object> memberMap = jsonParser.parseMap(idString);
		Classifier classifier = new Classifier();
		classifier.setName((String) memberMap.get("name"));
		classifier.setCode((String) memberMap.get("code"));
		classifier.setOrigin((String) memberMap.get("origin"));
		return classifier;
	}

	public List<Paradigm> composeParadigms(List<ParadigmFormTuple> paradigmFormTuples) {

		List<Paradigm> paradigms = new ArrayList<>();

		Map<Long, Paradigm> paradigmsMap = new HashMap<>();
		List<Form> forms;

		for (ParadigmFormTuple tuple : paradigmFormTuples) {

			Long paradigmId = tuple.getParadigmId();

			Paradigm paradigm = paradigmsMap.get(paradigmId);
			if (paradigm == null) {
				forms = new ArrayList<>();
				paradigm = new Paradigm();
				paradigm.setParadigmId(paradigmId);
				paradigm.setComment(tuple.getParadigmComment());
				paradigm.setInflectionType(tuple.getInflectionType());
				paradigm.setInflectionTypeNr(tuple.getInflectionTypeNr());
				paradigm.setWordClass(tuple.getWordClass());
				paradigm.setForms(forms);
				paradigmsMap.put(paradigmId, paradigm);
				paradigms.add(paradigm);
			} else {
				forms = paradigm.getForms();
			}
			Form form = new Form();
			form.setId(tuple.getFormId());
			form.setValue(tuple.getFormValue());
			form.setValuePrese(tuple.getFormValuePrese());
			form.setComponents(tuple.getComponents());
			form.setDisplayForm(tuple.getDisplayForm());
			form.setMorphCode(tuple.getMorphCode());
			form.setMorphValue(tuple.getMorphValue());
			form.setMorphFrequency(tuple.getMorphFrequency());
			form.setFormFrequency(tuple.getFormFrequency());
			forms.add(form);
		}
		flagFormMorphCodes(paradigms);
		flagFormsExist(paradigms);

		return paradigms;
	}

	private void flagFormMorphCodes(List<Paradigm> paradigms) {

		for (Paradigm paradigm : paradigms) {
			List<Form> forms = paradigm.getForms();
			String previousFormMorphCode = null;
			for (Form form : forms) {
				boolean displayMorphCode = !StringUtils.equals(previousFormMorphCode, form.getMorphCode());
				form.setDisplayMorphCode(displayMorphCode);
				previousFormMorphCode = form.getMorphCode();
			}
		}
	}

	private void flagFormsExist(List<Paradigm> paradigms) {

		for (Paradigm paradigm : paradigms) {
			List<Form> forms = paradigm.getForms();
			boolean formsExist = CollectionUtils.isNotEmpty(forms);
			paradigm.setFormsExist(formsExist);
		}
	}

	public List<LexemeLangGroup> composeLexemeLangGroups(List<Lexeme> lexemes, List<ClassifierSelect> languagesOrder) {

		List<String> langCodeOrder;
		List<String> selectedLangCodes;
		if (CollectionUtils.isEmpty(languagesOrder)) {
			langCodeOrder = lexemes.stream()
					.map(lexeme -> lexeme.getLexemeWord().getLang())
					.distinct()
					.collect(Collectors.toList());
			selectedLangCodes = new ArrayList<>();
		} else {
			langCodeOrder = languagesOrder.stream()
					.map(Classifier::getCode)
					.collect(Collectors.toList());
			selectedLangCodes = languagesOrder.stream()
					.filter(ClassifierSelect::isSelected)
					.map(ClassifierSelect::getCode)
					.collect(Collectors.toList());
		}
		List<LexemeLangGroup> lexemeLangGroups = new ArrayList<>();
		Map<String, LexemeLangGroup> lexemeLangGroupMap = new HashMap<>();
		List<Lexeme> lexemesOrderBy = lexemes.stream().sorted(Comparator.comparing(Lexeme::getOrderBy)).collect(Collectors.toList());

		for (Lexeme lexeme : lexemesOrderBy) {

			Word word = lexeme.getLexemeWord();
			String lang = word.getLang();
			LexemeLangGroup lexemeLangGroup = lexemeLangGroupMap.get(lang);
			if (lexemeLangGroup == null) {
				boolean isSelected = selectedLangCodes.contains(lang);
				lexemeLangGroup = new LexemeLangGroup();
				lexemeLangGroup.setLang(lang);
				lexemeLangGroup.setSelected(isSelected);
				lexemeLangGroup.setLexemes(new ArrayList<>());
				lexemeLangGroupMap.put(lang, lexemeLangGroup);
				lexemeLangGroups.add(lexemeLangGroup);
			}
			lexemeLangGroup.getLexemes().add(lexeme);
		}

		lexemeLangGroups.sort((LexemeLangGroup gr1, LexemeLangGroup gr2) -> {
			String lang1 = gr1.getLang();
			String lang2 = gr2.getLang();
			int langOrder1 = langCodeOrder.indexOf(lang1);
			int langOrder2 = langCodeOrder.indexOf(lang2);
			return langOrder1 - langOrder2;
		});

		return lexemeLangGroups;
	}

	public List<NoteLangGroup> composeNoteLangGroups(List<? extends Note> notes, List<ClassifierSelect> languagesOrder) {

		if (CollectionUtils.isEmpty(notes)) {
			return Collections.emptyList();
		}

		List<String> langCodeOrder;
		List<String> selectedLangCodes;
		if (CollectionUtils.isEmpty(languagesOrder)) {
			langCodeOrder = notes.stream().map(Note::getLang).distinct().collect(Collectors.toList());
			selectedLangCodes = new ArrayList<>();
		} else {
			langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
			selectedLangCodes = languagesOrder.stream().filter(ClassifierSelect::isSelected).map(ClassifierSelect::getCode).collect(Collectors.toList());
		}
		List<NoteLangGroup> noteLangGroups = new ArrayList<>();
		Map<String, NoteLangGroup> noteLangGroupMap = new HashMap<>();
		List<Note> notesOrderBy = notes.stream().sorted(Comparator.comparing(Note::getOrderBy)).collect(Collectors.toList());

		for (Note note : notesOrderBy) {
			String lang = note.getLang();
			NoteLangGroup noteLangGroup = noteLangGroupMap.get(lang);
			if (noteLangGroup == null) {
				boolean isSelected = selectedLangCodes.contains(lang);
				noteLangGroup = new NoteLangGroup();
				noteLangGroup.setLang(lang);
				noteLangGroup.setSelected(isSelected);
				noteLangGroup.setNotes(new ArrayList<>());
				noteLangGroupMap.put(lang, noteLangGroup);
				noteLangGroups.add(noteLangGroup);
			}
			noteLangGroup.getNotes().add(note);
		}

		noteLangGroups.sort((NoteLangGroup gr1, NoteLangGroup gr2) -> {
			String lang1 = gr1.getLang();
			String lang2 = gr2.getLang();
			int langOrder1 = langCodeOrder.indexOf(lang1);
			int langOrder2 = langCodeOrder.indexOf(lang2);
			return langOrder1 - langOrder2;
		});

		return noteLangGroups;
	}

	public List<DefinitionLangGroup> composeMeaningDefinitionLangGroups(List<Definition> definitions, List<ClassifierSelect> languagesOrder) {

		List<String> langCodeOrder;
		List<String> selectedLangCodes;
		if (CollectionUtils.isEmpty(languagesOrder)) {
			langCodeOrder = definitions.stream().map(Definition::getLang).distinct().collect(Collectors.toList());
			selectedLangCodes = new ArrayList<>();
		} else {
			langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
			selectedLangCodes = languagesOrder.stream().filter(ClassifierSelect::isSelected).map(ClassifierSelect::getCode).collect(Collectors.toList());
		}
		List<DefinitionLangGroup> definitionLangGroups = new ArrayList<>();
		Map<String, DefinitionLangGroup> definitionLangGroupMap = new HashMap<>();
		List<Definition> definitionsOrderBy = definitions.stream().sorted(Comparator.comparing(Definition::getOrderBy)).collect(Collectors.toList());

		for (Definition definition : definitionsOrderBy) {
			String lang = definition.getLang();
			DefinitionLangGroup definitionLangGroup = definitionLangGroupMap.get(lang);
			if (definitionLangGroup == null) {
				boolean isSelected = selectedLangCodes.contains(lang);
				definitionLangGroup = new DefinitionLangGroup();
				definitionLangGroup.setLang(lang);
				definitionLangGroup.setSelected(isSelected);
				definitionLangGroup.setDefinitions(new ArrayList<>());
				definitionLangGroupMap.put(lang, definitionLangGroup);
				definitionLangGroups.add(definitionLangGroup);
			}
			definitionLangGroup.getDefinitions().add(definition);
		}

		definitionLangGroups.sort((DefinitionLangGroup gr1, DefinitionLangGroup gr2) -> {
			String lang1 = gr1.getLang();
			String lang2 = gr2.getLang();
			int langOrder1 = langCodeOrder.indexOf(lang1);
			int langOrder2 = langCodeOrder.indexOf(lang2);
			return langOrder1 - langOrder2;
		});

		return definitionLangGroups;
	}

	public List<WordGroup> composeWordGroups(List<WordRelation> groupsMembers, List<Classifier> allAspects) {

		List<String> aspectCodeOrder;
		if (CollectionUtils.isEmpty(allAspects)) {
			aspectCodeOrder = new ArrayList<>();
		} else {
			aspectCodeOrder = allAspects.stream().map(Classifier::getCode).collect(Collectors.toList());
		}

		List<WordGroup> groups = new ArrayList<>();
		Map<Long, List<WordRelation>> memberGroups = groupsMembers.stream().collect(groupingBy(WordRelation::getGroupId));
		for (Long groupId : memberGroups.keySet()) {
			List<WordRelation> groupMembers = memberGroups.get(groupId);
			WordRelation firstGroupMember = groupMembers.get(0);
			String groupWordRelTypeCode = firstGroupMember.getGroupWordRelTypeCode();
			if (StringUtils.equals(WORD_REL_TYPE_CODE_ASCPECTS, groupWordRelTypeCode)) {
				groupMembers.sort((WordRelation rel1, WordRelation rel2) -> {
					String aspectCode1 = rel1.getWordAspectCode();
					String aspectCode2 = rel2.getWordAspectCode();
					if (StringUtils.isBlank(aspectCode1) || StringUtils.isBlank(aspectCode2)) {
						return 0;
					}
					int aspectOrder1 = aspectCodeOrder.indexOf(aspectCode1);
					int aspectOrder2 = aspectCodeOrder.indexOf(aspectCode2);
					return aspectOrder1 - aspectOrder2;
				});
			}
			WordGroup wordGroup = new WordGroup();
			wordGroup.setId(groupId);
			wordGroup.setMembers(groupMembers);
			wordGroup.setGroupTypeLabel(firstGroupMember.getRelTypeLabel());
			groups.add(wordGroup);
		}
		return groups;
	}

	public WordRelationDetails composeWordRelationDetails(List<WordRelation> wordRelations, List<WordGroup> wordGroups, String wordLang, List<Classifier> allWordRelationTypes) {

		WordRelationDetails wordRelationDetails = new WordRelationDetails();
		wordRelationDetails.setWordGroups(wordGroups);
		wordRelationDetails.setPrimaryWordRelationGroups(new ArrayList<>());
		wordRelationDetails.setSecondaryWordRelationGroups(new ArrayList<>());

		Map<String, List<WordRelation>> wordRelationsMap = wordRelations.stream().collect(groupingBy(WordRelation::getRelTypeCode));

		for (Classifier wordRelationType : allWordRelationTypes) {
			String relTypeCode = wordRelationType.getCode();
			String relTypeLabel = wordRelationType.getValue();
			List<WordRelation> relatedWordsOfType = wordRelationsMap.get(relTypeCode);
			List<WordGroup> wordRelationGroups;
			if (ArrayUtils.contains(PRIMARY_WORD_REL_TYPE_CODES, relTypeCode)) {
				wordRelationGroups = wordRelationDetails.getPrimaryWordRelationGroups();
				handleWordRelType(relTypeCode, relTypeLabel, relatedWordsOfType, wordRelationGroups, wordLang);
			} else if (CollectionUtils.isNotEmpty(relatedWordsOfType)) {
				wordRelationGroups = wordRelationDetails.getSecondaryWordRelationGroups();
				handleWordRelType(relTypeCode, relTypeLabel, relatedWordsOfType, wordRelationGroups, wordLang);
			}
		}

		boolean groupRelationExists = CollectionUtils.isNotEmpty(wordRelationDetails.getSecondaryWordRelationGroups())
				|| CollectionUtils.isNotEmpty(wordRelationDetails.getWordGroups());
		wordRelationDetails.setGroupRelationExists(groupRelationExists);

		return wordRelationDetails;
	}

	private void handleWordRelType(String relTypeCode, String relTypeLabel, List<WordRelation> wordRelations, List<WordGroup> wordRelationGroups, String wordLang) {

		WordGroup wordRelationGroup;
		Locale locale = LocaleContextHolder.getLocale();

		if (StringUtils.equals(WORD_REL_TYPE_CODE_RAW, relTypeCode)) {

			String synLabel = messageSource.getMessage("classifier.word_rel_type.raw.syn", new Object[0], locale);
			String matchLabel = messageSource.getMessage("classifier.word_rel_type.raw.match", new Object[0], locale);
			List<WordRelation> wordRelationSyns = null;
			List<WordRelation> wordRelationMatches = null;
			if (CollectionUtils.isNotEmpty(wordRelations)) {
				Map<Boolean, List<WordRelation>> wordRelationSynOrMatchMap = wordRelations.stream()
						.collect(Collectors.groupingBy(wordRelation -> StringUtils.equals(wordLang, wordRelation.getWordLang())));
				wordRelationSyns = wordRelationSynOrMatchMap.get(Boolean.TRUE);
				wordRelationMatches = wordRelationSynOrMatchMap.get(Boolean.FALSE);
			}

			// raw rel syn group
			wordRelationGroup = new WordGroup();
			wordRelationGroup.setGroupTypeCode(relTypeCode);
			wordRelationGroup.setGroupTypeLabel(synLabel);
			wordRelationGroup.setMembers(wordRelationSyns);
			wordRelationGroups.add(wordRelationGroup);

			// raw rel match group w lang grouping
			wordRelationGroup = new WordGroup();
			wordRelationGroup.setGroupTypeCode(relTypeCode);
			wordRelationGroup.setGroupTypeLabel(matchLabel);
			wordRelationGroup.setMembers(wordRelationMatches);
			wordRelationGroups.add(wordRelationGroup);

		} else if (StringUtils.equals(WORD_REL_TYPE_CODE_COMP, relTypeCode)) {

			String compGroupLabel = messageSource.getMessage("classifier.word_rel_type.comp", new Object[0], locale);
			wordRelationGroup = new WordGroup();
			wordRelationGroup.setGroupTypeCode(relTypeCode);
			wordRelationGroup.setGroupTypeLabel(compGroupLabel);
			wordRelationGroup.setMembers(wordRelations);
			wordRelationGroups.add(wordRelationGroup);

		} else {

			wordRelationGroup = new WordGroup();
			wordRelationGroup.setGroupTypeCode(relTypeCode);
			wordRelationGroup.setGroupTypeLabel(relTypeLabel);
			wordRelationGroup.setMembers(wordRelations);
			wordRelationGroups.add(wordRelationGroup);
		}
	}

	@Deprecated
	public List<WordEtym> composeWordEtymology(List<WordEtymTuple> wordEtymTuples) {

		List<WordEtym> wordEtyms = new ArrayList<>();
		Map<Long, WordEtym> wordEtymMap = new HashMap<>();
		List<Long> wordEtymSourceLinkIds = new ArrayList<>();
		List<Long> wordEtymRelIds = new ArrayList<>();

		for (WordEtymTuple tuple : wordEtymTuples) {

			Long wordEtymId = tuple.getWordEtymId();
			Long wordEtymSourceLinkId = tuple.getWordEtymSourceLinkId();
			Long wordEtymRelId = tuple.getWordEtymRelId();

			WordEtym wordEtym = wordEtymMap.get(wordEtymId);
			if (wordEtym == null) {
				wordEtym = new WordEtym();
				wordEtym.setWordEtymId(tuple.getWordEtymId());
				wordEtym.setEtymologyTypeCode(tuple.getEtymologyTypeCode());
				wordEtym.setEtymologyYear(tuple.getEtymologyYear());
				wordEtym.setComment(tuple.getWordEtymComment());
				wordEtym.setQuestionable(tuple.isWordEtymQuestionable());
				wordEtym.setWordEtymSourceLinks(new ArrayList<>());
				wordEtym.setWordEtymRelations(new ArrayList<>());
				wordEtymMap.put(wordEtymId, wordEtym);
				wordEtyms.add(wordEtym);
			}
			if (wordEtymSourceLinkId != null) {
				if (!wordEtymSourceLinkIds.contains(wordEtymSourceLinkId)) {
					SourceLink sourceLink = new SourceLink();
					sourceLink.setId(wordEtymSourceLinkId);
					sourceLink.setSourceId(tuple.getWordEtymSourceId());
					//sourceLink.setSourceName(tuple.getWordEtymSourceName());
					wordEtym.getWordEtymSourceLinks().add(sourceLink);
					wordEtymSourceLinkIds.add(wordEtymSourceLinkId);
				}
			}
			if (wordEtymRelId != null) {
				if (!wordEtymRelIds.contains(wordEtymRelId)) {
					WordEtymRel wordEtymRel = new WordEtymRel();
					wordEtymRel.setCommentPrese(tuple.getWordEtymRelComment());
					wordEtymRel.setQuestionable(tuple.isWordEtymRelQuestionable());
					wordEtymRel.setCompound(tuple.isWordEtymRelCompound());
					wordEtymRel.setRelatedWordId(tuple.getRelatedWordId());
					wordEtymRel.setRelatedWord(tuple.getRelatedWord());
					wordEtymRel.setRelatedWordLang(tuple.getRelatedWordLang());
					wordEtym.getWordEtymRelations().add(wordEtymRel);
					wordEtymRelIds.add(wordEtymRelId);
				}
			}
		}
		return wordEtyms;
	}

	public List<SynonymLangGroup> composeSynonymLangGroups(
			List<MeaningRelation> synMeaningRelations, List<MeaningWord> meaningWords, List<InexactSynonym> inexactSynonyms, EkiUserProfile userProfile,
			String wordLang, List<ClassifierSelect> languagesOrder) {

		List<Synonym> synonyms = new ArrayList<>();

		for (MeaningWord meaningWord : meaningWords) {
			Synonym meaningWordSyn = new Synonym();
			meaningWordSyn.setType(SynonymType.MEANING_WORD);
			meaningWordSyn.setWordLang(meaningWord.getLang());
			meaningWordSyn.setWeight(meaningWord.getLexemeWeight());
			meaningWordSyn.setOrderBy(meaningWord.getOrderBy());

			SynWord synWord = new SynWord();
			synWord.setLexemeId(meaningWord.getLexemeId());
			synWord.setWordId(meaningWord.getWordId());
			synWord.setWordValue(meaningWord.getWordValue());
			synWord.setWordValuePrese(meaningWord.getWordValuePrese());
			synWord.setHomonymNr(meaningWord.getHomonymNr());
			synWord.setHomonymsExist(meaningWord.isHomonymsExist());
			synWord.setLang(meaningWord.getLang());
			synWord.setWordTypeCodes(meaningWord.getWordTypeCodes());
			synWord.setPrefixoid(meaningWord.isPrefixoid());
			synWord.setSuffixoid(meaningWord.isSuffixoid());
			synWord.setForeign(meaningWord.isForeign());
			synWord.setLexemeRegisterCodes(meaningWord.getLexRegisterCodes());
			synWord.setLexemeComplexity(meaningWord.getLexemeComplexity());
			synWord.setLexemePublic(meaningWord.isLexemePublic());
			meaningWordSyn.setWords(Arrays.asList(synWord));

			synonyms.add(meaningWordSyn);
		}

		if (CollectionUtils.isNotEmpty(synMeaningRelations)) {
			List<Synonym> meaningRelSyns = new ArrayList<>();
			boolean showFirstWordOnly = false;
			boolean showSourceLangWords = false;
			if (userProfile != null) {
				showFirstWordOnly = userProfile.isShowMeaningRelationFirstWordOnly();
				showSourceLangWords = userProfile.isShowLexMeaningRelationSourceLangWords();
			}

			List<String> prefWordLangs = new ArrayList<>();
			if (showSourceLangWords && StringUtils.isNotEmpty(wordLang)) {
				prefWordLangs.add(wordLang);
			} else if (userProfile != null) {
				prefWordLangs = userProfile.getPreferredMeaningRelationWordLangs();
			}

			Map<Long, List<MeaningRelation>> groupedByIdRelationsMap = synMeaningRelations.stream().collect(groupingBy(MeaningRelation::getId));
			for (List<MeaningRelation> groupedByIdRelations : groupedByIdRelationsMap.values()) {

				Map<String, List<MeaningRelation>> groupedByLangRelationsMap = groupedByIdRelations.stream().collect(groupingBy(MeaningRelation::getWordLang));
				for (List<MeaningRelation> groupedByLangRelations : groupedByLangRelationsMap.values()) {

					String groupWordLang = groupedByLangRelations.get(0).getWordLang();
					if (CollectionUtils.isNotEmpty(prefWordLangs) && !prefWordLangs.contains(groupWordLang)) {
						continue;
					}

					Synonym meaningRelSyn = null;
					boolean isFirstWord = true;
					List<SynWord> synWords = new ArrayList<>();
					for (MeaningRelation groupedByLangRelation : groupedByLangRelations) {
						if (meaningRelSyn == null) {
							meaningRelSyn = new Synonym();
							meaningRelSyn.setType(SynonymType.MEANING_REL);
							meaningRelSyn.setWordLang(groupWordLang);
							meaningRelSyn.setRelationId(groupedByLangRelation.getId());
							meaningRelSyn.setMeaningId(groupedByLangRelation.getMeaningId());
							meaningRelSyn.setWeight(groupedByLangRelation.getWeight());
							meaningRelSyn.setOrderBy(groupedByLangRelation.getOrderBy());
						}

						if (showFirstWordOnly && !isFirstWord) {
							break;
						}
						SynWord synWord = new SynWord();
						synWord.setWordId(groupedByLangRelation.getWordId());
						synWord.setWordValue(groupedByLangRelation.getWordValue());
						synWord.setWordValuePrese(groupedByLangRelation.getWordValuePrese());
						synWord.setHomonymNr(groupedByLangRelation.getWordHomonymNr());
						synWord.setHomonymsExist(groupedByLangRelation.isHomonymsExist());
						synWord.setLang(groupedByLangRelation.getWordLang());
						synWord.setLexemeRegisterCodes(groupedByLangRelation.getLexemeRegisterCodes());
						synWord.setLexemeLevels(groupedByLangRelation.getLexemeLevels());
						synWords.add(synWord);
						isFirstWord = false;
					}
					meaningRelSyn.setWords(synWords);
					meaningRelSyns.add(meaningRelSyn);
				}
			}
			meaningRelSyns.sort(Comparator.comparing(Synonym::getOrderBy));
			synonyms.addAll(meaningRelSyns);
		}

		if (CollectionUtils.isEmpty(synonyms) && CollectionUtils.isEmpty(inexactSynonyms)) {
			return new ArrayList<>();
		}

		List<SynonymLangGroup> synonymLangGroups = new ArrayList<>();
		Map<String, SynonymLangGroup> synonymLangGroupMap = new HashMap<>();

		List<String> langCodeOrder;
		List<String> selectedLangCodes;
		if (CollectionUtils.isEmpty(languagesOrder)) {
			langCodeOrder = synonyms.stream().map(Synonym::getWordLang).distinct().collect(Collectors.toList());
			selectedLangCodes = new ArrayList<>();
		} else {
			langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
			selectedLangCodes = languagesOrder.stream().filter(ClassifierSelect::isSelected).map(ClassifierSelect::getCode).collect(Collectors.toList());
		}

		for (Synonym synonym : synonyms) {
			String lang = synonym.getWordLang();
			SynonymLangGroup synonymLangGroup = synonymLangGroupMap.get(lang);
			if (synonymLangGroup == null) {
				synonymLangGroup = initSynonymLangGroup(lang, selectedLangCodes);
				synonymLangGroupMap.put(lang, synonymLangGroup);
				synonymLangGroups.add(synonymLangGroup);
			}
			synonymLangGroup.getSynonyms().add(synonym);
		}

		for (InexactSynonym inexactSynonym : inexactSynonyms) {
			String lang = inexactSynonym.getTranslationLang();
			SynonymLangGroup synonymLangGroup = synonymLangGroupMap.get(lang);
			if (synonymLangGroup == null) {
				synonymLangGroup = initSynonymLangGroup(lang, selectedLangCodes);
				synonymLangGroupMap.put(lang, synonymLangGroup);
				synonymLangGroups.add(synonymLangGroup);
			}
			synonymLangGroup.getInexactSynonyms().add(inexactSynonym);
		}

		synonymLangGroups.sort((SynonymLangGroup gr1, SynonymLangGroup gr2) -> {
			String lang1 = gr1.getLang();
			String lang2 = gr2.getLang();
			int langOrder1 = langCodeOrder.indexOf(lang1);
			int langOrder2 = langCodeOrder.indexOf(lang2);
			return langOrder1 - langOrder2;
		});

		return synonymLangGroups;
	}

	private SynonymLangGroup initSynonymLangGroup(String lang, List<String> selectedLangCodes) {

		boolean isSelected = selectedLangCodes.contains(lang);
		SynonymLangGroup synonymLangGroup = new SynonymLangGroup();
		synonymLangGroup.setLang(lang);
		synonymLangGroup.setSelected(isSelected);
		synonymLangGroup.setSynonyms(new ArrayList<>());
		synonymLangGroup.setInexactSynonyms(new ArrayList<>());

		return synonymLangGroup;
	}

	public List<List<MeaningRelation>> composeViewMeaningRelations(List<MeaningRelation> relations, EkiUserProfile userProfile, String sourceLang, List<ClassifierSelect> languagesOrder) {

		List<Long> relationIds = relations.stream().map(MeaningRelation::getId).distinct().collect(Collectors.toList());
		Map<Long, List<MeaningRelation>> groupedRelationsMap = relations.stream().collect(groupingBy(MeaningRelation::getId));
		List<List<MeaningRelation>> groupedRelationsList = relationIds.stream().map(relationId -> groupedRelationsMap.get(relationId)).collect(Collectors.toList());
		if (userProfile == null) {
			return groupedRelationsList;
		}

		List<String> allLangs = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
		boolean showFirstWordOnly = userProfile.isShowMeaningRelationFirstWordOnly();
		boolean showDatasets = userProfile.isShowMeaningRelationWordDatasets();
		boolean showSourceLangWords = userProfile.isShowLexMeaningRelationSourceLangWords();

		List<String> prefWordLangs;
		if (showSourceLangWords && StringUtils.isNotEmpty(sourceLang)) {
			prefWordLangs = Collections.singletonList(sourceLang);
		} else {
			prefWordLangs = userProfile.getPreferredMeaningRelationWordLangs();
		}

		groupedRelationsList.forEach(groupedRelations -> {
			filterMeaningRelations(prefWordLangs, allLangs, groupedRelations, showFirstWordOnly);
			if (!showDatasets) {
				groupedRelations.forEach(relation -> relation.setDatasetCodes(Collections.emptyList()));
			}
		});

		return groupedRelationsList;
	}

	private void filterMeaningRelations(List<String> wordLangs, List<String> allLangs, List<MeaningRelation> meaningRelations, boolean showFirstWordOnly) {

		boolean relationLangIsInWordLangs = meaningRelations.stream().anyMatch(relation -> wordLangs.contains(relation.getWordLang()));
		if (relationLangIsInWordLangs) {
			meaningRelations.removeIf(relation -> !wordLangs.contains(relation.getWordLang()));
		} else {
			for (String lang : allLangs) {
				boolean relationLangEqualsLang = meaningRelations.stream().anyMatch(relation -> lang.equals(relation.getWordLang()));
				if (relationLangEqualsLang) {
					meaningRelations.removeIf(relation -> !lang.equals(relation.getWordLang()));
					break;
				}
			}
		}

		if (showFirstWordOnly) {
			Iterator<MeaningRelation> relationIterator = meaningRelations.iterator();
			List<String> occurredLangs = new ArrayList<>();
			while (relationIterator.hasNext()) {
				String iteratorLang = relationIterator.next().getWordLang();
				if (occurredLangs.contains(iteratorLang)) {
					relationIterator.remove();
				} else {
					occurredLangs.add(iteratorLang);
				}
			}
		}
	}

	public Source composeSource(Source source, List<SourcePropertyTuple> sourcePropertyTuples) {

		if (CollectionUtils.isEmpty(sourcePropertyTuples)) {
			return source;
		}

		List<SourceProperty> sourceProperties = new ArrayList<>();
		for (SourcePropertyTuple tuple : sourcePropertyTuples) {

			Long sourcePropertyId = tuple.getSourcePropertyId();
			String sourcePropertyTypeCode = tuple.getSourcePropertyTypeCode();
			String sourcePropertyValue = tuple.getSourcePropertyValue();

			SourceProperty sourceProperty = new SourceProperty();
			sourceProperty.setId(sourcePropertyId);
			sourceProperty.setTypeCode(sourcePropertyTypeCode);
			sourceProperty.setValue(sourcePropertyValue);
			sourceProperties.add(sourceProperty);
		}
		source.setSourceProperties(sourceProperties);
		return source;
	}

	public List<Source> composeSources(List<SourcePropertyTuple> sourcePropertyTuples) {

		List<Source> sources = new ArrayList<>();
		Map<Long, Source> sourceMap = new HashMap<>();

		for (SourcePropertyTuple tuple : sourcePropertyTuples) {

			Long sourceId = tuple.getSourceId();
			Long sourcePropertyId = tuple.getSourcePropertyId();
			String sourcePropertyTypeCode = tuple.getSourcePropertyTypeCode();
			String sourcePropertyValue = tuple.getSourcePropertyValue();
			boolean sourcePropertyMatch = tuple.isSourcePropertyMatch();

			Source source = sourceMap.get(sourceId);
			if (source == null) {
				source = new Source();
				source.setId(sourceId);
				source.setDatasetCode(tuple.getSourceDatasetCode());
				source.setType(tuple.getSourceType());
				source.setName(tuple.getSourceName());
				source.setValue(tuple.getSourceValue());
				source.setValuePrese(tuple.getSourceValuePrese());
				source.setComment(tuple.getSourceComment());
				source.setPublic(tuple.isSourcePublic());
				source.setSourceProperties(new ArrayList<>());
				sources.add(source);
				sourceMap.put(sourceId, source);
			}

			SourceProperty sourceProperty = new SourceProperty();
			sourceProperty.setId(sourcePropertyId);
			sourceProperty.setTypeCode(sourcePropertyTypeCode);
			sourceProperty.setValue(sourcePropertyValue);
			sourceProperty.setValueMatch(sourcePropertyMatch);
			source.getSourceProperties().add(sourceProperty);
		}

		return sources;
	}

	public boolean isLexemesActiveTagComplete(DatasetPermission userRole, List<? extends LexemeTag> lexemes, Tag activeTag) {

		if (userRole == null) {
			return false;
		}
		String roleDatasetCode = userRole.getDatasetCode();

		List<Lexeme> roleDatasetWordLexemes = lexemes.stream()
				.filter(lexeme -> lexeme instanceof Lexeme)
				.map(lexeme -> (Lexeme) lexeme)
				.filter(lexeme -> lexeme.getDatasetCode().equals(roleDatasetCode))
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(roleDatasetWordLexemes)) {
			return isLexemesActiveTagComplete(roleDatasetWordLexemes, activeTag);
		}

		List<Lexeme> roleDatasetLexemes = lexemes.stream()
				.filter(lexeme -> lexeme instanceof Lexeme)
				.map(lexeme -> (Lexeme) lexeme)
				.filter(lexeme -> lexeme.getDatasetCode().equals(roleDatasetCode))
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(roleDatasetLexemes)) {
			return isLexemesActiveTagComplete(roleDatasetLexemes, activeTag);
		}

		return false;
	}

	public boolean isLexemesActiveTagComplete(List<? extends LexemeTag> lexemes, Tag activeTag) {

		if (activeTag == null) {
			return false;
		}
		String activeTagName = activeTag.getName();
		boolean removeToComplete = activeTag.isRemoveToComplete();

		if (removeToComplete) {
			return lexemes.stream().noneMatch(lexeme -> lexeme.getTags().stream().anyMatch(lexemeTagName -> StringUtils.equals(lexemeTagName, activeTagName)));
		} else {
			return lexemes.stream().allMatch(lexeme -> lexeme.getTags().stream().anyMatch(lexemeTagName -> StringUtils.equals(lexemeTagName, activeTagName)));
		}
	}

	public Timestamp dateStrToTimestamp(String dateStr) throws Exception {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date date = dateFormat.parse(dateStr);
		Timestamp timestamp = new Timestamp(date.getTime());
		return timestamp;
	}
}
