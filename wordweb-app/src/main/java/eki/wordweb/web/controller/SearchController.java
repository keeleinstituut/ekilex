package eki.wordweb.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.WebConstant;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SearchController extends AbstractSearchController {

	//backward compatibility support
	@SuppressWarnings("deprecation")
	@GetMapping({
			SEARCH_URI + LEX_URI + "/{langPair}/{searchMode}/{searchWord}/{homonymNr}",
			SEARCH_URI + LEX_URI + "/{langPair}/{searchMode}/{searchWord}"})
	public String searchLexWordsByUri(
			@PathVariable(name = "langPair") String langPair,
			@PathVariable(name = "searchMode") String searchMode,
			@PathVariable(name = "searchWord") String searchWord,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr) {

		Integer homonymNr = nullSafe(homonymNrStr);
		String searchUri = HOME_URI;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			searchUri = webUtil.composeDetailSearchUri(DESTIN_LANG_ALL, DATASET_ALL, searchWord, homonymNr);
		} else if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			searchUri = webUtil.composeSimpleSearchUri(DESTIN_LANG_ALL, searchWord, homonymNr);
		}
		return REDIRECT_PREF + searchUri;
	}
}
