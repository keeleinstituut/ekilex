package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.common.data.SearchStat;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.WordsMatch;
import eki.wordweb.data.os.OsSearchResult;
import eki.wordweb.service.OsSearchService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class OsSearchController extends AbstractSearchController {

	@Autowired
	private OsSearchService osSearchService;

	@GetMapping(OS_URI)
	public String home(HttpServletRequest request, Model model) {

		populateOsModel(new OsSearchResult(), model);

		return OS_HOME_PAGE;
	}

	@GetMapping(SEARCH_URI + OS_URI)
	public String search(HttpServletRequest request, Model model) {

		populateOsModel(new OsSearchResult(), model);

		return OS_SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI + OS_URI)
	public String searchWords(
			@RequestParam(name = "searchValue") String searchValue,
			RedirectAttributes redirectAttributes) {

		searchValue = decode(searchValue);
		searchValue = cleanupMain(searchValue);
		if (StringUtils.isBlank(searchValue)) {
			return REDIRECT_PREF + SEARCH_URI + OS_URI;
		}
		searchValue = textDecorationService.unifySymbols(searchValue);
		boolean isMaskedSearchCrit = webUtil.isMaskedSearchCrit(searchValue);

		Integer homonymNr;
		if (isMaskedSearchCrit) {
			String cleanMaskSearchValue = cleanupMask(searchValue);
			boolean isValidMaskedSearch = isValidMaskedSearch(cleanMaskSearchValue);
			if (!isValidMaskedSearch) {
				return REDIRECT_PREF + SEARCH_URI + OS_URI;
			}
			searchValue = cleanMaskSearchValue;
			homonymNr = null;
		} else {
			homonymNr = 1;
		}
		String searchUri = webUtil.composeOsSearchUri(searchValue, homonymNr);
		setSearchFormAttribute(redirectAttributes, Boolean.TRUE);

		return REDIRECT_PREF + searchUri;
	}

	@GetMapping({
			SEARCH_URI + OS_URI + "/{searchValue}",
			SEARCH_URI + OS_URI + "/{searchValue}/{homonymNr}"
	})
	public String searchOsWordsByUri(
			@PathVariable(name = "searchValue") String searchValue,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			HttpServletRequest request,
			Model model) throws Exception {

		searchValue = decode(searchValue);
		searchValue = cleanupMain(searchValue);
		if (StringUtils.isBlank(searchValue)) {
			return REDIRECT_PREF + SEARCH_URI + OS_URI;
		}
		searchValue = textDecorationService.unifySymbols(searchValue);
		boolean isMaskedSearchCrit = webUtil.isMaskedSearchCrit(searchValue);
		boolean isSearchForm = isSearchForm(model);

		SessionBean sessionBean = getOrCreateSessionBean(model);
		sessionBean.setSearchWord(searchValue);

		if (isMaskedSearchCrit) {
			String cleanMaskSearchValue = cleanupMask(searchValue);
			boolean isValidMaskedSearch = isValidMaskedSearch(cleanMaskSearchValue);
			if (!isValidMaskedSearch) {
				return REDIRECT_PREF + SEARCH_URI + OS_URI;
			}
			searchValue = cleanMaskSearchValue;
			WordsMatch wordsMatch = osSearchService.getWordsWithMask(cleanMaskSearchValue);
			model.addAttribute("wordsMatch", wordsMatch);
			populateOsModel(null, model);

			String searchUri = webUtil.composeOsSearchUri(searchValue, null);
			SearchStat searchStat = statDataUtil.composeSearchStat(request, isSearchForm, SEARCH_MODE_OS, searchValue, null, searchUri, wordsMatch);
			statDataCollector.postSearchStat(searchStat);

			return OS_WORDS_PAGE;
		} else {
			Integer homonymNr = nullSafe(homonymNrStr);
			if (homonymNr == null) {
				homonymNr = 1;
				String searchUri = webUtil.composeOsSearchUri(searchValue, homonymNr);
				return REDIRECT_PREF + searchUri;
			}
			OsSearchResult searchResult = osSearchService.search(searchValue, homonymNr);
			populateOsModel(searchResult, model);

			String searchUri = webUtil.composeOsSearchUri(searchValue, homonymNr);
			SearchStat searchStat = statDataUtil.composeSearchStat(request, isSearchForm, SEARCH_MODE_OS, searchValue, homonymNr, searchUri, searchResult);
			statDataCollector.postSearchStat(searchStat);

			return OS_SEARCH_PAGE;
		}
	}

	@GetMapping(value = SEARCH_WORD_FRAG_URI + OS_URI + "/{wordFrag}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, List<String>> searchWordsByFragment(@PathVariable("wordFrag") String wordFragment) {

		wordFragment = cleanupBasic(wordFragment);
		Map<String, List<String>> wordsMap = osSearchService.getWordsByInfixLev(wordFragment, AUTOCOMPLETE_MAX_RESULTS_LIMIT);

		return wordsMap;
	}

	private void populateOsModel(OsSearchResult searchResult, Model model) {
		if (searchResult != null) {
			model.addAttribute("searchResult", searchResult);
		}
		model.addAttribute("feedbackServiceUrl", feedbackServiceUrl);
		model.addAttribute("feedbackType", "ÕS");
	}

	private SessionBean getOrCreateSessionBean(Model model) {

		SessionBean sessionBean;
		boolean isSessionBeanNotPresent = isSessionBeanNotPresent(model);
		if (isSessionBeanNotPresent) {
			sessionBean = new SessionBean();
			sessionBean.setDestinLangs(new ArrayList<>());
			sessionBean.setDatasetCodes(new ArrayList<>());
			sessionBean.setUiSections(new ArrayList<>());
			model.addAttribute(SESSION_BEAN, sessionBean);
		} else {
			sessionBean = getSessionBean(model);
		}
		return sessionBean;
	}
}
