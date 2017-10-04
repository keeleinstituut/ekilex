package eki.ekilex.web.controller;

import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@ConditionalOnWebApplication
@Controller
public class SearchController {

	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	private SearchService search;

	@GetMapping("/search")
	public String search(
			@RequestParam(required = false) String searchFilter,
			@RequestParam(name = "dicts", required = false) List<String> selectedDatasets,
			Model model) {

		logger.debug("doing search : {}, {}", searchFilter, selectedDatasets);
		if (isNotBlank(searchFilter)) {
			List<Word> words = search.findWords(searchFilter);
			model.addAttribute("wordsFoundBySearch", words);
			model.addAttribute("searchFilter", searchFilter);
		}
		model.addAttribute("selectedDatasets", selectedDatasets == null ? emptyList() : selectedDatasets);
		// TODO: remove comments when datasets are going to be used in searches
//		model.addAttribute("datasets", search.getDatasets().entrySet());
		return "search";
	}

	@GetMapping("/details/{id}")
	public String details(@PathVariable("id") Long id, Model model) {

		logger.debug("doing details : {}", id);
		WordDetails details = search.findWordDetails(id);
		model.addAttribute("detailsName", id + "_details");
		model.addAttribute("details", details);
		return "search :: details";
	}

}
