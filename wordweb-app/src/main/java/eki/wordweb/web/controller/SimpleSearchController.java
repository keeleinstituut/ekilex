package eki.wordweb.web.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.common.data.SearchStat;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.AbstractSearchResult;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.SimpleSearchService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SimpleSearchController extends AbstractSearchController {

	@Autowired
	private SimpleSearchService simpleSearchService;

	@GetMapping(LITE_URI)
	public String home(HttpServletRequest request, Model model) {
		populateSearchModel("", request, model);
		model.addAttribute("wordsData", new WordsData());
		return LITE_HOME_PAGE;
	}

	@GetMapping(SEARCH_URI + LITE_URI)
	public String search(HttpServletRequest request, Model model) {
		populateSearchModel("", request, model);
		model.addAttribute("wordsData", new WordsData());
		return LITE_SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI + LITE_URI)
	public String searchWords(
			@RequestParam(name = "searchWord") String searchWord,
			@RequestParam(name = "destinLangsStr") String destinLangsStr,
			@RequestParam(name = "selectedWordHomonymNr", required = false) String selectedWordHomonymNrStr,
			@RequestParam(name = "selectedWordLang", required = false) String selectedWordLang,
			RedirectAttributes redirectAttributes) {

		searchWord = decode(searchWord);
		searchWord = cleanupMain(searchWord);
		if (StringUtils.isBlank(searchWord)) {
			return REDIRECT_PREF + SEARCH_URI + LITE_URI;
		}
		Integer selectedWordHomonymNr = null;
		if (webUtil.isMaskedSearchCrit(searchWord)) {
			String cleanMaskSearchWord = cleanupMask(searchWord);
			boolean isValidMaskedSearch = isValidMaskedSearch(searchWord, cleanMaskSearchWord);
			if (!isValidMaskedSearch) {
				return REDIRECT_PREF + SEARCH_URI + LITE_URI;
			}
			searchWord = cleanMaskSearchWord;
		} else {
			selectedWordHomonymNr = nullSafe(selectedWordHomonymNrStr);
		}
		searchWord = textDecorationService.unifyToApostrophe(searchWord);
		String searchUri = webUtil.composeSimpleSearchUri(destinLangsStr, searchWord, selectedWordHomonymNr, selectedWordLang);
		setSearchFormAttribute(redirectAttributes, Boolean.TRUE);

		return REDIRECT_PREF + searchUri;
	}

	@GetMapping({
			SEARCH_URI + LITE_URI + "/{destinLangs}/{searchWord}/{homonymNr}/{lang}",
			SEARCH_URI + LITE_URI + "/{destinLangs}/{searchWord}"})
	public String searchSimpleWordsByUri(
			@PathVariable(name = "destinLangs") String destinLangsStr,
			@PathVariable(name = "searchWord") String searchWord,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			@PathVariable(name = "lang", required = false) String lang,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes,
			Model model) throws Exception {

		String providedServletPath = request.getServletPath();
		boolean sessionBeanNotPresent = isSessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (sessionBeanNotPresent) {
			sessionBean = createSessionBean(true, request, model);
		} else {
			sessionBean = getSessionBean(model);
		}

		boolean isSearchForm = isSearchForm(model);
		searchWord = decode(searchWord);
		boolean isMaskedSearchCrit = webUtil.isMaskedSearchCrit(searchWord);

		SearchValidation searchValidation;
		if (isMaskedSearchCrit) {
			searchValidation = validateAndCorrectMaskedSearch(destinLangsStr, searchWord);
		} else {
			searchValidation = validateAndCorrectWordSearch(destinLangsStr, searchWord, homonymNrStr, lang);
		}

		sessionBean.setSearchWord(searchValidation.getSearchWord());
		sessionBean.setDestinLangs(searchValidation.getDestinLangs());
		sessionBean.setDatasetCodes(searchValidation.getDatasetCodes());

		if (!searchValidation.isValid()) {
			String validSearchUri = searchValidation.getSearchUri();
			if (StringUtils.equals(providedServletPath, validSearchUri)) {
				// unhandled condition that may cause infinite redirect
				return REDIRECT_PREF + SEARCH_URI + LITE_URI;
			} else {
				setSearchFormAttribute(redirectAttributes, isSearchForm);
				return REDIRECT_PREF + validSearchUri;
			}
		}

		String pageName;
		AbstractSearchResult searchResult;

		if (isMaskedSearchCrit) {

			searchResult = simpleSearchService.getWordsWithMask(searchValidation);
			model.addAttribute("wordsMatch", searchResult);
			pageName = LITE_WORDS_PAGE;

		} else {

			searchResult = simpleSearchService.getWords(searchValidation);
			model.addAttribute("wordsData", searchResult);
			pageName = LITE_SEARCH_PAGE;
		}

		populateSearchModel(searchWord, request, model);

		SearchStat searchStat = statDataUtil.composeSearchStat(request, isSearchForm, SEARCH_MODE_SIMPLE, searchValidation, searchResult);
		statDataCollector.postSearchStat(searchStat);

		return pageName;
	}

	@GetMapping(value = SEARCH_WORD_FRAG_URI + LITE_URI + "/{wordFrag}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, List<String>> searchWordsByFragment(
			@PathVariable("wordFrag") String wordFragment,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		wordFragment = cleanupBasic(wordFragment);
		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		SearchFilter searchFilter = new SearchFilter(destinLangs, datasetCodes);
		Map<String, List<String>> wordsMap = simpleSearchService.getWordsByInfixLev(wordFragment, searchFilter, AUTOCOMPLETE_MAX_RESULTS_LIMIT);

		return wordsMap;
	}

	@GetMapping(WORD_DETAILS_URI + LITE_URI + "/{wordId}")
	public String wordDetails(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		SearchFilter searchFilter = new SearchFilter(destinLangs, datasetCodes);
		WordData wordData = simpleSearchService.getWordData(wordId, searchFilter);

		populateUserPref(sessionBean, model);
		populateRecent(sessionBean, wordData);
		model.addAttribute("wordData", wordData);
		model.addAttribute("searchMode", SEARCH_MODE_SIMPLE);
		model.addAttribute("ekiKeeleinfoUrl", ekiKeeleinfoUrl);
		model.addAttribute("selectedLangs", destinLangs);

		return LITE_SEARCH_PAGE + " :: worddetails";
	}

	private void populateRecent(SessionBean sessionBean, WordData wordData) {
		Word word = wordData.getWord();
		String wordValue = word.getWord();
		sessionBean.setRecentWord(wordValue);
	}

	private SearchValidation validateAndCorrectWordSearch(String destinLangsStr, String searchWord, String homonymNrStr, String lang) {

		SearchValidation searchValidation = new SearchValidation();
		searchValidation.setValid(true);

		// lang and dataset
		applyDestinLangAndDatasetValidations(searchValidation, destinLangsStr);
		boolean isValid = searchValidation.isValid();

		// homonym nr
		Integer homonymNr = nullSafe(homonymNrStr);
		if (homonymNr == null) {
			homonymNr = 1;
		}

		// word lang
		if (StringUtils.length(lang) != 3) {
			lang = null;
		}

		destinLangsStr = StringUtils.join(searchValidation.getDestinLangs(), UI_FILTER_VALUES_SEPARATOR);
		String searchUri = webUtil.composeSimpleSearchUri(destinLangsStr, searchWord, homonymNr, lang);

		searchValidation.setSearchWord(searchWord);
		searchValidation.setHomonymNr(homonymNr);
		searchValidation.setLang(lang);
		searchValidation.setSearchUri(searchUri);
		searchValidation.setValid(isValid);

		return searchValidation;
	}

	private SearchValidation validateAndCorrectMaskedSearch(String destinLangsStr, String searchWord) {

		SearchValidation searchValidation = new SearchValidation();
		searchValidation.setValid(true);

		// lang and dataset
		applyDestinLangAndDatasetValidations(searchValidation, destinLangsStr);
		boolean isValid = searchValidation.isValid();

		// mask
		String cleanMaskSearchWord = cleanupMask(searchWord);
		isValid = isValid & isValidMaskedSearch(searchWord, cleanMaskSearchWord);
		isValid = isValid & StringUtils.equals(searchWord, cleanMaskSearchWord);

		destinLangsStr = StringUtils.join(searchValidation.getDestinLangs(), UI_FILTER_VALUES_SEPARATOR);
		String searchUri = webUtil.composeSimpleSearchUri(destinLangsStr, cleanMaskSearchWord, null, null);

		searchValidation.setSearchWord(cleanMaskSearchWord);
		searchValidation.setSearchUri(searchUri);
		searchValidation.setValid(isValid);

		return searchValidation;
	}

	private void applyDestinLangAndDatasetValidations(SearchValidation searchValidation, String destinLangsStr) {

		boolean isValid = searchValidation.isValid();

		// lang
		String[] destinLangsArr = StringUtils.split(destinLangsStr, UI_FILTER_VALUES_SEPARATOR);
		List<String> destinLangs = Arrays.stream(destinLangsArr)
				.filter(destinLang -> StringUtils.equalsAny(destinLang, SUPPORTED_SIMPLE_DESTIN_LANG_FILTERS))
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
		List<String> datasetCodes = Arrays.asList(DATASET_EKI);

		searchValidation.setDestinLangs(destinLangs);
		searchValidation.setDatasetCodes(datasetCodes);
		searchValidation.setValid(isValid);
	}

	private void populateSearchModel(String searchWord, HttpServletRequest request, Model model) {

		List<UiFilterElement> langFilter = commonDataService.getSimpleLangFilter();
		SessionBean sessionBean = populateCommonModel(true, request, model);
		populateLangFilter(langFilter, sessionBean, model);
		populateUserPref(sessionBean, model);
		populateLatestNewsModel(request, model);

		model.addAttribute("searchUri", SEARCH_URI + LITE_URI);
		model.addAttribute("searchMode", SEARCH_MODE_SIMPLE);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("wordData", new WordData());
	}
}
