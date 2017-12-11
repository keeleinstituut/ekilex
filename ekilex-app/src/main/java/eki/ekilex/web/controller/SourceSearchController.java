package eki.ekilex.web.controller;

import java.util.List;

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

import eki.common.constant.ContentKey;
import eki.ekilex.data.Source;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@Controller
public class SourceSearchController {

	private static final Logger logger = LoggerFactory.getLogger(SourceSearchController.class);

	@Autowired
	private SourceService sourceService;

	@GetMapping("/sourcesearch")
	public String sourceSearch(
			@RequestParam(required = false) String searchFilter,
			Model model, HttpSession session) {

		return doSourceSearch(searchFilter, model);
	}

	@GetMapping("/" + ContentKey.FREEFORM_REF_LINK + ":{refId}")
	public String ffRefLink(@PathVariable("refId") String refId, Model model) {

		return doSourceSearch(refId, model);
	}

	@GetMapping("/" + ContentKey.DEFINITION_REF_LINK + ":{refId}")
	public String defRefLink(@PathVariable("refId") String refId, Model model) {

		return doSourceSearch(refId, model);
	}

	private String doSourceSearch(String searchFilter, Model model) {

		logger.debug("Searching by : \"{}\"", searchFilter);

		List<Source> sources = sourceService.findSources(searchFilter);
		model.addAttribute("sourcesFoundBySearch", sources);
		
		return "sourcesearch";
	}
}
