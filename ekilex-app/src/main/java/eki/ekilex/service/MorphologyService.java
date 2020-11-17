package eki.ekilex.service;

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
import eki.ekilex.data.api.ParadigmWord;
import eki.ekilex.data.api.ParadigmWrapper;
import eki.ekilex.service.db.MorphologyDbService;

@Component
public class MorphologyService {

	private static final Logger logger = LoggerFactory.getLogger(MorphologyService.class);

	@Autowired
	private MorphologyDbService morphologyDbService;

	@Transactional
	public void replace(ParadigmWrapper paradigmWrapper) throws Exception {

		if (paradigmWrapper == null) {
			return;
		}

		List<Paradigm> paradigms = paradigmWrapper.getParadigms();
		List<ParadigmWord> paradigmWords = paradigms.stream().map(paradigm -> new ParadigmWord(paradigm.getWordId(), paradigm.getWordClass())).distinct().collect(Collectors.toList());

		logger.info("Replacing {} paradigms for {} words", paradigms.size(), paradigmWords.size());

		for (ParadigmWord paradigmWord : paradigmWords) {
			Long wordId = paradigmWord.getWordId();
			String wordClass = paradigmWord.getWordClass();
			morphologyDbService.deleteParadigmsForWord(wordId);
			morphologyDbService.updateWordClass(wordId, wordClass);
		}

		for (Paradigm paradigm : paradigms) {
			Long paradigmId = morphologyDbService.createParadigm(paradigm);
			List<Form> forms = paradigm.getForms();
			boolean orderByExists = forms.stream().allMatch(form -> form.getOrderBy() != null);
			if (orderByExists) {
				forms = forms.stream().sorted(Comparator.comparingLong(Form::getOrderBy)).collect(Collectors.toList());
			}
			for (Form form : forms) {
				form.setParadigmId(paradigmId);
				morphologyDbService.createForm(form);
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
