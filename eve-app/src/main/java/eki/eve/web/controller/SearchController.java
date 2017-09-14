package eki.eve.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Controller
public class SearchController {

	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@GetMapping("/search")
	public String search(@RequestParam String searchFilter, Model model) {
		logger.debug("doing search");
		if (isNotBlank(searchFilter)) {
			model.addAttribute("searchResults", Arrays.asList(searchFilter, "ooo vorm", "aaaa vorm"));
			model.addAttribute("searchFilter", searchFilter);
		}
		return "index";
	}

}
