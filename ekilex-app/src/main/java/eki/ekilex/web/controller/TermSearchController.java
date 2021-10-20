package eki.ekilex.web.controller;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.QueueAction;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.InternalLinkSearchRequest;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.QueueItem;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.TermSearchResultQueueContent;
import eki.ekilex.data.UserContextData;
import eki.ekilex.service.DataRequestService;
import eki.ekilex.service.QueueService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TermSearchController extends AbstractPrivateSearchController {

	private static final Logger logger = LoggerFactory.getLogger(TermSearchController.class);

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private DataRequestService dataRequestService;

	@Autowired
	private QueueService queueService;

	@GetMapping(value = TERM_SEARCH_URI)
	public String initSearch(Model model) {

		initSearchForms(TERM_SEARCH_PAGE, model);

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

		if (CollectionUtils.isEmpty(selectedDatasets)) {
			return "redirect:" + TERM_SEARCH_URI;
		}

		simpleSearchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(simpleSearchFilter);
		formDataCleanup(TERM_SEARCH_PAGE, detailSearchFilter);

		SessionBean sessionBean = getSessionBean(model);
		sessionBean.setTermSearchResultMode(resultMode);
		sessionBean.setTermSearchResultLang(resultLang);

		Long userId = userContext.getUserId();
		userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);

		String searchUri = searchHelper.composeSearchUri(searchMode, selectedDatasets, simpleSearchFilter, detailSearchFilter, resultMode, resultLang);
		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

	@GetMapping(value = TERM_SEARCH_URI + "/**")
	public String termSearch(Model model, HttpServletRequest request) throws Exception {

		final String searchPage = TERM_SEARCH_PAGE;

		// if redirect from login arrives
		initSearchForms(searchPage, model);

		String searchUri = StringUtils.removeStart(request.getRequestURI(), TERM_SEARCH_URI);
		logger.debug(searchUri);

		SearchUriData searchUriData = searchHelper.parseSearchUri(searchPage, searchUri);

		if (!searchUriData.isValid()) {
			model.addAttribute("invalidSearch", Boolean.TRUE);
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

		boolean noLimit = false;

		Long userId = userContext.getUserId();
		userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);

		TermSearchResult termSearchResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			searchHelper.addValidationMessages(detailSearchFilter);
			termSearchResult = termSearchService.getTermSearchResult(detailSearchFilter, selectedDatasets, resultMode, resultLang, DEFAULT_OFFSET, noLimit);
		} else {
			termSearchResult = termSearchService.getTermSearchResult(simpleSearchFilter, selectedDatasets, resultMode, resultLang, DEFAULT_OFFSET, noLimit);
		}
		boolean noResults = termSearchResult.getResultCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("termSearchResult", termSearchResult);
		model.addAttribute("noResults", noResults);
		model.addAttribute("searchUri", searchUri);

		return TERM_SEARCH_PAGE;
	}

	@GetMapping(TERM_MEANING_DETAILS_URI + "/{meaningId}")
	public String meaningDetails(@PathVariable("meaningId") Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) throws Exception {

		logger.debug("meaning details for {}", meaningId);

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		List<String> selectedDatasets = userProfile.getPreferredDatasets();
		UserContextData userContextData = getUserContextData();
		Tag activeTag = userContextData.getActiveTag();
		Meaning meaning = termSearchService.getMeaning(meaningId, selectedDatasets, languagesOrder, userProfile, user, activeTag);
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
		boolean noLimit = false;

		if ("next".equals(direction)) {
			offset += DEFAULT_MAX_RESULTS_LIMIT;
		} else if ("previous".equals(direction)) {
			offset -= DEFAULT_MAX_RESULTS_LIMIT;
		}

		TermSearchResult termSearchResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			termSearchResult = termSearchService.getTermSearchResult(detailSearchFilter, selectedDatasets, resultMode, resultLang, offset, noLimit);
		} else {
			termSearchResult = termSearchService.getTermSearchResult(simpleSearchFilter, selectedDatasets, resultMode, resultLang, offset, noLimit);
		}

		termSearchResult.setOffset(offset);
		model.addAttribute("termSearchResult", termSearchResult);
		model.addAttribute("searchUri", searchUri);

		return TERM_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "term_search_result";
	}

	@PostMapping(MEANING_INTERNAL_LINK_SEARCH_URI)
	public String searchMeaningInternalLink(@RequestBody InternalLinkSearchRequest internalLinkSearchRequest, Model model) throws Exception {

		String searchFilter = internalLinkSearchRequest.getSearchFilter();
		logger.debug("meaning internal link search {}", searchFilter);

		List<String> datasets = Collections.emptyList();
		SearchResultMode resultMode = SearchResultMode.MEANING;
		String resultLang = null;
		boolean noLimit = true;

		TermSearchResult termSearchResult = termSearchService.getTermSearchResult(searchFilter, datasets, resultMode, resultLang, DEFAULT_OFFSET, noLimit);
		termSearchResult.setShowPaging(false);

		model.addAttribute("termSearchResult", termSearchResult);

		return TERM_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "search_result_rows";
	}

	@PostMapping(TERM_SEARCH_RESULT_DOWNLOAD_URI)
	public ResponseEntity<byte[]> downloadTermSearchResult(
			@RequestParam("searchUri") String searchUri,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		SearchUriData searchUriData = searchHelper.parseSearchUri(TERM_SEARCH_PAGE, searchUri);
		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		String resultLang = searchUriData.getResultLang();

		byte[] bytes = null;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			bytes = termSearchService.serialiseTermSearchResult(detailSearchFilter, selectedDatasets, languagesOrder, resultLang, userProfile, user);
		} else {
			bytes = termSearchService.serialiseTermSearchResult(simpleSearchFilter, selectedDatasets, languagesOrder, resultLang, userProfile, user);
		}

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=meanings.json")
				.contentType(MediaType.APPLICATION_JSON)
				.body(bytes);
	}

	@PostMapping(TERM_SEARCH_RESULT_QUEUE_URI)
	@ResponseStatus(HttpStatus.OK)
	public void queueTermSearchResult(
			@RequestParam("searchUri") String searchUri,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean) {

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		SearchUriData searchUriData = searchHelper.parseSearchUri(TERM_SEARCH_PAGE, searchUri);

		TermSearchResultQueueContent content = new TermSearchResultQueueContent();
		content.setLanguagesOrder(languagesOrder);
		content.setUserProfile(userProfile);
		content.setSearchUri(searchUri);
		content.setSearchUriData(searchUriData);

		QueueItem queueItem = new QueueItem();
		queueItem.setAction(QueueAction.TERM_SEARCH_RESULT_EMAIL);
		queueItem.setUser(user);
		queueItem.setContent(content);
		queueService.queue(queueItem);
	}

	@GetMapping(TERM_SEARCH_RESULT_ACCESS_URI + "/{requestKey}")
	public ResponseEntity<byte[]> accessTermSearchResult(
			@PathVariable("requestKey") String requestKey,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String requestContent = dataRequestService.getDataRequestContent(requestKey);
		byte[] bytes = requestContent.getBytes(StandardCharsets.UTF_8);
		String filename = "data-request-" + requestKey + ".json";
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.APPLICATION_JSON)
				.body(bytes);
	}
}
