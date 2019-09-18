package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class BackController extends AbstractPageController {

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@GetMapping(WORD_BACK_URI + "/{wordId}")
	public String wordBack(@PathVariable("wordId") Long wordId) {

		List<String> datasets = getUserPreferredDatasetCodes();
		Word word = commonDataService.getWord(wordId);
		String wordValue = word.getValue();
		String searchUri = searchHelper.composeSearchUri(datasets, wordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping(LEX_BACK_URI + "/{lexemeId}")
	public String lexemeBack(@PathVariable("lexemeId") Long lexemeId) {

		List<String> datasets = getUserPreferredDatasetCodes();
		WordLexeme lexeme = commonDataService.getWordLexeme(lexemeId);
		String firstWordValue = lexeme.getWords()[0];
		String searchUri = searchHelper.composeSearchUri(datasets, firstWordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping(MEANING_BACK_URI + "/{meaningId}")
	public String meaningBack(@PathVariable("meaningId") Long meaningId) {

		List<String> datasets = getUserPreferredDatasetCodes();
		String firstWordValue = termSearchService.getMeaningFirstWordValue(meaningId, datasets);
		String searchUri = searchHelper.composeSearchUri(datasets, firstWordValue);

		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

	@GetMapping(WORD_VALUE_BACK_URI + "/{wordValue}/{returnPage}")
	public String wordValueBack(@PathVariable("wordValue") String wordValue, @PathVariable("returnPage") String returnPage) {

		List<String> datasets = getUserPreferredDatasetCodes();
		String searchUri = searchHelper.composeSearchUri(datasets, wordValue);
		String redirectUri = "";
		if (StringUtils.equals(returnPage, RETURN_PAGE_LEX_SEARCH)) {
			redirectUri = LEX_SEARCH_URI + searchUri;
		} else if (StringUtils.equals(returnPage, RETURN_PAGE_TERM_SEARCH)) {
			redirectUri = TERM_SEARCH_URI + searchUri;
		}

		return "redirect:" + redirectUri;
	}

}
