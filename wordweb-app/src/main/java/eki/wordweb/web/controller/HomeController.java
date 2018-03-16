package eki.wordweb.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.Word;
import eki.wordweb.service.LexSearchService;

@ConditionalOnWebApplication
@Controller
public class HomeController implements WebConstant {

	//TODO should be set by defaults and/or ui
	private static final String DISPLAY_LANG = "est";

	@Autowired
	private LexSearchService lexSearchService;

	@RequestMapping(value = HOME_URI, method = RequestMethod.GET)
	public String home() {

		//TODO set defaults

		return HOME_PAGE;
	}

	@RequestMapping(value = HOME_URI, method = RequestMethod.POST)
	public String searchWords(@RequestParam(name = "simpleSearchFilter", required = false) String searchFilter, Model model) {

		List<Word> words = lexSearchService.findWords(searchFilter);
		model.addAttribute("words", words);

		return HOME_PAGE;
	}

	@GetMapping("/worddetails/{wordId}")
	public String wordDetails(@PathVariable("wordId") Long wordId, Model model) {

		List<Lexeme> lexemes = lexSearchService.findLexemes(wordId, DISPLAY_LANG);
		model.addAttribute("lexemes", lexemes);

		//TODO implement
		return HOME_PAGE + " :: worddetails";
	}
}
