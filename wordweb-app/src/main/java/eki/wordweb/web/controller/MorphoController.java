package eki.wordweb.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
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
public class MorphoController implements WebConstant, SystemConstant, GlobalConstant, InitializingBean {

	@Autowired
	private MorphoService morphoService;

	private Map<String, String> wordClassToViewMap;

	@Override
	public void afterPropertiesSet() throws Exception {
		wordClassToViewMap = new HashMap<>();
		wordClassToViewMap.put("noomen", "morpho-noun");
		wordClassToViewMap.put("verb", "morpho-verb");
		wordClassToViewMap.put("muutumatu", "morpho-indecl");
	}

	@GetMapping(MORPHO_URI + "/{wordId}/{wordClass}")
	public String getMorpho(
			@PathVariable("wordId") Long wordId,
			@PathVariable("wordClass") String wordClass,
			Model model) {

		List<StaticParadigm> staticParadigms = morphoService.getStaticParadigms(wordId);
		model.addAttribute("paradigms", staticParadigms);
		String viewFragment = wordClassToViewMap.get(wordClass);

		return MORPHO_PAGE + " :: " + viewFragment;
	}

}
