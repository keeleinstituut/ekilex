package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
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
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.CorporaServiceEst;
import eki.wordweb.service.CorporaServiceRus;
import eki.wordweb.service.StatDataCollector;
import eki.wordweb.service.UnifSearchService;
import eki.wordweb.web.bean.SessionBean;
import eki.wordweb.web.util.WebUtil;

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

	@Autowired
	private WebUtil webUtil;

	@GetMapping(SEARCH_URI)
	public String home(Model model) {

		populateDefaultSearchModel(model);

		return SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI)
	public String searchWords(
			@RequestParam(name = "destinLangsStr") String destinLangStr,
			@RequestParam(name = "datasetCodesStr") String datasetCodesStr,
			@RequestParam(name = "searchMode") String searchMode,
			@RequestParam(name = "searchWord") String searchWord,
			@RequestParam(name = "selectedWordHomonymNr", required = false) String selectedWordHomonymNrStr) {

		searchWord = StringUtils.trim(searchWord);
		if (StringUtils.isBlank(searchWord)) {
			return "redirect:" + SEARCH_PAGE;
		}
		Integer selectedWordHomonymNr = nullSafe(selectedWordHomonymNrStr);
		String searchUri = webUtil.composeSearchUri(destinLangStr, datasetCodesStr, searchMode, searchWord, selectedWordHomonymNr);

		return "redirect:" + searchUri;
	}

	@GetMapping({
			SEARCH_URI + UNIF_URI + "/{destinLangs}/{datasetCodes}/{searchMode}/{searchWord}/{homonymNr}",
			SEARCH_URI + UNIF_URI + "/{destinLangs}/{datasetCodes}/{searchMode}/{searchWord}"})
	public String searchUnifWordsByUri(
			@PathVariable(name = "destinLangs") String destinLangsStr,
			@PathVariable(name = "datasetCodes") String datasetCodesStr,
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
		SearchValidation searchValidation = validateAndCorrectSearch(destinLangsStr, datasetCodesStr, searchMode, searchWord, homonymNrStr);
		sessionBean.setSearchWord(searchWord);

		if (sessionBeanNotPresent) {
			//to get rid of the sessionid in the url
			return "redirect:" + searchValidation.getSearchUri();
		} else if (!searchValidation.isValid()) {
			return "redirect:" + searchValidation.getSearchUri();
		}

		List<String> destinLangs = searchValidation.getDestinLangs();
		List<String> datasetCodes = searchValidation.getDatasetCodes();
		searchMode = searchValidation.getSearchMode();
		sessionBean.setDestinLangs(destinLangs);
		sessionBean.setDatasetCodes(datasetCodes);
		sessionBean.setSearchMode(searchMode);

		WordsData wordsData = unifSearchService.getWords(searchValidation);
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
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr) {

		Integer homonymNr = nullSafe(homonymNrStr);
		String searchUri = webUtil.composeSearchUri(DESTIN_LANG_ALL, DATASET_ALL, searchMode, searchWord, homonymNr);

		return "redirect:" + searchUri;
	}

	@GetMapping(value = "/searchwordfrag/{wordFrag}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, List<String>> searchWordsByFragment(@PathVariable("wordFrag") String wordFragment) {

		if (StringUtils.equals(AUTOCOMPLETE_ALG, AUTOCOMPLETE_BY_INFIX_LEV)) {
			return unifSearchService.getWordsByInfixLev(wordFragment, AUTOCOMPLETE_MAX_RESULTS_LIMIT);
		} else if (StringUtils.equals(AUTOCOMPLETE_ALG, AUTOCOMPLETE_BY_PREFIX)) {
			return unifSearchService.getWordsByPrefix(wordFragment, AUTOCOMPLETE_MAX_RESULTS_LIMIT);
		}
		return new HashedMap<>();
	}

	@GetMapping(WORD_DETAILS_URI + "/{wordId}")
	public String wordDetails(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		String searchMode = sessionBean.getSearchMode();
		SearchFilter searchFilter = new SearchFilter(destinLangs, datasetCodes, searchMode);
		WordData wordData = unifSearchService.getWordData(wordId, searchFilter, DISPLAY_LANG);

		model.addAttribute("wordData", wordData);
		populateRecent(sessionBean, wordData);

		return SEARCH_PAGE + " :: worddetails";
	}

	private void populateRecent(SessionBean sessionBean, WordData wordData) {
		Word word = wordData.getWord();
		String wordValue = word.getWord();
		sessionBean.setRecentWord(wordValue);
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

	private SearchValidation validateAndCorrectSearch(
			String destinLangsStr, String datasetCodesStr, String searchMode, String searchWord, String homonymNrStr) {

		boolean isValid = true;

		// lang
		String[] destinLangsArr = StringUtils.split(destinLangsStr, UI_FILTER_VALUES_SEPARATOR);
		List<String> destinLangs = Arrays.stream(destinLangsArr)
				.filter(destinLang -> StringUtils.equalsAny(destinLang, SUPPORTED_DESTIN_LANG_FILTERS))
				.collect(Collectors.toList());

		if (destinLangsArr.length != destinLangs.size()) {
			destinLangs = Arrays.asList(DESTIN_LANG_ALL);
			isValid = isValid & false;
		} else if (CollectionUtils.isEmpty(destinLangs)) {
			destinLangs = Arrays.asList(DESTIN_LANG_ALL);
			isValid = isValid & false;
		} else if (destinLangs.contains(DESTIN_LANG_ALL) && (destinLangs.size() > 1)) {
			destinLangs = Arrays.asList(DESTIN_LANG_ALL);
			isValid = isValid & false;
		}

		// dataset
		List<String> supportedDatasetCodes = commonDataService.getSupportedDatasetCodes();
		String[] datasetCodesArr = StringUtils.split(datasetCodesStr, UI_FILTER_VALUES_SEPARATOR);
		List<String> datasetCodes = Arrays.stream(datasetCodesArr)
				.map(datasetCode -> UriUtils.decode(datasetCode, SystemConstant.UTF_8))
				.filter(datasetCode -> supportedDatasetCodes.contains(datasetCode))
				.collect(Collectors.toList());

		if (datasetCodesArr.length != datasetCodes.size()) {
			datasetCodes = Arrays.asList(DATASET_ALL);
			isValid = isValid & false;
		} else if (CollectionUtils.isEmpty(datasetCodes)) {
			datasetCodes = Arrays.asList(DATASET_ALL);
			isValid = isValid & false;
		} else if (datasetCodes.contains(DATASET_ALL) && (datasetCodes.size() > 1)) {
			datasetCodes = Arrays.asList(DATASET_ALL);
			isValid = isValid & false;
		}

		// search mode
		if (!StringUtils.equalsAny(searchMode, SEARCH_MODE_SIMPLE, SEARCH_MODE_DETAIL)) {
			searchMode = SEARCH_MODE_DETAIL;
			isValid = isValid & false;
		}
		if (StringUtils.equals(searchMode, SEARCH_MODE_SIMPLE)) {
			datasetCodes = Arrays.asList(DATASET_ALL);
		}

		// homonym nr
		Integer homonymNr = nullSafe(homonymNrStr);
		if (homonymNr == null) {
			homonymNr = 1;
			isValid = isValid & false;
		}

		destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
		String searchUri = webUtil.composeSearchUri(destinLangsStr, datasetCodesStr, searchMode, searchWord, homonymNr);

		SearchValidation searchValidation = new SearchValidation();

		searchValidation.setDestinLangs(destinLangs);
		searchValidation.setDatasetCodes(datasetCodes);
		searchValidation.setSearchMode(searchMode);
		searchValidation.setSearchWord(searchWord);
		searchValidation.setHomonymNr(homonymNr);
		searchValidation.setSearchUri(searchUri);
		searchValidation.setValid(isValid);

		return searchValidation;
	}

	private Integer nullSafe(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		if (!StringUtils.isNumeric(value)) {
			return null;
		}
		return new Integer(value);
	}
}
