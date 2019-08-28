package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.util.UriUtils;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.CorporaSentence;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.CorporaServiceEst;
import eki.wordweb.service.CorporaServiceRus;
import eki.wordweb.service.LexSearchService;
import eki.wordweb.service.StatDataCollector;
import eki.wordweb.web.bean.SessionBean;
import eki.wordweb.web.util.UserAgentUtil;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SearchController extends AbstractController {

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private CorporaServiceEst corporaServiceEst;

	@Autowired
	private CorporaServiceRus corporaServiceRus;

	@Autowired
	private StatDataCollector statDataCollector;

	@Autowired
	private UserAgentUtil userAgentUtil;

	@GetMapping(SEARCH_URI)
	public String home(Model model) {

		populateSearchModel("", new WordsData(SEARCH_MODE_DETAIL), model);

		return SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI)
	public String searchWords(
			@RequestParam(name = "searchWord") String searchWord,
			@RequestParam(name = "sourceLang") String sourceLang,
			@RequestParam(name = "destinLang") String destinLang,
			@RequestParam(name = "searchMode") String searchMode) {

		searchWord = StringUtils.trim(searchWord);
		if (StringUtils.isBlank(searchWord)) {
			return "redirect:" + SEARCH_PAGE;
		}
		String searchUri = composeSearchUri(searchWord, sourceLang, destinLang, null, searchMode);

		return "redirect:" + searchUri;
	}

	@GetMapping({
		SEARCH_URI + "/{langPair}/{searchMode}/{searchWord}/{homonymNr}",
		SEARCH_URI + "/{langPair}/{searchMode}/{searchWord}"})
	public String searchWordsByUri(
			@PathVariable(name = "langPair") String langPair,
			@PathVariable(name = "searchMode") String searchMode,
			@PathVariable(name = "searchWord") String searchWord,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			HttpServletRequest request,
			Model model) {

		boolean sessionBeanNotPresent = sessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (sessionBeanNotPresent) {
			sessionBean = createSessionBean(model);
		} else {
			sessionBean = getSessionBean(model);
		}

		searchWord = UriUtils.decode(searchWord, SystemConstant.UTF_8);
		SearchFilter searchFilter = validate(langPair, searchWord, homonymNrStr, searchMode);
		sessionBean.setLastSearchWord(searchWord);

		if (sessionBeanNotPresent) {
			//to get rid of the sessionid in the url
			return "redirect:" + searchFilter.getSearchUri();
		} else if (!searchFilter.isValid()) {
			return "redirect:" + searchFilter.getSearchUri();
		}

		String sourceLang = searchFilter.getSourceLang();
		String destinLang = searchFilter.getDestinLang();
		Integer homonymNr = searchFilter.getHomonymNr();
		searchMode = searchFilter.getSearchMode();
		sessionBean.setSourceLang(sourceLang);
		sessionBean.setDestinLang(destinLang);
		sessionBean.setSearchMode(searchMode);

		WordsData wordsData = lexSearchService.getWords(searchWord, sourceLang, destinLang, homonymNr, searchMode);
		sessionBean.setSearchMode(wordsData.getSearchMode());
		populateSearchModel(searchWord, wordsData, model);

		boolean isIeUser = userAgentUtil.isTraditionalMicrosoftUser(request);
		statDataCollector.addSearchStat(langPair, wordsData.getSearchMode(), wordsData.isForcedSearchMode(), wordsData.isResultsExist(), isIeUser);

		return SEARCH_PAGE;
	}

	@GetMapping(value = "/prefix/{sourceLang}/{destinLang}/{wordPrefix}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, List<String>> searchWordsByPrefix(
			@PathVariable("sourceLang") String sourceLang,
			@PathVariable("destinLang") String destinLang,
			@PathVariable("wordPrefix") String wordPrefix) {

		Map<String, List<String>> searchResultCandidates = lexSearchService.getWordsByPrefix(wordPrefix, sourceLang, destinLang, AUTOCOMPLETE_MAX_RESULTS_LIMIT);
		return searchResultCandidates;
	}

	@GetMapping("/worddetails/{wordId}")
	public String wordDetails(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		String sourceLang = sessionBean.getSourceLang();
		String destinLang = sessionBean.getDestinLang();
		String searchMode = sessionBean.getSearchMode();
		WordData wordData = lexSearchService.getWordData(wordId, sourceLang, destinLang, DISPLAY_LANG, searchMode);
		model.addAttribute("wordData", wordData);

		return SEARCH_PAGE + " :: worddetails";
	}

	@GetMapping("/korp/{lang}/{sentence}")
	public String searchFromCorpora(@PathVariable("lang") String language, @PathVariable("sentence") String sentence, Model model) {

		List<CorporaSentence> textCorpus = new ArrayList<>();
		if (StringUtils.equalsIgnoreCase(language, "est")) {
			textCorpus = corporaServiceEst.getSentences(sentence);
		} else if (StringUtils.equalsIgnoreCase(language, "rus")) {
			textCorpus = corporaServiceRus.getSentences(sentence);
		}
		model.addAttribute("sentences", textCorpus);
		model.addAttribute("sentence", sentence);
		model.addAttribute("corp_language", language);

		return "common-search :: korp";
	}

	private SearchFilter validate(String langPair, String searchWord, String homonymNrStr, String searchMode) {

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
		if (!StringUtils.equalsAny(searchMode, SEARCH_MODE_SIMPLE, SEARCH_MODE_DETAIL)) {
			searchMode = SEARCH_MODE_DETAIL;
		}

		String searchUri = composeSearchUri(searchWord, sourceLang, destinLang, homonymNr, searchMode);

		SearchFilter searchFilter = new SearchFilter();
		searchFilter.setSearchWord(searchWord);
		searchFilter.setSourceLang(sourceLang);
		searchFilter.setDestinLang(destinLang);
		searchFilter.setHomonymNr(homonymNr);
		searchFilter.setSearchMode(searchMode);
		searchFilter.setSearchUri(searchUri);
		searchFilter.setValid(isValid);

		return searchFilter;
	}

	private String composeSearchUri(String searchWord, String sourceLang, String destinLang, Integer homonymNr, String searchMode) {

		String encodedSearchWord = UriUtils.encode(searchWord, SystemConstant.UTF_8);
		String searchUri = SEARCH_URI + "/" + sourceLang + LANGUAGE_PAIR_SEPARATOR + destinLang + "/" + searchMode + "/" + encodedSearchWord;
		if (homonymNr != null) {
			searchUri += "/" + homonymNr;
		}
		return searchUri;
	}

}
