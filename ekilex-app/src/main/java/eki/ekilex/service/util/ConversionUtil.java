package eki.ekilex.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Form;
import eki.ekilex.data.FormRelation;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Rection;
import eki.ekilex.data.RectionUsageTranslationDefinitionTuple;
import eki.ekilex.data.UsageMeaning;
import eki.ekilex.data.UsageMember;

@Component
public class ConversionUtil {

	private static final Logger logger = LoggerFactory.getLogger(ConversionUtil.class);

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
			List<Form> forms = paradigm.getForms();
			String title = null;
			for (Form form : forms) {
				if (!form.isWord()) {
					title = form.getDisplayForm();
					if (StringUtils.isBlank(title)) {
						title = form.getValue();
					}
					break;
				}
			}
			paradigm.setTitle(title);
		} else {
			for (Paradigm thisParadigm : paradigms) {
				String title = null;
				Long paradigmId = thisParadigm.getParadigmId();
				List<Form> thisForms = thisParadigm.getForms();
				for (Form thisForm : thisForms) {
					String thisMorphCode = thisForm.getMorphCode();
					String titleCandidate = thisForm.getDisplayForm();
					if (StringUtils.isBlank(titleCandidate)) {
						titleCandidate = thisForm.getValue();
					}
					boolean isDifferentTitle = isDifferentTitle(paradigms, paradigmId, thisMorphCode, titleCandidate);
					if (isDifferentTitle) {
						title = titleCandidate;
						break;
					}
				}
				if (StringUtils.isBlank(title)) {
					logger.warn("Could not compose paradigm title. Fix this!");
				}
				thisParadigm.setTitle(title);
			}
		}
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

	public List<Rection> composeRections(List<RectionUsageTranslationDefinitionTuple> rectionUsageTranslationDefinitionTuples) {

		List<Rection> rections = new ArrayList<>();

		Map<Long, Rection> rectionMap = new HashMap<>();
		Map<Long, UsageMeaning> usageMeaningMap = new HashMap<>();
		Map<Long, UsageMember> usageMap = new HashMap<>();
		Map<Long, UsageMember> usageTranslationMap = new HashMap<>();
		Map<Long, UsageMember> usageDefinitionMap = new HashMap<>();

		for (RectionUsageTranslationDefinitionTuple tuple : rectionUsageTranslationDefinitionTuples) {

			Long rectionId = tuple.getRectionId();
			Long usageMeaningId = tuple.getUsageMeaningId();
			Long usageId = tuple.getUsageId();
			Long usageTranslationId = tuple.getUsageTranslationId();
			Long usageDefinitionId = tuple.getUsageDefinitionId();

			Rection rection = rectionMap.get(rectionId);
			if (rection == null) {
				rection = new Rection();
				rection.setId(rectionId);
				rection.setValue(tuple.getRectionValue());
				rection.setUsageMeanings(new ArrayList<>());
				rectionMap.put(rectionId, rection);
				rections.add(rection);
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
				rection.getUsageMeanings().add(usageMeaning);
			}
			if (usageId != null) {
				UsageMember usage = usageMap.get(usageId);
				if (usage == null) {
					usage = new UsageMember();
					usage.setId(usageId);
					usage.setValue(tuple.getUsageValue());
					usage.setLang(tuple.getUsageLang());
					usage.setAuthor(tuple.getUsageAuthor());
					usage.setAuthorType(tuple.getUsageAuthorType());
					usage.setType(tuple.getUsageType());
					usageMap.put(usageId, usage);
					usageMeaning.getUsages().add(usage);
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
		return rections;
	}
}
