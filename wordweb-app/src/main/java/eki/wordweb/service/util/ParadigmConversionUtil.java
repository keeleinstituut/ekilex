package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Form;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.ParadigmGroup;
import eki.wordweb.data.StaticParadigm;
import eki.wordweb.data.Word;

@Component
public class ParadigmConversionUtil implements WebConstant, SystemConstant {

	@Autowired
	private ClassifierUtil classifierUtil;

	public void calcFreqScale(List<Form> forms) {
		if (CollectionUtils.isEmpty(forms)) {
			return;
		}
		for (Form form : forms) {
			if (form.getFormFreqValue() != null) {
				float scaledParadigmFormFreqRankRounded = Math.round(((float) form.getParadigmFormFreqRank() * (float) FORM_FREQ_SCALE) / (float) form.getMaxParadigmFormFreqRank());
				int scaledParadigmFormFreqRankInt = FORM_FREQ_SCALE - new Float(scaledParadigmFormFreqRankRounded).intValue();
				double scaledTotalFormFreqRankRounded = Math.round(((double) form.getTotalFormFreqRank() * (double) FORM_FREQ_SCALE) / (double) form.getMaxTotalFormFreqRank());
				int scaledTotalFormFreqRankInt = FORM_FREQ_SCALE - new Double(scaledTotalFormFreqRankRounded).intValue();
				form.setScaledTotalFormFreqRank(scaledTotalFormFreqRankInt);
				form.setScaledParadigmFormFreqRank(scaledParadigmFormFreqRankInt);
			}
		}
	}

	public List<StaticParadigm> composeStaticParadigms(List<Form> forms, String displayLang) {

		List<StaticParadigm> paradigms = new ArrayList<>();
		if (CollectionUtils.isEmpty(forms)) {
			return paradigms;
		}

		Map<Long, List<Form>> paradigmFormsMap = forms.stream().collect(Collectors.groupingBy(Form::getParadigmId));

		List<Long> paradigmIds = new ArrayList<>(paradigmFormsMap.keySet());
		Collections.sort(paradigmIds);

		for (Long paradigmId : paradigmIds) {

			List<Form> paradigmForms = paradigmFormsMap.get(paradigmId);
			paradigmForms.sort(Comparator.comparing(Form::getOrderBy));

			for (Form form : paradigmForms) {
				classifierUtil.applyClassifiers(form, displayLang);
			}

			Form firstForm = paradigmForms.get(0);
			String inflectionType = firstForm.getInflectionType();

			Map<String, List<Form>> formMorphCodeMap = paradigmForms.stream().collect(Collectors.groupingBy(Form::getMorphCode));
			StaticParadigm paradigm = new StaticParadigm();
			paradigm.setParadigmId(paradigmId);
			paradigm.setInflectionType(inflectionType);
			paradigm.setFormMorphCodeMap(formMorphCodeMap);
			paradigms.add(paradigm);
		}
		return paradigms;
	}

