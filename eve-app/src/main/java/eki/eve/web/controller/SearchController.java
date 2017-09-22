package eki.eve.web.controller;

import eki.eve.service.SearchService;

import org.jooq.Record3;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Controller
public class SearchController {

	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	SearchService search;

	@GetMapping("/search")
	public String search(@RequestParam(required = false) String searchFilter, Model model) {
		logger.debug("doing search");
		if (isNotBlank(searchFilter)) {
			Result<Record3<Long, String,Integer>> words = search.findWords(searchFilter);
			model.addAttribute("searchResults", words);
			model.addAttribute("searchFilter", searchFilter);
		}
		return "search";
	}

	@GetMapping("/details/{id}")
	public String details(@PathVariable("id") Long id, Model model) {
		logger.debug("doing details");
		model.addAttribute("detailsName", id + "_details");
		model.addAttribute("forms", search.findConnectedForms(id));
		model.addAttribute("descriptions", search.findFormDefinitions(id));
		model.addAttribute("datasets", search.allDatasetsAsMap());
		return "search :: details";
	}

	private String asString(Object value) {
		return Objects.isNull(value) ? "" : String.valueOf(value);
	}
}
