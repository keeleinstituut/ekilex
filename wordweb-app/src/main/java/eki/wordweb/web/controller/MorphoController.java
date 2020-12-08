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
import eki.wordweb.data.StaticParadigm;
import eki.wordweb.service.MorphoService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class MorphoController implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	private MorphoService morphoService;

	@GetMapping(MORPHO_URI + LITE_URI + "/{paradigmId}/{wordClass}/{lang}")
	public String getMorphoLite(
			@PathVariable("paradigmId") Long paradigmId,
			@PathVariable("wordClass") String wordClass,
			@PathVariable("lang") String lang,
			Model model) {

		Integer maxDisplayLevel = SIMPLE_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		return getMorpho(paradigmId, wordClass, lang, maxDisplayLevel, model);
	}

	@GetMapping(MORPHO_URI + UNIF_URI + "/{paradigmId}/{wordClass}/{lang}")
	public String getMorphoUnif(
			@PathVariable("paradigmId") Long paradigmId,
			@PathVariable("wordClass") String wordClass,
			@PathVariable("lang") String lang,
			Model model) {

		Integer maxDisplayLevel = DEFAULT_MORPHOLOGY_MAX_DISPLAY_LEVEL;
		return getMorpho(paradigmId, wordClass, lang, maxDisplayLevel, model);
	}

	private String getMorpho(Long paradigmId, String wordClass, String lang, Integer maxDisplayLevel, Model model) {

		StaticParadigm staticParadigm = morphoService.getStaticParadigm(paradigmId, maxDisplayLevel);
		model.addAttribute("paradigm", staticParadigm);
		String viewFragment = "morpho-" + wordClass + '_' + lang;
		return MORPHO_PAGE + " :: " + viewFragment;
	}

}
