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

	@GetMapping(MORPHO_URI + "/{paradigmId}/{wordClass}/{lang}")
	public String getMorpho(
			@PathVariable("paradigmId") Long paradigmId,
			@PathVariable("wordClass") String wordClass,
			@PathVariable("lang") String lang,
			Model model) {

		StaticParadigm staticParadigm = morphoService.getStaticParadigm(paradigmId);
		model.addAttribute("paradigm", staticParadigm);

		String viewFragment = "morpho-" + wordClass + '_' + lang;
		return MORPHO_PAGE + " :: " + viewFragment;
	}

}
