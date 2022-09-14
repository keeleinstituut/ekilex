package eki.ekilex.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eki.ekilex.data.MeaningTableSearchResult;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.service.MeaningTableService;
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

		MeaningTableSearchResult meaningTableSearchResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			meaningTableSearchResult = meaningTableService.getMeaningTableSearchResult(detailSearchFilter, selectedDatasets, resultLang);
		} else {
			meaningTableSearchResult = meaningTableService.getMeaningTableSearchResult(simpleSearchFilter, selectedDatasets, resultLang);
		}

		model.addAttribute("searchResult", meaningTableSearchResult);

		return TERM_MEANING_TABLE_PAGE;
	}
}
