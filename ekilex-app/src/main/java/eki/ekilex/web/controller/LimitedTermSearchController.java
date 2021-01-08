package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.service.TermSearchService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
@PreAuthorize("@permEval.isLimitedPageAccessPermitted(authentication)")
public class LimitedTermSearchController extends AbstractSearchController {

	private static final Logger logger = LoggerFactory.getLogger(LimitedTermSearchController.class);

	@Autowired
	private TermSearchService termSearchService;

	@GetMapping(LIMITED_TERM_SEARCH_URI)
	public String initSearch(Model model) {

		initSearchForms(LIMITED_TERM_SEARCH_PAGE, model);
		return LIMITED_TERM_SEARCH_PAGE;
	}

	@PostMapping(LIMITED_TERM_SEARCH_URI)
	public String limitedTermSearch(
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter) throws Exception {

		simpleSearchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(simpleSearchFilter);
		formDataCleanup(LIMITED_TERM_SEARCH_PAGE, detailSearchFilter);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}

		String roleDatasetCode = getDatasetCodeFromRole();
		List<String> roleDatasets = new ArrayList<>(Arrays.asList(roleDatasetCode));

		String searchUri = searchHelper.composeSearchUri(searchMode, roleDatasets, simpleSearchFilter, detailSearchFilter, SearchResultMode.MEANING, null);

		return "redirect:" + LIMITED_TERM_SEARCH_URI + searchUri;
	}

	@GetMapping(value = LIMITED_TERM_SEARCH_URI + "/**")
	public String limitedTermSearch(Model model, HttpServletRequest request) throws Exception {

		final String searchPage = LIMITED_TERM_SEARCH_PAGE;

		// if redirect from login arrives
		initSearchForms(searchPage, model);

		String searchUri = StringUtils.removeStart(request.getRequestURI(), LIMITED_TERM_SEARCH_URI);
		logger.debug(searchUri);

		SearchUriData searchUriData = searchHelper.parseSearchUri(searchPage, searchUri);

		if (!searchUriData.isValid()) {
			model.addAttribute("invalidSearch", Boolean.TRUE);
			return LIMITED_TERM_SEARCH_PAGE;
		}

		String roleDatasetCode = getDatasetCodeFromRole();
		List<String> roleDatasets = new ArrayList<>(Arrays.asList(roleDatasetCode));
		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		SearchResultMode resultMode = SearchResultMode.MEANING;
		String resultLang = null;
		boolean fetchAll = false;

		TermSearchResult termSearchResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			termSearchResult = termSearchService.getTermSearchResult(detailSearchFilter, roleDatasets, resultMode, resultLang, fetchAll, DEFAULT_OFFSET);
		} else {
			termSearchResult = termSearchService.getTermSearchResult(simpleSearchFilter, roleDatasets, resultMode, resultLang, fetchAll, DEFAULT_OFFSET);
		}
		boolean noResults = termSearchResult.getResultCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("termSearchResult", termSearchResult);
		model.addAttribute("noResults", noResults);
		model.addAttribute("searchUri", searchUri);

		return LIMITED_TERM_SEARCH_PAGE;
	}

}
