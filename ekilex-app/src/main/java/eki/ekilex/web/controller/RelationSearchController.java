package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class RelationSearchController extends AbstractMutableDataPageController {

	private static final Logger logger = LoggerFactory.getLogger(RelationSearchController.class);

	@Autowired
	private LexSearchService lexSearchService;

	@GetMapping("/lexemesearch")
	public String searchLexeme(
			@RequestParam String searchFilter,
			@RequestParam Long lexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		logger.debug("lexeme search {}, lexeme {}", searchFilter, lexemeId);

		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);

		String lexemeDatasetCode = lookupService.getLexemeDatasetCode(lexemeId);
		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();
		List<String> datasetCodes = Arrays.asList(lexemeDatasetCode);

		List<WordLexeme> lexemes = lexSearchService.getWordLexemesWithDefinitionsData(searchFilter, datasetCodes, userRole, tagNames);

		model.addAttribute("lexemesFoundBySearch", lexemes);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "lexeme_search_result";
	}

	@GetMapping("/meaningsearch")
	public String searchMeaning(
			@RequestParam String searchFilter,
			@RequestParam Long meaningId,
			Model model) throws Exception {

		logger.debug("meaning search {}, meaning {}", searchFilter, meaningId);

		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();
		List<String> datasetCodes = userContextData.getPreferredDatasetCodes();

		List<WordLexeme> lexemes = lexSearchService.getWordLexemesWithDefinitionsData(searchFilter, datasetCodes, userRole, tagNames);

		List<WordLexeme> lexemesFilteredByMeaning = new ArrayList<>();
		List<Long> distinctMeanings = new ArrayList<>();
		for (WordLexeme lexeme : lexemes) {
			Long lexemeMeaningId = lexeme.getMeaningId();
			boolean isMeaningExcluded = Objects.equals(meaningId, lexemeMeaningId);
			if (!distinctMeanings.contains(lexemeMeaningId) && !isMeaningExcluded) {
				lexemesFilteredByMeaning.add(lexeme);
				distinctMeanings.add(lexemeMeaningId);
			}
		}

		model.addAttribute("lexemesFoundBySearch", lexemesFilteredByMeaning);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "meaning_search_result";
	}

}
