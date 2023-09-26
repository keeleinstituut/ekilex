package eki.ekilex.service.api;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.api.Form;
import eki.ekilex.data.api.Paradigm;
import eki.ekilex.data.api.ParadigmWrapper;
import eki.ekilex.service.db.MorphologyDbService;

@Component
public class MorphologyService {

	private static final Logger logger = LoggerFactory.getLogger(MorphologyService.class);

	@Autowired
	private MorphologyDbService morphologyDbService;

	@Transactional
	public void replace(ParadigmWrapper paradigmWrapper) {

		if (paradigmWrapper == null) {
			return;
		}

		List<Paradigm> paradigms = paradigmWrapper.getParadigms();
		List<Long> wordIds = paradigms.stream().map(Paradigm::getWordId).distinct().collect(Collectors.toList());

		logger.info("Replacing {} paradigms for {} words", paradigms.size(), wordIds.size());

		for (Long wordId : wordIds) {
			morphologyDbService.deleteParadigmsForWord(wordId);
		}
		morphologyDbService.deleteFloatingForms();

		for (Paradigm paradigm : paradigms) {
			Long paradigmId = morphologyDbService.createParadigm(paradigm);
			Long wordId = paradigm.getWordId();
			List<Form> forms = paradigm.getForms();
			boolean orderByExists = forms.stream().allMatch(form -> form.getOrderBy() != null);
			if (orderByExists) {
				forms = forms.stream().sorted(Comparator.comparingLong(Form::getOrderBy)).collect(Collectors.toList());
			}
			for (Form form : forms) {
				form.setParadigmId(paradigmId);
				morphologyDbService.createForm(form, wordId);
			}
		}
	}

	@Transactional
	public List<Paradigm> getParadigms(Long wordId) {

		List<Paradigm> paradigms = morphologyDbService.getParadigms(wordId);
		for (Paradigm paradigm : paradigms) {
			List<Form> forms = morphologyDbService.getForms(paradigm.getId());
			paradigm.setForms(forms);
		}
		return paradigms;
	}
}
