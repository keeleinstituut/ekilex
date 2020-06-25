package eki.ekilex.service.util;

import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.FormMode;
import eki.common.constant.GlobalConstant;
import eki.common.constant.ReferenceType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.CollocMember;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationRelGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.DefSourceAndNoteSourceTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionNote;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Form;
import eki.ekilex.data.Image;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.LexemeWordTuple;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordLangGroup;
import eki.ekilex.data.Note;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.OdUsageAlternative;
import eki.ekilex.data.OdUsageDefinition;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.TypeTermMeaningWord;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageDefinition;
import eki.ekilex.data.UsageTranslation;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymRel;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordGroup;
import eki.ekilex.data.WordNote;

@Component
public class ConversionUtil implements GlobalConstant {

	public static String getClassifierValue(String code, List<Classifier> classifiers) {
		Optional<Classifier> classifier = classifiers.stream().filter(c -> c.getCode().equals(code)).findFirst();
		return classifier.isPresent() ? classifier.get().getValue() : code;
	}

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
				paradigm.setInflectionTypeNr(tuple.getInflectionTypeNr());
				paradigm.setForms(forms);
				paradigmsMap.put(paradigmId, paradigm);
				paradigms.add(paradigm);
			} else {
				forms = paradigm.getForms();
			}
			Form form = new Form();
			form.setId(tuple.getFormId());
			form.setValue(tuple.getForm());
			form.setMode(tuple.getMode());
			form.setComponents(tuple.getComponents());
			form.setDisplayForm(tuple.getDisplayForm());
			form.setVocalForm(tuple.getVocalForm());
			form.setMorphCode(tuple.getMorphCode());
			form.setMorphValue(tuple.getMorphValue());
			form.setFormFrequencies(tuple.getFormFrequencies());
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

	public List<Usage> composeUsages(List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples) {

		List<Usage> usages = new ArrayList<>();

		Map<Long, Usage> usageMap = new HashMap<>();
		Map<Long, SourceLink> usageSourceMap = new HashMap<>();
		Map<Long, UsageTranslation> usageTranslationMap = new HashMap<>();
		Map<Long, UsageDefinition> usageDefinitionMap = new HashMap<>();
		Map<Long, OdUsageDefinition> odUsageDefinitionMap = new HashMap<>();
		Map<Long, OdUsageAlternative> odUsageAlternativeMap = new HashMap<>();

		for (UsageTranslationDefinitionTuple tuple : usageTranslationDefinitionTuples) {

			Long usageId = tuple.getUsageId();
			Long usageTranslationId = tuple.getUsageTranslationId();
			Long usageDefinitionId = tuple.getUsageDefinitionId();
			Long usageSourceLinkId = tuple.getUsageSourceLinkId();
			Long odUsageDefinitionId = tuple.getOdUsageDefinitionId();
			Long odUsageAlternativeId = tuple.getOdUsageAlternativeId();

			Usage usage = usageMap.get(usageId);
			if (usage == null) {
				usage = new Usage();
				usage.setId(usageId);
				usage.setValue(tuple.getUsageValue());
				usage.setLang(tuple.getUsageLang());
				usage.setComplexity(tuple.getUsageComplexity());
				usage.setOrderBy(tuple.getUsageOrderBy());
				usage.setTypeCode(tuple.getUsageTypeCode());
				usage.setTypeValue(tuple.getUsageTypeValue());
				usage.setTranslations(new ArrayList<>());
				usage.setDefinitions(new ArrayList<>());
				usage.setOdDefinitions(new ArrayList<>());
				usage.setOdAlternatives(new ArrayList<>());
				usage.setAuthors(new ArrayList<>());
				usage.setSourceLinks(new ArrayList<>());
				usage.setPublic(tuple.isUsagePublic());
				usageMap.put(usageId, usage);
				usages.add(usage);
			}
			if (usageSourceLinkId != null) {
				SourceLink usageSource = usageSourceMap.get(usageSourceLinkId);
				if (usageSource == null) {
					usageSource = new SourceLink();
					usageSource.setId(tuple.getUsageSourceLinkId());
					usageSource.setType(tuple.getUsageSourceLinkType());
					usageSource.setName(tuple.getUsageSourceLinkName());
					if (StringUtils.isBlank(tuple.getUsageSourceLinkValue())) {
						usageSource.setValue(tuple.getUsageSourceName());
					} else {
						usageSource.setValue(tuple.getUsageSourceLinkValue());
					}
					usageSourceMap.put(usageSourceLinkId, usageSource);
					if (ReferenceType.AUTHOR.equals(tuple.getUsageSourceLinkType())) {
						usage.getAuthors().add(usageSource);
					} else if (ReferenceType.TRANSLATOR.equals(tuple.getUsageSourceLinkType())) {
						usage.getAuthors().add(usageSource);
					} else {
						usage.getSourceLinks().add(usageSource);
					}
				}
			}
			if (usageTranslationId != null) {
				UsageTranslation usageTranslation = usageTranslationMap.get(usageTranslationId);
				if (usageTranslation == null) {
					usageTranslation = new UsageTranslation();
					usageTranslation.setId(usageTranslationId);
					usageTranslation.setValue(tuple.getUsageTranslationValue());
					usageTranslation.setLang(tuple.getUsageTranslationLang());
					usageTranslationMap.put(usageTranslationId, usageTranslation);
					usage.getTranslations().add(usageTranslation);
				}
			}
			if (usageDefinitionId != null) {
				UsageDefinition usageDefinition = usageDefinitionMap.get(usageDefinitionId);
				if (usageDefinition == null) {
					usageDefinition = new UsageDefinition();
					usageDefinition.setId(usageDefinitionId);
					usageDefinition.setValue(tuple.getUsageDefinitionValue());
					usageDefinition.setLang(tuple.getUsageDefinitionLang());
					usageDefinitionMap.put(usageDefinitionId, usageDefinition);
					usage.getDefinitions().add(usageDefinition);
				}
			}
			if (odUsageDefinitionId != null) {
				OdUsageDefinition odUsageDefinition = odUsageDefinitionMap.get(odUsageDefinitionId);
				if (odUsageDefinition == null) {
					odUsageDefinition = new OdUsageDefinition();
					odUsageDefinition.setId(odUsageDefinitionId);
					odUsageDefinition.setValue(tuple.getOdUsageDefinitionValue());
					odUsageDefinitionMap.put(odUsageDefinitionId, odUsageDefinition);
					usage.getOdDefinitions().add(odUsageDefinition);
				}
			}
			if (odUsageAlternativeId != null) {
				OdUsageAlternative odUsageAlternative = odUsageAlternativeMap.get(odUsageAlternativeId);
				if (odUsageAlternative == null) {
					odUsageAlternative = new OdUsageAlternative();
					odUsageAlternative.setId(odUsageAlternativeId);
					odUsageAlternative.setValue(tuple.getOdUsageAlternativeValue());
					odUsageAlternativeMap.put(odUsageAlternativeId, odUsageAlternative);
					usage.getOdAlternatives().add(odUsageAlternative);
				}
			}
		}
		return usages;
	}

	public <T extends Note> List<T> composeNotes(Class<T> type, Long parentId, List<NoteSourceTuple> noteSourceTuples) throws Exception {

		List<T> notes = new ArrayList<>();

		Map<Long, T> noteMap = new HashMap<>();
		List<SourceLink> sourceLinks;

		for (NoteSourceTuple tuple : noteSourceTuples) {

			Long noteId = tuple.getFreeformId();
			Long sourceLinkId = tuple.getSourceLinkId();

			T note = noteMap.get(noteId);
			if (note == null) {
				note = type.newInstance();
				sourceLinks = new ArrayList<>();
				note.setSourceLinks(sourceLinks);
				note.setId(noteId);
				note.setValueText(tuple.getValueText());
				note.setValuePrese(tuple.getValuePrese());
				note.setLang(tuple.getLang());
				note.setComplexity(tuple.getComplexity());
				note.setPublic(tuple.isPublic());
				note.setOrderBy(tuple.getOrderBy());
				if (note instanceof LexemeNote) {
					((LexemeNote) note).setLexemeId(parentId);
				} else if (note instanceof MeaningNote) {
					((MeaningNote) note).setMeaningId(parentId);
				} else if (note instanceof WordNote) {
					((WordNote) note).setWordId(parentId);
				}
				noteMap.put(noteId, note);
				notes.add(note);
			} else {
				sourceLinks = note.getSourceLinks();
			}
			if (sourceLinkId != null) {
				SourceLink sourceLink = new SourceLink();
				sourceLink.setId(sourceLinkId);
				sourceLink.setType(tuple.getSourceLinkType());
				sourceLink.setName(tuple.getSourceLinkName());
				sourceLink.setValue(tuple.getSourceLinkValue());
				sourceLinks.add(sourceLink);
			}
		}

		return notes;
	}

	public List<Image> composeMeaningImages(List<ImageSourceTuple> imageSourceTuples) {

		List<Image> images = new ArrayList<>();

		Map<Long, Image> imageMap = new HashMap<>();
		List<SourceLink> sourceLinks;

		for (ImageSourceTuple tuple : imageSourceTuples) {
			Long imageId = tuple.getImageFreeformId();
			Long sourceLinkId = tuple.getSourceLinkId();

			Image image = imageMap.get(imageId);
			if (image == null) {
				image = new Image();
				sourceLinks = new ArrayList<>();
				image.setSourceLinks(sourceLinks);
				image.setId(imageId);
				image.setFileName(tuple.getImageFreeformValueText());
				image.setTitle(tuple.getTitleFreeformValueText());
				imageMap.put(imageId, image);
				images.add(image);
			} else {
				sourceLinks = image.getSourceLinks();
			}
			if (sourceLinkId != null) {
				SourceLink sourceLink = new SourceLink();
				sourceLink.setId(sourceLinkId);
				sourceLink.setType(tuple.getSourceLinkType());
				sourceLink.setName(tuple.getSourceLinkName());
				sourceLink.setValue(tuple.getSourceLinkValue());
				sourceLinks.add(sourceLink);
			}
		}

		return images;
	}

	public Lexeme composeLexeme(LexemeWordTuple lexemeWordTuple) {

		Lexeme lexeme = new Lexeme();
		lexeme.setLexemeId(lexemeWordTuple.getLexemeId());
		lexeme.setWordId(lexemeWordTuple.getWordId());
		lexeme.setMeaningId(lexemeWordTuple.getMeaningId());
		lexeme.setDatasetCode(lexemeWordTuple.getDatasetCode());
		lexeme.setLevel1(lexemeWordTuple.getLevel1());
		lexeme.setLevel2(lexemeWordTuple.getLevel2());
		lexeme.setLexemeFrequencyGroupCode(lexemeWordTuple.getLexemeFrequencyGroupCode());
		lexeme.setLexemeFrequencies(lexemeWordTuple.getLexemeFrequencies());
		lexeme.setLexemeValueStateCode(lexemeWordTuple.getLexemeValueStateCode());
		lexeme.setLexemeProcessStateCode(lexemeWordTuple.getLexemeProcessStateCode());
		lexeme.setComplexity(lexemeWordTuple.getComplexity());
		lexeme.setOrderBy(lexemeWordTuple.getOrderBy());
		lexeme.setPos(lexemeWordTuple.getPos());
		lexeme.setDerivs(lexemeWordTuple.getDerivs());
		lexeme.setRegisters(lexemeWordTuple.getRegisters());
		lexeme.setRegions(lexemeWordTuple.getRegions());

		Word word = new Word();
		word.setWordId(lexemeWordTuple.getWordId());
		word.setWordValue(lexemeWordTuple.getWordValue());
		word.setWordValuePrese(lexemeWordTuple.getWordValuePrese());
		word.setHomonymNr(lexemeWordTuple.getHomonymNr());
		word.setLang(lexemeWordTuple.getWordLang());
		word.setGenderCode(lexemeWordTuple.getWordGenderCode());
		word.setDisplayMorphCode(lexemeWordTuple.getWordDisplayMorphCode());
		word.setWordTypeCodes(lexemeWordTuple.getWordTypeCodes());
		word.setPrefixoid(lexemeWordTuple.isPrefixoid());
		word.setSuffixoid(lexemeWordTuple.isSuffixoid());
		word.setForeign(lexemeWordTuple.isForeign());
		lexeme.setWord(word);
		return lexeme;
	}

	public List<LexemeLangGroup> composeLexemeLangGroups(List<Lexeme> lexemes, List<ClassifierSelect> languagesOrder) {

		List<String> langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
		List<String> selectedLangCodes = languagesOrder.stream().filter(ClassifierSelect::isSelected).map(ClassifierSelect::getCode).collect(Collectors.toList());
		List<LexemeLangGroup> lexemeLangGroups = new ArrayList<>();
		Map<String, LexemeLangGroup> lexemeLangGroupMap = new HashMap<>();
		List<Lexeme> lexemesOrderBy = lexemes.stream().sorted(Comparator.comparing(Lexeme::getOrderBy)).collect(Collectors.toList());

		for (Lexeme lexeme : lexemesOrderBy) {
			Word word = lexeme.getWord();
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

		List<String> langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
		List<String> selectedLangCodes = languagesOrder.stream().filter(ClassifierSelect::isSelected).map(ClassifierSelect::getCode).collect(Collectors.toList());
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

	public void composeMeaningDefinitions(List<Definition> definitions, List<DefSourceAndNoteSourceTuple> definitionsDataTuples) {

		Map<Long, Definition> definitionMap = definitions.stream().collect(Collectors.toMap(Definition::getId, definition -> definition));
		Set<Long> handledSourceLinkIds = new HashSet<>();
		Map<Long, DefinitionNote> noteMap = new HashMap<>();

		for (DefSourceAndNoteSourceTuple definitionData : definitionsDataTuples) {
			Long definitionId = definitionData.getDefinitionId();
			Long definitionSourceLinkId = definitionData.getDefinitionSourceLinkId();
			Long noteId = definitionData.getNoteId();
			Long noteSourceLinkId = definitionData.getNoteSourceLinkId();

			Definition definition = definitionMap.get(definitionId);
			if (definition == null) {
				continue;
			}
			if (definition.getSourceLinks() == null) {
				definition.setSourceLinks(new ArrayList<>());
			}
			if (definition.getNotes() == null) {
				definition.setNotes(new ArrayList<>());
			}

			if (definitionSourceLinkId != null && !handledSourceLinkIds.contains(definitionSourceLinkId)) {
				ReferenceType definitionSourceLinkType = definitionData.getDefinitionSourceLinkType();
				String definitionSourceLinkName = definitionData.getDefinitionSourceLinkName();
				String definitionSourceLinkValue = definitionData.getDefinitionSourceLinkValue();
				SourceLink definitionSourceLink = new SourceLink();
				definitionSourceLink.setId(definitionSourceLinkId);
				definitionSourceLink.setType(definitionSourceLinkType);
				definitionSourceLink.setName(definitionSourceLinkName);
				definitionSourceLink.setValue(definitionSourceLinkValue);
				definition.getSourceLinks().add(definitionSourceLink);
				handledSourceLinkIds.add(definitionSourceLinkId);
			}

			if (noteId != null) {
				DefinitionNote note = noteMap.get(noteId);
				if (note == null) {
					String noteValueText = definitionData.getNoteValueText();
					String noteValuePrese = definitionData.getNoteValuePrese();
					String noteLang = definitionData.getNoteLang();
					Complexity noteComplexity = definitionData.getNoteComplexity();
					boolean isNotePublic = definitionData.isNotePublic();
					Long noteOrderBy = definitionData.getNoteOrderBy();
					note = new DefinitionNote();
					note.setDefinitionId(definitionId);
					note.setId(noteId);
					note.setValueText(noteValueText);
					note.setValuePrese(noteValuePrese);
					note.setLang(noteLang);
					note.setComplexity(noteComplexity);
					note.setPublic(isNotePublic);
					note.setOrderBy(noteOrderBy);
					note.setSourceLinks(new ArrayList<>());
					definition.getNotes().add(note);
					noteMap.put(noteId, note);
				}
				if (noteSourceLinkId != null) {
					ReferenceType noteSourceLinkType = definitionData.getNoteSourceLinkType();
					String noteSourceLinkName = definitionData.getNoteSourceLinkName();
					String noteSourceLinkValue = definitionData.getNoteSourceLinkValue();
					SourceLink noteSourceLink = new SourceLink();
					noteSourceLink.setId(noteSourceLinkId);
					noteSourceLink.setType(noteSourceLinkType);
					noteSourceLink.setName(noteSourceLinkName);
					noteSourceLink.setValue(noteSourceLinkValue);
					note.getSourceLinks().add(noteSourceLink);
				}
			}
		}
	}

	public List<DefinitionLangGroup> composeMeaningDefinitionLangGroups(List<Definition> definitions, List<ClassifierSelect> languagesOrder) {

		List<DefinitionLangGroup> definitionLangGroups = new ArrayList<>();
		if (languagesOrder == null) {
			return definitionLangGroups;
		}
		List<String> langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
		List<String> selectedLangCodes = languagesOrder.stream().filter(ClassifierSelect::isSelected).map(ClassifierSelect::getCode).collect(Collectors.toList());
		Map<String, DefinitionLangGroup> definitionLangGroupMap = new HashMap<>();

		for (Definition definition : definitions) {
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

	public List<WordGroup> composeWordGroups(List<Relation> groupMembers) {

		List<WordGroup> groups = new ArrayList<>();
		Map<Long, List<Relation>> memberGroups = groupMembers.stream().collect(groupingBy(Relation::getGroupId));
		for (Long groupId : memberGroups.keySet()) {
			WordGroup wordGroup = new WordGroup();
			wordGroup.setId(groupId);
			wordGroup.setMembers(memberGroups.get(groupId));
			wordGroup.setGroupTypeLabel(wordGroup.getMembers().get(0).getRelTypeLabel());
			groups.add(wordGroup);
		}
		return groups;
	}

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
					sourceLink.setType(tuple.getWordEtymSourceLinkType());
					sourceLink.setValue(tuple.getWordEtymSourceLinkValue());
					wordEtym.getWordEtymSourceLinks().add(sourceLink);
					wordEtymSourceLinkIds.add(wordEtymSourceLinkId);
				}
			}
			if (wordEtymRelId != null) {
				if (!wordEtymRelIds.contains(wordEtymRelId)) {
					WordEtymRel wordEtymRel = new WordEtymRel();
					wordEtymRel.setComment(tuple.getWordEtymRelComment());
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

	public List<CollocationPosGroup> composeCollocPosGroups(List<CollocationTuple> collocTuples) {

		List<CollocationPosGroup> collocationPosGroups = new ArrayList<>();
		Map<Long, CollocationPosGroup> collocPosGroupMap = new HashMap<>();
		Map<Long, CollocationRelGroup> collocRelGroupMap = new HashMap<>();
		Map<Long, Collocation> collocMap = new HashMap<>();

		for (CollocationTuple collocTuple : collocTuples) {

			Long collocPosGroupId = collocTuple.getPosGroupId();
			Long collocRelGroupId = collocTuple.getRelGroupId();
			CollocationPosGroup collocPosGroup = collocPosGroupMap.get(collocPosGroupId);
			if (collocPosGroup == null) {
				collocPosGroup = new CollocationPosGroup();
				collocPosGroup.setCode(collocTuple.getPosGroupCode());
				collocPosGroup.setRelationGroups(new ArrayList<>());
				collocPosGroupMap.put(collocPosGroupId, collocPosGroup);
				collocationPosGroups.add(collocPosGroup);
			}
			CollocationRelGroup collocRelGroup = collocRelGroupMap.get(collocRelGroupId);
			if (collocRelGroup == null) {
				collocRelGroup = new CollocationRelGroup();
				collocRelGroup.setName(collocTuple.getRelGroupName());
				collocRelGroup.setFrequency(collocTuple.getRelGroupFrequency());
				collocRelGroup.setScore(collocTuple.getRelGroupScore());
				collocRelGroup.setCollocations(new ArrayList<>());
				collocRelGroupMap.put(collocRelGroupId, collocRelGroup);
				collocPosGroup.getRelationGroups().add(collocRelGroup);
			}
			Collocation collocation = addCollocation(collocMap, collocTuple, collocRelGroup.getCollocations());
			addCollocMember(collocTuple, collocation);
		}
		return collocationPosGroups;
	}

	public List<Collocation> composeCollocations(List<CollocationTuple> collocTuples) {

		List<Collocation> collocations = new ArrayList<>();
		Map<Long, Collocation> collocMap = new HashMap<>();

		for (CollocationTuple collocTuple : collocTuples) {

			Collocation collocation = addCollocation(collocMap, collocTuple, collocations);
			addCollocMember(collocTuple, collocation);
		}
		return collocations;
	}

	public List<List<Relation>> composeViewMeaningRelations(List<Relation> relations, EkiUserProfile userProfile, String sourceLang, List<ClassifierSelect> languagesOrder) {

		Map<Long, List<Relation>> groupedRelationsMap = relations.stream().collect(groupingBy(Relation::getId));
		ArrayList<List<Relation>> groupedRelationsList = new ArrayList<>(groupedRelationsMap.values());
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

	private void filterMeaningRelations(List<String> wordLangs, List<String> allLangs, List<Relation> meaningRelations, boolean showFirstWordOnly) {

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
			Iterator<Relation> relationIterator = meaningRelations.iterator();
			Set<String> occurredLangs = new HashSet<>();
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

	public List<OrderedClassifier> removeOrderedClassifierDuplicates(List<OrderedClassifier> allClassifiers) {

		List<OrderedClassifier> distinctClassifiers = new ArrayList<>();
		for (OrderedClassifier classifierCandidate : allClassifiers) {
			String classifCandidateCode = classifierCandidate.getCode();
			boolean alreadyExists = distinctClassifiers.stream().anyMatch(classif -> StringUtils.equals(classif.getCode(), classifCandidateCode));
			if (!alreadyExists) {
				distinctClassifiers.add(classifierCandidate);
			}
		}
		return distinctClassifiers;
	}

	public List<Classifier> removeClassifierDuplicates(List<Classifier> allClassifiers) {

		List<Classifier> distinctClassifiers = new ArrayList<>();
		for (Classifier classifierCandidate : allClassifiers) {
			String classifCandidateCode = classifierCandidate.getCode();
			boolean alreadyExists = distinctClassifiers.stream().anyMatch(classif -> StringUtils.equals(classif.getCode(), classifCandidateCode));
			if (!alreadyExists) {
				distinctClassifiers.add(classifierCandidate);
			}
		}
		return distinctClassifiers;
	}

	private Collocation addCollocation(Map<Long, Collocation> collocMap, CollocationTuple collocTuple, List<Collocation> collocations) {

		Long collocId = collocTuple.getCollocId();
		Collocation collocation = collocMap.get(collocId);
		if (collocation == null) {
			collocation = new Collocation();
			collocation.setValue(collocTuple.getCollocValue());
			collocation.setDefinition(collocTuple.getCollocDefinition());
			collocation.setFrequency(collocTuple.getCollocFrequency());
			collocation.setScore(collocTuple.getCollocScore());
			collocation.setCollocUsages(collocTuple.getCollocUsages());
			collocation.setCollocMembers(new ArrayList<>());
			collocMap.put(collocId, collocation);
			collocations.add(collocation);
		}
		return collocation;
	}

	private void addCollocMember(CollocationTuple collocTuple, Collocation collocation) {

		boolean isWordModeWord = StringUtils.equals(FormMode.WORD.name(), collocTuple.getCollocMemberMode());
		CollocMember collocMember = new CollocMember();
		collocMember.setWordId(collocTuple.getCollocMemberWordId());
		collocMember.setWord(collocTuple.getCollocMemberWord());
		collocMember.setWeight(collocTuple.getCollocMemberWeight());
		collocMember.setWordModeWord(isWordModeWord);

		collocation.getCollocMembers().add(collocMember);
	}

	public List<MeaningWordLangGroup> composeMeaningWordLangGroups(List<MeaningWord> meaningWords, String headwordLang) {

		List<MeaningWordLangGroup> meaningWordLangGroups = new ArrayList<>();
		Map<String, MeaningWordLangGroup> meaningWordLangGroupMap = new HashMap<>();
		List<MeaningWord> meaningWordsOrderBy = meaningWords.stream().sorted(Comparator.comparing(MeaningWord::getOrderBy)).collect(Collectors.toList());

		for (MeaningWord meaningWord : meaningWordsOrderBy) {
			String lang = meaningWord.getLang();
			MeaningWordLangGroup meaningWordLangGroup = meaningWordLangGroupMap.get(lang);
			if (meaningWordLangGroup == null) {
				meaningWordLangGroup = new MeaningWordLangGroup();
				meaningWordLangGroup.setLang(lang);
				meaningWordLangGroup.setMeaningWords(new ArrayList<>());
				meaningWordLangGroupMap.put(lang, meaningWordLangGroup);
				meaningWordLangGroups.add(meaningWordLangGroup);
			}
			meaningWordLangGroup.getMeaningWords().add(meaningWord);
		}

		meaningWordLangGroups.sort(Comparator.comparing(meaningWordLangGroup -> !StringUtils.equals(meaningWordLangGroup.getLang(), headwordLang)));
		return meaningWordLangGroups;
	}
}
