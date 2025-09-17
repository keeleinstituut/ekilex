package eki.wordweb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Form;
import eki.wordweb.data.Paradigm;
import eki.wordweb.service.db.SearchDbService;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.service.util.ParadigmConversionUtil;

@Component
public class MorphoService implements WebConstant, SystemConstant {

	@Autowired
	private SearchDbService searchDbService;

	@Autowired
	private ParadigmConversionUtil paradigmConversionUtil;

	@Autowired
	private LanguageContext languageContext;

	@Transactional
	public Paradigm getParadigm(Long paradigmId, Integer maxDisplayLevel, boolean excludeQuestionable) {

		List<Form> forms = searchDbService.getParadigmForms(paradigmId, maxDisplayLevel, excludeQuestionable);
		paradigmConversionUtil.calcFreqScale(forms);
		String displayLang = languageContext.getDisplayLang();
		Paradigm paradigm = paradigmConversionUtil.composeParadigm(paradigmId, forms, displayLang);
		return paradigm;
	}
}
