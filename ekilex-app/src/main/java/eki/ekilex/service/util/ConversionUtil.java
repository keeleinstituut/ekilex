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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DbConstant;
import eki.common.constant.FormMode;
import eki.common.constant.ReferenceType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.CollocMember;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationRelGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Form;
import eki.ekilex.data.Image;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordLangGroup;
import eki.ekilex.data.Note;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.OdUsageAlternative;
import eki.ekilex.data.OdUsageDefinition;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.RelationParam;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.SynRelationParamTuple;
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

@Component
public class ConversionUtil implements DbConstant {

	private static final Logger logger = LoggerFactory.getLogger(ConversionUtil.class);

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
			meaningWords.forEach(meaningWord -> {
				boolean isPrefixoid = ArrayUtils.contains(meaningWord.getWordTypeCodes(), WORD_TYPE_CODE_PREFIXOID);
				boolean isSuffixoid = ArrayUtils.contains(meaningWord.getWordTypeCodes(), WORD_TYPE_CODE_SUFFIXOID);
				meaningWord.setPrefixoid(isPrefixoid);
				meaningWord.setSuffixoid(isSuffixoid);
			});
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
		composeParadigmTitles(paradigms);
		flagFormMorphCodes(paradigms);
		flagFormsExist(paradigms);

