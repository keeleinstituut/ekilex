package eki.wordweb.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.WebConstant;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SearchController extends AbstractController {

	@PostMapping(SEARCH_URI)
	public String searchWords(
			@RequestParam(name = "destinLangsStr") String destinLangStr,
			@RequestParam(name = "datasetCodesStr") String datasetCodesStr,
			@RequestParam(name = "searchMode") String searchMode,
			@RequestParam(name = "searchWord") String searchWord,
			@RequestParam(name = "selectedWordHomonymNr", required = false) String selectedWordHomonymNrStr) {

		searchWord = StringUtils.trim(searchWord);
		if (StringUtils.isBlank(searchWord)) {
			return "redirect:" + SEARCH_URI + UNIF_URI;
			/* soon...
			if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
				return "redirect:" + SEARCH_URI + UNIF_URI;
			} else if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
				return "redirect:" + SEARCH_URI + LITE_URI;
			}
			*/
		}
		Integer selectedWordHomonymNr = nullSafe(selectedWordHomonymNrStr);
		String searchUri = webUtil.composeSearchUri(destinLangStr, datasetCodesStr, searchMode, searchWord, selectedWordHomonymNr);

		return "redirect:" + searchUri;
	}
}
