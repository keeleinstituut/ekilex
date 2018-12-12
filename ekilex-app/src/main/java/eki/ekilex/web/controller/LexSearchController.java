package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import eki.ekilex.data.WordLexeme;
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

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexSearchController extends AbstractSearchController {

	private static final Logger logger = LoggerFactory.getLogger(LexSearchController.class);

	@Autowired
	private LexSearchService lexSearchService;

	@GetMapping(value = LEX_SEARCH_URI)
	public String initSearch(Model model) throws Exception {

		if (model.containsAttribute(SEARCH_WORD_KEY)) {
			String searchWord = model.asMap().get(SEARCH_WORD_KEY).toString();
			SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
			return search(sessionBean.getSelectedDatasets(), null, searchWord, false, null, sessionBean, model);
		}

		initSearchForms(model);

		return LEX_SEARCH_PAGE;
	}

	@PostMapping(value = LEX_SEARCH_URI)
	public String search(
			@RequestParam(name = "selectedDatasets", required = false) List<String> selectedDatasets,
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@RequestParam(name = "fetchAll", required = false) boolean fetchAll,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		logger.debug("Searching by \"{}\" in {}", simpleSearchFilter, selectedDatasets);

		cleanup(selectedDatasets, null, simpleSearchFilter, detailSearchFilter, sessionBean, model);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}
		WordsResult result;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			result = lexSearchService.findWords(detailSearchFilter, selectedDatasets, fetchAll);
		} else {
			result = lexSearchService.findWords(simpleSearchFilter, selectedDatasets, fetchAll);
		}
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("wordsFoundBySearch", result.getWords());
		model.addAttribute("totalCount", result.getTotalCount());

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

		return 	" lexsearch_components :: word_search_result";
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

		return 	" lexsearch_components :: lexeme_search_result";
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

		return 	" lexsearch_components :: meaning_search_result";
	}

	@GetMapping(WORD_DETAILS_URI + "/{wordId}")
	public String details(@PathVariable("wordId") Long wordId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		logger.debug("Requesting details by word {}", wordId);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			List<Dataset> allDatasets = commonDataService.getDatasets();
			selectedDatasets = allDatasets.stream().map(dataset -> dataset.getCode()).collect(Collectors.toList());
		}
		WordDetails details = lexSearchService.getWordDetails(wordId, selectedDatasets);
		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);

		return LEX_SEARCH_PAGE + " :: details";
	}

}
