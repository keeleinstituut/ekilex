package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.LayerName;
import eki.common.constant.SourceType;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.Source;
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
public class LexSearchController extends AbstractSearchController {

	private static final Logger logger = LoggerFactory.getLogger(LexSearchController.class);

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private SourceService sourceService;

	@GetMapping(value = LEX_SEARCH_URI)
	public String initSearch(Model model) throws Exception {

		initSearchForms(LEX_SEARCH_PAGE, model);
		resetUserRole(model);

		WordsResult wordsResult = new WordsResult();
		model.addAttribute("wordsResult", wordsResult);

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
			return "redirect:" + LEX_SEARCH_URI;
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
		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping(value = LEX_SEARCH_URI + "/**")
	public String lexSearch(Model model, HttpServletRequest request) throws Exception {

		final String searchPage = LEX_SEARCH_PAGE;

		// if redirect from login arrives
		initSearchForms(searchPage, model);
		resetUserRole(model);

		String searchUri = StringUtils.removeStart(request.getRequestURI(), LEX_SEARCH_URI);
		logger.debug(searchUri);

		SearchUriData searchUriData = searchHelper.parseSearchUri(searchPage, searchUri);

		if (!searchUriData.isValid()) {
			initSearchForms(searchPage, model);
			model.addAttribute("wordsResult", new WordsResult());
			model.addAttribute("invalidSearch", true);
			return LEX_SEARCH_PAGE;
		}

		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean fetchAll = false;

		UserContextData userContextData = getUserContextData();
		Long userId = userContextData.getUserId();
		DatasetPermission userRole = userContextData.getUserRole();
		LayerName layerName = userContextData.getLayerName();

		userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			wordsResult = lexSearchService.getWords(detailSearchFilter, selectedDatasets, userRole, layerName, fetchAll, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);
		} else {
			wordsResult = lexSearchService.getWords(simpleSearchFilter, selectedDatasets, userRole, layerName, fetchAll, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);
		}
		boolean noResults = wordsResult.getTotalCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("noResults", noResults);
		model.addAttribute("searchUri", searchUri);

		return LEX_SEARCH_PAGE;
	}

	@PostMapping(LEX_PAGING_URI)
	public String paging(
			@RequestParam("offset") int offset,
			@RequestParam("searchUri") String searchUri,
			@RequestParam("direction") String direction,
			Model model) throws Exception {

		SearchUriData searchUriData = searchHelper.parseSearchUri(LEX_SEARCH_PAGE, searchUri);

		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean fetchAll = false;

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		LayerName layerName = userContextData.getLayerName();

		if (StringUtils.equals("next", direction)) {
			offset += DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("previous", direction)) {
			offset -= DEFAULT_MAX_RESULTS_LIMIT;
		}

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			wordsResult = lexSearchService.getWords(detailSearchFilter, selectedDatasets, userRole, layerName, fetchAll, offset, DEFAULT_MAX_RESULTS_LIMIT);
		} else {
			wordsResult = lexSearchService.getWords(simpleSearchFilter, selectedDatasets, userRole, layerName, fetchAll, offset, DEFAULT_MAX_RESULTS_LIMIT);
		}

		wordsResult.setOffset(offset);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("searchUri", searchUri);
		return LEX_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "search_result";
	}

	@GetMapping("/wordsearch")
	public String searchWord(@RequestParam String searchFilter, Model model) {

		logger.debug("word search {}", searchFilter);

		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		LayerName layerName = userContextData.getLayerName();
		List<String> datasetCodes = userContextData.getPreferredDatasetCodes();

		WordsResult result = lexSearchService.getWords(searchFilter, datasetCodes, userRole, layerName, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);
		model.addAttribute("wordsFoundBySearch", result.getWords());
		model.addAttribute("totalCount", result.getTotalCount());

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "word_search_result";
	}

	@GetMapping("/lexemesearch")
	public String searchLexeme(
			@RequestParam String searchFilter,
			@RequestParam Long lexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		logger.debug("lexeme search {}, lexeme {}", searchFilter, lexemeId);

		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		WordLexeme lexeme = lexSearchService.getDefaultWordLexeme(lexemeId, languagesOrder);

		UserContextData userContextData = getUserContextData();
		Long userId = userContextData.getUserId();
		DatasetPermission userRole = userContextData.getUserRole();
		LayerName layerName = userContextData.getLayerName();
		List<String> datasetCodes = Arrays.asList(lexeme.getDatasetCode());

		List<WordLexeme> lexemes = lexSearchService.getWordLexemesWithDefinitionsData(searchFilter, datasetCodes, userId, userRole, layerName);

		model.addAttribute("lexemesFoundBySearch", lexemes);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "lexeme_search_result";
	}

	@GetMapping("/meaningsearch")
	public String searchMeaning(@RequestParam String searchFilter, Model model) {

		logger.debug("meaning search {}", searchFilter);

		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);

		UserContextData userContextData = getUserContextData();
		Long userId = userContextData.getUserId();
		DatasetPermission userRole = userContextData.getUserRole();
		LayerName layerName = userContextData.getLayerName();
		List<String> datasetCodes = userContextData.getPreferredDatasetCodes();

		List<WordLexeme> lexemes = lexSearchService.getWordLexemesWithDefinitionsData(searchFilter, datasetCodes, userId, userRole, layerName);

		List<WordLexeme> lexemesFileterdByMeaning = new ArrayList<>();
		List<Long> distinctMeanings = new ArrayList<>();
		for (WordLexeme lexeme : lexemes) {
			if (!distinctMeanings.contains(lexeme.getMeaningId())) {
				lexemesFileterdByMeaning.add(lexeme);
				distinctMeanings.add(lexeme.getMeaningId());
			}
		}
		model.addAttribute("lexemesFoundBySearch", lexemesFileterdByMeaning);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "meaning_search_result";
	}

	@GetMapping("/personsearch")
	public String searchPersons(@RequestParam String searchFilter, Model model) {

		logger.debug("person search {}", searchFilter);

		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);
		List<Source> sources = sourceService.getSources(searchFilter, SourceType.PERSON);
		model.addAttribute("sourcesFoundBySearch", sources);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_search_result";
	}

	@GetMapping(WORD_DETAILS_URI + "/{wordId}")
	public String wordDetails(@PathVariable("wordId") Long wordId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) throws Exception {

		logger.debug("word details for {}", wordId);

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		List<String> datasetCodes = getUserPreferredDatasetCodes();
		WordDetails details = lexSearchService.getWordDetails(wordId, datasetCodes, languagesOrder, user, userProfile, false);
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

}
