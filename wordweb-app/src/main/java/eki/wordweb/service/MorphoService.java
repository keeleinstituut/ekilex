package eki.wordweb.service;

import java.util.List;
import java.util.Map;

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
	public List<StaticParadigm> getStaticParadigms(Long wordId) {

		Integer maxDisplayLevel = DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		Map<Long, List<Form>> paradigmFormsMap = searchDbService.getWordForms(wordId, maxDisplayLevel);
		List<StaticParadigm> staticParadigms = paradigmConversionUtil.composeStaticParadigms(paradigmFormsMap, DISPLAY_LANG);

		return staticParadigms;
	}
}
