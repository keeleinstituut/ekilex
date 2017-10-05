package eki.ekilex.web.controller;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.service.SearchService;

@ConditionalOnWebApplication
@Controller
public class TermSearchController {

	private static final Logger logger = LoggerFactory.getLogger(TermSearchController.class);

	@Autowired
	private SearchService search;

	@GetMapping("/termsearch")
	public String termSearch(
			@RequestParam(required = false) String searchFilter,
			Model model) {

		logger.debug("Searching by : \"{}\"", searchFilter);

		if (isNotBlank(searchFilter)) {
			List<Word> words = search.findWords(searchFilter);
			model.addAttribute("wordsFoundBySearch", words);
			model.addAttribute("searchFilter", searchFilter);
		}

		return "termsearch";
	}

	@GetMapping("/termdetails/{formId}")
	public String details(@PathVariable("formId") Long formId, Model model) {

		logger.debug("Retrieving details by form : {}", formId);
		WordDetails details = search.findWordDetails(formId);
		model.addAttribute("detailsName", formId + "_details");
		model.addAttribute("details", details);
		return "termsearch :: details";
	}

}
