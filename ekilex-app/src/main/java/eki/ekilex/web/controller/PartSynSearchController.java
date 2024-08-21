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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import eki.common.data.Count;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.Tag;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.security.EkilexPermissionEvaluator;
import eki.ekilex.service.PartSynSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class PartSynSearchController extends AbstractPrivateSearchController {

	private static final Logger logger = LoggerFactory.getLogger(PartSynSearchController.class);

	@Autowired
	private PartSynSearchService partSynSearchService;

	@Autowired
	private EkilexPermissionEvaluator permissionEvaluator;

	@GetMapping(value = PART_SYN_SEARCH_URI)
	public String initPage(Authentication authentication, Model model) {

		boolean isSynPageAccessPermitted = permissionEvaluator.isSynPageAccessPermitted(authentication);
		if (!isSynPageAccessPermitted) {
			return REDIRECT_PREF + HOME_URI;
		}

		initSearchForms(PART_SYN_SEARCH_PAGE, model);

		return PART_SYN_SEARCH_PAGE;
	}

	@PostMapping(value = PART_SYN_SEARCH_URI)
	@PreAuthorize("@permEval.isSynPageAccessPermitted(authentication)")
	public String synSearch(
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			Model model) throws Exception {

		final SearchResultMode resultMode = SearchResultMode.WORD;
		final String resultLang = null;

		simpleSearchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(simpleSearchFilter);
		formDataCleanup(PART_SYN_SEARCH_PAGE, detailSearchFilter);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}

		String roleDatasetCode = getDatasetCodeFromRole();
		List<String> roleDatasets = new ArrayList<>(Arrays.asList(roleDatasetCode));
		String searchUri = searchHelper.composeSearchUri(searchMode, roleDatasets, simpleSearchFilter, detailSearchFilter, resultMode, resultLang);

		return REDIRECT_PREF + PART_SYN_SEARCH_URI + searchUri;
	}

	@GetMapping(value = PART_SYN_SEARCH_URI + "/**")
	@PreAuthorize("@permEval.isSynPageAccessPermitted(authentication)")
	public String synSearch(Model model, HttpServletRequest request) throws Exception {

		String searchUri = StringUtils.removeStart(request.getRequestURI(), PART_SYN_SEARCH_URI);
		logger.debug(searchUri);

		initSearchForms(PART_SYN_SEARCH_PAGE, model);

		SearchUriData searchUriData = searchHelper.parseSearchUri(PART_SYN_SEARCH_PAGE, searchUri);

		if (!searchUriData.isValid()) {
			model.addAttribute("invalidSearch", Boolean.TRUE);
			return PART_SYN_SEARCH_PAGE;
		}

		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean noLimit = false;

		UserContextData userContextData = getUserContextData();
		EkiUser user = userContextData.getUser();
		List<String> tagNames = userContextData.getTagNames();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();
		if (StringUtils.isEmpty(userRoleDatasetCode)) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		List<String> datasetCodes = new ArrayList<>(Arrays.asList(userRoleDatasetCode));

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			searchHelper.addValidationMessages(detailSearchFilter);
			wordsResult = partSynSearchService.getWords(detailSearchFilter, datasetCodes, tagNames, user, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		} else {
			wordsResult = partSynSearchService.getWords(simpleSearchFilter, datasetCodes, tagNames, user, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		}
		boolean noResults = wordsResult.getTotalCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("noResults", noResults);
		model.addAttribute("searchUri", PART_SYN_SEARCH_URI + searchUri);

		return PART_SYN_SEARCH_PAGE;
	}

	@GetMapping(PART_SYN_WORD_DETAILS_URI + "/{wordId}")
	@PreAuthorize("@permEval.isSynPageAccessPermitted(authentication)")
	public String details(
			@PathVariable("wordId") Long wordId,
			@RequestParam(required = false) Long markedSynMeaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		logger.debug("Requesting details by word {}", wordId);

		EkiUser user = userContext.getUser();
		UserContextData userContextData = getUserContextData();
		Long userId = userContextData.getUserId();
		List<String> synCandidateLangCodes = userContextData.getPartSynCandidateLangCodes();
		List<String> synMeaningWordLangCodes = userContextData.getSynMeaningWordLangCodes();
		Tag activeTag = userContextData.getActiveTag();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		Count meaningCount = new Count();
		WordDetails details = partSynSearchService.getWordPartSynDetails(wordId, languagesOrder, synCandidateLangCodes, synMeaningWordLangCodes, activeTag, user, userProfile);

		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);
		model.addAttribute("markedSynMeaningId", markedSynMeaningId);
		model.addAttribute("meaningCount", meaningCount);

		return PART_SYN_SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@GetMapping(PART_SYN_SEARCH_WORDS_URI)
	@PreAuthorize("@permEval.isSynPageAccessPermitted(authentication)")
	public String searchSynWords(
			@RequestParam String searchFilter,
			@RequestParam(required = false) List<Long> excludedIds,
			@RequestParam(required = false) String language,
			@RequestParam(required = false) String morphCode,
			Model model) throws Exception {

		logger.debug("word search {}", searchFilter);

		final int maxResultsLimit = 250;
		UserContextData userContextData = getUserContextData();
		EkiUser user = userContextData.getUser();
		List<String> datasetCodes = userContextData.getPreferredDatasetCodes();
		List<String> tagNames = userContextData.getTagNames();

		WordsResult result = partSynSearchService.getWords(searchFilter, datasetCodes, tagNames, user, DEFAULT_OFFSET, maxResultsLimit, false);

		model.addAttribute("wordsFoundBySearch", result.getWords());
		model.addAttribute("totalCount", result.getTotalCount());
		model.addAttribute("existingIds", excludedIds);
		model.addAttribute("searchedWord", searchFilter);
		model.addAttribute("selectedWordLanguage", language);
		model.addAttribute("selectedWordMorphCode", morphCode);

		return PART_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "syn_word_search_result";
	}

}
