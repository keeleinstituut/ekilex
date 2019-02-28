package eki.ekilex.service.util;

import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

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
import eki.ekilex.data.Form;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.TermMeaningWord;
import eki.ekilex.data.TermMeaningWordTuple;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageDefinition;
import eki.ekilex.data.UsageTranslation;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.WordGroup;

@Component
public class ConversionUtil {

	private static final Logger logger = LoggerFactory.getLogger(ConversionUtil.class);

	public static String getClassifierValue(String code, List<Classifier> classifiers) {
		Optional<Classifier> classifier = classifiers.stream().filter(c -> c.getCode().equals(code)).findFirst();
		return classifier.isPresent() ? classifier.get().getValue() : code;
	}

	public List<TermMeaning> composeTermMeanings(List<TermMeaningWordTuple> termMeaningWordTuples) {
		
		List<TermMeaning> termMeanings = new ArrayList<>();

		Map<Long, TermMeaning> termMeaningMap = new HashMap<>();
		Map<Long, TermMeaningWord> termMeaningMainWordMap = new HashMap<>();
		Map<Long, TermMeaningWord> termMeaningOtherWordMap = new HashMap<>();

		TermMeaningWord termMeaningMainWord;

		for (TermMeaningWordTuple tuple : termMeaningWordTuples) {

			Long meaningId = tuple.getMeaningId();
			TermMeaning termMeaning = termMeaningMap.get(meaningId);
			if (termMeaning == null) {
				termMeaning = new TermMeaning();
				termMeaning.setMeaningId(meaningId);
				termMeaning.setConceptId(tuple.getConceptId());
				termMeaning.setMainWord(null);
				termMeaning.setOtherWords(new ArrayList<>());
				termMeanings.add(termMeaning);
				termMeaningMap.put(meaningId, termMeaning);
			}

			Long mainWordId = tuple.getMainWordId();
			if (mainWordId != null) {
				termMeaningMainWord = termMeaningMainWordMap.get(mainWordId);
				if (termMeaningMainWord == null) {
					termMeaningMainWord = new TermMeaningWord();
					termMeaningMainWord.setMeaningId(meaningId);
					termMeaningMainWord.setWordId(tuple.getMainWordId());
					termMeaningMainWord.setWord(tuple.getMainWord());
					termMeaningMainWord.setHomonymNr(tuple.getMainWordHomonymNr());
					termMeaningMainWord.setWordLang(tuple.getMainWordLang());
					termMeaningMainWord.setDatasetCodesWrapup(tuple.getMainWordDatasetCodesWrapup());
					termMeaningMainWordMap.put(mainWordId, termMeaningMainWord);
				}
				termMeaning.setMainWord(termMeaningMainWord);
			}

			Long otherWordId = tuple.getOtherWordId();
			if (otherWordId != null) {
				TermMeaningWord termMeaningOtherWord = termMeaningOtherWordMap.get(otherWordId);
				if (termMeaningOtherWord == null) {
					termMeaningOtherWord = new TermMeaningWord();
					termMeaningOtherWord.setMeaningId(meaningId);
					termMeaningOtherWord.setWordId(tuple.getOtherWordId());
					termMeaningOtherWord.setWord(tuple.getOtherWord());
					termMeaningOtherWord.setHomonymNr(tuple.getOtherWordHomonymNr());
					termMeaningOtherWord.setWordLang(tuple.getOtherWordLang());
					termMeaningOtherWord.setOrderBy(tuple.getOtherWordOrderBy());
					termMeaningOtherWord.setDatasetCodesWrapup(tuple.getOtherWordDatasetCodesWrapup());
					termMeaningOtherWordMap.put(otherWordId, termMeaningOtherWord);
				}
				List<TermMeaningWord> termMeaningOtherWords = termMeaning.getOtherWords();
				if (!termMeaningOtherWords.contains(termMeaningOtherWord)) {
					termMeaningOtherWords.add(termMeaningOtherWord);
				}
			}
		}

		for (TermMeaning termMeaning : termMeanings) {
			if ((termMeaning.getMainWord() == null) && StringUtils.isBlank(termMeaning.getConceptId())) {
				termMeaning.setConceptId("#");
			}
			termMeaning.getOtherWords().sort(Comparator.comparing(TermMeaningWord::getOrderBy));
		}
		return termMeanings;
	}

	public Classifier classifierFromIdString(String idString) {
		JsonParser jsonParser = JsonParserFactory.getJsonParser();
		Map<String, Object>  memberMap = jsonParser.parseMap(idString);
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

		for (UsageTranslationDefinitionTuple tuple : usageTranslationDefinitionTuples) {

			Long usageId = tuple.getUsageId();
			Long usageTranslationId = tuple.getUsageTranslationId();
			Long usageDefinitionId = tuple.getUsageDefinitionId();
			Long usageSourceLinkId = tuple.getUsageSourceLinkId();

			Usage usage = usageMap.get(usageId);
			if (usage == null) {
				usage = new Usage();
				usage.setId(usageId);
				usage.setValue(tuple.getUsageValue());
				usage.setLang(tuple.getUsageLang());
				usage.setTypeCode(tuple.getUsageTypeCode());
				usage.setTypeValue(tuple.getUsageTypeValue());
				usage.setTranslations(new ArrayList<>());
				usage.setDefinitions(new ArrayList<>());
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
		}
		return usages;
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
				Long definitionOrderBy = definitionRefTuple.getDefinitionOrderBy();
				definition = new Definition();
				definition.setId(definitionId);
				definition.setValue(definitionValue);
				definition.setLang(definitionLang);
				definition.setOrderBy(definitionOrderBy);
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

	public List<List<Relation>> groupRelationsById(List<Relation> relations) {
		Map<Long, List<Relation>> groupedById = relations.stream().collect(groupingBy(Relation::getId));
		return new ArrayList<>(groupedById.values());
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

}
