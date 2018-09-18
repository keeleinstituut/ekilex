package eki.wordweb.web.controller;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.CorporaSentence;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.CorporaService;
import eki.wordweb.service.LexSearchService;
import eki.wordweb.web.bean.SessionBean;
import org.springframework.web.util.UriUtils;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SearchController extends AbstractController {

	private static String IS_BEGINNER = "simple";

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private CorporaService corporaService;

	@GetMapping(SEARCH_URI)
	public String home(Model model) {

		populateModel("", new WordsData(emptyList(), emptyList()), model);

		return SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI)
	public String searchWords(
			@RequestParam(name = "searchWord") String searchWord,
			@RequestParam(name = "sourceLang") String sourceLang,
			@RequestParam(name = "destinLang") String destinLang,
			@RequestParam(name = "isBeginner") Boolean isBeginner,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		if (isBeginner == null) {
			isBeginner = Boolean.FALSE;
		}
		sessionBean.setBeginner(isBeginner);

		searchWord = StringUtils.trim(searchWord);
		if (StringUtils.isBlank(searchWord)) {
			return "redirect:" + SEARCH_PAGE;
		}
		String searchUri = composeSearchUri(searchWord, sourceLang, destinLang, null, isBeginner);

		return "redirect:" + searchUri;
	}

	@GetMapping({
		SEARCH_URI + "/{langPair}/{searchWord}/{homonymNr}/{isSimple}",
		SEARCH_URI + "/{langPair}/{searchWord}/{homonymNr}",
		SEARCH_URI + "/{langPair}/{searchWord}"})
	public String searchWordsByUri(
			@PathVariable(name = "langPair") String langPair,
			@PathVariable(name = "searchWord") String searchWord,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			@PathVariable(name = "isSimple", required = false) String isSimple,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		searchWord = UriUtils.decode(searchWord, SystemConstant.UTF_8);
		boolean isBeginner = isSimple != null ? Objects.equals(isSimple, IS_BEGINNER) : sessionBean.isBeginner();
		SearchFilter searchFilter = validate(langPair, searchWord, homonymNrStr, isBeginner);

		if (!searchFilter.isValid()) {
			return "redirect:" + searchFilter.getSearchUri();
		}

		String sourceLang = searchFilter.getSourceLang();
		String destinLang = searchFilter.getDestinLang();
		Integer homonymNr = searchFilter.getHomonymNr();
		isBeginner = searchFilter.isBeginner();
		sessionBean.setSourceLang(sourceLang);
		sessionBean.setDestinLang(destinLang);
		sessionBean.setBeginner(isBeginner);

		WordsData wordsData = lexSearchService.findWords(searchWord, sourceLang, destinLang, homonymNr, isBeginner);
		if (isBeginner && wordsData.getFullMatchWords().isEmpty()) {
			wordsData = lexSearchService.findWords(searchWord, sourceLang, destinLang, homonymNr, false);
			if (!wordsData.getFullMatchWords().isEmpty()) {
				sessionBean.setBeginner(false);
				model.addAttribute("switchedToDetailMode", true);
			}
		}
		populateModel(searchWord, wordsData, model);

		return SEARCH_PAGE;
	}

	@GetMapping(value = "/prefix/{sourceLang}/{destinLang}/{wordPrefix}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, List<String>> searchWordsByPrefix(
			@PathVariable("sourceLang") String sourceLang,
			@PathVariable("destinLang") String destinLang,
			@PathVariable("wordPrefix") String wordPrefix) {

		Map<String, List<String>> searchResultCandidates = lexSearchService.findWordsByPrefix(wordPrefix, sourceLang, destinLang, AUTOCOMPLETE_MAX_RESULTS_LIMIT);
		return searchResultCandidates;
	}

	@GetMapping("/worddetails/{wordId}")
	public String wordDetails(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		String sourceLang = sessionBean.getSourceLang();
		String destinLang = sessionBean.getDestinLang();
		boolean isBeginner = sessionBean.isBeginner();
		WordData wordData = lexSearchService.getWordData(wordId, sourceLang, destinLang, DISPLAY_LANG, isBeginner);
		model.addAttribute("wordData", wordData);

		return SEARCH_PAGE + " :: worddetails";
	}

	@GetMapping("/korp/{sentence}")
	public String generateSoundFileUrl(@PathVariable String sentence, Model model) {

		List<CorporaSentence> textCorpus = corporaService.fetchSentences(sentence);
		model.addAttribute("sentences", textCorpus);

		return SEARCH_PAGE + " :: korp";
	}

	private SearchFilter validate(String langPair, String searchWord, String homonymNrStr, boolean isBeginner) {

		boolean isValid = true;
		String[] languages = StringUtils.split(langPair, LANGUAGE_PAIR_SEPARATOR);

		String sourceLang;
		String destinLang;
		Integer homonymNr;

		if (languages.length == 2) {
			if (ArrayUtils.contains(SUPPORTED_LANGUAGES, languages[0])) {
				sourceLang = languages[0];
				isValid = isValid & true;
			} else {
				sourceLang = DEFAULT_SOURCE_LANG;
				isValid = isValid & false;
			}
			if (ArrayUtils.contains(SUPPORTED_LANGUAGES, languages[1])) {
				destinLang = languages[1];
				isValid = isValid & true;
			} else {
				destinLang = DEFAULT_DESTIN_LANG;
				isValid = isValid & false;
			}
		} else {
			sourceLang = DEFAULT_SOURCE_LANG;
			destinLang = DEFAULT_DESTIN_LANG;
			isValid = isValid & false;
		}
		if (StringUtils.isBlank(homonymNrStr)) {
			homonymNr = 1;
			isValid = isValid & false;
		} else if (!StringUtils.isNumeric(homonymNrStr)) {
			homonymNr = 1;
			isValid = isValid & false;
		} else {
			homonymNr = new Integer(homonymNrStr);
			isValid = isValid & true;
		}

		if (!StringUtils.equals(sourceLang, DEFAULT_SOURCE_LANG)
				|| !StringUtils.equals(destinLang, DEFAULT_DESTIN_LANG)) {
			isBeginner = false;
		}

		String searchUri = composeSearchUri(searchWord, sourceLang, destinLang, homonymNr, isBeginner);

		SearchFilter searchFilter = new SearchFilter();
		searchFilter.setSearchWord(searchWord);
		searchFilter.setSourceLang(sourceLang);
		searchFilter.setDestinLang(destinLang);
		searchFilter.setHomonymNr(homonymNr);
		searchFilter.setBeginner(isBeginner);
		searchFilter.setSearchUri(searchUri);
		searchFilter.setValid(isValid);

		return searchFilter;
	}

	private String composeSearchUri(String searchWord, String sourceLang, String destinLang, Integer homonymNr, boolean isBeginner) {

		String encodedSearchWord = UriUtils.encodePathSegment(searchWord, SystemConstant.UTF_8);
		String searchUri = SEARCH_URI + "/" + sourceLang + LANGUAGE_PAIR_SEPARATOR + destinLang + "/" + encodedSearchWord;
		if (homonymNr != null) {
			searchUri += "/" + homonymNr;
		}
		if (isBeginner) {
			searchUri += "/" + IS_BEGINNER;
		}
		return searchUri;
	}

}
