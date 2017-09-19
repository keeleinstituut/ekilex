package eki.eve.web.controller;

import eki.eve.service.SearchService;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Controller
public class SearchController {

	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	SearchService search;

	@GetMapping("/search")
	public String search(@RequestParam(required = false) String searchFilter, Model model) {
		logger.debug("doing search");
		if (isNotBlank(searchFilter)) {
			model.addAttribute("searchResults", search.findForms(searchFilter));
			model.addAttribute("searchFilter", searchFilter);
		}
		return "search";
	}

	@GetMapping("/details/{id}")
	public String details(@PathVariable("id") Long id, Model model) {
		logger.debug("doing details");
		List<String> forms = (List<String>) search.findConnectedForms(id).stream().map(r -> {
			Record rec = (Record) r;
			return String.format("%s (%s-%s; %s; %s)",
					asString(rec.get("value")),
					asString(rec.get("morph_code")),
					asString(rec.get("morph_value")),
					asString(rec.get("display_form")),
					asString(rec.get("vocal_form"))); }
		).collect(toList());
		model.addAttribute("detailsName", id + "_details");
		model.addAttribute("forms", forms);
		model.addAttribute("descriptions", search.findFormDefinitions(id));
		return "search :: details";
	}

	private String asString(Object value) {
		return Objects.isNull(value) ? "" : String.valueOf(value);
	}
}
