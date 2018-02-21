package eki.ekilex.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import eki.ekilex.data.WordsResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.WordDetails;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexSearchController extends AbstractSearchController {

	private static final Logger logger = LoggerFactory.getLogger(LexSearchController.class);

	@Autowired
	private LexSearchService lexSearchService;

	@RequestMapping(value = LEX_SEARCH_URI, method = RequestMethod.GET)
	public String initSearch(Model model) {

		initSearchForms(model);

		return LEX_SEARCH_PAGE;
	}

	@RequestMapping(value = LEX_SEARCH_URI, method = RequestMethod.POST)
	public String search(
			@RequestParam(name = "selectedDatasets", required = false) List<String> selectedDatasets,
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@RequestParam(name = "fetchAll", required = false) boolean fetchAll,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		logger.debug("Searching by \"{}\" in {}", simpleSearchFilter, selectedDatasets);

		cleanup(selectedDatasets, null, simpleSearchFilter, detailSearchFilter, sessionBean, model);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}
		WordsResult result;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			result = commonDataService.findWords(detailSearchFilter, selectedDatasets, fetchAll);
		} else {
			result = commonDataService.findWords(simpleSearchFilter, selectedDatasets, fetchAll);
		}
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("wordsFoundBySearch", result.getWords());
		model.addAttribute("totalCount", result.getTotalCount());

		return LEX_SEARCH_PAGE;
	}

	@GetMapping("/lexdetails/{wordId}")
	public String details(@PathVariable("wordId") Long wordId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		logger.debug("Requesting details by word {}", wordId);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			List<Dataset> allDatasets = commonDataService.getDatasets();
			selectedDatasets = allDatasets.stream().map(dataset -> dataset.getCode()).collect(Collectors.toList());
		}
		WordDetails details = lexSearchService.getWordDetails(wordId, selectedDatasets);
		model.addAttribute("wordId", wordId);
		model.addAttribute("details", details);

		return LEX_SEARCH_PAGE + " :: details";
	}

}
