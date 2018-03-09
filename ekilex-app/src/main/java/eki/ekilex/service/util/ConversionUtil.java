package eki.ekilex.service.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationRelGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.Form;
import eki.ekilex.data.FormRelation;
import eki.ekilex.data.Government;
import eki.ekilex.data.GovernmentUsageTranslationDefinitionTuple;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.RefLink;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.UsageMeaning;
import eki.ekilex.data.UsageMember;
import eki.ekilex.data.WordTuple;

@Component
public class ConversionUtil {

	private static final Logger logger = LoggerFactory.getLogger(ConversionUtil.class);

	public List<TermMeaning> convert(Map<Long, List<WordTuple>> termMeaningsMap) {
		List<TermMeaning> termMeanings = termMeaningsMap.entrySet().stream()
				.map(meaningMapEntry -> {
					TermMeaning termMeaning = new TermMeaning();
					termMeaning.setMeaningId(meaningMapEntry.getKey());
					termMeaning.setWordTuples(meaningMapEntry.getValue());
					return termMeaning;
				}).sorted(Comparator.comparing(TermMeaning::getMeaningId))
				.collect(Collectors.toList());
		return termMeanings;
	}

	public List<Paradigm> composeParadigms(List<ParadigmFormTuple> paradigmFormTuples, List<FormRelation> wordFormRelations) {

		List<Paradigm> paradigms = new ArrayList<>();

		Map<Long, Paradigm> paradigmsMap = new HashMap<>();
		List<Form> forms;
		List<FormRelation> formRelations; 

		for (ParadigmFormTuple tuple : paradigmFormTuples) {

			Long paradigmId = tuple.getParadigmId();

			Paradigm paradigm = paradigmsMap.get(paradigmId);
			if (paradigm == null) {
				forms = new ArrayList<>();
				formRelations = new ArrayList<>();
				paradigm = new Paradigm();
				paradigm.setParadigmId(paradigmId);
				paradigm.setInflectionTypeNr(tuple.getInflectionTypeNr());
				paradigm.setForms(forms);
				paradigm.setFormRelations(formRelations);
				paradigmsMap.put(paradigmId, paradigm);
				paradigms.add(paradigm);
			} else {
				forms = paradigm.getForms();
			}
			Form form = new Form();
			form.setId(tuple.getFormId());
			form.setValue(tuple.getForm());
			form.setWord(tuple.isWord());
			form.setComponents(tuple.getComponents());
			form.setDisplayForm(tuple.getDisplayForm());
			form.setVocalForm(tuple.getVocalForm());
			form.setMorphCode(tuple.getMorphCode());
			form.setMorphValue(tuple.getMorphValue());
			forms.add(form);
		}
		composeParadigmTitles(paradigms);
		flagFormMorphCodes(paradigms);
		flagFormsExist(paradigms);

		for (FormRelation formRelation : wordFormRelations) {

			Long paradigmId = formRelation.getParadigmId();

			Paradigm paradigm = paradigmsMap.get(paradigmId);
			formRelations = paradigm.getFormRelations();
			formRelations.add(formRelation);
		}

		return paradigms;
	}

