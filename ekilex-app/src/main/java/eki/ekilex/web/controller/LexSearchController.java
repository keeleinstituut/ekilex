package eki.ekilex.web.controller;

import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.LexSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@ConditionalOnWebApplication
@Controller
public class LexSearchController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(LexSearchController.class);

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private LexSearchService lexSearchService;

	@RequestMapping(LEX_SEARCH_URI)
	public String search(
			@RequestParam(required = false) String searchFilter,
			@RequestParam(name = "dicts", required = false) List<String> selectedDatasets,
			@RequestParam(required = false) String searchMode,
			@ModelAttribute(name = "detailSearch") SearchFilter detailSearch,
			Model model, HttpSession session) {

		logger.debug("Searching by \"{}\" in {}", searchFilter, selectedDatasets);

		Map<String, String> datasets = commonDataService.getDatasetNameMap();
		if (selectedDatasets == null) {
			if (session.getAttribute("datasets") == null) {
				selectedDatasets = new ArrayList<>(datasets.keySet());
			} else {
				selectedDatasets = (List<String>) session.getAttribute("datasets");
			}
		}
		model.addAttribute("datasets", datasets.entrySet());
		model.addAttribute("selectedDatasets", selectedDatasets);
		session.setAttribute("datasets", selectedDatasets);
		model.addAttribute("searchFilter", searchFilter);
		detailSearch = initialiseOrCleanUp(detailSearch);
		model.addAttribute("detailSearch", detailSearch);
		searchMode = "DETAIL".equals(searchMode) ? "DETAIL" : "SIMPLE";
		model.addAttribute("searchMode", searchMode);

		List<Word> words = emptyList();
		if ("SIMPLE".equals(searchMode)) {
			words = commonDataService.findWords(searchFilter, selectedDatasets);
		} else {
			try {
				List<SearchCriterion> criterions = detailSearch.getSearchCriteria().stream()
						.filter(c -> c.getSearchValue() != null && !c.getSearchValue().toString().isEmpty()).collect(Collectors.toList());
				if (!criterions.isEmpty()) {
					SearchFilter filter = new SearchFilter();
					filter.setSearchCriteria(criterions);
					words = commonDataService.findWords(filter, selectedDatasets);
				}
			} catch (Exception e) {
				words = emptyList();
				e.printStackTrace();
			}
		}
		model.addAttribute("wordsFoundBySearch", words);

		return LEX_SEARCH_PAGE;
	}

	private SearchFilter initialiseOrCleanUp(SearchFilter detailSearch) {
		if (detailSearch == null || detailSearch.getSearchCriteria() == null) {
			detailSearch = new SearchFilter();
			detailSearch.setSearchCriteria(new ArrayList<>());
			SearchCriterion defaultCriterion = new SearchCriterion();
			defaultCriterion.setSearchKey(SearchKey.WORD_VALUE);
			defaultCriterion.setSearchOperand(SearchKey.WORD_VALUE.getOperands()[0]);
			detailSearch.getSearchCriteria().add(defaultCriterion);
		} else {
			List<SearchCriterion> criteriaWithoutNullElements =
					detailSearch.getSearchCriteria().stream().filter(c -> c.getSearchKey() != null).collect(Collectors.toList());
			detailSearch.setSearchCriteria(criteriaWithoutNullElements);
		}
		return detailSearch;
	}

	@GetMapping("/lexdetails/{wordId}")
	public String details(@PathVariable("wordId") Long wordId, Model model, HttpSession session) {

		logger.debug("Requesting details by word {}", wordId);

		List<String> selectedDatasets = (List<String>) session.getAttribute("datasets");
		if (selectedDatasets == null) {
			Map<String, String> datasets = commonDataService.getDatasetNameMap();
			selectedDatasets = new ArrayList<>(datasets.keySet());
		}
		WordDetails details = lexSearchService.getWordDetails(wordId, selectedDatasets);
		model.addAttribute("details", details);

		return LEX_SEARCH_PAGE + " :: details";
	}

}
