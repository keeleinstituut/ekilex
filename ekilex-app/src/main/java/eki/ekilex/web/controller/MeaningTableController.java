package eki.ekilex.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eki.ekilex.data.EkiUser;
import eki.ekilex.data.MeaningTableRow;
import eki.ekilex.data.MeaningTableSearchResult;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.service.MeaningTableService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
public class MeaningTableController extends AbstractPrivateSearchController {

	@Autowired
	private MeaningTableService meaningTableService;

	@Autowired
	protected SearchHelper searchHelper;

	@PostMapping(TERM_MEANING_TABLE_URI)
	public String termMeaningTable(@RequestParam("searchUri") String searchUri) {

		return REDIRECT_PREF + TERM_MEANING_TABLE_URI + searchUri;
	}

	@GetMapping(TERM_MEANING_TABLE_URI + "/**")
	public String termMeaningTable(Model model, HttpServletRequest request) throws Exception {

		final String searchPage = TERM_SEARCH_PAGE;
		String searchUri = StringUtils.removeStart(request.getRequestURI(), TERM_MEANING_TABLE_URI);
		SearchUriData searchUriData = searchHelper.parseSearchUri(searchPage, searchUri);

		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		String resultLang = searchUriData.getResultLang();
		EkiUser user = userContext.getUser();

		MeaningTableSearchResult meaningTableSearchResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			meaningTableSearchResult = meaningTableService.getMeaningTableSearchResult(detailSearchFilter, selectedDatasets, resultLang, user);
		} else {
			meaningTableSearchResult = meaningTableService.getMeaningTableSearchResult(simpleSearchFilter, selectedDatasets, resultLang, user);
		}

		model.addAttribute("searchResult", meaningTableSearchResult);
		model.addAttribute("searchUri", searchUri);

		return TERM_MEANING_TABLE_PAGE;
	}

	@PostMapping(TERM_MEANING_TABLE_URI + UPDATE_MEANING_URI)
	public String updateMeaning(
			@ModelAttribute("meaning") MeaningTableRow meaning,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		EkiUser user = userContext.getUser();
		Long meaningId = meaning.getMeaningId();

		meaningTableService.updateTermMeaningTableMeaning(meaning, user, isManualEventOnUpdateEnabled);
		MeaningTableRow meaningTableRow = meaningTableService.getMeaningTableRow(meaningId, user);

		model.addAttribute("meaningTableRow", meaningTableRow);
		return TERM_MEANING_TABLE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "meaning_table_row";
	}

	@PostMapping(TERM_MEANING_TABLE_URI + UPDATE_DEFINITIONS_PUBLICITY_URI)
	public String updateDefinitionsPublicity(
			@RequestParam("definitionIds") List<Long> definitionIds,
			@RequestParam("public") boolean isPublic,
			@RequestParam("searchUri") String searchUri) {

		// TODO service and user message
		return REDIRECT_PREF + TERM_MEANING_TABLE_URI + searchUri;
	}

	@PostMapping(TERM_MEANING_TABLE_URI + UPDATE_LEXEMES_PUBLICITY_URI)
	public String updateLexemesPublicity(
			@RequestParam("lexemeIds") List<Long> lexemeIds,
			@RequestParam("public") boolean isPublic,
			@RequestParam("searchUri") String searchUri) {

		// TODO service and user message
		return REDIRECT_PREF + TERM_MEANING_TABLE_URI + searchUri;
	}

	@PostMapping(TERM_MEANING_TABLE_URI + UPDATE_USAGES_PUBLICITY_URI)
	public String updateUsagesPublicity(
			@RequestParam("usageIds") List<Long> usageIds,
			@RequestParam("public") boolean isPublic,
			@RequestParam("searchUri") String searchUri) {

		// TODO service and user message
		return REDIRECT_PREF + TERM_MEANING_TABLE_URI + searchUri;
	}
}
