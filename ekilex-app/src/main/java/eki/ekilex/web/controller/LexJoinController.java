package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.web.bean.SessionBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexJoinController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(LexJoinController.class);

	private final LexSearchService lexSearchService;

	public LexJoinController(LexSearchService lexSearchService) {
		this.lexSearchService = lexSearchService;
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
		List<WordLexeme> lexemes = new ArrayList<>();
		if (isNotBlank(searchFilter)) {
			String cleanedUpFilter = searchFilter.replace("*", "").replace("?", "").replace("%", "").replace("_", "");
			List<String> datasets = Collections.singletonList(lexeme.getDatasetCode());
			WordsResult words = lexSearchService.findWords(cleanedUpFilter, datasets, true);
			if (!words.getWords().isEmpty()) {
				for (Word word : words.getWords()) {
					WordDetails wordDetails = lexSearchService.getWordDetails(word.getWordId(), datasets);
					lexemes.addAll(wordDetails.getLexemes());
				}
			}
		}

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
			Model model,
			RedirectAttributes attributes) {

		WordLexeme lexeme = lexSearchService.getWordLexeme(lexemeId);
		attributes.addFlashAttribute("searchWord", lexeme.getWords()[0]);
		return "redirect:" + LEX_SEARCH_URI;
	}

}
