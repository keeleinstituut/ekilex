package eki.ekilex.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			form.setComponents(tuple.getComponents());
			form.setDisplayForm(tuple.getDisplayForm());
			form.setVocalForm(tuple.getVocalForm());
			form.setMorphCode(tuple.getMorphCode());
			form.setMorphValue(tuple.getMorphValue());
			forms.add(form);
		}

		for (FormRelation formRelation : wordFormRelations) {

			Long paradigmId = formRelation.getParadigmId();

			Paradigm paradigm = paradigmsMap.get(paradigmId);
			formRelations = paradigm.getFormRelations();
			formRelations.add(formRelation);
		}

		return paradigms;
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
