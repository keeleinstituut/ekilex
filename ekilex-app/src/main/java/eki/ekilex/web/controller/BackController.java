package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

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

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@GetMapping("/wordback/{wordId}")
	public String wordBack(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		Word word = commonDataService.getWord(wordId);
		String wordValue = word.getValue();
		String searchUri = searchHelper.composeSearchUri(sessionBean.getSelectedDatasets(), wordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping("/lexback/{lexemeId}")
	public String lexemeBack(
			@PathVariable("lexemeId") Long lexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		WordLexeme lexeme = lexSearchService.getWordLexeme(lexemeId);
		String firstWordValue = lexeme.getWords()[0];
		String searchUri = searchHelper.composeSearchUri(sessionBean.getSelectedDatasets(), firstWordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping("/meaningback/{meanigId}")
	public String meaningBack(
			@PathVariable("meanigId") Long meaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		String firstWordValue = termSearchService.getMeaningFirstWordValue(meaningId, sessionBean.getSelectedDatasets());
		String searchUri = searchHelper.composeSearchUri(sessionBean.getSelectedDatasets(), firstWordValue);

		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

}
