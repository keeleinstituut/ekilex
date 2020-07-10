package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Word;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class BackController extends AbstractPageController {

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private LexSearchService lexSearchService;

	@GetMapping(WORD_BACK_URI + "/{wordId}")
	public String wordBack(@PathVariable("wordId") Long wordId) {

		List<String> datasets = getUserPreferredDatasetCodes();
		Word word = lexSearchService.getWord(wordId);
		String wordValue = word.getWordValue();
		String searchUri = searchHelper.composeSearchUri(datasets, wordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping(LEX_BACK_URI + "/{lexemeId}")
	public String lexemeBack(@PathVariable("lexemeId") Long lexemeId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		List<String> datasets = getUserPreferredDatasetCodes();
		String lexemeWordValue = lookupService.getLexemeWordValue(lexemeId);
		String searchUri = searchHelper.composeSearchUri(datasets, lexemeWordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping(MEANING_BACK_URI + "/{meaningId}")
	public String meaningBack(@PathVariable("meaningId") Long meaningId) {

		List<String> datasets = getUserPreferredDatasetCodes();
		String firstWordValue = termSearchService.getMeaningFirstWordValue(meaningId, datasets);
		String searchUri = searchHelper.composeSearchUri(datasets, firstWordValue);

		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

}
