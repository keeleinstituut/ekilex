package eki.ekilex.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Source;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SourceSearchController {

	private static final Logger logger = LoggerFactory.getLogger(SourceSearchController.class);

	@Autowired
	private SourceService sourceService;

	@GetMapping("/sourcesearch")
	public String initSearch() {

		return "sourcesearch";
	}

	@PostMapping("/sourcesearch")
	public String search(@RequestParam String searchFilter, Model model) {

		logger.debug("Searching by : \"{}\"", searchFilter);

		List<Source> sources = sourceService.findSourcesByName(searchFilter);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("sources", sources);
		model.addAttribute("sourceCount", sources.size());

		return "sourcesearch";
	}

	@GetMapping("/sourcesearchajax")
	public String sourceSearchAjax(@RequestParam String searchFilter, Model model) {

		logger.debug("Searching by : \"{}\"", searchFilter);

		List<Source> sources = sourceService.findSourcesByName(searchFilter);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("sources", sources);
		model.addAttribute("sourceCount", sources.size());

		return "common :: sourceLinkDlgContent";
	}

}
