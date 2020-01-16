package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.ekilex.data.Source;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@Controller
public class SourceSearchController extends AbstractPageController {

	private static final Logger logger = LoggerFactory.getLogger(SourceSearchController.class);

	private static final int AUTOCOMPLETE_MAX_RESULTS_LIMIT = 15;

	@Autowired
	private SourceService sourceService;

	@GetMapping(SOURCE_SEARCH_URI)
	public String initSearch() {

		return SOURCE_SEARCH_PAGE;
	}

	@PostMapping(SOURCE_SEARCH_URI)
	public String search(@RequestParam String searchFilter, Model model) {

		logger.debug("Searching by : \"{}\"", searchFilter);

		List<Source> sources = sourceService.getSources(searchFilter);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("sources", sources);
		model.addAttribute("sourceCount", sources.size());

		return SOURCE_SEARCH_PAGE;
	}

	@GetMapping(SEARCH_SOURCES_URI)
	public String sourceSearch(@RequestParam String searchFilter, Model model) {

		logger.debug("Searching by : \"{}\"", searchFilter);

		List<Source> sources = sourceService.getSources(searchFilter);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("sources", sources);
		model.addAttribute("sourceCount", sources.size());

		return COMMON_PAGE + PAGE_FRAGMENT_ELEM + "source_link_dlg";
	}

	@GetMapping(SOURCE_SEARCH_URI + "/{sourceId}")
	public String searchById(@PathVariable("sourceId") Long sourceId, Model model) {

		logger.debug("Searching by id: \"{}\"", sourceId);

		List<Source> sources = new ArrayList<>();
		Source source = sourceService.getSource(sourceId);
		sources.add(source);

		model.addAttribute("sources", sources);
		model.addAttribute("sourceCount", sources.size());

		return SOURCE_SEARCH_PAGE;
	}

	@GetMapping(SOURCE_NAME_SEARCH_URI + "/{nameSearchFilter}")
	@ResponseBody
	public List<String> sourceNameSearch(@PathVariable("nameSearchFilter") String nameSearchFilter) {

		List<String> sourceNames = sourceService.getSourceNames(nameSearchFilter, AUTOCOMPLETE_MAX_RESULTS_LIMIT);
		return sourceNames;
	}

}
