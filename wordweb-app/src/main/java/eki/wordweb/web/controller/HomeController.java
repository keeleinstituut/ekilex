package eki.wordweb.web.controller;

import java.util.Collections;
import java.util.List;

import eki.wordweb.data.WordsData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.CorporaSentence;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.service.CorporaService;
import eki.wordweb.service.LexSearchService;
import eki.wordweb.web.bean.SessionBean;

import static java.util.Collections.emptyList;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class HomeController implements WebConstant {

	//TODO should be set by defaults and/or ui
	private static final String DISPLAY_LANG = "est";

	private static final String DEFAULT_SOURCE_LANG = "est";

	private static final String DEFAULT_DESTIN_LANG = "est";

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private CorporaService corporaService;

	@Value("${speech.recognition.service.url:}")
	private String speechRecognitionServiceUrl;

	@RequestMapping(value = HOME_URI, method = RequestMethod.GET)
	public String home(Model model) {

		populateModel("", new WordsData(emptyList(), emptyList()), new WordData(), model);

		return HOME_PAGE;
	}

	@RequestMapping(value = HOME_URI, method = RequestMethod.POST)
	public String searchWords(
			@RequestParam(name = "simpleSearchFilter", required = false) String searchFilter,
			@RequestParam(name = "sourceLang") String sourceLang,
			@RequestParam(name = "destinLang") String destinLang,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		searchFilter = StringUtils.trim(searchFilter);
		WordsData words = lexSearchService.findWords(searchFilter, sourceLang, destinLang);
		populateModel(searchFilter, words, new WordData(), model);

		return HOME_PAGE;
	}

	@GetMapping("/worddetails/{wordId}")
	public String wordDetails(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		String sourceLang = sessionBean.getSourceLang();
		String destinLang = sessionBean.getDestinLang();
		WordData wordData = lexSearchService.getWordData(wordId, sourceLang, destinLang, DISPLAY_LANG);
		model.addAttribute("wordData", wordData);

		return HOME_PAGE + " :: worddetails";
	}

	@GetMapping("/korp/{sentence}")
	public String generateSoundFileUrl(@PathVariable String sentence, Model model) {

		List<CorporaSentence> textCorpus = corporaService.fetchSentences(sentence);
		model.addAttribute("sentences", textCorpus);

		return HOME_PAGE + " :: korp";
	}

	private void populateModel(String simpleSearchFilter, WordsData words, WordData wordData, Model model) {

		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		if (sessionBean == null) {
			sessionBean = new SessionBean();
			model.addAttribute(SESSION_BEAN, sessionBean);
		}
		if (StringUtils.isBlank(sessionBean.getSourceLang())) {
			sessionBean.setSourceLang(DEFAULT_SOURCE_LANG);
		}
		if (StringUtils.isBlank(sessionBean.getDestinLang())) {
			sessionBean.setDestinLang(DEFAULT_DESTIN_LANG);
		}
		if (StringUtils.equals(sessionBean.getSourceLang(), "rus")
				&& StringUtils.equals(sessionBean.getDestinLang(), "rus")) {
			sessionBean.setSourceLang(DEFAULT_SOURCE_LANG);
		}

		model.addAttribute("speechRecognitionServiceUrl", speechRecognitionServiceUrl);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("words", words.getFullWords());
		model.addAttribute("wordData", wordData);
		model.addAttribute("moreWords", words.getPartialWords());
	}
}
