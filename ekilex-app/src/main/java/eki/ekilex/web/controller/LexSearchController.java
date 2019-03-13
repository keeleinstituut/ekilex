package eki.ekilex.web.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.SourceType;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.Source;
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

		initSearchForms(model);

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
			@RequestParam(name = "fetchAll", required = false) boolean fetchAll,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		formDataCleanup(selectedDatasets, simpleSearchFilter, detailSearchFilter, null, sessionBean, model);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}
		selectedDatasets = sessionBean.getSelectedDatasets();

		String searchUri = searchHelper.composeSearchUri(searchMode, selectedDatasets, simpleSearchFilter, detailSearchFilter, fetchAll);
		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping(value = LEX_SEARCH_URI + "/**")
	public String lexSearch(@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model, HttpServletRequest request) {

		String searchUri = StringUtils.removeStart(request.getRequestURI(), LEX_SEARCH_URI);
		logger.debug(searchUri);

		SearchUriData searchUriData = searchHelper.parseSearchUri(searchUri);

		if (!searchUriData.isValid()) {
			initSearchForms(model);
			model.addAttribute("noResults", true);
			return LEX_SEARCH_PAGE;
		}

		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean fetchAll = searchUriData.isFetchAll();

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			wordsResult = lexSearchService.findWords(detailSearchFilter, selectedDatasets, fetchAll);
		} else {
			wordsResult = lexSearchService.findWords(simpleSearchFilter, selectedDatasets, fetchAll);
		}
		boolean noResults = wordsResult.getTotalCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("noResults", noResults);

		return LEX_SEARCH_PAGE;
	}

	@GetMapping("/wordsearchajax")
	public String searchWordAjax(
			@RequestParam String searchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {
		logger.debug("word search ajax {}", searchFilter);

		WordsResult result = lexSearchService.findWords(searchFilter, sessionBean.getSelectedDatasets(), false);
		model.addAttribute("wordsFoundBySearch", result.getWords());
		model.addAttribute("totalCount", result.getTotalCount());

		return COMPONENTS_PAGE + " :: word_search_result";
	}

	@GetMapping("/lexemesearchajax")
	public String searchLexemeAjax(
			@RequestParam String searchFilter,
			@RequestParam Long lexemeId,
			Model model) {
		logger.debug("lexeme search ajax {}, lexeme {}", searchFilter, lexemeId);

		WordLexeme lexeme = lexSearchService.getWordLexeme(lexemeId);
		List<String> datasets = Collections.singletonList(lexeme.getDatasetCode());
		List<WordLexeme> lexemes = lexSearchService.findWordLexemesWithDefinitionsData(searchFilter, datasets);
		model.addAttribute("lexemesFoundBySearch", lexemes);

		return COMPONENTS_PAGE + " :: lexeme_search_result";
	}

	@GetMapping("/meaningsearchajax")
	public String searchMeaningAjax(
			@RequestParam String searchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {
		logger.debug("meaning search ajax {}", searchFilter);

		List<WordLexeme> lexemes = lexSearchService.findWordLexemesWithDefinitionsData(searchFilter, sessionBean.getSelectedDatasets());
		List<WordLexeme> lexemesFileterdByMeaning = new ArrayList<>();
		List<Long> distinctMeanings = new ArrayList<>();
		for (WordLexeme lexeme : lexemes) {
			if (!distinctMeanings.contains(lexeme.getMeaningId())) {
				lexemesFileterdByMeaning.add(lexeme);
				distinctMeanings.add(lexeme.getMeaningId());
			}
		}
		model.addAttribute("lexemesFoundBySearch", lexemesFileterdByMeaning);

		return COMPONENTS_PAGE + " :: meaning_search_result";
	}

	@GetMapping("/personsearchajax")
	public String searchPersonsAjax(
			@RequestParam String searchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {
		logger.debug("person search ajax {}", searchFilter);

		List<Source> sources = sourceService.findSourcesByNameAndType(searchFilter, SourceType.PERSON);
		model.addAttribute("sourcesFoundBySearch", sources);

		return COMPONENTS_PAGE + " :: source_search_result";
	}

	@GetMapping(WORD_DETAILS_URI + "/{wordId}")
	public String details(@PathVariable("wordId") Long wordId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		logger.debug("Requesting details by word {}", wordId);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			selectedDatasets = commonDataService.getDatasetCodes();
		}
		WordDetails details = lexSearchService.getWordDetails(wordId, selectedDatasets);
		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);

		return LEX_SEARCH_PAGE + " :: details";
	}

}
