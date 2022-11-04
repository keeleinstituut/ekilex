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
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.Tag;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.FullSynSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class FullSynSearchController extends AbstractPrivateSearchController {

	private static final Logger logger = LoggerFactory.getLogger(FullSynSearchController.class);

	@Autowired
	private FullSynSearchService fullSynSearchService;

	@GetMapping(value = FULL_SYN_SEARCH_URI)
	public String initPage(Model model) {

		DatasetPermission userRole = userContext.getUserRole();
		if (userRole == null) {
			return REDIRECT_PREF + HOME_URI;
		}
		if (userRole.isSuperiorPermission()) {
			return REDIRECT_PREF + HOME_URI;
		}

		initSearchForms(FULL_SYN_SEARCH_PAGE, model);

		return FULL_SYN_SEARCH_PAGE;
	}

	@PostMapping(value = FULL_SYN_SEARCH_URI)
	public String synSearch(
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			Model model) throws Exception {

		final SearchResultMode resultMode = SearchResultMode.WORD;
		final String resultLang = null;

		simpleSearchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(simpleSearchFilter);
		formDataCleanup(FULL_SYN_SEARCH_PAGE, detailSearchFilter);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}

		String roleDatasetCode = getDatasetCodeFromRole();
		List<String> roleDatasets = new ArrayList<>(Arrays.asList(roleDatasetCode));

		String searchUri = searchHelper.composeSearchUri(searchMode, roleDatasets, simpleSearchFilter, detailSearchFilter, resultMode, resultLang);
		return REDIRECT_PREF + FULL_SYN_SEARCH_URI + searchUri;
	}

	@GetMapping(value = FULL_SYN_SEARCH_URI + "/**")
	public String synSearch(Model model, HttpServletRequest request) throws Exception {

		String searchUri = StringUtils.removeStart(request.getRequestURI(), FULL_SYN_SEARCH_URI);
		logger.debug(searchUri);

		initSearchForms(FULL_SYN_SEARCH_PAGE, model);

		SearchUriData searchUriData = searchHelper.parseSearchUri(FULL_SYN_SEARCH_PAGE, searchUri);

		if (!searchUriData.isValid()) {
			model.addAttribute("invalidSearch", Boolean.TRUE);
			return FULL_SYN_SEARCH_PAGE;
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
			wordsResult = fullSynSearchService.getWords(detailSearchFilter, datasetCodes, userRole, tagNames, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		} else {
			wordsResult = fullSynSearchService.getWords(simpleSearchFilter, datasetCodes, userRole, tagNames, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		}
		boolean noResults = wordsResult.getTotalCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("noResults", noResults);
		model.addAttribute("searchUri", searchUri);

		return FULL_SYN_SEARCH_PAGE;
	}

	@GetMapping(FULL_SYN_WORD_DETAILS_URI + "/{wordId}")
	public String details(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		logger.debug("Requesting details by word {}", wordId);

		EkiUser user = userContext.getUser();
		UserContextData userContextData = getUserContextData();
		Long userId = userContextData.getUserId();
		List<String> synMeaningWordLangCodes = userContextData.getSynMeaningWordLangCodes();
		String synCandidateLangCode = userContextData.getFullSynCandidateLangCode();
		String synCandidateDatasetCode = userContextData.getFullSynCandidateDatasetCode();
		Tag activeTag = userContextData.getActiveTag();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		Count meaningCount = new Count();
		WordDetails details = fullSynSearchService.getWordFullSynDetails(
				wordId, languagesOrder, synCandidateDatasetCode, synCandidateLangCode, synMeaningWordLangCodes, activeTag, user, userProfile);

		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);
		model.addAttribute("synCandidateLangCode", synCandidateLangCode);
		model.addAttribute("synCandidateDatasetCode", synCandidateDatasetCode);
		model.addAttribute("meaningCount", meaningCount);

		return FULL_SYN_SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@PostMapping(FULL_SYN_SEARCH_WORDS_URI + "/{targetMeaningId}/{wordRelationId}")
	public String searchSynWordRelationWords(
			@PathVariable Long targetMeaningId,
			@PathVariable Long wordRelationId,
			Model model) {

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<WordDescript> wordCandidates = fullSynSearchService.getRelationWordCandidates(wordRelationId, userRole);

		model.addAttribute("wordCandidates", wordCandidates);
		model.addAttribute("targetMeaningId", targetMeaningId);
		model.addAttribute("wordRelationId", wordRelationId);

		return FULL_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "full_syn_relation_word_select";
	}

	@GetMapping(FULL_SYN_SEARCH_WORDS_URI)
	public String searchSynMeaningWords(
			@RequestParam Long targetMeaningId,
			@RequestParam String wordValue,
			Model model) {

		logger.debug("Syn meaning word search: {}", wordValue);

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		String wordLang = userContextData.getFullSynCandidateLangCode();
		List<WordDescript> wordCandidates = fullSynSearchService.getMeaningWordCandidates(wordValue, wordLang, userRole);

		model.addAttribute("wordCandidates", wordCandidates);
		model.addAttribute("targetMeaningId", targetMeaningId);
		model.addAttribute("wordValue", wordValue);

		return FULL_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "full_syn_meaning_word_select";
	}

}
