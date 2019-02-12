package eki.ekilex.web.controller;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
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
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.UpdateService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexModifyController implements WebConstant {

	private final LexSearchService lexSearchService;

	private final UpdateService updateService;

	private final SearchHelper searchHelper;

	public LexModifyController(LexSearchService lexSearchService, UpdateService updateService, SearchHelper searchHelper) {
		this.lexSearchService = lexSearchService;
		this.updateService = updateService;
		this.searchHelper = searchHelper;
	}

	@GetMapping("/lexjoin/{lexemeId}")
	public String show(@PathVariable("lexemeId") Long lexemeId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		model.addAttribute("sourceLexeme", lexSearchService.getWordLexeme(lexemeId));
		model.addAttribute("searchFilter", null);

		return LEX_JOIN_PAGE;
	}

	@PostMapping("/lexjoin/{lexemeId}")
	public String search(
			@PathVariable("lexemeId") Long lexemeId,
			@RequestParam(name = "searchFilter", required = false) String searchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		WordLexeme lexeme = lexSearchService.getWordLexeme(lexemeId);
		List<String> datasets = Collections.singletonList(lexeme.getDatasetCode());
		if (CollectionUtils.isNotEmpty(sessionBean.getSelectedDatasets())) {
			datasets = sessionBean.getSelectedDatasets();
		}
		List<WordLexeme> lexemes = lexSearchService.findWordLexemesWithMinimalData(searchFilter, datasets);
		model.addAttribute("sourceLexeme", lexeme);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("lexemes", lexemes);

		return LEX_JOIN_PAGE;
	}

	@GetMapping("/lexjoin/{lexemeId}/{lexemeId2}")
	public String join(
			@PathVariable("lexemeId") Long lexemeId,
			@PathVariable("lexemeId2") Long lexemeId2,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		WordLexeme lexeme = lexSearchService.getWordLexeme(lexemeId);
		List<String> validationMessages = updateService.validateLexemeJoin(lexemeId, lexemeId2);
		if (!validationMessages.isEmpty()) {
			model.addAttribute("sourceLexeme", lexeme);
			model.addAttribute("validationMessages", validationMessages);
			return LEX_JOIN_PAGE;
		}
		updateService.joinLexemes(lexemeId, lexemeId2);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		String firstWordValue = lexeme.getWords()[0];
		String searchUri = searchHelper.composeSearchUri(selectedDatasets, firstWordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping("/lexseparate/{lexemeId}")
	public String separate(
			@PathVariable("lexemeId") Long lexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		WordLexeme lexeme = lexSearchService.getWordLexeme(lexemeId);
		updateService.separateLexemeMeanings(lexemeId);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		String firstWordValue = lexeme.getWords()[0];
		String searchUri = searchHelper.composeSearchUri(selectedDatasets, firstWordValue);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

}
