package eki.ekilex.service.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.exception.ApiException;
import eki.common.exception.OperationDeniedException;
import eki.ekilex.data.api.Form;
import eki.ekilex.data.api.FormUnit;
import eki.ekilex.data.api.Paradigm;
import eki.ekilex.data.api.ParadigmForm;
import eki.ekilex.data.api.ParadigmWrapper;
import eki.ekilex.service.db.MorphologyDbService;

@Component
public class MorphologyService {

	private static final Logger logger = LoggerFactory.getLogger(MorphologyService.class);

	@Autowired
	private MorphologyDbService morphologyDbService;

	@Transactional
	public List<Paradigm> getParadigms(Long wordId) {

		List<Paradigm> paradigms = morphologyDbService.getParadigms(wordId);
		for (Paradigm paradigm : paradigms) {
			List<ParadigmForm> paradigmForms = morphologyDbService.getParadigmForms(paradigm.getId());
			paradigm.setParadigmForms(paradigmForms);
		}
		return paradigms;
	}

	@Transactional
	public void save(ParadigmWrapper paradigmWrapper) throws Exception {

		if (paradigmWrapper == null) {
			return;
		}

		List<Paradigm> providedParadigms = paradigmWrapper.getParadigms();
		validate(providedParadigms);

		Map<Long, List<Paradigm>> providedWordParadigmMap = providedParadigms.stream()
				.collect(Collectors.groupingBy(Paradigm::getWordId));
		List<Long> wordIds = new ArrayList<>(providedWordParadigmMap.keySet());
		Collections.sort(wordIds);

		logger.info("Saving {} paradigms for {} words", providedParadigms.size(), wordIds.size());

		for (Long wordId : wordIds) {

			List<Form> existingWordForms = morphologyDbService.getForms(wordId);
			List<Paradigm> providedWordParadigms = providedWordParadigmMap.get(wordId);
			List<Form> providedWordForms = providedWordParadigms.stream()
					.map(Paradigm::getParadigmForms)
					.flatMap(List::stream)
					.map(form -> new Form(null, form.getValue(), form.getValuePrese(), form.getMorphCode()))
					.distinct()
					.collect(Collectors.toList());

			Map<FormUnit, Long> formIdMap = new HashMap<>();

			for (Form providedWordForm : providedWordForms) {

				String providedWordFormValuePrese = providedWordForm.getValuePrese();
				Form existingWordForm = getWordForm(providedWordForm, existingWordForms);
				Long formId;
				if (existingWordForm == null) {
					formId = morphologyDbService.createForm(providedWordForm);
				} else if (StringUtils.isNotBlank(providedWordFormValuePrese)
						&& !StringUtils.equals(providedWordFormValuePrese, existingWordForm.getValuePrese())) {
					formId = existingWordForm.getId();
					morphologyDbService.updateForm(formId, providedWordFormValuePrese);
				} else {
					formId = existingWordForm.getId();
				}
				String formValue = providedWordForm.getValue();
				String morphCode = providedWordForm.getMorphCode();
				FormUnit formUnit = new FormUnit(formValue, morphCode);
				formIdMap.put(formUnit, formId);
			}

			for (Form existingWordForm : existingWordForms) {

				Long formId = existingWordForm.getId();
				String formValue = existingWordForm.getValue();
				String morphCode = existingWordForm.getMorphCode();
				FormUnit formUnit = new FormUnit(formValue, morphCode);
				if (!formIdMap.containsKey(formUnit)) {
					boolean isFormInUse = morphologyDbService.isFormInUse(formId);
					if (isFormInUse) {
						throw new OperationDeniedException("Can't delete form. Form \"" + formValue + " - " + morphCode + "\" is in use by a collocation");
					}
					morphologyDbService.deleteForm(formId);
				}
			}

			morphologyDbService.deleteParadigmsForWord(wordId);

			for (Paradigm providedWordParadigm : providedWordParadigms) {

				Long paradigmId = morphologyDbService.createParadigm(wordId, providedWordParadigm);
				List<ParadigmForm> providedParadigmForms = providedWordParadigm.getParadigmForms();

				for (ParadigmForm providedParadigmForm : providedParadigmForms) {

					String formValue = providedParadigmForm.getValue();
					String morphCode = providedParadigmForm.getMorphCode();
					FormUnit formUnit = new FormUnit(formValue, morphCode);
					Long formId = formIdMap.get(formUnit);

					morphologyDbService.createParadigmForm(paradigmId, formId, providedParadigmForm);
				}
			}
		}

		logger.info("Done saving paradigms for words {}", wordIds);
	}

	private void validate(List<Paradigm> paradigms) throws ApiException {
		if (CollectionUtils.isEmpty(paradigms)) {
			throw new ApiException("Paradigms not present");
		}
		for (Paradigm paradigm : paradigms) {
			if (paradigm.getWordId() == null) {
				throw new ApiException("Word not specified");
			}
			if (CollectionUtils.isEmpty(paradigm.getParadigmForms())) {
				throw new ApiException("Paradigm has no forms");
			}
		}
	}

	private Form getWordForm(Form find, List<Form> forms) {
		if (CollectionUtils.isEmpty(forms)) {
			return null;
		}
		for (Form form : forms) {
			if (StringUtils.equals(find.getValue(), form.getValue())
					&& StringUtils.equals(find.getMorphCode(), form.getMorphCode())) {
				return form;
			}
		}
		return null;
	}

}
