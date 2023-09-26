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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.UserContextData;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
@PreAuthorize("@permEval.isLimitedPageAccessPermitted(authentication)")
public class LimitedTermSearchController extends AbstractSearchController {

	private static final Logger logger = LoggerFactory.getLogger(LimitedTermSearchController.class);

	private final List<String> limitedDatasets = new ArrayList<>(Arrays.asList(DATASET_LIMITED));

	@Autowired
	private TermSearchService termSearchService;

	@GetMapping(LIM_TERM_SEARCH_URI)
	public String initSearch(Model model) {

		initSearchForms(LIM_TERM_SEARCH_PAGE, model);
		return LIM_TERM_SEARCH_PAGE;
	}

	@PostMapping(LIM_TERM_SEARCH_URI)
	public String limitedTermSearch(
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter) throws Exception {

		simpleSearchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(simpleSearchFilter);
		formDataCleanup(LIM_TERM_SEARCH_PAGE, detailSearchFilter);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}

		String searchUri = searchHelper.composeSearchUri(searchMode, limitedDatasets, simpleSearchFilter, detailSearchFilter, SearchResultMode.MEANING, null);

		return REDIRECT_PREF + LIM_TERM_SEARCH_URI + searchUri;
	}

	@GetMapping(value = LIM_TERM_SEARCH_URI + "/**")
	public String limitedTermSearch(Model model, HttpServletRequest request) throws Exception {

		final String searchPage = LIM_TERM_SEARCH_PAGE;

		// if redirect from login arrives
		initSearchForms(searchPage, model);

		String searchUri = StringUtils.removeStart(request.getRequestURI(), LIM_TERM_SEARCH_URI);
		logger.debug(searchUri);

		SearchUriData searchUriData = searchHelper.parseSearchUri(searchPage, searchUri);

		if (!searchUriData.isValid()) {
			model.addAttribute("invalidSearch", Boolean.TRUE);
			return LIM_TERM_SEARCH_PAGE;
		}

		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		SearchResultMode resultMode = SearchResultMode.MEANING;
		String resultLang = null;
		boolean noLimit = false;

		TermSearchResult termSearchResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			searchHelper.addValidationMessages(detailSearchFilter);
			termSearchResult = termSearchService.getTermSearchResult(detailSearchFilter, limitedDatasets, resultMode, resultLang, DEFAULT_OFFSET, noLimit);
		} else {
			termSearchResult = termSearchService.getTermSearchResult(simpleSearchFilter, limitedDatasets, resultMode, resultLang, DEFAULT_OFFSET, noLimit);
		}
		boolean noResults = termSearchResult.getResultCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("termSearchResult", termSearchResult);
		model.addAttribute("noResults", noResults);
		model.addAttribute("searchUri", searchUri);

		return LIM_TERM_SEARCH_PAGE;
	}

	@GetMapping(LIM_TERM_MEANING_DETAILS_URI + "/{meaningId}")
	public String meaningDetails(@PathVariable("meaningId") Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) throws Exception {

		logger.debug("meaning details for {}", meaningId);

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		UserContextData userContextData = getUserContextData();
		Tag activeTag = userContextData.getActiveTag();
		Meaning meaning = termSearchService.getMeaning(meaningId, limitedDatasets, languagesOrder, userProfile, user, activeTag);
		model.addAttribute("meaning", meaning);
		model.addAttribute("meaningId", meaningId);

		return LIM_TERM_SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@GetMapping(LIM_TERM_MEANING_BACK_URI + "/{meaningId}")
	public String limMeaningBack(@PathVariable("meaningId") Long meaningId) {

		String firstWordValue = termSearchService.getMeaningFirstWordValue(meaningId, limitedDatasets);
		String searchUri = searchHelper.composeSearchUriAndAppendId(limitedDatasets, firstWordValue, meaningId);

		return REDIRECT_PREF + LIM_TERM_SEARCH_URI + searchUri;
	}

	@PostMapping(LIM_TERM_PAGING_URI)
	public String paging(
			@RequestParam("offset") int offset,
			@RequestParam("searchUri") String searchUri,
			@RequestParam("direction") String direction,
			@RequestParam(name = "pageNum", required = false) Integer pageNum,
			Model model) throws Exception {

		SearchUriData searchUriData = searchHelper.parseSearchUri(LIM_TERM_SEARCH_PAGE, searchUri);

		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean noLimit = false;

		if (StringUtils.equals("next", direction)) {
			offset += DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("previous", direction)) {
			offset -= DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("page", direction)) {
			offset = (pageNum - 1) * DEFAULT_MAX_RESULTS_LIMIT;
		}

		TermSearchResult termSearchResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			termSearchResult = termSearchService.getTermSearchResult(detailSearchFilter, limitedDatasets, SearchResultMode.MEANING, null, offset, noLimit);
		} else {
			termSearchResult = termSearchService.getTermSearchResult(simpleSearchFilter, limitedDatasets, SearchResultMode.MEANING, null, offset, noLimit);
		}

		termSearchResult.setOffset(offset);
		model.addAttribute("termSearchResult", termSearchResult);
		model.addAttribute("searchUri", searchUri);

		return TERM_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "lim_term_search_result";
	}

}
