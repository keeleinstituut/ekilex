package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Form;
import eki.wordweb.data.Paradigm;

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
				double formFreqRankScaledRoundedDec = Math.round(((double) form.getFormFreqRank() * (double) FORM_FREQ_SCALE) / (double) form.getFormFreqRankMax());
				int formFreqRankScaled = FORM_FREQ_SCALE - Double.valueOf(formFreqRankScaledRoundedDec).intValue();
				form.setFormFreqRankScaled(formFreqRankScaled);
			}
			if (form.getMorphFreqValue() != null) {
				double morphFreqRankScaledRoundedDec = Math.round(((double) form.getMorphFreqRank() * (double) FORM_FREQ_SCALE) / (double) form.getMorphFreqRankMax());
				int morphFreqRankScaled = FORM_FREQ_SCALE - Double.valueOf(morphFreqRankScaledRoundedDec).intValue();
				form.setMorphFreqRankScaled(morphFreqRankScaled);
			}
		}
	}

	public List<Paradigm> composeParadigms(List<Form> allForms, String displayLang) {
		Map<Long, List<Form>> paradigmsMap = allForms.stream().collect(Collectors.groupingBy(Form::getParadigmId));
		List<Long> paradigmIds = new ArrayList<>(paradigmsMap.keySet());
		Collections.sort(paradigmIds);
		List<Paradigm> paradigms = new ArrayList<>();
		for (Long paradigmId : paradigmIds) {
			List<Form> paradigmForms = paradigmsMap.get(paradigmId);
			Paradigm paradigm = composeParadigm(paradigmId, paradigmForms, displayLang);
			paradigms.add(paradigm);
		}
		return paradigms;
	}

	public Paradigm composeParadigm(Long paradigmId, List<Form> paradigmForms, String displayLang) {

		if (CollectionUtils.isEmpty(paradigmForms)) {
			return null;
		}

		paradigmForms.sort(Comparator.comparing(Form::getOrderBy));

		for (Form form : paradigmForms) {
			classifierUtil.applyClassifiers(form, displayLang);
		}

		Form firstForm = paradigmForms.get(0);
		String wordClass = firstForm.getWordClass();
		String paradigmComment = firstForm.getParadigmComment();
		String inflectionType = firstForm.getInflectionType();
		String inflectionTypeNr = firstForm.getInflectionTypeNr();

		Map<String, List<Form>> formMorphCodeMap = paradigmForms.stream().collect(Collectors.groupingBy(Form::getMorphCode));
		Paradigm paradigm = new Paradigm();
		paradigm.setParadigmId(paradigmId);
		paradigm.setWordClass(wordClass);
		paradigm.setComment(paradigmComment);
		paradigm.setInflectionType(inflectionType);
		paradigm.setInflectionTypeNr(inflectionTypeNr);
		paradigm.setFormMorphCodeMap(formMorphCodeMap);
		return paradigm;
	}

}
