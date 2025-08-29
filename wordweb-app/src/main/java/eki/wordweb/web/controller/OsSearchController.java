package eki.wordweb.web.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.wordweb.data.WordsMatch;
import eki.wordweb.data.os.OsSearchResult;
import eki.wordweb.service.OsSearchService;

@ConditionalOnWebApplication
@Controller
public class OsSearchController extends AbstractSearchController {

	@Autowired
	private OsSearchService osSearchService;

	@GetMapping(OS_URI)
	public String home(HttpServletRequest request, Model model) {

		model.addAttribute("searchResult", new OsSearchResult());

		return OS_HOME_PAGE;
	}

	@GetMapping(SEARCH_URI + OS_URI)
	public String search(HttpServletRequest request, Model model) {

		model.addAttribute("searchResult", new OsSearchResult());

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
			Model model) throws Exception {

		searchValue = decode(searchValue);
		searchValue = cleanupMain(searchValue);
		if (StringUtils.isBlank(searchValue)) {
			return REDIRECT_PREF + SEARCH_URI + OS_URI;
		}
		searchValue = textDecorationService.unifySymbols(searchValue);
		boolean isMaskedSearchCrit = webUtil.isMaskedSearchCrit(searchValue);

		if (isMaskedSearchCrit) {
			String cleanMaskSearchValue = cleanupMask(searchValue);
			boolean isValidMaskedSearch = isValidMaskedSearch(cleanMaskSearchValue);
			if (!isValidMaskedSearch) {
				return REDIRECT_PREF + SEARCH_URI + OS_URI;
			}
			searchValue = cleanMaskSearchValue;
			WordsMatch wordsMatch = osSearchService.getWordsWithMask(cleanMaskSearchValue);
			model.addAttribute("wordsMatch", wordsMatch);

			return OS_WORDS_PAGE;
		} else {
			Integer homonymNr = nullSafe(homonymNrStr);
			if (homonymNr == null) {
				homonymNr = 1;
				String searchUri = webUtil.composeOsSearchUri(searchValue, homonymNr);
				return REDIRECT_PREF + searchUri;
			}
			OsSearchResult searchResult = osSearchService.search(searchValue, homonymNr);
			model.addAttribute("searchResult", searchResult);

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
}
