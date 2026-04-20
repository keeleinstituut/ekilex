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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import eki.wordweb.data.WordSearchResult;
import eki.wordweb.service.UnifSearchService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class UnifSearchController extends AbstractMainSearchController {

	private static Logger logger = LoggerFactory.getLogger(UnifSearchController.class);

	@Autowired
	private UnifSearchService unifSearchService;

	@GetMapping(HOME_URI)
	public String home(HttpServletRequest request, Model model) {

		populateSearchModel("", false, request, model);
		model.addAttribute("searchResult", new WordSearchResult());

		return UNIF_HOME_PAGE;
	}

	@GetMapping(SEARCH_URI + UNIF_URI)
	public String search(HttpServletRequest request, Model model) {

		populateSearchModel("", false, request, model);
		model.addAttribute("searchResult", new WordSearchResult());

		return UNIF_SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI + UNIF_URI)
	public String searchWords(
			@RequestParam(name = "searchWordValue") String searchWordValue,
			@RequestParam(name = "selectedWordHomonymNr", required = false) String selectedWordHomonymNrStr,
			@RequestParam(name = "selectedWordLang", required = false) String selectedWordLang,
			@RequestParam(name = "destinLangsStr") String destinLangsStr,
			@RequestParam(name = "datasetCodesStr") String datasetCodesStr,
			@RequestParam(name = "linkedLexemeId", required = false) Long linkedLexemeId,
			RedirectAttributes redirectAttributes) {

		searchWordValue = decode(searchWordValue);
		searchWordValue = cleanupMain(searchWordValue);
		if (StringUtils.isBlank(searchWordValue)) {
			return REDIRECT_PREF + SEARCH_URI + UNIF_URI;
		}
		Integer selectedWordHomonymNr = null;
		if (webUtil.isMaskedSearchCrit(searchWordValue)) {
			String cleanMaskSearchWord = cleanupMask(searchWordValue);
			boolean isValidMaskedSearch = isValidMaskedSearch(cleanMaskSearchWord);
			if (!isValidMaskedSearch) {
				return REDIRECT_PREF + SEARCH_URI + UNIF_URI;
			}
			searchWordValue = cleanMaskSearchWord;
		} else {
			selectedWordHomonymNr = nullSafe(selectedWordHomonymNrStr);
		}
		String searchUri = webUtil.composeAndEncodeDetailSearchUri(destinLangsStr, datasetCodesStr, searchWordValue, selectedWordHomonymNr, selectedWordLang);
		setSearchFormAttribute(redirectAttributes, Boolean.TRUE);
		redirectAttributes.addFlashAttribute("linkedLexemeId", linkedLexemeId);

		return REDIRECT_PREF + searchUri;
	}

	@GetMapping({
			SEARCH_URI + UNIF_URI + "/{destinLangs}/{datasetCodes}/{searchWordValue}/{homonymNr}/{lang}",
			SEARCH_URI + UNIF_URI + "/{destinLangs}/{datasetCodes}/{searchWordValue}"})
	public String searchUnifWordsByUri(
			@PathVariable(name = "destinLangs") String destinLangsStr,
			@PathVariable(name = "datasetCodes") String datasetCodesStr,
			@PathVariable(name = "searchWordValue") String searchWordValue,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			@PathVariable(name = "lang", required = false) String lang,
			HttpServletRequest request,
			HttpServletResponse response,
			RedirectAttributes redirectAttributes,
			Model model) throws Exception {

		String providedServletPath = request.getServletPath();
		boolean isSessionBeanNotPresent = isSessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (isSessionBeanNotPresent) {
			sessionBean = createSessionBean(true, request, model);
		} else {
			sessionBean = getSessionBean(model);
		}

		boolean isSearchForm = isSearchForm(model);
		Long linkedLexemeId = getLinkedLexemeId(model);
		searchWordValue = decode(searchWordValue);
		boolean isMaskedSearchCrit = webUtil.isMaskedSearchCrit(searchWordValue);

		SearchValidation searchValidation;
		if (isMaskedSearchCrit) {
			searchValidation = validateAndCorrectMaskedSearch(destinLangsStr, datasetCodesStr, searchWordValue);
		} else {
			searchValidation = validateAndCorrectWordSearch(destinLangsStr, datasetCodesStr, searchWordValue, homonymNrStr, lang);
		}

		sessionBean.setSearchWordValue(searchValidation.getSearchWordValue());
		sessionBean.setDestinLangs(searchValidation.getDestinLangs());
		sessionBean.setDatasetCodes(searchValidation.getDatasetCodes());
		sessionBean.setLinkedLexemeId(linkedLexemeId);

		if (!searchValidation.isValid()) {
			String validSearchUri = searchValidation.getSearchUri();
			if (StringUtils.equals(providedServletPath, validSearchUri)) {
				// unhandled condition that may cause infinite redirect
				logger.warn("Invalid request that isn't properly resolved: \"{}\"", providedServletPath);
				return REDIRECT_PREF + SEARCH_URI + UNIF_URI;
			} else {
				setSearchFormAttribute(redirectAttributes, isSearchForm);
				return REDIRECT_PREF + validSearchUri;
			}
		}

		String pageName;
		AbstractSearchResult searchResult;

		setSearchCookies(request, response, searchValidation);

		if (isMaskedSearchCrit) {

			searchResult = unifSearchService.getWordsWithMask(searchValidation);
			model.addAttribute("searchResult", searchResult);
			pageName = UNIF_WORDS_PAGE;

		} else {

			searchResult = unifSearchService.getWords(searchValidation);
			model.addAttribute("searchResult", searchResult);
			pageName = UNIF_SEARCH_PAGE;
		}

		populateSearchModel(searchWordValue, true, request, model);

		SearchStat searchStat = statDataUtil.composeSearchStat(request, isSearchForm, SEARCH_MODE_DETAIL, searchValidation, searchResult);
		statDataCollector.postSearchStat(searchStat);

		return pageName;
	}

	@GetMapping(value = SEARCH_WORD_FRAG_URI + UNIF_URI + "/{wordFrag}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, List<String>> searchWordsByFragment(
			@PathVariable("wordFrag") String wordFragment,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean) {

		wordFragment = cleanupBasic(wordFragment);
		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		SearchFilter searchFilter = new SearchFilter(destinLangs, datasetCodes);
		Map<String, List<String>> wordsMap = unifSearchService.getWordsByInfixLev(wordFragment, searchFilter, AUTOCOMPLETE_MAX_RESULTS_LIMIT);

		return wordsMap;
	}

	@GetMapping(FEELING_LUCKY_URI)
	public String feelingLucky() {

		String randomWord = unifSearchService.getRandomWord();
		String searchUri = webUtil.composeAndEncodeDetailSearchUri(DESTIN_LANG_ALL, DATASET_ALL, randomWord, null, null);

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

	private SearchValidation validateAndCorrectWordSearch(String destinLangsStr, String datasetCodesStr, String searchWordValue, String homonymNrStr, String lang) {

		SearchValidation searchValidation = new SearchValidation();
		searchValidation.setValid(true);

		// lang and dataset filter
		applyDestinLangAndDatasetValidations(searchValidation, destinLangsStr, datasetCodesStr);
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
		datasetCodesStr = StringUtils.join(searchValidation.getDatasetCodes(), UI_FILTER_VALUES_SEPARATOR);
		String searchUri = webUtil.composeAndEncodeDetailSearchUri(destinLangsStr, datasetCodesStr, searchWordValue, homonymNr, lang);

		searchValidation.setSearchWordValue(searchWordValue);
		searchValidation.setHomonymNr(homonymNr);
		searchValidation.setLang(lang);
		searchValidation.setSearchUri(searchUri);
		searchValidation.setValid(isValid);

		return searchValidation;
	}

	private SearchValidation validateAndCorrectMaskedSearch(String destinLangsStr, String datasetCodesStr, String searchWordValue) {

		SearchValidation searchValidation = new SearchValidation();
		searchValidation.setValid(true);

		// lang and dataset
		applyDestinLangAndDatasetValidations(searchValidation, destinLangsStr, datasetCodesStr);
		boolean isValid = searchValidation.isValid();

		// mask
		String cleanMaskSearchWord = cleanupMask(searchWordValue);
		isValid = isValid & isValidMaskedSearch(cleanMaskSearchWord);
		isValid = isValid & StringUtils.equals(searchWordValue, cleanMaskSearchWord);

		destinLangsStr = StringUtils.join(searchValidation.getDestinLangs(), UI_FILTER_VALUES_SEPARATOR);
		datasetCodesStr = StringUtils.join(searchValidation.getDatasetCodes(), UI_FILTER_VALUES_SEPARATOR);
		String searchUri = webUtil.composeAndEncodeDetailSearchUri(destinLangsStr, datasetCodesStr, cleanMaskSearchWord, null, null);

		searchValidation.setSearchWordValue(cleanMaskSearchWord);
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

	private void populateSearchModel(String searchWordValue, boolean isSearchFilterPresent, HttpServletRequest request, Model model) {

		List<UiFilterElement> langFilter = commonDataService.getUnifLangFilter();
		SessionBean sessionBean = populateCommonModel(isSearchFilterPresent, request, model);
		populateLangFilter(langFilter, sessionBean, model);
		populateDatasetFilter(sessionBean, model);
		populateUserPref(sessionBean, model);
		populateLatestNewsModel(request, model);

		model.addAttribute("searchUri", SEARCH_URI + UNIF_URI);
		model.addAttribute("searchMode", SEARCH_MODE_DETAIL);
		model.addAttribute("searchWordValue", searchWordValue);
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
