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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceSearchResult;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@Controller
public class SourceSearchController extends AbstractPrivateSearchController {

	private static final Logger logger = LoggerFactory.getLogger(SourceSearchController.class);

	@Autowired
	private SourceService sourceService;

	@GetMapping(SOURCE_SEARCH_URI)
	public String initSearch(Model model) {

		initSearchForms(SOURCE_SEARCH_PAGE, model);
		model.addAttribute("sourceSearchResult", new SourceSearchResult());

		return SOURCE_SEARCH_PAGE;
	}

	@PostMapping(SOURCE_SEARCH_URI)
	public String search(
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter) throws Exception {

		simpleSearchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(simpleSearchFilter);
		formDataCleanup(SOURCE_SEARCH_PAGE, detailSearchFilter);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}

		String roleDatasetCode = getDatasetCodeFromRole();
		List<String> roleDatasets = new ArrayList<>(Arrays.asList(roleDatasetCode));

		String searchUri = searchHelper.composeSearchUri(searchMode, roleDatasets, simpleSearchFilter, detailSearchFilter, SearchResultMode.SOURCE, null);

		return REDIRECT_PREF + SOURCE_SEARCH_URI + searchUri;
	}

	@GetMapping(value = SOURCE_SEARCH_URI + "/**")
	public String sourceSearch(Model model, HttpServletRequest request) throws Exception {

		String searchUri = StringUtils.removeStart(request.getRequestURI(), SOURCE_SEARCH_URI);
		logger.debug(searchUri);

		initSearchForms(SOURCE_SEARCH_PAGE, model);

		SearchUriData searchUriData = searchHelper.parseSearchUri(SOURCE_SEARCH_PAGE, searchUri);

		if (!searchUriData.isValid()) {
			initSearchForms(SOURCE_SEARCH_PAGE, model);
			model.addAttribute("sourceSearchResult", new SourceSearchResult());
			return SOURCE_SEARCH_PAGE;
		}

		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		EkiUser user = userContext.getUser();

		int offset = getPageNumAndCalculateOffset(request);

		SourceSearchResult sourceSearchResult;
		if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			sourceSearchResult = sourceService.getSourceSearchResult(simpleSearchFilter, user, offset, DEFAULT_MAX_RESULTS_LIMIT);
		} else {
			sourceSearchResult = sourceService.getSourceSearchResult(detailSearchFilter, user, offset, DEFAULT_MAX_RESULTS_LIMIT);
		}

		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("sourceSearchResult", sourceSearchResult);
		model.addAttribute("searchUri", SOURCE_SEARCH_URI + searchUri);

		return SOURCE_SEARCH_PAGE;
	}

	@PostMapping(SOURCE_PAGING_URI)
	public String paging(
			@RequestParam("offset") int offset,
			@RequestParam("searchUri") String searchUri,
			@RequestParam("direction") String direction,
			@RequestParam(name = "pageNum", required = false) Integer pageNum,
			Model model) throws Exception {

		SearchUriData searchUriData = searchHelper.parseSearchUri(SOURCE_SEARCH_PAGE, searchUri);
		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		EkiUser user = userContext.getUser();

		if (StringUtils.equals("next", direction)) {
			offset += DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("previous", direction)) {
			offset -= DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("page", direction)) {
			offset = (pageNum - 1) * DEFAULT_MAX_RESULTS_LIMIT;
		}

		SourceSearchResult sourceSearchResult;
		if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			sourceSearchResult = sourceService.getSourceSearchResult(simpleSearchFilter, user, offset, DEFAULT_MAX_RESULTS_LIMIT);
		} else {
			sourceSearchResult = sourceService.getSourceSearchResult(detailSearchFilter, user, offset, DEFAULT_MAX_RESULTS_LIMIT);
		}

		model.addAttribute("sourceSearchResult", sourceSearchResult);
		model.addAttribute("searchUri", SOURCE_SEARCH_URI + searchUri);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "search_result";
	}

	@GetMapping(SOURCE_ID_SEARCH_URI + "/{sourceId}")
	public String sourceIdSearch(@PathVariable("sourceId") Long sourceId, Model model) {

		EkiUser user = userContext.getUser();

		SourceSearchResult sourceSearchResult = new SourceSearchResult();
		sourceSearchResult.setSources(new ArrayList<>());
		Source source = sourceService.getSource(sourceId, user);
		if (source != null) {
			sourceSearchResult.getSources().add(source);
			sourceSearchResult.setResultCount(1);
			sourceSearchResult.setResultExist(true);
		}

		initSearchForms(SOURCE_SEARCH_PAGE, model);
		model.addAttribute("sourceSearchResult", sourceSearchResult);

		return SOURCE_SEARCH_PAGE;
	}

	@GetMapping(SOURCE_DETAIL_SEARCH_URI + "/{searchPage}/{sourceId}")
	public String sourceDetailSearch(@PathVariable("searchPage") String searchPage, @PathVariable("sourceId") Long sourceId) {

		List<String> selectedDatasets = getUserPreferredDatasetCodes();
		SearchFilter detailSearchFilter = searchHelper.createSourceDetailSearchFilter(sourceId);
		String searchUri = searchHelper.composeSearchUri(SEARCH_MODE_DETAIL, selectedDatasets, null, detailSearchFilter, SearchResultMode.MEANING, null);
		if (LEX_SEARCH_PAGE.equals(searchPage)) {
			return REDIRECT_PREF + LEX_SEARCH_URI + searchUri;
		} else {
			return REDIRECT_PREF + TERM_SEARCH_URI + searchUri;
		}
	}

}
