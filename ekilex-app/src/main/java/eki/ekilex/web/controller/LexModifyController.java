package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.UpdateService;
import eki.ekilex.web.bean.SessionBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexModifyController implements WebConstant {

	private final LexSearchService lexSearchService;

	private final UpdateService updateService;

	public LexModifyController(LexSearchService lexSearchService, UpdateService updateService) {
		this.lexSearchService = lexSearchService;
		this.updateService = updateService;
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
			RedirectAttributes attributes) {

		WordLexeme lexeme = lexSearchService.getWordLexeme(lexemeId);
		updateService.joinLexemeMeanings(lexemeId, lexemeId2);
		attributes.addFlashAttribute(SEARCH_WORD_KEY, lexeme.getWords()[0]);

		return "redirect:" + LEX_SEARCH_URI;
	}

	@GetMapping("/lexseparate/{lexemeId}")
	public String separate(
			@PathVariable("lexemeId") Long lexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) {

		WordLexeme lexeme = lexSearchService.getWordLexeme(lexemeId);
		updateService.separateLexemeMeanings(lexemeId);
		attributes.addFlashAttribute(SEARCH_WORD_KEY, lexeme.getWords()[0]);

		return "redirect:" + LEX_SEARCH_URI;
	}

}
