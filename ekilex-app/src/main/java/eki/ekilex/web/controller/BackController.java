package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Word;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class BackController implements WebConstant {

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private TermSearchService termSearchService;

	@GetMapping("/wordback/{wordId}")
	public String wordBack(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		Word word = commonDataService.getWord(wordId);
		attributes.addFlashAttribute(SEARCH_WORD_KEY, word.getValue());

		return "redirect:" + LEX_SEARCH_URI;
	}

	@GetMapping("/meaningback/{meanigId}")
	public String meaningBack(
			@PathVariable("meanigId") Long meaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		String word = termSearchService.getFirstMeaningWord(meaningId, sessionBean.getSelectedDatasets());
		attributes.addFlashAttribute(SEARCH_WORD_KEY, word);

		return "redirect:" + TERM_SEARCH_URI;
	}

}
