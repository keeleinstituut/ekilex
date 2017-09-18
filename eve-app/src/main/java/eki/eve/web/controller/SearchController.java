package eki.eve.web.controller;

import eki.eve.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	@ResponseBody
	public String details(@PathVariable("id") String id) {
		logger.debug("doing details");
		return "<div name=\"" + id + "_details\">Hunnikutes põnevaid detaile vormi <span style='font-weight: bold;'>" + id + "</span> kohta</div>";
	}

}
