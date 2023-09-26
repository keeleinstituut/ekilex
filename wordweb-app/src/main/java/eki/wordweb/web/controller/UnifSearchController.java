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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.LinkedWordSearchElement;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.SearchRequest;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.data.WordsMatchResult;
import eki.wordweb.service.UnifSearchService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class UnifSearchController extends AbstractSearchController {

	@Autowired
	private UnifSearchService unifSearchService;

	@GetMapping(HOME_URI)
	public String home(Model model) {
		populateSearchModel("", model);
		model.addAttribute("wordsData", new WordsData());
		return UNIF_HOME_PAGE;
	}

	@GetMapping(SEARCH_URI + UNIF_URI)
	public String search(Model model) {
		populateSearchModel("", model);
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
		if (StringUtils.isBlank(searchWord)) {
			return REDIRECT_PREF + SEARCH_URI + UNIF_URI;
		}
		searchWord = decode(searchWord);
		searchWord = textDecorationService.unifyToApostrophe(searchWord);
		Integer selectedWordHomonymNr = nullSafe(selectedWordHomonymNrStr);
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
			RedirectAttributes redirectAttributes,
			Model model) throws Exception {

		boolean isSearchForm = isSearchForm(model);
		Long linkedLexemeId = getLinkedLexemeId(model);
		boolean sessionBeanNotPresent = sessionBeanNotPresent(model);
		SessionBean sessionBean;
		if (sessionBeanNotPresent) {
			sessionBean = createSessionBean(model);
		} else {
			sessionBean = getSessionBean(model);
		}

		searchWord = decode(searchWord);
		SearchValidation searchValidation = validateAndCorrectSearch(destinLangsStr, datasetCodesStr, searchWord, homonymNrStr);
		sessionBean.setSearchWord(searchWord);
		sessionBean.setLinkedLexemeId(linkedLexemeId);

		if (sessionBeanNotPresent) {
			//to get rid of the sessionid in the url
			return REDIRECT_PREF + searchValidation.getSearchUri();
		} else if (!searchValidation.isValid()) {
			setSearchFormAttribute(redirectAttributes, isSearchForm);
			return REDIRECT_PREF + searchValidation.getSearchUri();
		}

		List<String> destinLangs = searchValidation.getDestinLangs();
		List<String> datasetCodes = searchValidation.getDatasetCodes();
		sessionBean.setDestinLangs(destinLangs);
		sessionBean.setDatasetCodes(datasetCodes);

		if (webUtil.isMaskedSearchCrit(searchWord)) {

			WordsMatchResult wordsMatchResult = unifSearchService.getWordsWithMask(searchValidation);
			populateSearchModel(searchWord, model);
			model.addAttribute("wordsMatchResult", wordsMatchResult);

			return UNIF_WORDS_PAGE;

		} else {

			WordsData wordsData = unifSearchService.getWords(searchValidation);
			populateSearchModel(searchWord, model);
			model.addAttribute("wordsData", wordsData);

			SearchRequest searchRequest = populateSearchRequest(request, isSearchForm, SEARCH_MODE_DETAIL, searchValidation, wordsData);
			statDataCollector.postSearchStat(searchRequest);

			return UNIF_SEARCH_PAGE;
		}
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

		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		Long linkedLexemeId = sessionBean.getLinkedLexemeId();
		SearchFilter searchFilter = new SearchFilter(destinLangs, datasetCodes);
		WordData wordData = unifSearchService.getWordData(wordId, searchFilter);
		wordData.setLinkedLexemeId(linkedLexemeId);

		String wordValue = wordData.getWord().getWord();
		sessionBean.setRecentWord(wordValue);
		sessionBean.setLinkedLexemeId(null);

		String ekilexLimTermSearchUrl = webUtil.getEkilexLimTermSearchUrl();
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

	private SearchValidation validateAndCorrectSearch(String destinLangsStr, String datasetCodesStr, String searchWord, String homonymNrStr) {

		boolean isValid = true;

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

		// mask cleanup
		// homonym nr
		Integer homonymNr = nullSafe(homonymNrStr);
		boolean isMaskedSearchCrit = webUtil.isMaskedSearchCrit(searchWord);
		if (isMaskedSearchCrit) {
			searchWord = cleanupMask(searchWord);
		} else if (homonymNr == null) {
			homonymNr = 1;
			isValid = isValid & false;
		}

		destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
		String searchUri = webUtil.composeDetailSearchUri(destinLangsStr, datasetCodesStr, searchWord, homonymNr);

		SearchValidation searchValidation = new SearchValidation();
		searchValidation.setDestinLangs(destinLangs);
		searchValidation.setDatasetCodes(datasetCodes);
		searchValidation.setSearchWord(searchWord);
		searchValidation.setHomonymNr(homonymNr);
		searchValidation.setSearchUri(searchUri);
		searchValidation.setValid(isValid);

		return searchValidation;
	}

	private void populateSearchModel(String searchWord, Model model) {

		List<UiFilterElement> langFilter = commonDataService.getUnifLangFilter();
		SessionBean sessionBean = populateCommonModel(model);
		populateLangFilter(langFilter, sessionBean, model);
		populateDatasetFilter(sessionBean, model);

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
