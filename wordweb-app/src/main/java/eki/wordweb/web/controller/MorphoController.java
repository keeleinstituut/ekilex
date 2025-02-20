package eki.wordweb.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Paradigm;
import eki.wordweb.service.MorphoService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class MorphoController implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	private MorphoService morphoService;

	@GetMapping(MORPHO_URI + LITE_URI + "/{paradigmId}/{lang}")
	public String getMorphoLite(
			@PathVariable("paradigmId") Long paradigmId,
			@PathVariable("lang") String lang,
			Model model) {

		Integer maxDisplayLevel = SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		boolean excludeQuestionable = true;

		return getMorpho(paradigmId, lang, maxDisplayLevel, excludeQuestionable, model);
	}

	@GetMapping(MORPHO_URI + UNIF_URI + "/{paradigmId}/{lang}")
	public String getMorphoUnif(
			@PathVariable("paradigmId") Long paradigmId,
			@PathVariable("lang") String lang,
			Model model) {

		Integer maxDisplayLevel = DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		boolean excludeQuestionable = false;

		return getMorpho(paradigmId, lang, maxDisplayLevel, excludeQuestionable, model);
	}

	private String getMorpho(Long paradigmId, String lang, Integer maxDisplayLevel, boolean excludeQuestionable, Model model) {

		Paradigm paradigm = morphoService.getParadigm(paradigmId, maxDisplayLevel, excludeQuestionable);
		String wordClass = paradigm.getWordClass();
		String viewFragment = "morpho-" + wordClass + '_' + lang;
		model.addAttribute("paradigm", paradigm);

		return MORPHO_FULL_PAGE + " :: " + viewFragment;
	}

}
