package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.SynSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SynSearchController extends AbstractSearchController implements SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(SynSearchController.class);

	@Autowired
	private SynSearchService synSearchService;

	@GetMapping(value = SYN_SEARCH_URI)
	public String initSearch(Model model) throws Exception {

		initSearchForms(model);
		resetUserRole(model);

		WordsResult wordsResult = new WordsResult();
		model.addAttribute("wordsResult", wordsResult);

		return SYN_SEARCH_PAGE;
	}

	@PostMapping(value = SYN_SEARCH_URI)
	public String synSearch(
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			Model model) throws Exception {

		SessionBean sessionBean = getSessionBean(model);

		formDataCleanup(null, detailSearchFilter, null, sessionBean);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}

		List<String> roleDatasetCode = getDatasetCodeFromRole(sessionBean);

		String searchUri = searchHelper.composeSearchUri(searchMode, roleDatasetCode, simpleSearchFilter, detailSearchFilter);
		return "redirect:" + SYN_SEARCH_URI + searchUri;
	}

	@GetMapping(value = SYN_SEARCH_URI + "/**")
	public String synSearch(Model model, HttpServletRequest request) throws Exception {

		// if redirect from login arrives
		initSearchForms(model);
		resetUserRole(model);

		String searchUri = StringUtils.removeStart(request.getRequestURI(), SYN_SEARCH_URI);
		logger.debug(searchUri);

		SearchUriData searchUriData = searchHelper.parseSearchUri(searchUri);

		if (!searchUriData.isValid()) {
			initSearchForms(model);
			model.addAttribute("wordsResult", new WordsResult());
			model.addAttribute("noResults", true);
			return SYN_SEARCH_PAGE;
		}

		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean fetchAll = false;

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			wordsResult = synSearchService.getWords(detailSearchFilter, selectedDatasets, fetchAll, DEFAULT_OFFSET);
		} else {
			wordsResult = synSearchService.getWords(simpleSearchFilter, selectedDatasets, fetchAll, DEFAULT_OFFSET);
		}
		boolean noResults = wordsResult.getTotalCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("noResults", noResults);

		return SYN_SEARCH_PAGE;
	}

	@GetMapping(SYN_WORD_DETAILS_URI + "/{wordId}")
	public String details(@PathVariable("wordId") Long wordId, @RequestParam(required = false) Long markedSynWordId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		logger.debug("Requesting details by word {}", wordId);

		List<String> dataSetCode = getDatasetCodeFromRole(sessionBean);
		WordSynDetails details = synSearchService.getWordSynDetails(wordId, dataSetCode);
		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);
		model.addAttribute("markedSynWordId", markedSynWordId);

		return SYN_SEARCH_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	private List<String> getDatasetCodeFromRole(SessionBean sessionBean) {
		DatasetPermission role = sessionBean.getUserRole();
		if (role == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		return new ArrayList<>(Collections.singletonList(role.getDatasetCode()));
	}


	@PostMapping(SYN_CHANGE_RELATION_STATUS)
	@PreAuthorize("authentication.principal.datasetPermissionsExist")
	@ResponseBody
	public String changeRelationStatus(@RequestParam Long id, @RequestParam String status) {
		logger.debug("Changing syn relation status id {}, new status {}", id, status);

		synSearchService.changeRelationStatus(id, status);

		return "{}";
	}

	@PostMapping(SYN_CREATE_LEXEME + "/{meaningId}/{wordId}/{lexemeId}")
	@PreAuthorize("authentication.principal.datasetPermissionsExist")
	@ResponseBody
	public String createSynLexeme(@PathVariable Long meaningId, @PathVariable Long wordId, @PathVariable Long lexemeId, Model model) {
		logger.debug("Adding lexeme to syn candidate word Id {}, meaning Id {} , existing lexeme Id {}", wordId, meaningId, lexemeId);
		SessionBean sessionBean = getSessionBean(model);
		String datasetCode = getDatasetCodeFromRole(sessionBean).get(0);

		synSearchService.createSynLexeme(meaningId, wordId, datasetCode, lexemeId);
		return "{}";
	}


	@GetMapping(SYN_SEARCH_WORDS)
	public String searchSynWords(
			@RequestParam String searchFilter,
			@RequestParam(required = false) List<Long> excludedIds,
			@RequestParam(required = false) String language,
			@RequestParam(required = false) String morphCode,
			Model model) {
		logger.debug("word search ajax {}", searchFilter);

		List<String> selectedDatasets = getUserPreferredDatasetsCodes();
		WordsResult result = synSearchService.getWords(searchFilter, selectedDatasets, false, DEFAULT_OFFSET);

		model.addAttribute("wordsFoundBySearch", result.getWords());
		model.addAttribute("totalCount", result.getTotalCount());
		model.addAttribute("existingIds", excludedIds);

		model.addAttribute("searchedWord", searchFilter);
		model.addAttribute("selectedWordLanguage", language);
		model.addAttribute("selectedWordMorphCode", morphCode);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "syn_word_search_result";
	}

}