		return paradigms;
	}

	private void composeParadigmTitles(List<Paradigm> paradigms) {

		if (CollectionUtils.isEmpty(paradigms)) {
			return;
		}
		if (paradigms.size() == 1) {
			Paradigm paradigm = paradigms.get(0);
			String title = getFirstAvailableTitle(paradigm, FormMode.FORM);
			if (StringUtils.isBlank(title)) {
				title = getFirstAvailableTitle(paradigm, FormMode.WORD);
			}
			String inflectionTypeNr = paradigm.getInflectionTypeNr();
			if (StringUtils.isNotBlank(inflectionTypeNr)) {
				title = title + " " + inflectionTypeNr;
			}
			paradigm.setTitle(title);
		} else {
			for (Paradigm paradigm : paradigms) {
				String title = getFirstDifferentTitle(paradigm, paradigms);
				if (StringUtils.isBlank(title)) {
					logger.warn("Could not compose paradigm title. Fix this!");
				}
				String inflectionTypeNr = paradigm.getInflectionTypeNr();
				if (StringUtils.isNotBlank(inflectionTypeNr)) {
					title = title + " " + inflectionTypeNr;
				}
				paradigm.setTitle(title);
			}
		}
	}

	private String getFirstAvailableTitle(Paradigm paradigm, FormMode mode) {

		List<Form> forms = paradigm.getForms();
		for (Form form : forms) {
			if (form.getMode().equals(mode)) {
				String title = form.getDisplayForm();
				if (StringUtils.isBlank(title)) {
					title = form.getValue();
				}
				return title;
			}
		}
		return null;
	}

	private String getFirstDifferentTitle(Paradigm paradigm, List<Paradigm> paradigms) {

		List<Form> forms = paradigm.getForms();
		Long paradigmId = paradigm.getParadigmId();
		for (Form form : forms) {
			String thisMorphCode = form.getMorphCode();
			String titleCandidate = form.getDisplayForm();
			if (StringUtils.isBlank(titleCandidate)) {
				titleCandidate = form.getValue();
			}
			boolean isDifferentTitle = isDifferentTitle(paradigms, paradigmId, thisMorphCode, titleCandidate);
			if (isDifferentTitle) {
				return titleCandidate;
			}
		}
		return null;
	}

	private boolean isDifferentTitle(List<Paradigm> paradigms, Long currentParadigmId, String currentMorphCode, String currentTitle) {

		for (Paradigm otherParadigm : paradigms) {
			if (currentParadigmId.equals(otherParadigm.getParadigmId())) {
				continue;
			}
			List<Form> otherForms = otherParadigm.getForms();
			for (Form otherForm : otherForms) {
				String otherMorphCode = otherForm.getMorphCode();
				if (!StringUtils.equals(currentMorphCode, otherMorphCode)) {
					continue;
				}
				String otherTitle = otherForm.getDisplayForm();
				if (StringUtils.isBlank(otherTitle)) {
					otherTitle = otherForm.getValue();
				}
				if (StringUtils.equals(currentTitle, otherTitle)) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
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
				usage.setTypeCode(tuple.getUsageTypeCode());
				usage.setTypeValue(tuple.getUsageTypeValue());
				usage.setTranslations(new ArrayList<>());
				usage.setDefinitions(new ArrayList<>());
				usage.setOdDefinitions(new ArrayList<>());
				usage.setOdAlternatives(new ArrayList<>());
				usage.setAuthors(new ArrayList<>());
				usage.setSourceLinks(new ArrayList<>());
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

	public List<Note> composeNotes(List<NoteSourceTuple> noteSourceTuples) {

		List<Note> notes = new ArrayList<>();

		Map<Long, Note> noteMap = new HashMap<>();
		List<SourceLink> sourceLinks;

		for (NoteSourceTuple tuple : noteSourceTuples) {

			Long noteId = tuple.getFreeformId();
			Long sourceLinkId = tuple.getSourceLinkId();

			Note note = noteMap.get(noteId);
			if (note == null) {
				note = new Note();
				sourceLinks = new ArrayList<>();
				note.setSourceLinks(sourceLinks);
				note.setId(noteId);
				note.setValueText(tuple.getValueText());
				note.setValuePrese(tuple.getValuePrese());
				note.setComplexity(tuple.getComplexity());
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

	public List<LexemeLangGroup> composeLexemeLangGroups(List<Lexeme> lexemes, List<ClassifierSelect> languagesOrder) {

		List<String> langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
		List<String> selectedLangCodes = languagesOrder.stream().filter(ClassifierSelect::isSelected).map(ClassifierSelect::getCode).collect(Collectors.toList());
		List<LexemeLangGroup> lexemeLangGroups = new ArrayList<>();
		Map<String, LexemeLangGroup> lexemeLangGroupMap = new HashMap<>();
		List<Lexeme> lexemesOrderBy = lexemes.stream().sorted(Comparator.comparing(Lexeme::getOrderBy)).collect(Collectors.toList());

		for (Lexeme lexeme : lexemesOrderBy) {
			String lang = lexeme.getWordLang();
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

	public List<Definition> composeMeaningDefinitions(List<DefinitionRefTuple> definitionRefTuples) {

		List<Definition> definitions = new ArrayList<>();
		Map<Long, Definition> definitionMap = new HashMap<>();

		for (DefinitionRefTuple definitionRefTuple : definitionRefTuples) {

			Long definitionId = definitionRefTuple.getDefinitionId();
			Long sourceLinkId = definitionRefTuple.getSourceLinkId();
			Definition definition = definitionMap.get(definitionId);
			if (definition == null) {
				String definitionValue = definitionRefTuple.getDefinitionValue();
				String definitionLang = definitionRefTuple.getDefinitionLang();
				Complexity definitionComplexity = definitionRefTuple.getDefinitionComplexity();
				Long definitionOrderBy = definitionRefTuple.getDefinitionOrderBy();
				String definitionTypeCode = definitionRefTuple.getDefinitionTypeCode();
				String definitionTypeValue = definitionRefTuple.getDefinitionTypeValue();
				List<String> definitionDatasetCodes = definitionRefTuple.getDefinitionDatasetCodes();
				definition = new Definition();
				definition.setId(definitionId);
				definition.setValue(definitionValue);
				definition.setLang(definitionLang);
				definition.setComplexity(definitionComplexity);
				definition.setOrderBy(definitionOrderBy);
				definition.setTypeCode(definitionTypeCode);
				definition.setTypeValue(definitionTypeValue);
				definition.setDatasetCodes(definitionDatasetCodes);
				definition.setSourceLinks(new ArrayList<>());
				definitionMap.put(definitionId, definition);
				definitions.add(definition);
			}
			if (sourceLinkId != null) {
				ReferenceType sourceLinkType = definitionRefTuple.getSourceLinkType();
				String sourceLinkName = definitionRefTuple.getSourceLinkName();
				String sourceLinkValue = definitionRefTuple.getSourceLinkValue();
				SourceLink sourceLink = new SourceLink();
				sourceLink.setId(sourceLinkId);
				sourceLink.setType(sourceLinkType);
				sourceLink.setName(sourceLinkName);
				sourceLink.setValue(sourceLinkValue);
				definition.getSourceLinks().add(sourceLink);
			}
		}

		return definitions;
	}

	public List<DefinitionLangGroup> composeMeaningDefinitionLangGroups(List<Definition> definitions, List<ClassifierSelect> languagesOrder) {

		List<String> langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
		List<String> selectedLangCodes = languagesOrder.stream().filter(ClassifierSelect::isSelected).map(ClassifierSelect::getCode).collect(Collectors.toList());
		List<DefinitionLangGroup> definitionLangGroups = new ArrayList<>();
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
			wordGroup.setGroupTypeLabel(wordGroup.getMembers().get(0).getRelationTypeLabel());
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
				groupedRelations.forEach(relation -> relation.setWordLexemeDatasetCodes(Collections.emptyList()));
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

	public List<SynRelation> composeSynRelations(List<SynRelationParamTuple> synRelationParamTuples) {

		List<SynRelation> synRelations = new ArrayList<>();
		Map<Long, SynRelation> relationMap = new HashMap<>();

		List<String> relationParamNames = new ArrayList<>();

		Integer otherHomonymNo = null;
		for (SynRelationParamTuple paramTuple : synRelationParamTuples) {
			SynRelation relation = relationMap.get(paramTuple.getRelationId());
			if (relation == null) {
				relation = new SynRelation();
				relation.setId(paramTuple.getRelationId());
				relation.setWord(paramTuple.getWord());
				relation.setWordId(paramTuple.getWordId());
				relation.setOppositeWordId(paramTuple.getOppositeWordId());
				relation.setOrderBy(paramTuple.getOrderBy());
				relation.setRelationStatus(paramTuple.getRelationStatus());
				relation.setHomonymNumber(paramTuple.getHomonymNumber());
				relation.setDefinition(paramTuple.getDefinitionValue());
				relation.setSuffixoid(paramTuple.getSuffixoid());
				relation.setPrefixoid(paramTuple.getPrefixoid());

				otherHomonymNo = paramTuple.getOtherHomonymNumber();

				relation.setRelationParams(new ArrayList<>());
				relationMap.put(relation.getId(), relation);
				synRelations.add(relation);

				relationParamNames.clear();
			} else if (StringUtils.isBlank(relation.getDefinition())) {
				relation.setDefinition(paramTuple.getDefinitionValue());
			}

			if (otherHomonymNo != paramTuple.getOtherHomonymNumber()) {
				relation.setOtherHomonymsExist(true);
			}

			if (paramTuple.getOppositeRelationStatus() != null) {
				relation.setOppositeRelationStatus(paramTuple.getOppositeRelationStatus());
			}

			if (StringUtils.isNotBlank(paramTuple.getParamName()) && !relationParamNames.contains(paramTuple.getParamName())) {
				RelationParam param = new RelationParam();
				param.setName(paramTuple.getParamName());
				param.setValue(paramTuple.getParamValue());
				relation.getRelationParams().add(param);

				relationParamNames.add(param.getName());
			}
		}
		return synRelations;
	}

	public void setWordTypeFlags(Word word, List<Classifier> wordTypes) {

		if (CollectionUtils.isNotEmpty(wordTypes)) {
			boolean isPrefixoid = wordTypes.stream().anyMatch(type -> type.getCode().equals(WORD_TYPE_CODE_PREFIXOID));
			boolean isSuffixoid = wordTypes.stream().anyMatch(type -> type.getCode().equals(WORD_TYPE_CODE_SUFFIXOID));
			word.setPrefixoid(isPrefixoid);
			word.setSuffixoid(isSuffixoid);
		}
	}

	public List<MeaningWordLangGroup> composeMeaningWordLangGroups(List<MeaningWord> meaningWords, String mainWordLang) {

		List<MeaningWordLangGroup> meaningWordLangGroups = new ArrayList<>();
		Map<String, MeaningWordLangGroup> meaningWordLangGroupMap = new HashMap<>();
		List<MeaningWord> meaningWordsOrderBy = meaningWords.stream().sorted(Comparator.comparing(MeaningWord::getOrderBy)).collect(Collectors.toList());

		for (MeaningWord meaningWord : meaningWordsOrderBy) {
			String lang = meaningWord.getLanguage();
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

		meaningWordLangGroups.sort(Comparator.comparing(meaningWordLangGroup -> !StringUtils.equals(meaningWordLangGroup.getLang(), mainWordLang)));
		return meaningWordLangGroups;
	}
}
