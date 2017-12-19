package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import eki.ekilex.data.TermDetails;
import eki.ekilex.data.Word;
import eki.ekilex.service.SearchService;
import eki.ekilex.service.TermSearchService;

@ConditionalOnWebApplication
@Controller
public class TermSearchController {

	private static final Logger logger = LoggerFactory.getLogger(TermSearchController.class);

	@Autowired
	private SearchService searchService;

	@Autowired
	private TermSearchService termSearchService;

	@GetMapping("/termsearch")
	public String termSearch(
			@RequestParam(required = false) String searchFilter,
			@RequestParam(name = "dicts", required = false) List<String> selectedDatasets,
			Model model, HttpSession session) {

		logger.debug("Searching by \"{}\" in {}", searchFilter, selectedDatasets);

		Map<String, String> datasetNameMap = searchService.getDatasetNameMap();
		if (selectedDatasets == null) {
			if (session.getAttribute("datasets") == null) {
				selectedDatasets = new ArrayList<>(datasetNameMap.keySet());
			} else {
				selectedDatasets = (List<String>) session.getAttribute("datasets");
			}
		}
		session.setAttribute("datasets", selectedDatasets);

		List<Word> words = searchService.findWordsInDatasets(searchFilter, selectedDatasets);

		model.addAttribute("datasets", datasetNameMap.entrySet());
		model.addAttribute("selectedDatasets", selectedDatasets);
		model.addAttribute("wordsFoundBySearch", words);
		model.addAttribute("searchFilter", searchFilter);

		return "termsearch";
	}

	@GetMapping("/termdetails/{formId}")
	public String details(@PathVariable("formId") Long formId, Model model, HttpSession session) {

		logger.debug("Retrieving details by form {}", formId);

		List<String> selectedDatasets = (List<String>) session.getAttribute("datasets");
		if (selectedDatasets == null) {
			Map<String, String> datasets = searchService.getDatasetNameMap();
			selectedDatasets = new ArrayList<>(datasets.keySet());
		}
		TermDetails details = termSearchService.findWordDetailsInDatasets(formId, selectedDatasets);

		model.addAttribute("detailsName", formId + "_details");
		model.addAttribute("details", details);

		return "termsearch :: details";
	}
}
