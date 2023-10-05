package eki.ekilex.web.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.SourceType;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.InternalLinkSearchRequest;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.Source;
import eki.ekilex.data.Tag;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.SourceService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexSearchController extends AbstractPrivateSearchController {

	private static final Logger logger = LoggerFactory.getLogger(LexSearchController.class);

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private SourceService sourceService;

	@GetMapping(value = LEX_SEARCH_URI)
	public String initSearch(Model model) throws Exception {

		initSearchForms(LEX_SEARCH_PAGE, model);

		return LEX_SEARCH_PAGE;
	}

	@PostMapping(value = LEX_SEARCH_URI)
	public String lexSearch(
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "selectedDatasets", required = false) List<String> selectedDatasets,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			Model model) throws Exception {

		if (CollectionUtils.isEmpty(selectedDatasets)) {
			return REDIRECT_PREF + LEX_SEARCH_URI;
		}

		final SearchResultMode resultMode = SearchResultMode.WORD;
		final String resultLang = null;

		simpleSearchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(simpleSearchFilter);
		formDataCleanup(LEX_SEARCH_PAGE, detailSearchFilter);

		Long userId = userContext.getUserId();
		userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}

		String searchUri = searchHelper.composeSearchUri(searchMode, selectedDatasets, simpleSearchFilter, detailSearchFilter, resultMode, resultLang);
		return REDIRECT_PREF + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping(value = LEX_SEARCH_URI + "/**")
	public String lexSearch(Model model, HttpServletRequest request) throws Exception {

		final String searchPage = LEX_SEARCH_PAGE;

		// if redirect from login arrives
		initSearchForms(searchPage, model);

		String searchUri = StringUtils.removeStart(request.getRequestURI(), LEX_SEARCH_URI);
		logger.debug(searchUri);

		SearchUriData searchUriData = searchHelper.parseSearchUri(searchPage, searchUri);

		if (!searchUriData.isValid()) {
			model.addAttribute("invalidSearch", Boolean.TRUE);
			return LEX_SEARCH_PAGE;
		}

		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean noLimit = false;

		UserContextData userContextData = getUserContextData();
		Long userId = userContextData.getUserId();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();

		userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);

		Integer pageNum = getPageNum(request);
		int offset = calculateOffset(pageNum);

		WordsResult wordsResult;
		Long selectedMeaningId = null;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			searchHelper.addValidationMessages(detailSearchFilter);
			wordsResult = lexSearchService.getWords(detailSearchFilter, selectedDatasets, userRole, tagNames, offset, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
			selectedMeaningId = searchHelper.getMeaningIdSearchMeaningId(detailSearchFilter);
		} else {
			wordsResult = lexSearchService.getWords(simpleSearchFilter, selectedDatasets, userRole, tagNames, offset, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		}
		boolean noResults = wordsResult.getTotalCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("noResults", noResults);
		model.addAttribute("searchUri", searchUri);
		model.addAttribute("selectedMeaningId", selectedMeaningId);

		return LEX_SEARCH_PAGE;
	}

	@PostMapping(LEX_PAGING_URI)
	public String paging(
			@RequestParam("offset") int offset,
			@RequestParam("searchUri") String searchUri,
			@RequestParam("direction") String direction,
			@RequestParam(name = "pageNum", required = false) Integer pageNum,
			Model model) throws Exception {

		SearchUriData searchUriData = searchHelper.parseSearchUri(LEX_SEARCH_PAGE, searchUri);

		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean noLimit = false;

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();

		if (StringUtils.equals("next", direction)) {
			offset += DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("previous", direction)) {
			offset -= DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("page", direction)) {
			offset = (pageNum - 1) * DEFAULT_MAX_RESULTS_LIMIT;
		}

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			wordsResult = lexSearchService.getWords(detailSearchFilter, selectedDatasets, userRole, tagNames, offset, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		} else {
			wordsResult = lexSearchService.getWords(simpleSearchFilter, selectedDatasets, userRole, tagNames, offset, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		}

		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("searchUri", searchUri);
		return LEX_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "search_result";
	}

	@GetMapping("/wordsearch")
	public String searchWord(@RequestParam String searchFilter, Model model) throws Exception {

		logger.debug("word search {}", searchFilter);

		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();
		List<String> datasetCodes = userContextData.getPreferredDatasetCodes();

		WordsResult result = lexSearchService.getWords(searchFilter, datasetCodes, userRole, tagNames, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, false);
		model.addAttribute("wordsFoundBySearch", result.getWords());
		model.addAttribute("totalCount", result.getTotalCount());

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "word_search_result";
	}

	@GetMapping("/personsearch")
	public String searchPersons(@RequestParam String searchFilter, Model model) {

		logger.debug("person search {}", searchFilter);

		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);
		List<Source> sources = sourceService.getSources(searchFilter, SourceType.PERSON);
		model.addAttribute("sourcesFoundBySearch", sources);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_search_result";
	}

	@GetMapping(value = {
			WORD_DETAILS_URI + "/{wordId}",
			WORD_DETAILS_URI + "/{wordId}/{selectedMeaningId}"
	})
	public String wordDetails(
			@PathVariable("wordId") Long wordId,
			@PathVariable(value = "selectedMeaningId", required = false) Long selectedMeaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		logger.debug("word details for {}", wordId);

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		List<String> datasetCodes = userProfile.getPreferredDatasets();
		UserContextData userContextData = getUserContextData();
		Tag activeTag = userContextData.getActiveTag();
		WordDetails details = lexSearchService.getWordDetails(wordId, selectedMeaningId, datasetCodes, languagesOrder, user, userProfile, activeTag, false);
		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);

		return LEX_SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "word_details";
	}

	@GetMapping(LEXEME_DETAILS_URI + "/{composition}/{lexemeId}/{levels}")
	public String lexemeDetails(
			@PathVariable("composition") String composition,
			@PathVariable("lexemeId") Long lexemeId,
			@PathVariable("levels") String levels,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		logger.debug("lexeme {} details for {}", composition, lexemeId);

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		boolean isFullData = StringUtils.equals(composition, "full");
		WordLexeme lexeme = lexSearchService.getWordLexeme(lexemeId, languagesOrder, userProfile, user, isFullData);
		lexeme.setLevels(levels);
		model.addAttribute("lexeme", lexeme);

		return "lexdetail" + PAGE_FRAGMENT_ELEM + "lexeme_details_" + composition;
	}

	@PostMapping(WORD_INTERNAL_LINK_SEARCH_URI)
	public String searchWordInternalLink(@RequestBody InternalLinkSearchRequest internalLinkSearchRequest, Model model) throws Exception {

		String searchFilter = internalLinkSearchRequest.getSearchFilter();
		logger.debug("word internal link search {}", searchFilter);

		List<String> datasets = Collections.emptyList();
		boolean noLimit = true;

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();

		WordsResult wordsResult = lexSearchService.getWords(searchFilter, datasets, userRole, tagNames, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		wordsResult.setShowPaging(false);

		model.addAttribute("wordsResult", wordsResult);

		return LEX_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "search_result_rows";
	}

}
