package eki.wordweb.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.wordweb.data.od.OdSearchResult;
import eki.wordweb.service.OdSearchService;

@ConditionalOnWebApplication
@Controller
public class OdSearchController extends AbstractSearchController {

	@Autowired
	private OdSearchService odSearchService;

	@GetMapping(OD_URI)
	public String home(HttpServletRequest request, Model model) {

		model.addAttribute("searchResult", new OdSearchResult());

		return OD_HOME_PAGE;
	}

	@GetMapping(SEARCH_URI + OD_URI)
	public String search(HttpServletRequest request, Model model) {

		model.addAttribute("searchResult", new OdSearchResult());

		return OD_SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI + OD_URI)
	public String searchWords(
			@RequestParam(name = "searchValue") String searchValue,
			RedirectAttributes redirectAttributes) {

		searchValue = decode(searchValue);
		searchValue = cleanupMain(searchValue);
		if (StringUtils.isBlank(searchValue)) {
			return REDIRECT_PREF + SEARCH_URI + OD_URI;
		}
		searchValue = textDecorationService.unifySymbols(searchValue);
		Integer homonymNr = 1;
		String searchUri = webUtil.composeOdSearchUri(searchValue, homonymNr);
		setSearchFormAttribute(redirectAttributes, Boolean.TRUE);

		return REDIRECT_PREF + searchUri;
	}

	@GetMapping({
			SEARCH_URI + OD_URI + "/{searchValue}",
			SEARCH_URI + OD_URI + "/{searchValue}/{homonymNr}"
	})
	public String searchUnifWordsByUri(
			@PathVariable(name = "searchValue") String searchValue,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			Model model) throws Exception {

		Integer homonymNr = nullSafe(homonymNrStr);
		if (homonymNr == null) {
			homonymNr = 1;
			String searchUri = webUtil.composeOdSearchUri(searchValue, homonymNr);
			return REDIRECT_PREF + searchUri;
		}
		OdSearchResult searchResult = odSearchService.search(searchValue, homonymNr);

		model.addAttribute("searchResult", searchResult);

		return OD_SEARCH_PAGE;
	}
}
