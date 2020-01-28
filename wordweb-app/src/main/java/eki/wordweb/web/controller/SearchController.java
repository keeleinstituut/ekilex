package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
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
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.CorporaServiceEst;
import eki.wordweb.service.CorporaServiceRus;
import eki.wordweb.service.StatDataCollector;
import eki.wordweb.service.UnifSearchService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SearchController extends AbstractController {

	@Autowired
	private UnifSearchService unifSearchService;

	@Autowired
	private CorporaServiceEst corporaServiceEst;

	@Autowired
	private CorporaServiceRus corporaServiceRus;

	@Autowired
	private StatDataCollector statDataCollector;

	@GetMapping(SEARCH_URI)
	public String home(Model model) {

		populateSearchModel("", new WordsData(SEARCH_MODE_DETAIL), model);

		return SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI)
	public String searchWords(
			@RequestParam(name = "destinLangsStr") String destinLangStr,
			@RequestParam(name = "searchMode") String searchMode,
			@RequestParam(name = "searchWord") String searchWord) {

		searchWord = StringUtils.trim(searchWord);
		if (StringUtils.isBlank(searchWord)) {
			return "redirect:" + SEARCH_PAGE;
		}
		String searchUri = composeSearchUri(destinLangStr, searchMode, searchWord, null);

		return "redirect:" + searchUri;
	}

	@GetMapping({
			SEARCH_URI + UNIF_URI + "/{destinLangsStr}/{searchMode}/{searchWord}/{homonymNr}",
			SEARCH_URI + UNIF_URI + "/{destinLangsStr}/{searchMode}/{searchWord}"})
	public String searchUnifWordsByUri(
			@PathVariable(name = "destinLangsStr") String destinLangsStr,
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
		String recentWord = sessionBean.getRecentWord();
		Integer recentHomonymNr = sessionBean.getRecentHomonymNr();
		SearchFilter searchFilter = validateSearch(destinLangsStr, searchMode, searchWord, homonymNrStr, recentWord, recentHomonymNr);
		sessionBean.setSearchWord(searchWord);

		if (sessionBeanNotPresent) {
			//to get rid of the sessionid in the url
			return "redirect:" + searchFilter.getSearchUri();
		} else if (!searchFilter.isValid()) {
			return "redirect:" + searchFilter.getSearchUri();
		}

		List<String> destinLangs = searchFilter.getDestinLangs();
		Integer homonymNr = searchFilter.getHomonymNr();
		searchMode = searchFilter.getSearchMode();
		sessionBean.setDestinLangs(destinLangs);
		sessionBean.setSearchMode(searchMode);

		WordsData wordsData = unifSearchService.getWords(searchWord, homonymNr, destinLangs, searchMode);
		sessionBean.setSearchMode(wordsData.getSearchMode());
		populateSearchModel(searchWord, wordsData, model);

		boolean isIeUser = userAgentUtil.isTraditionalMicrosoftUser(request);
		statDataCollector.addSearchStat(destinLangs, wordsData.getSearchMode(), wordsData.isResultsExist(), isIeUser);

		return SEARCH_PAGE;
	}

	//backward compatibility support
	@GetMapping({
			SEARCH_URI + LEX_URI + "/{langPair}/{searchMode}/{searchWord}/{homonymNr}",
			SEARCH_URI + LEX_URI + "/{langPair}/{searchMode}/{searchWord}"})
	public String searchLexWordsByUri(
			@PathVariable(name = "langPair") String langPair,
			@PathVariable(name = "searchMode") String searchMode,
			@PathVariable(name = "searchWord") String searchWord,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			HttpServletRequest request,
			Model model) {

		Integer homonymNr = null;
		if (StringUtils.isNotBlank(homonymNrStr) && StringUtils.isNumeric(homonymNrStr)) {
			homonymNr = Integer.valueOf(homonymNrStr);
		}
		String searchUri = composeSearchUri(DESTIN_LANG_ALL, searchMode, searchWord, homonymNr);

		return "redirect:" + searchUri;
	}

	@GetMapping(value = "/prefix/{wordPrefix}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, List<String>> searchWordsByPrefix(@PathVariable("wordPrefix") String wordPrefix) {

		Map<String, List<String>> searchResultCandidates = unifSearchService.getWordsByPrefix(wordPrefix, AUTOCOMPLETE_MAX_RESULTS_LIMIT);
		return searchResultCandidates;
	}

	@GetMapping(WORD_DETAILS_URI + "/{wordId}")
	public String wordDetails(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		List<String> destinLangs = sessionBean.getDestinLangs();
		String searchMode = sessionBean.getSearchMode();
		WordData wordData = unifSearchService.getWordData(wordId, destinLangs, searchMode, DISPLAY_LANG);

		model.addAttribute("wordData", wordData);
		populateRecent(sessionBean, wordData);

		if (wordData.isLexResultsExist()) {
			return SEARCH_PAGE + " :: worddetails";
		} else {
			return SEARCH_PAGE + " :: worddetailsempty";
		}
	}

	private void populateRecent(SessionBean sessionBean, WordData wordData) {
		Word word = wordData.getWord();
		String wordValue = word.getWord();
		Integer homonymNr = word.getHomonymNr();
		sessionBean.setRecentWord(wordValue);
		sessionBean.setRecentHomonymNr(homonymNr);
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

	private SearchFilter validateSearch(
			String destinLangsStr, String searchMode, String searchWord, String homonymNrStr, String recentSearchWord, Integer recentHomonymNr) {

		boolean isValid = true;

		Integer homonymNr;

		String[] destinLangsArr = StringUtils.split(destinLangsStr, LANG_FILTER_SEPARATOR);
		List<String> destinLangs = Arrays.stream(destinLangsArr)
				.filter(destinLang -> StringUtils.equalsAny(destinLang, SUPPORTED_DESTIN_LANG_FILTERS))
				.collect(Collectors.toList());
		String destinLangsStrClean = StringUtils.join(destinLangs, LANG_FILTER_SEPARATOR);

		// lang
		if (!StringUtils.equals(destinLangsStr, destinLangsStrClean)) {
			destinLangs = Arrays.asList(DESTIN_LANG_ALL);
			isValid = isValid & false;
		} else if (CollectionUtils.isEmpty(destinLangs)) {
			destinLangs = Arrays.asList(DESTIN_LANG_ALL);
			isValid = isValid & false;
		} else if (destinLangs.contains(DESTIN_LANG_ALL) && (destinLangs.size() > 1)) {
			destinLangs = Arrays.asList(DESTIN_LANG_ALL);
			isValid = isValid & false;
		}
		destinLangsStr = StringUtils.join(destinLangs, LANG_FILTER_SEPARATOR);

		// search mode
		if (!StringUtils.equalsAny(searchMode, SEARCH_MODE_SIMPLE, SEARCH_MODE_DETAIL)) {
			searchMode = SEARCH_MODE_DETAIL;
			isValid = isValid & false;
		}

		// homonym nr
		if (StringUtils.isBlank(homonymNrStr)) {
			if (isValid && StringUtils.equals(searchWord, recentSearchWord)) {
				homonymNr = recentHomonymNr;
				isValid = isValid & true;
			} else {
				homonymNr = 1;
				isValid = isValid & false;
			}
		} else if (!StringUtils.isNumeric(homonymNrStr)) {
			homonymNr = 1;
			isValid = isValid & false;
		} else {
			homonymNr = new Integer(homonymNrStr);
			isValid = isValid & true;
		}

		String searchUri = composeSearchUri(destinLangsStr, searchMode, searchWord, homonymNr);

		SearchFilter searchFilter = new SearchFilter();
		searchFilter.setSearchWord(searchWord);
		searchFilter.setDestinLangs(destinLangs);
		searchFilter.setHomonymNr(homonymNr);
		searchFilter.setSearchMode(searchMode);
		searchFilter.setSearchUri(searchUri);
		searchFilter.setValid(isValid);

		return searchFilter;
	}

	private String composeSearchUri(String destinLangStr, String searchMode, String word, Integer homonymNr) {

		String encodedWord = UriUtils.encode(word, SystemConstant.UTF_8);
		String searchUri = SEARCH_URI + UNIF_URI + "/" + destinLangStr + "/" + searchMode + "/" + encodedWord;
		if (homonymNr != null) {
			searchUri += "/" + homonymNr;
		}
		return searchUri;
	}
}
