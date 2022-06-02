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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import eki.common.data.Count;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.Tag;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.SynSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SynSearchController extends AbstractPrivateSearchController {

	private static final Logger logger = LoggerFactory.getLogger(SynSearchController.class);

	@Autowired
	protected SynSearchService synSearchService;

	@GetMapping(value = SYN_SEARCH_URI)
	public String initPage(Model model) {

		DatasetPermission userRole = userContext.getUserRole();
		if (userRole == null) {
			return "redirect:" + HOME_URI;
		}
		if (userRole.isSuperiorPermission()) {
			return "redirect:" + HOME_URI;
		}

		initSearchForms(SYN_SEARCH_PAGE, model);

		return SYN_SEARCH_PAGE;
	}

	@PostMapping(value = SYN_SEARCH_URI)
	public String synSearch(
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			Model model) throws Exception {

		final SearchResultMode resultMode = SearchResultMode.WORD;
		final String resultLang = null;

		simpleSearchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(simpleSearchFilter);
		formDataCleanup(SYN_SEARCH_PAGE, detailSearchFilter);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}

		String roleDatasetCode = getDatasetCodeFromRole();
		List<String> roleDatasets = new ArrayList<>(Arrays.asList(roleDatasetCode));

		String searchUri = searchHelper.composeSearchUri(searchMode, roleDatasets, simpleSearchFilter, detailSearchFilter, resultMode, resultLang);
		return "redirect:" + SYN_SEARCH_URI + searchUri;
	}

	@GetMapping(value = SYN_SEARCH_URI + "/**")
	public String synSearch(Model model, HttpServletRequest request) throws Exception {

		String searchUri = StringUtils.removeStart(request.getRequestURI(), SYN_SEARCH_URI);
		logger.debug(searchUri);

		initSearchForms(SYN_SEARCH_PAGE, model);

		SearchUriData searchUriData = searchHelper.parseSearchUri(SYN_SEARCH_PAGE, searchUri);

		if (!searchUriData.isValid()) {
			model.addAttribute("invalidSearch", Boolean.TRUE);
			return SYN_SEARCH_PAGE;
		}

		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean noLimit = false;

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();
		if (StringUtils.isEmpty(userRoleDatasetCode)) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		List<String> datasetCodes = new ArrayList<>(Arrays.asList(userRoleDatasetCode));

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			searchHelper.addValidationMessages(detailSearchFilter);
			wordsResult = synSearchService.getWords(detailSearchFilter, datasetCodes, userRole, tagNames, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		} else {
			wordsResult = synSearchService.getWords(simpleSearchFilter, datasetCodes, userRole, tagNames, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		}
		boolean noResults = wordsResult.getTotalCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("noResults", noResults);
		model.addAttribute("searchUri", searchUri);

		return SYN_SEARCH_PAGE;
	}

	@PostMapping(SYN_PAGING_URI)
	public String paging(
			@RequestParam("offset") int offset,
			@RequestParam("searchUri") String searchUri,
			@RequestParam("direction") String direction,
			@RequestParam(name = "userInputPage", required = false) Integer userInputPage,
			Model model) throws Exception {

		SearchUriData searchUriData = searchHelper.parseSearchUri(SYN_SEARCH_PAGE, searchUri);

		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean noLimit = false;

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();
		if (StringUtils.isEmpty(userRoleDatasetCode)) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		List<String> datasetCodes = new ArrayList<>(Arrays.asList(userRoleDatasetCode));

		if (StringUtils.equals("next", direction)) {
			offset += DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("previous", direction)) {
			offset -= DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("page", direction)) {
			offset = (userInputPage - 1) * DEFAULT_MAX_RESULTS_LIMIT;
		}

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			wordsResult = synSearchService.getWords(detailSearchFilter, datasetCodes, userRole, tagNames, offset, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		} else {
			wordsResult = synSearchService.getWords(simpleSearchFilter, datasetCodes, userRole, tagNames, offset, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		}

		wordsResult.setOffset(offset);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("searchUri", searchUri);
		return SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "search_result";
	}

	@GetMapping(SYN_WORD_DETAILS_URI + "/{wordId}")
	public String details(
			@PathVariable("wordId") Long wordId,
			@RequestParam(required = false) Long markedSynMeaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		logger.debug("Requesting details by word {}", wordId);

		UserContextData userContextData = getUserContextData();
		Long userId = userContextData.getUserId();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> synCandidateLangCodes = userContextData.getSynCandidateLangCodes();
		List<String> synMeaningWordLangCodes = userContextData.getSynMeaningWordLangCodes();
		Tag activeTag = userContextData.getActiveTag();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		Count meaningCount = new Count();
		WordDetails details = synSearchService.getWordSynDetails(wordId, languagesOrder, synCandidateLangCodes, synMeaningWordLangCodes, activeTag, userRole, userProfile);

		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);
		model.addAttribute("markedSynMeaningId", markedSynMeaningId);
		model.addAttribute("meaningCount", meaningCount);

		return SYN_SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@PostMapping(SYN_RELATION_STATUS_URI)
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String updateRelationStatus(@RequestParam Long id, @RequestParam String status, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Updating syn relation status id {}, new status {}", id, status);
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		synSearchService.updateRelationStatus(id, status, isManualEventOnUpdateEnabled);
		return RESPONSE_OK_VER2;
	}

	@PostMapping(SYN_RELATION_STATUS_URI + "/delete")
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String updateWordSynRelationsStatusDeleted(@RequestParam Long wordId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Updating word {} syn relation status to \"DELETED\"", wordId);
		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		String datasetCode = userRole.getDatasetCode();
		List<String> synCandidateLangCodes = userContextData.getSynCandidateLangCodes();

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		synSearchService.updateWordSynRelationsStatusDeleted(wordId, datasetCode, synCandidateLangCodes, isManualEventOnUpdateEnabled);
		return RESPONSE_OK_VER2;
	}

	@PostMapping(SYN_CREATE_MEANING_RELATION_URI + "/{targetMeaningId}/{sourceMeaningId}/{wordRelationId}")
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String createSynMeaningRelation(
			@PathVariable Long targetMeaningId,
			@PathVariable Long sourceMeaningId,
			@PathVariable Long wordRelationId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		synSearchService.createSynMeaningRelation(targetMeaningId, sourceMeaningId, wordRelationId, isManualEventOnUpdateEnabled);
		return RESPONSE_OK_VER2;
	}

	@GetMapping(SYN_SEARCH_WORDS_URI)
	public String searchSynWords(
			@RequestParam String searchFilter,
			@RequestParam(required = false) List<Long> excludedIds,
			@RequestParam(required = false) String language,
			@RequestParam(required = false) String morphCode,
			Model model) throws Exception {

		logger.debug("word search {}", searchFilter);

		final int maxResultsLimit = 250;
		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> datasetCodes = userContextData.getPreferredDatasetCodes();
		List<String> tagNames = userContextData.getTagNames();

		WordsResult result = synSearchService.getWords(searchFilter, datasetCodes, userRole, tagNames, DEFAULT_OFFSET, maxResultsLimit, false);

		model.addAttribute("wordsFoundBySearch", result.getWords());
		model.addAttribute("totalCount", result.getTotalCount());
		model.addAttribute("existingIds", excludedIds);

		model.addAttribute("searchedWord", searchFilter);
		model.addAttribute("selectedWordLanguage", language);
		model.addAttribute("selectedWordMorphCode", morphCode);

		return SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "syn_word_search_result";
	}

}
