package eki.wordweb.web.controller;

import java.util.List;

import eki.wordweb.data.CorporaSentence;
import eki.wordweb.service.CorporaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.service.LexSearchService;

@ConditionalOnWebApplication
@Controller
public class HomeController implements WebConstant {

	//TODO should be set by defaults and/or ui
	private static final String DISPLAY_LANG = "est";

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private CorporaService corporaService;

	@Value("${speech.recognition.service.url:}")
	private String speechRecognitionServiceUrl;

	@RequestMapping(value = HOME_URI, method = RequestMethod.GET)
	public String home(Model model) {

		//TODO set defaults
		WordData wordData = new WordData();
		model.addAttribute("simpleSearchFilter", "");
		model.addAttribute("speechRecognitionServiceUrl", speechRecognitionServiceUrl);
		model.addAttribute("wordData", wordData);

		return HOME_PAGE;
	}

	@RequestMapping(value = HOME_URI, method = RequestMethod.POST)
	public String searchWords(@RequestParam(name = "simpleSearchFilter", required = false) String searchFilter, Model model) {

		List<Word> words = lexSearchService.findWords(searchFilter);
		WordData wordData = new WordData();
		model.addAttribute("words", words);
		model.addAttribute("simpleSearchFilter", searchFilter);
		model.addAttribute("speechRecognitionServiceUrl", speechRecognitionServiceUrl);
		model.addAttribute("wordData", wordData);

		return HOME_PAGE;
	}

	@GetMapping("/worddetails/{wordId}")
	public String wordDetails(@PathVariable("wordId") Long wordId, Model model) {

		WordData wordData = lexSearchService.getWordData(wordId, DISPLAY_LANG);
		Word word = lexSearchService.getWord(wordId);
		model.addAttribute("word", word);
		model.addAttribute("wordData", wordData);

		return HOME_PAGE + " :: worddetails";
	}

	@GetMapping("/korp/{sentence}")
	public String generateSoundFileUrl(@PathVariable String sentence, Model model) {

		List<CorporaSentence> textCorpus = corporaService.fetchSentences(sentence);
		model.addAttribute("sentences", textCorpus);

		return HOME_PAGE + " :: korp";
	}

}