	public List<Paradigm> composeParadigms(Word word, List<Form> forms, String displayLang) {

		List<Paradigm> paradigms = new ArrayList<>();
		if (CollectionUtils.isEmpty(forms)) {
			return paradigms;
		}
		final String keyValSep = "-";
		String wordClass = word.getWordClass();
		Map<Long, List<Form>> paradigmFormsMap = forms.stream().collect(Collectors.groupingBy(Form::getParadigmId));
		List<Long> paradigmIds = new ArrayList<>(paradigmFormsMap.keySet());
		Collections.sort(paradigmIds);

		ParadigmGroup paradigmGroup1;
		ParadigmGroup paradigmGroup2;
		ParadigmGroup paradigmGroup3;
		String formGroupKey;
		List<Form> groupForms;
		List<ParadigmGroup> validParadigmGroups;
		Form firstForm;
		List<String> paradigmTitleElements;

		for (Long paradigmId : paradigmIds) {

			List<Form> paradigmForms = paradigmFormsMap.get(paradigmId);
			paradigmForms.sort(Comparator.comparing(Form::getOrderBy));

			for (Form form : paradigmForms) {
				classifierUtil.applyClassifiers(form, displayLang);
			}

			paradigmTitleElements = new ArrayList<>();
			if (StringUtils.isNotBlank(wordClass)) {
				paradigmTitleElements.add(wordClass);
			}
			firstForm = paradigmForms.get(0);
			if (StringUtils.isNotBlank(firstForm.getInflectionType())) {
				paradigmTitleElements.add(firstForm.getInflectionType());
			}
			String paradigmTitle = null;
			if (CollectionUtils.isNotEmpty(paradigmTitleElements)) {
				paradigmTitle = StringUtils.join(paradigmTitleElements, ", ");
			}
			boolean isExpandable = paradigmForms.stream().anyMatch(form -> form.getDisplayLevel() > 1);

			Paradigm paradigm = new Paradigm();
			paradigm.setParadigmId(paradigmId);
			paradigm.setTitle(paradigmTitle);
			paradigm.setGroups(new ArrayList<>());
			paradigm.setExpandable(isExpandable);
			paradigms.add(paradigm);

			Map<String, List<Form>> formGroupsMap = paradigmForms.stream()
					.collect(Collectors.groupingBy(form -> form.getMorphGroup1() + keyValSep + form.getMorphGroup2() + keyValSep + form.getMorphGroup3()));

			List<String> morphGroup1Names = paradigmForms.stream().filter(form -> StringUtils.isNotBlank(form.getMorphGroup1()))
					.map(Form::getMorphGroup1).distinct().collect(Collectors.toList());
			List<String> morphGroup2Names = paradigmForms.stream().filter(form -> StringUtils.isNotBlank(form.getMorphGroup2()))
					.map(Form::getMorphGroup2).distinct().collect(Collectors.toList());
			List<String> morphGroup3Names = paradigmForms.stream().filter(form -> StringUtils.isNotBlank(form.getMorphGroup3()))
					.map(Form::getMorphGroup3).distinct().collect(Collectors.toList());

			if (CollectionUtils.isEmpty(morphGroup1Names)) {
				ParadigmGroup paradigmGroup = new ParadigmGroup();
				paradigm.getGroups().add(paradigmGroup);
				distributeParadigmGroupForms(null, paradigmGroup, paradigmForms);
			} else {
				for (String morphGroup1Name : morphGroup1Names) {
					paradigmGroup1 = newParadigmGroup(morphGroup1Name);
					paradigm.getGroups().add(paradigmGroup1);
					if (CollectionUtils.isEmpty(morphGroup2Names)) {
						formGroupKey = morphGroup1Name + "-null-null";
						groupForms = formGroupsMap.get(formGroupKey);
						if (CollectionUtils.isEmpty(groupForms)) {
							continue;
						}
						distributeParadigmGroupForms(morphGroup1Names, paradigmGroup1, groupForms);
					} else {
						for (String morphGroup2Name : morphGroup2Names) {
							paradigmGroup2 = newParadigmGroup(morphGroup2Name);
							paradigmGroup1.getGroups().add(paradigmGroup2);
							if (CollectionUtils.isEmpty(morphGroup3Names)) {
								formGroupKey = morphGroup1Name + keyValSep + morphGroup2Name + "-null";
								groupForms = formGroupsMap.get(formGroupKey);
								if (CollectionUtils.isEmpty(groupForms)) {
									continue;
								}
								distributeParadigmGroupForms(morphGroup2Names, paradigmGroup2, groupForms);
							} else {
								for (String morphGroup3Name : morphGroup3Names) {
									formGroupKey = morphGroup1Name + keyValSep + morphGroup2Name + keyValSep + morphGroup3Name;
									groupForms = formGroupsMap.get(formGroupKey);
									if (CollectionUtils.isEmpty(groupForms)) {
										continue;
									}
									paradigmGroup3 = newParadigmGroup(morphGroup3Name);
									paradigmGroup2.getGroups().add(paradigmGroup3);
									distributeParadigmGroupForms(morphGroup3Names, paradigmGroup3, groupForms);
									calculateFormDisplayFlags(paradigmGroup3);
								}
							}
							validParadigmGroups = paradigmGroup2.getGroups().stream()
									.filter(paradigmGroup -> paradigmGroup.isFormsExist() || paradigmGroup.isGroupsExist())
									.collect(Collectors.toList());
							paradigmGroup2.setGroups(validParadigmGroups);
							calculateFormDisplayFlags(paradigmGroup2);
						}
					}
					validParadigmGroups = paradigmGroup1.getGroups().stream()
							.filter(paradigmGroup -> paradigmGroup.isFormsExist() || paradigmGroup.isGroupsExist())
							.collect(Collectors.toList());
					paradigmGroup1.setGroups(validParadigmGroups);
					calculateFormDisplayFlags(paradigmGroup1);
				}
				validParadigmGroups = paradigm.getGroups().stream()
						.filter(paradigmGroup -> paradigmGroup.isFormsExist() || paradigmGroup.isGroupsExist())
						.collect(Collectors.toList());
				paradigm.setGroups(validParadigmGroups);
			}
		}
		return paradigms;
	}

