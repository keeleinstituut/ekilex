package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import eki.common.constant.LayerName;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class BilingSearchController extends AbstractSynSearchController {

	private static final Logger logger = LoggerFactory.getLogger(BilingSearchController.class);

	@GetMapping(value = BILING_SEARCH_URI)
	public String initPage(Model model) {

		initPage(BILING_SEARCH_PAGE, model);
		return BILING_SEARCH_PAGE;
	}

	@PostMapping(value = BILING_SEARCH_URI)
	public String bilingSearch(
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			Model model) throws Exception {

		final SearchResultMode resultMode = SearchResultMode.WORD;
		final String resultLang = null;

		SessionBean sessionBean = getSessionBean(model);

		formDataCleanup(BILING_SEARCH_PAGE, null, detailSearchFilter, sessionBean);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}

		String roleDatasetCode = getDatasetCodeFromRole(sessionBean);
		List<String> datasetCodeList = new ArrayList<>(Collections.singletonList(roleDatasetCode));

		String searchUri = searchHelper.composeSearchUri(searchMode, datasetCodeList, simpleSearchFilter, detailSearchFilter, resultMode, resultLang);
		return "redirect:" + BILING_SEARCH_URI + searchUri;
	}

	@GetMapping(value = BILING_SEARCH_URI + "/**")
	public String bilingSearch(Model model, HttpServletRequest request) throws Exception {

		final String searchPage = BILING_SEARCH_PAGE;
		String searchUri = StringUtils.removeStart(request.getRequestURI(), BILING_SEARCH_URI);
		logger.debug(searchUri);

		initSearch(model, searchPage, searchUri);
		return searchPage;
	}

	@GetMapping(BILING_WORD_DETAILS_URI + "/{wordId}")
	public String details(
			@PathVariable("wordId") Long wordId,
			@RequestParam(required = false) Long markedSynWordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		logger.debug("Requesting details by word {}", wordId);

		String datasetCode = getDatasetCodeFromRole(sessionBean);
		Long userId = userService.getAuthenticatedUser().getId();
		EkiUserProfile userProfile = userService.getUserProfile(userId);
		List<String> candidateLangCodes = userProfile.getPreferredBilingCandidateLangs();
		List<String> meaningWordLangCodes = userProfile.getPreferredBilingLexMeaningWordLangs();

		WordSynDetails details = synSearchService.getWordSynDetails(wordId, datasetCode, LayerName.BILING_RUS, candidateLangCodes, meaningWordLangCodes);

		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);
		model.addAttribute("markedSynWordId", markedSynWordId);
		model.addAttribute("candidateLangCodes", candidateLangCodes);
		model.addAttribute("meaningWordLangCodes", meaningWordLangCodes);

		return BILING_SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@PostMapping(UPDATE_BILING_CANDIDATE_LANGS_URI)
	@ResponseBody
	public String updateCandidateLangs(@RequestParam("languages") List<String> languages) {

		userService.updateUserPreferredBilingCandidateLangs(languages);
		return RESPONSE_OK_VER1;
	}

	@PostMapping(UPDATE_BILING_MEANING_WORD_LANGS_URI)
	@ResponseBody
	public String updateMeaningWordLangs(@RequestParam("languages") List<String> languages) {

		userService.updateUserPreferredMeaningWordLangs(languages);
		return RESPONSE_OK_VER1;
	}

}
