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

import eki.common.constant.GlobalConstant;
import eki.common.constant.LayerName;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.SynSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SynSearchController extends AbstractSearchController implements SystemConstant, GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(SynSearchController.class);

	@Autowired
	protected SynSearchService synSearchService;

	@GetMapping(value = SYN_SEARCH_URI)
	public String initPage(Model model) {

		initSearchForms(SYN_SEARCH_PAGE, model);
		resetUserRole(model);

		WordsResult wordsResult = new WordsResult();
		model.addAttribute("wordsResult", wordsResult);

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

		SessionBean sessionBean = getSessionBean(model);
		String roleDatasetCode = getDatasetCodeFromRole(sessionBean);
		List<String> roleDatasets = new ArrayList<>(Arrays.asList(roleDatasetCode));

		String searchUri = searchHelper.composeSearchUri(searchMode, roleDatasets, simpleSearchFilter, detailSearchFilter, resultMode, resultLang);
		return "redirect:" + SYN_SEARCH_URI + searchUri;
	}

	@GetMapping(value = SYN_SEARCH_URI + "/**")
	public String synSearch(Model model, HttpServletRequest request) throws Exception {

		Long userId = userService.getAuthenticatedUser().getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		LayerName layerName = userProfile.getPreferredLayerName();
		String searchUri = StringUtils.removeStart(request.getRequestURI(), SYN_SEARCH_URI);
		logger.debug(searchUri);

		initSearchForms(SYN_SEARCH_PAGE, model);
		resetUserRole(model);

		SearchUriData searchUriData = searchHelper.parseSearchUri(SYN_SEARCH_PAGE, searchUri);

		if (!searchUriData.isValid()) {
			initSearchForms(SYN_SEARCH_PAGE, model);
			model.addAttribute("wordsResult", new WordsResult());
			model.addAttribute("noResults", true);
			return SYN_SEARCH_PAGE;
		}

		SessionBean sessionBean = getSessionBean(model);
		String roleDatasetCode = getDatasetCodeFromRole(sessionBean);
		List<String> roleDatasets = new ArrayList<>(Arrays.asList(roleDatasetCode));

		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();

		boolean fetchAll = false;

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			wordsResult = synSearchService.getWords(detailSearchFilter, roleDatasets, layerName, fetchAll, DEFAULT_OFFSET);
		} else {
			wordsResult = synSearchService.getWords(simpleSearchFilter, roleDatasets, layerName, fetchAll, DEFAULT_OFFSET);
		}
		boolean noResults = wordsResult.getTotalCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("noResults", noResults);

		return SYN_SEARCH_PAGE;
	}

	@GetMapping(SYN_WORD_DETAILS_URI + "/{wordId}")
	public String details(
			@PathVariable("wordId") Long wordId,
			@RequestParam(required = false) Long markedSynWordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		logger.debug("Requesting details by word {}", wordId);

		String datasetCode = getDatasetCodeFromRole(sessionBean);
		Long userId = userService.getAuthenticatedUser().getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		List<String> candidateLangCodes = userProfile.getPreferredSynCandidateLangs();
		List<String> meaningWordLangCodes = userProfile.getPreferredSynLexMeaningWordLangs();
		LayerName layerName = userProfile.getPreferredLayerName();

		WordSynDetails details = synSearchService.getWordSynDetails(wordId, datasetCode, layerName, candidateLangCodes, meaningWordLangCodes);

		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);
		model.addAttribute("markedSynWordId", markedSynWordId);
		model.addAttribute("candidateLangCodes", candidateLangCodes);
		model.addAttribute("meaningWordLangCodes", meaningWordLangCodes);

		return SYN_SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@PostMapping(SYN_CHANGE_RELATION_STATUS)
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String changeRelationStatus(@RequestParam Long id, @RequestParam String status) {
		logger.debug("Changing syn relation status id {}, new status {}", id, status);

		synSearchService.changeRelationStatus(id, status);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(SYN_CREATE_LEXEME + "/{meaningId}/{wordId}/{lexemeId}/{relationId}")
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String createSynLexeme(@PathVariable Long meaningId, @PathVariable Long wordId, @PathVariable Long lexemeId, @PathVariable Long relationId, Model model) {
		logger.debug("Adding lexeme to syn candidate word Id {}, meaning Id {} , existing lexeme Id {}, relation Id {}", wordId, meaningId, lexemeId, relationId);
		SessionBean sessionBean = getSessionBean(model);
		String datasetCode = getDatasetCodeFromRole(sessionBean);

		synSearchService.createSecondarySynLexeme(meaningId, wordId, datasetCode, lexemeId, relationId);

		return RESPONSE_OK_VER2;
	}

	@GetMapping(SYN_SEARCH_WORDS)
	public String searchSynWords(
			@RequestParam String searchFilter,
			@RequestParam(required = false) List<Long> excludedIds,
			@RequestParam(required = false) String language,
			@RequestParam(required = false) String morphCode,
			Model model) {
		logger.debug("word search {}", searchFilter);

		List<String> selectedDatasets = getUserPreferredDatasetCodes();
		WordsResult result = synSearchService.getWords(searchFilter, selectedDatasets, false, DEFAULT_OFFSET);

		model.addAttribute("wordsFoundBySearch", result.getWords());
		model.addAttribute("totalCount", result.getTotalCount());
		model.addAttribute("existingIds", excludedIds);

		model.addAttribute("searchedWord", searchFilter);
		model.addAttribute("selectedWordLanguage", language);
		model.addAttribute("selectedWordMorphCode", morphCode);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "syn_word_search_result";
	}

	@PostMapping(UPDATE_SYN_CANDIDATE_LANGS_URI)
	@ResponseBody
	public String updateCandidateLangs(@RequestParam("languages") List<String> languages) {

		Long userId = userService.getAuthenticatedUser().getId();
		userProfileService.updateUserPreferredSynCandidateLangs(languages, userId);
		return RESPONSE_OK_VER1;
	}

	@PostMapping(UPDATE_SYN_MEANING_WORD_LANGS_URI)
	@ResponseBody
	public String updateMeaningWordLangs(@RequestParam("languages") List<String> languages) {

		Long userId = userService.getAuthenticatedUser().getId();
		userProfileService.updateUserPreferredMeaningWordLangs(languages, userId);
		return RESPONSE_OK_VER1;
	}

	private String getDatasetCodeFromRole(SessionBean sessionBean) {
		DatasetPermission role = sessionBean.getUserRole();
		if (role == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		return role.getDatasetCode();
	}

}
