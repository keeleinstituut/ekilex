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

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.SearchRequest;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.data.WordsMatchResult;
import eki.wordweb.service.SimpleSearchService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SimpleSearchController extends AbstractSearchController {

	@Autowired
	private SimpleSearchService simpleSearchService;

	@GetMapping(LITE_URI)
	public String home(Model model) {
		populateSearchModel("", model);
		model.addAttribute("wordsData", new WordsData());
		return LITE_HOME_PAGE;
	}

	@GetMapping(SEARCH_URI + LITE_URI)
	public String search(Model model) {
		populateSearchModel("", model);
		model.addAttribute("wordsData", new WordsData());
		return LITE_SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI + LITE_URI)
	public String searchWords(
			@RequestParam(name = "destinLangsStr") String destinLangStr,
			@RequestParam(name = "searchWord") String searchWord,
			@RequestParam(name = "selectedWordHomonymNr", required = false) String selectedWordHomonymNrStr,
			RedirectAttributes redirectAttributes) {

		searchWord = StringUtils.trim(searchWord);
		searchWord = decode(searchWord);
		if (StringUtils.isBlank(searchWord)) {
			return REDIRECT_PREF + SEARCH_URI + LITE_URI;
		}
		if (webUtil.isMaskedSearchCrit(searchWord)) {
			searchWord = cleanupMask(searchWord);
			if (StringUtils.length(searchWord) < MASKED_SEARCH_WORD_MIN_LENGTH) {
				return REDIRECT_PREF + SEARCH_URI + LITE_URI;
			}
		}
		searchWord = textDecorationService.unifyToApostrophe(searchWord);
		Integer selectedWordHomonymNr = nullSafe(selectedWordHomonymNrStr);
		String searchUri = webUtil.composeSimpleSearchUri(destinLangStr, searchWord, selectedWordHomonymNr);
		setSearchFormAttribute(redirectAttributes, Boolean.TRUE);

		return REDIRECT_PREF + searchUri;
	}

	@GetMapping({
			SEARCH_URI + LITE_URI + "/{destinLangs}/{searchWord}/{homonymNr}",
			SEARCH_URI + LITE_URI + "/{destinLangs}/{searchWord}"})
	public String searchSimpleWordsByUri(
			@PathVariable(name = "destinLangs") String destinLangsStr,
			@PathVariable(name = "searchWord") String searchWord,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes,
			Model model) throws Exception {

		boolean isSearchForm = isSearchForm(model);
		boolean sessionBeanNotPresent = sessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (sessionBeanNotPresent) {
			sessionBean = createSessionBean(model);
		} else {
			sessionBean = getSessionBean(model);
		}

		searchWord = decode(searchWord);
		SearchValidation searchValidation = validateAndCorrectSearch(destinLangsStr, searchWord, homonymNrStr);
		sessionBean.setSearchWord(searchWord);

		if (sessionBeanNotPresent) {
			//to get rid of the sessionid in the url
			return REDIRECT_PREF + searchValidation.getSearchUri();
		} else if (!searchValidation.isValid()) {
			setSearchFormAttribute(redirectAttributes, isSearchForm);
			return REDIRECT_PREF + searchValidation.getSearchUri();
		}

		List<String> destinLangs = searchValidation.getDestinLangs();
		sessionBean.setDestinLangs(destinLangs);

		if (webUtil.isMaskedSearchCrit(searchWord)) {

			WordsMatchResult wordsMatchResult = simpleSearchService.getWordsWithMask(searchValidation);
			populateSearchModel(searchWord, model);
			model.addAttribute("wordsMatchResult", wordsMatchResult);

			return LITE_WORDS_PAGE;

		} else {

			WordsData wordsData = simpleSearchService.getWords(searchValidation);
			populateSearchModel(searchWord, model);
			model.addAttribute("wordsData", wordsData);

			SearchRequest searchRequest = populateSearchRequest(request, isSearchForm, SEARCH_MODE_SIMPLE, searchValidation, wordsData);
			statDataCollector.postSearchStat(searchRequest);

			return LITE_SEARCH_PAGE;
		}
	}

	@GetMapping(value = SEARCH_WORD_FRAG_URI + LITE_URI + "/{wordFrag}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, List<String>> searchWordsByFragment(
			@PathVariable("wordFrag") String wordFragment,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		SearchFilter searchFilter = new SearchFilter(destinLangs, datasetCodes);

		return simpleSearchService.getWordsByInfixLev(wordFragment, searchFilter, AUTOCOMPLETE_MAX_RESULTS_LIMIT);
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

		model.addAttribute("wordData", wordData);
		model.addAttribute("searchMode", SEARCH_MODE_SIMPLE);
		populateRecent(sessionBean, wordData);

		return LITE_SEARCH_PAGE + " :: worddetails";
	}

	private void populateRecent(SessionBean sessionBean, WordData wordData) {
		Word word = wordData.getWord();
		String wordValue = word.getWord();
		sessionBean.setRecentWord(wordValue);
	}

	private SearchValidation validateAndCorrectSearch(String destinLangsStr, String searchWord, String homonymNrStr) {

		boolean isValid = true;

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

		// mask cleanup
		// homonym nr
		Integer homonymNr = nullSafe(homonymNrStr);
		boolean isMaskedSearchCrit = webUtil.isMaskedSearchCrit(searchWord);
		if (isMaskedSearchCrit) {
			String cleanSearchWord = cleanupMask(searchWord);
			isValid = isValid & StringUtils.equals(searchWord, cleanSearchWord);
			searchWord = cleanSearchWord;
		} else if (homonymNr == null) {
			homonymNr = 1;
			isValid = isValid & false;
		}

		destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		String searchUri = webUtil.composeSimpleSearchUri(destinLangsStr, searchWord, homonymNr);

		SearchValidation searchValidation = new SearchValidation();
		searchValidation.setDestinLangs(destinLangs);
		searchValidation.setDatasetCodes(datasetCodes);
		searchValidation.setSearchWord(searchWord);
		searchValidation.setHomonymNr(homonymNr);
		searchValidation.setSearchUri(searchUri);
		searchValidation.setValid(isValid);

		return searchValidation;
	}

	protected void populateSearchModel(String searchWord, Model model) {

		List<UiFilterElement> langFilter = commonDataService.getSimpleLangFilter();
		SessionBean sessionBean = populateCommonModel(model);
		populateLangFilter(langFilter, sessionBean, model);

		model.addAttribute("searchUri", SEARCH_URI + LITE_URI);
		model.addAttribute("searchMode", SEARCH_MODE_SIMPLE);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("wordData", new WordData());
	}
}