	private void calculateFormDisplayFlags(ParadigmGroup paradigmGroup) {
		List<Form> groupFormsTest = new ArrayList<>();
		groupFormsTest.addAll(paradigmGroup.getForms1());
		groupFormsTest.addAll(paradigmGroup.getForms2());
		boolean formsExist = CollectionUtils.isNotEmpty(groupFormsTest);
		boolean primaryFormsExist = groupFormsTest.stream().anyMatch(form -> form.getDisplayLevel() == 1);
		boolean groupsExist = CollectionUtils.isNotEmpty(paradigmGroup.getGroups());
		paradigmGroup.setFormsExist(formsExist);
		paradigmGroup.setPrimaryFormsExist(primaryFormsExist);
		paradigmGroup.setGroupsExist(groupsExist);
	}

	private ParadigmGroup newParadigmGroup(String morphGroupName) {
		ParadigmGroup paradigmGroup = new ParadigmGroup();
		paradigmGroup.setName(morphGroupName);
		paradigmGroup.setForms1(new ArrayList<>());
		paradigmGroup.setForms2(new ArrayList<>());
		paradigmGroup.setGroups(new ArrayList<>());
		return paradigmGroup;
	}

	private void distributeParadigmGroupForms(List<String> morphGroupNames, ParadigmGroup paradigmGroup, List<Form> groupForms) {

		List<String> groupMorphCodes = groupForms.stream().map(Form::getMorphCode).distinct().collect(Collectors.toList());
		Map<String, List<Form>> groupFormsByMorph = groupForms.stream().collect(Collectors.groupingBy(Form::getMorphCode));
		List<Form> groupedForms = new ArrayList<>();
		List<Form> morphForms;
		Form morphForm;
		List<String> forms;
		List<String> displayForms;
		String formsWrapup;
		String displayFormsWrapup;
		for (String morphCode : groupMorphCodes) {
			morphForms = groupFormsByMorph.get(morphCode);
			if (morphForms.size() > 1) {
				morphForms.sort(Comparator.comparing(Form::getDisplayLevel));
				morphForm = morphForms.get(0);
				forms = morphForms.stream().map(Form::getValue).collect(Collectors.toList());
				formsWrapup = StringUtils.join(forms, ALTERNATIVE_FORMS_SEPARATOR);
				displayForms = morphForms.stream().map(Form::getDisplayForm).collect(Collectors.toList());
				displayFormsWrapup = StringUtils.join(displayForms, ALTERNATIVE_FORMS_SEPARATOR);
			} else {
				morphForm = morphForms.get(0);
				formsWrapup = morphForm.getValue();
				displayFormsWrapup = morphForm.getDisplayForm();
			}
			if (StringUtils.isBlank(displayFormsWrapup)) {
				displayFormsWrapup = "-";
			}
			morphForm.setFormsWrapup(formsWrapup);
			morphForm.setDisplayFormsWrapup(displayFormsWrapup);
			groupedForms.add(morphForm);
		}
		if (CollectionUtils.isEmpty(morphGroupNames)) {
			paradigmGroup.setForms1(groupedForms);
		} else if (morphGroupNames.size() > 2) {
			paradigmGroup.getForms1().addAll(groupedForms);
		} else {
			if (CollectionUtils.isEmpty(paradigmGroup.getForms1())) {
				paradigmGroup.setForms1(groupedForms);
			} else if (CollectionUtils.isEmpty(paradigmGroup.getForms2())) {
				paradigmGroup.setForms2(groupedForms);
			}
		}
	}
}
