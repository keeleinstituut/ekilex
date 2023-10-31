package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import eki.common.data.SearchStat;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.AbstractSearchResult;
import eki.wordweb.data.LinkedWordSearchElement;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.UnifSearchService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class UnifSearchController extends AbstractSearchController {

	@Autowired
	private UnifSearchService unifSearchService;

	@GetMapping(HOME_URI)
	public String home(HttpServletRequest request, Model model) {

		populateSearchModel("", false, request, model);
		model.addAttribute("wordsData", new WordsData());

		return UNIF_HOME_PAGE;
	}

	@GetMapping(SEARCH_URI + UNIF_URI)
	public String search(HttpServletRequest request, Model model) {

		populateSearchModel("", false, request, model);
		model.addAttribute("wordsData", new WordsData());

		return UNIF_SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI + UNIF_URI)
	public String searchWords(
			@RequestParam(name = "searchWord") String searchWord,
			@RequestParam(name = "selectedWordHomonymNr", required = false) String selectedWordHomonymNrStr,
			@RequestParam(name = "destinLangsStr") String destinLangsStr,
			@RequestParam(name = "datasetCodesStr") String datasetCodesStr,
			@RequestParam(name = "linkedLexemeId", required = false) Long linkedLexemeId,
			RedirectAttributes redirectAttributes) {

		searchWord = StringUtils.trim(searchWord);
		searchWord = decode(searchWord);
		if (StringUtils.isBlank(searchWord)) {
			return REDIRECT_PREF + SEARCH_URI + UNIF_URI;
		}
		Integer selectedWordHomonymNr = null;
		if (webUtil.isMaskedSearchCrit(searchWord)) {
			String cleanMaskSearchWord = cleanupMask(searchWord);
			boolean isValidMaskedSearch = isValidMaskedSearch(searchWord, cleanMaskSearchWord);
			if (!isValidMaskedSearch) {
				return REDIRECT_PREF + SEARCH_URI + UNIF_URI;
			}
			searchWord = cleanMaskSearchWord;
		} else {
			selectedWordHomonymNr = nullSafe(selectedWordHomonymNrStr);
		}
		searchWord = textDecorationService.unifyToApostrophe(searchWord);
		String searchUri = webUtil.composeDetailSearchUri(destinLangsStr, datasetCodesStr, searchWord, selectedWordHomonymNr);
		setSearchFormAttribute(redirectAttributes, Boolean.TRUE);
		redirectAttributes.addFlashAttribute("linkedLexemeId", linkedLexemeId);

		return REDIRECT_PREF + searchUri;
	}

	@GetMapping({
			SEARCH_URI + UNIF_URI + "/{destinLangs}/{datasetCodes}/{searchWord}/{homonymNr}",
			SEARCH_URI + UNIF_URI + "/{destinLangs}/{datasetCodes}/{searchWord}"})
	public String searchUnifWordsByUri(
			@PathVariable(name = "destinLangs") String destinLangsStr,
			@PathVariable(name = "datasetCodes") String datasetCodesStr,
			@PathVariable(name = "searchWord") String searchWord,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			HttpServletRequest request,
			HttpServletResponse response,
			RedirectAttributes redirectAttributes,
			Model model) throws Exception {

		boolean isSessionBeanNotPresent = isSessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (isSessionBeanNotPresent) {
			sessionBean = createSessionBean(true, request, model);
		} else {
			sessionBean = getSessionBean(model);
		}

		boolean isSearchForm = isSearchForm(model);
		Long linkedLexemeId = getLinkedLexemeId(model);
		searchWord = decode(searchWord);
		boolean isMaskedSearchCrit = webUtil.isMaskedSearchCrit(searchWord);

		SearchValidation searchValidation;
		if (isMaskedSearchCrit) {
			searchValidation = validateAndCorrectMaskedSearch(destinLangsStr, datasetCodesStr, searchWord);
		} else {
			searchValidation = validateAndCorrectWordSearch(destinLangsStr, datasetCodesStr, searchWord, homonymNrStr);
		}

		sessionBean.setSearchWord(searchValidation.getSearchWord());
		sessionBean.setDestinLangs(searchValidation.getDestinLangs());
		sessionBean.setDatasetCodes(searchValidation.getDatasetCodes());
		sessionBean.setLinkedLexemeId(linkedLexemeId);

		if (isSessionBeanNotPresent) {
			//to get rid of the sessionid in the url
			return REDIRECT_PREF + searchValidation.getSearchUri();
		} else if (!searchValidation.isValid()) {
			setSearchFormAttribute(redirectAttributes, isSearchForm);
			return REDIRECT_PREF + searchValidation.getSearchUri();
		}

		String pageName;
		AbstractSearchResult searchResult;

		setSearchCookies(request, response, searchValidation);

		if (isMaskedSearchCrit) {

			searchResult = unifSearchService.getWordsWithMask(searchValidation);
			model.addAttribute("wordsMatch", searchResult);
			pageName = UNIF_WORDS_PAGE;

		} else {

			searchResult = unifSearchService.getWords(searchValidation);
			model.addAttribute("wordsData", searchResult);
			pageName = UNIF_SEARCH_PAGE;
		}

		populateSearchModel(searchWord, true, request, model);

		SearchStat searchStat = statDataUtil.composeSearchStat(request, isSearchForm, SEARCH_MODE_DETAIL, searchValidation, searchResult);
		statDataCollector.postSearchStat(searchStat);

		return pageName;
	}

	@GetMapping(value = SEARCH_WORD_FRAG_URI + UNIF_URI + "/{wordFrag}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, List<String>> searchWordsByFragment(
			@PathVariable("wordFrag") String wordFragment,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean) {

		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		SearchFilter searchFilter = new SearchFilter(destinLangs, datasetCodes);
		return unifSearchService.getWordsByInfixLev(wordFragment, searchFilter, AUTOCOMPLETE_MAX_RESULTS_LIMIT);
	}

	@GetMapping(WORD_DETAILS_URI + UNIF_URI + "/{wordId}")
	public String wordDetails(
			@PathVariable("wordId") Long wordId,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		String ekilexLimTermSearchUrl = webUtil.getEkilexLimTermSearchUrl();
		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		Long linkedLexemeId = sessionBean.getLinkedLexemeId();
		SearchFilter searchFilter = new SearchFilter(destinLangs, datasetCodes);
		WordData wordData = unifSearchService.getWordData(wordId, searchFilter);
		wordData.setLinkedLexemeId(linkedLexemeId);

		String wordValue = wordData.getWord().getWord();
		sessionBean.setRecentWord(wordValue);
		sessionBean.setLinkedLexemeId(null);

		populateUserPref(sessionBean, model);
		model.addAttribute("wordData", wordData);
		model.addAttribute("searchMode", SEARCH_MODE_DETAIL);
		model.addAttribute("ekilexLimTermSearchUrl", ekilexLimTermSearchUrl);

		return UNIF_SEARCH_PAGE + " :: worddetails";
	}

	@GetMapping(FEELING_LUCKY_URI)
	public String feelingLucky() {

		String randomWord = unifSearchService.getRandomWord();
		String searchUri = webUtil.composeDetailSearchUri(DESTIN_LANG_ALL, DATASET_ALL, randomWord, null);

		return REDIRECT_PREF + searchUri;
	}

	@GetMapping(SEARCH_LINK_URI + UNIF_URI + "/{linkType}/{linkId}")
	@ResponseBody
	public LinkedWordSearchElement getSearchUri(
			@PathVariable("linkType") String linkType,
			@PathVariable("linkId") Long linkId,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean) {

		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();

		LinkedWordSearchElement linkWord = unifSearchService.getLinkWord(linkType, linkId, destinLangs, datasetCodes);

		return linkWord;
	}

	@PostMapping(USER_PREF_URI + "/{elementName}/{elementValue}")
	@ResponseBody
	public String setUserPreference(
			@PathVariable("elementName") String elementName,
			@PathVariable("elementValue") String elementValue,
			@CookieValue(name = COOKIE_NAME_UI_SECTIONS, required = false) String uiSectionsStr,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean,
			HttpServletRequest request,
			HttpServletResponse response) {

		List<String> uiSections;
		if (StringUtils.isBlank(uiSectionsStr)) {
			uiSections = new ArrayList<>();
		} else {
			String[] uiSectionsArr = StringUtils.split(uiSectionsStr, COOKIE_VALUES_SEPARATOR);
			uiSections = new ArrayList<>(Arrays.asList(uiSectionsArr));
			deleteCookies(request, response, COOKIE_NAME_UI_SECTIONS);
		}
		if (StringUtils.equalsIgnoreCase(elementValue, "open")) {
			if (!uiSections.contains(elementName)) {
				uiSections.add(elementName);
			}
		} else if (StringUtils.equalsIgnoreCase(elementValue, "close")) {
			uiSections.remove(elementName);
		}
		sessionBean.setUiSections(uiSections);
		uiSectionsStr = StringUtils.join(uiSections, COOKIE_VALUES_SEPARATOR);

		setCookie(response, COOKIE_NAME_UI_SECTIONS, uiSectionsStr);

		return "OK";
	}

	private SearchValidation validateAndCorrectWordSearch(String destinLangsStr, String datasetCodesStr, String searchWord, String homonymNrStr) {

		SearchValidation searchValidation = new SearchValidation();
		searchValidation.setValid(true);

		// lang and dataset
		applyDestinLangAndDatasetValidations(searchValidation, destinLangsStr, datasetCodesStr);
		boolean isValid = searchValidation.isValid();

		// homonym nr
		Integer homonymNr = nullSafe(homonymNrStr);
		if (homonymNr == null) {
			homonymNr = 1;
			isValid = isValid & false;
		}

		destinLangsStr = StringUtils.join(searchValidation.getDestinLangs(), UI_FILTER_VALUES_SEPARATOR);
		datasetCodesStr = StringUtils.join(searchValidation.getDatasetCodes(), UI_FILTER_VALUES_SEPARATOR);
		String searchUri = webUtil.composeDetailSearchUri(destinLangsStr, datasetCodesStr, searchWord, homonymNr);

		searchValidation.setSearchWord(searchWord);
		searchValidation.setHomonymNr(homonymNr);
		searchValidation.setSearchUri(searchUri);
		searchValidation.setValid(isValid);

		return searchValidation;
	}

	private SearchValidation validateAndCorrectMaskedSearch(String destinLangsStr, String datasetCodesStr, String searchWord) {

		SearchValidation searchValidation = new SearchValidation();
		searchValidation.setValid(true);

		// lang and dataset
		applyDestinLangAndDatasetValidations(searchValidation, destinLangsStr, datasetCodesStr);
		boolean isValid = searchValidation.isValid();

		// mask
		String cleanMaskSearchWord = cleanupMask(searchWord);
		isValid = isValid & isValidMaskedSearch(searchWord, cleanMaskSearchWord);
		isValid = isValid & StringUtils.equals(searchWord, cleanMaskSearchWord);

		destinLangsStr = StringUtils.join(searchValidation.getDestinLangs(), UI_FILTER_VALUES_SEPARATOR);
		datasetCodesStr = StringUtils.join(searchValidation.getDatasetCodes(), UI_FILTER_VALUES_SEPARATOR);
		String searchUri = webUtil.composeDetailSearchUri(destinLangsStr, datasetCodesStr, cleanMaskSearchWord, null);

		searchValidation.setSearchWord(cleanMaskSearchWord);
		searchValidation.setSearchUri(searchUri);
		searchValidation.setValid(isValid);

		return searchValidation;
	}

	private void applyDestinLangAndDatasetValidations(SearchValidation searchValidation, String destinLangsStr, String datasetCodesStr) {

		boolean isValid = searchValidation.isValid();

		// lang
		String[] destinLangsArr = StringUtils.split(destinLangsStr, UI_FILTER_VALUES_SEPARATOR);
		List<String> destinLangs = Arrays.stream(destinLangsArr)
				.filter(destinLang -> StringUtils.equalsAny(destinLang, SUPPORTED_DETAIL_DESTIN_LANG_FILTERS))
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
				.map(datasetCode -> UriUtils.decode(datasetCode, UTF_8))
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

		searchValidation.setDestinLangs(destinLangs);
		searchValidation.setDatasetCodes(datasetCodes);
		searchValidation.setValid(isValid);
	}

	private void populateSearchModel(String searchWord, boolean isSearchFilterPresent, HttpServletRequest request, Model model) {

		List<UiFilterElement> langFilter = commonDataService.getUnifLangFilter();
		SessionBean sessionBean = populateCommonModel(isSearchFilterPresent, request, model);
		populateLangFilter(langFilter, sessionBean, model);
		populateDatasetFilter(sessionBean, model);
		populateUserPref(sessionBean, model);

		model.addAttribute("searchUri", SEARCH_URI + UNIF_URI);
		model.addAttribute("searchMode", SEARCH_MODE_DETAIL);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("wordData", new WordData());
	}

	private void populateDatasetFilter(SessionBean sessionBean, Model model) {

		List<UiFilterElement> datasetFilter = commonDataService.getDatasetFilter();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		if (CollectionUtils.isEmpty(datasetCodes)) {
			datasetCodes = new ArrayList<>();
			datasetCodes.add(DATASET_ALL);
			sessionBean.setDatasetCodes(datasetCodes);
		}
		String selectedDatasetsStr = null;
		for (UiFilterElement datasetFilterElement : datasetFilter) {
			boolean isSelected = datasetCodes.contains(datasetFilterElement.getCode());
			datasetFilterElement.setSelected(isSelected);
			if (isSelected) {
				selectedDatasetsStr = datasetFilterElement.getValue();
			}
		}
		String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
		long selectedDatasetCount = datasetFilter.stream().filter(UiFilterElement::isSelected).count();
		if (selectedDatasetCount > 1) {
			selectedDatasetsStr = String.valueOf(selectedDatasetCount);
		}
		boolean isDatasetFiltered = !StringUtils.equals(datasetCodesStr, DATASET_ALL);

		model.addAttribute("datasetFilter", datasetFilter);
		model.addAttribute("datasetCodesStr", datasetCodesStr);
		model.addAttribute("selectedDatasetsStr", selectedDatasetsStr);
		model.addAttribute("isDatasetFiltered", isDatasetFiltered);
	}

	private Long getLinkedLexemeId(Model model) {
		Long linkedLexemeId = (Long) model.asMap().get("linkedLexemeId");
		return linkedLexemeId;
	}
}
