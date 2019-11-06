package eki.ekilex.web.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TermSearchController extends AbstractSearchController implements SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(TermSearchController.class);

	@Autowired
	private TermSearchService termSearchService;

	@RequestMapping(value = TERM_SEARCH_URI, method = RequestMethod.GET)
	public String initSearch(Model model) {

		initSearchForms(TERM_SEARCH_PAGE, model);
		resetUserRole(model);

		TermSearchResult termSearchResult = new TermSearchResult();
		model.addAttribute("termSearchResult", termSearchResult);

		return TERM_SEARCH_PAGE;
	}

	@PostMapping(value = TERM_SEARCH_URI)
	public String termSearch(
			@RequestParam(name = "selectedDatasets", required = false) List<String> selectedDatasets,
			@RequestParam(name = "searchMode", required = true) String searchMode,
			@RequestParam(name = "resultMode", required = true) SearchResultMode resultMode,
			@RequestParam(name = "resultLang", required = false) String resultLang,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			Model model) throws Exception {

		SessionBean sessionBean = getSessionBean(model);

		formDataCleanup(TERM_SEARCH_PAGE, selectedDatasets, detailSearchFilter, sessionBean);
		sessionBean.setTermSearchResultMode(resultMode);
		sessionBean.setTermSearchResultLang(resultLang);

		selectedDatasets = getUserPreferredDatasetCodes();

		String searchUri = searchHelper.composeSearchUri(searchMode, selectedDatasets, simpleSearchFilter, detailSearchFilter, resultMode, resultLang);
		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

	@GetMapping(value = TERM_SEARCH_URI + "/**")
	public String termSearch(Model model, HttpServletRequest request) throws Exception {

		final String searchPage = TERM_SEARCH_PAGE;

		// if redirect from login arrives
		initSearchForms(searchPage, model);
		resetUserRole(model);

		String searchUri = StringUtils.removeStart(request.getRequestURI(), TERM_SEARCH_URI);
		logger.debug(searchUri);

		SearchUriData searchUriData = searchHelper.parseSearchUri(searchPage, searchUri);

		if (!searchUriData.isValid()) {
			initSearchForms(searchPage, model);
			model.addAttribute("termSearchResult", new TermSearchResult());
			model.addAttribute("invalidSearch", true);
			return TERM_SEARCH_PAGE;
		}

		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		SearchResultMode resultMode = searchUriData.getResultMode();
		String resultLang = searchUriData.getResultLang();

		SessionBean sessionBean = getSessionBean(model);
		sessionBean.setTermSearchResultMode(resultMode);
		sessionBean.setTermSearchResultLang(resultLang);

		boolean fetchAll = false;

		TermSearchResult termSearchResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			termSearchResult = termSearchService.getTermSearchResult(detailSearchFilter, selectedDatasets, resultMode, resultLang, fetchAll, DEFAULT_OFFSET);
		} else {
			termSearchResult = termSearchService.getTermSearchResult(simpleSearchFilter, selectedDatasets, resultMode, resultLang, fetchAll, DEFAULT_OFFSET);
		}
		boolean noResults = termSearchResult.getMeaningCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("termSearchResult", termSearchResult);
		model.addAttribute("noResults", noResults);
		model.addAttribute("searchUri", searchUri);

		return TERM_SEARCH_PAGE;
	}

	@GetMapping(MEANING_DETAILS_URI + "/{meaningId}")
	public String details(@PathVariable("meaningId") Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		logger.debug("Requesting meaning {} details", meaningId);

		List<String> selectedDatasets = getUserPreferredDatasetCodes();
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		Meaning meaning = termSearchService.getMeaning(meaningId, selectedDatasets, languagesOrder);
		model.addAttribute("meaning", meaning);
		model.addAttribute("meaningId", meaningId);

		return TERM_SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@PostMapping(TERM_PAGING_URI)
	public String paging(
			@RequestParam("offset") int offset,
			@RequestParam("searchUri") String searchUri,
			@RequestParam("direction") String direction,
			Model model) throws Exception {

		SearchUriData searchUriData = searchHelper.parseSearchUri(TERM_SEARCH_PAGE, searchUri);

		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		SearchResultMode resultMode = searchUriData.getResultMode();
		String resultLang = searchUriData.getResultLang();
		boolean fetchAll = false;

		if ("next".equals(direction)) {
			offset += MAX_RESULTS_LIMIT;
		} else if ("previous".equals(direction)) {
			offset -= MAX_RESULTS_LIMIT;
		}

		TermSearchResult termSearchResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			termSearchResult = termSearchService.getTermSearchResult(detailSearchFilter, selectedDatasets, resultMode, resultLang, fetchAll, offset);
		} else {
			termSearchResult = termSearchService.getTermSearchResult(simpleSearchFilter, selectedDatasets, resultMode, resultLang, fetchAll, offset);
		}

		termSearchResult.setOffset(offset);
		model.addAttribute("termSearchResult", termSearchResult);
		model.addAttribute("searchUri", searchUri);

		return TERM_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "search_result";
	}
}
