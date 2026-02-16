package eki.wwexam.web.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.common.constant.FeedbackType;
import eki.common.data.SearchStat;
import eki.wwexam.constant.WebConstant;
import eki.wwexam.data.WordsMatch;
import eki.wwexam.data.os.OsSearchResult;
import eki.wwexam.service.OsSearchService;
import eki.wwexam.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class OsSearchController extends AbstractSearchController {

	private static final Locale OS_LOCALE = new Locale("et");

	@Autowired
	private LocaleResolver localeResolver;

	@Autowired
	private OsSearchService osSearchService;

	@GetMapping(HOME_URI)
	public String initPage(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {

		populateOsModel(new OsSearchResult(), request, response, model);

		return OS_HOME_PAGE;
	}

	@GetMapping(SEARCH_URI)
	public String search(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {

		populateOsModel(new OsSearchResult(), request, response, model);

		return OS_SEARCH_PAGE;
	}

	@PostMapping(SEARCH_URI)
	public String searchWords(
			@RequestParam(name = "searchValue") String searchValue,
			RedirectAttributes redirectAttributes) {

		searchValue = decode(searchValue);
		searchValue = cleanupMain(searchValue);
		if (StringUtils.isBlank(searchValue)) {
			return REDIRECT_PREF + SEARCH_URI;
		}
		searchValue = textDecorationService.unifySymbols(searchValue);
		boolean isMaskedSearchCrit = webUtil.isMaskedSearchCrit(searchValue);

		Integer homonymNr;
		if (isMaskedSearchCrit) {
			String cleanMaskSearchValue = cleanupMask(searchValue);
			boolean isValidMaskedSearch = isValidMaskedSearch(cleanMaskSearchValue);
			if (!isValidMaskedSearch) {
				return REDIRECT_PREF + SEARCH_URI;
			}
			searchValue = cleanMaskSearchValue;
			homonymNr = null;
		} else {
			homonymNr = 1;
		}
		String searchUri = webUtil.composeSearchUri(searchValue, homonymNr);
		setSearchFormAttribute(redirectAttributes, Boolean.TRUE);

		return REDIRECT_PREF + searchUri;
	}

	@GetMapping({
			SEARCH_URI + "/{searchValue}",
			SEARCH_URI + "/{searchValue}/{homonymNr}"
	})
	public String searchWordsByUri(
			@PathVariable(name = "searchValue") String searchValue,
			@PathVariable(name = "homonymNr", required = false) String homonymNrStr,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws Exception {

		searchValue = decode(searchValue);
		searchValue = cleanupMain(searchValue);
		if (StringUtils.isBlank(searchValue)) {
			return REDIRECT_PREF + SEARCH_URI;
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
				return REDIRECT_PREF + SEARCH_URI;
			}
			searchValue = cleanMaskSearchValue;
			WordsMatch wordsMatch = osSearchService.getWordsWithMask(cleanMaskSearchValue);
			model.addAttribute("wordsMatch", wordsMatch);
			populateOsModel(null, request, response, model);

			String searchUri = webUtil.composeSearchUri(searchValue, null);
			SearchStat searchStat = statDataUtil.composeSearchStat(request, isSearchForm, SEARCH_MODE_EXAM, searchValue, null, searchUri, wordsMatch);
			statDataCollector.postSearchStat(searchStat);

			return OS_WORDS_PAGE;
		} else {
			Integer homonymNr = nullSafe(homonymNrStr);
			if (homonymNr == null) {
				homonymNr = 1;
				String searchUri = webUtil.composeSearchUri(searchValue, homonymNr);
				return REDIRECT_PREF + searchUri;
			}
			OsSearchResult searchResult = osSearchService.search(searchValue, homonymNr);
			populateOsModel(searchResult, request, response, model);

			String searchUri = webUtil.composeSearchUri(searchValue, homonymNr);
			SearchStat searchStat = statDataUtil.composeSearchStat(request, isSearchForm, SEARCH_MODE_EXAM, searchValue, homonymNr, searchUri, searchResult);
			statDataCollector.postSearchStat(searchStat);

			return OS_SEARCH_PAGE;
		}
	}

	@GetMapping(SEARCH_FRAG_URI + "/{wordFrag}")
	@ResponseBody
	public Map<String, List<String>> searchWordsByFragment(@PathVariable("wordFrag") String wordFragment) {

		wordFragment = cleanupBasic(wordFragment);
		Map<String, List<String>> wordsMap = osSearchService.getWordsByInfixLev(wordFragment, AUTOCOMPLETE_MAX_RESULTS_LIMIT);

		return wordsMap;
	}

	private void populateOsModel(
			OsSearchResult searchResult,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {

		localeResolver.setLocale(request, response, OS_LOCALE);

		if (searchResult != null) {
			model.addAttribute("searchResult", searchResult);
		}
		model.addAttribute("feedbackType", FeedbackType.OS);
	}

	private SessionBean getOrCreateSessionBean(Model model) {

		SessionBean sessionBean;
		boolean isSessionBeanNotPresent = isSessionBeanNotPresent(model);
		if (isSessionBeanNotPresent) {
			sessionBean = new SessionBean();
			model.addAttribute(SESSION_BEAN, sessionBean);
		} else {
			sessionBean = getSessionBean(model);
		}
		return sessionBean;
	}
}