	private void composeParadigmTitles(List<Paradigm> paradigms) {

		if (CollectionUtils.isEmpty(paradigms)) {
			return;
		}
		if (paradigms.size() == 1) {
			Paradigm paradigm = paradigms.get(0);
			String title = getFirstAvailableTitle(paradigm, false);
			if (StringUtils.isBlank(title)) {
				title = getFirstAvailableTitle(paradigm, true);
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

	private String getFirstAvailableTitle(Paradigm paradigm, boolean isWord) {

		List<Form> forms = paradigm.getForms();
		for (Form form : forms) {
			if (form.isWord() == isWord) {
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

	public List<Government> composeGovernments(List<GovernmentUsageTranslationDefinitionTuple> governmentUsageTranslationDefinitionTuples) {

		List<Government> governments = new ArrayList<>();

		Map<Long, Government> governmentMap = new HashMap<>();
		Map<Long, UsageMeaning> usageMeaningMap = new HashMap<>();
		Map<Long, UsageMember> usageMap = new HashMap<>();
		Map<Long, UsageMember> usageTranslationMap = new HashMap<>();
		Map<Long, UsageMember> usageDefinitionMap = new HashMap<>();

		for (GovernmentUsageTranslationDefinitionTuple tuple : governmentUsageTranslationDefinitionTuples) {

			Long governmentId = tuple.getGovernmentId();
			Long usageMeaningId = tuple.getUsageMeaningId();
			Long usageId = tuple.getUsageId();
			Long usageTranslationId = tuple.getUsageTranslationId();
			Long usageDefinitionId = tuple.getUsageDefinitionId();
			Long usageSourceRefLinkId = tuple.getUsageSourceRefLinkId();

			Government government = governmentMap.get(governmentId);
			if (government == null) {
				government = new Government();
				government.setId(governmentId);
				government.setValue(tuple.getGovernmentValue());
				government.setUsageMeanings(new ArrayList<>());
				governmentMap.put(governmentId, government);
				governments.add(government);
			}
			if (usageMeaningId == null) {
				continue;
			}
			UsageMeaning usageMeaning = usageMeaningMap.get(usageMeaningId);
			if (usageMeaning == null) {
				usageMeaning = new UsageMeaning();
				usageMeaning.setId(usageMeaningId);
				usageMeaning.setUsages(new ArrayList<>());
				usageMeaning.setUsageTranslations(new ArrayList<>());
				usageMeaning.setUsageDefinitions(new ArrayList<>());
				usageMeaningMap.put(usageMeaningId, usageMeaning);
				government.getUsageMeanings().add(usageMeaning);
			}
			if (usageId != null) {
				UsageMember usage = usageMap.get(usageId);
				if (usage == null) {
					usage = new UsageMember();
					usage.setId(usageId);
					usage.setType(tuple.getUsageType());
					usage.setValue(tuple.getUsageValue());
					usage.setLang(tuple.getUsageLang());
					usage.setAuthor(tuple.getUsageAuthor());
					usage.setTranslator(tuple.getUsageTranslator());
					usage.setRefLinks(new ArrayList<>());
					usageMap.put(usageId, usage);
					usageMeaning.getUsages().add(usage);
				}
				if (usageSourceRefLinkId != null) {
					RefLink usageSource = new RefLink();
					usageSource.setId(tuple.getUsageSourceRefLinkId());
					usageSource.setName(tuple.getUsageSourceRefLinkName());
					usageSource.setValue(tuple.getUsageSourceRefLinkValue());
					usage.getRefLinks().add(usageSource);
				}
			}
			if (usageTranslationId != null) {
				UsageMember usageTranslation = usageTranslationMap.get(usageTranslationId);
				if (usageTranslation == null) {
					usageTranslation = new UsageMember();
					usageTranslation.setId(usageTranslationId);
					usageTranslation.setValue(tuple.getUsageTranslationValue());
					usageTranslation.setLang(tuple.getUsageTranslationLang());
					usageTranslationMap.put(usageTranslationId, usageTranslation);
					usageMeaning.getUsageTranslations().add(usageTranslation);
				}
			}
			if (usageDefinitionId != null) {
				UsageMember usageDefinition = usageDefinitionMap.get(usageDefinitionId);
				if (usageDefinition == null) {
					usageDefinition = new UsageMember();
					usageDefinition.setId(usageDefinitionId);
					usageDefinition.setValue(tuple.getUsageDefinitionValue());
					usageDefinition.setLang(tuple.getUsageDefinitionLang());
					usageDefinitionMap.put(usageDefinitionId, usageDefinition);
					usageMeaning.getUsageDefinitions().add(usageDefinition);
				}
			}
		}
		return governments;
	}

	public List<Definition> composeMeaningDefinitions(List<DefinitionRefTuple> definitionRefTuples) {

		List<Definition> definitions = new ArrayList<>();
		Map<Long, Definition> definitionMap = new HashMap<>();

		for (DefinitionRefTuple definitionRefTuple : definitionRefTuples) {

			Long definitionId = definitionRefTuple.getDefinitionId();
			Long refLinkId = definitionRefTuple.getRefLinkId();
			Definition definition = definitionMap.get(definitionId);
			if (definition == null) {
				String definitionValue = definitionRefTuple.getDefinitionValue();
				Long definitionOrderBy = definitionRefTuple.getDefinitionOrderBy();
				definition = new Definition();
				definition.setId(definitionId);
				definition.setValue(definitionValue);
				definition.setOrderBy(definitionOrderBy);
				definition.setRefLinks(new ArrayList<>());
				definitionMap.put(definitionId, definition);
				definitions.add(definition);
			}
			if (refLinkId != null) {
				String refLinkName = definitionRefTuple.getRefLinkName();
				String refLinkValue = definitionRefTuple.getRefLinkValue();
				RefLink refLink = new RefLink();
				refLink.setId(refLinkId);
				refLink.setName(refLinkName);
				refLink.setValue(refLinkValue);
				definition.getRefLinks().add(refLink);
			}
		}

		return definitions;
	}

	public List<CollocationPosGroup> composeCollocPosGroups(List<CollocationTuple> collocTuples) {

		List<CollocationPosGroup> collocationPosGroups = new ArrayList<>();
		Map<Long, CollocationPosGroup> collocPosGroupMap = new HashMap<>();
		Map<Long, CollocationRelGroup> collocRelGroupMap = new HashMap<>();
		String name;
		Float frequency;
		Float score;

		for (CollocationTuple collocTuple : collocTuples) {

			Long collocationPosGroupId = collocTuple.getCollocationPosGroupId();
			Long collocationRelGroupId = collocTuple.getCollocationRelGroupId();
			CollocationPosGroup collocationPosGroup = collocPosGroupMap.get(collocationPosGroupId);
			if (collocationPosGroup == null) {
				name = collocTuple.getCollocationPosGroupName();
				collocationPosGroup = new CollocationPosGroup();
				collocationPosGroup.setName(name);
				collocationPosGroup.setRelationGroups(new ArrayList<>());
				collocPosGroupMap.put(collocationPosGroupId, collocationPosGroup);
				collocationPosGroups.add(collocationPosGroup);
			}
			CollocationRelGroup collocationRelGroup = collocRelGroupMap.get(collocationRelGroupId);
			if (collocationRelGroup == null) {
				name = collocTuple.getCollocationRelGroupName();
				frequency = collocTuple.getCollocationRelGroupFrequency();
				score = collocTuple.getCollocationRelGroupScore();
				collocationRelGroup = new CollocationRelGroup();
				collocationRelGroup.setName(name);
				collocationRelGroup.setFrequency(frequency);
				collocationRelGroup.setScore(score);
				collocationRelGroup.setCollocations(new ArrayList<>());
				collocRelGroupMap.put(collocationRelGroupId, collocationRelGroup);
				collocationPosGroup.getRelationGroups().add(collocationRelGroup);
			}
			Long collocationWordId = collocTuple.getCollocationWordId();
			String colloc = collocTuple.getCollocation();
			frequency = collocTuple.getCollocationFrequency();
			score = collocTuple.getCollocationScore();
			List<String> collocationUsages = collocTuple.getCollocationUsages();
			Collocation collocation = new Collocation();
			collocation.setCollocationWordId(collocationWordId);
			collocation.setValue(colloc);
			collocation.setFrequency(frequency);
			collocation.setScore(score);
			collocation.setCollocationUsages(collocationUsages);
			collocationRelGroup.getCollocations().add(collocation);
		}
		return collocationPosGroups;
	}

}
