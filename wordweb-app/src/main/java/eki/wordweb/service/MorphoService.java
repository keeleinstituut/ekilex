package eki.wordweb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Form;
import eki.wordweb.data.StaticParadigm;
import eki.wordweb.service.db.SearchDbService;
import eki.wordweb.service.util.ParadigmConversionUtil;

@Component
public class MorphoService implements WebConstant, SystemConstant {

	@Autowired
	protected SearchDbService searchDbService;

	@Autowired
	protected ParadigmConversionUtil paradigmConversionUtil;

	@Transactional
	public StaticParadigm getStaticParadigm(Long paradigmId, Integer maxDisplayLevel) {

		List<Form> forms = searchDbService.getParadigmForms(paradigmId, maxDisplayLevel);
		paradigmConversionUtil.calcFreqScale(forms);
		StaticParadigm staticParadigm = paradigmConversionUtil.composeStaticParadigm(paradigmId, forms, DISPLAY_LANG);
		return staticParadigm;
	}
}
