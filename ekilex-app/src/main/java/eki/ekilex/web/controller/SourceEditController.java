package eki.ekilex.web.controller;

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

import eki.common.constant.FreeformType;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Source;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@Controller
public class SourceEditController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(ModifyController.class);

	@Autowired
	private SourceService sourceService;

	@PostMapping("/edit_source_property")
	public String editSourceProperty(@RequestParam("sourceId") Long sourceId, @RequestParam("sourcePropertyId") Long sourcePropertyId,
			@RequestParam("type") FreeformType type, @RequestParam("valueText") String valueText, @RequestParam("searchResultCount") String count,
			Model model) {

		sourceService.editSourceProperty(sourcePropertyId, type, valueText);
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);
		model.addAttribute("sourceTypes", sourceFreeformTypes);
		model.addAttribute("count", count);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_search_result";
	}

	@PostMapping("/add_source_property")
	public String addSourceProperty(@RequestParam("sourceId") Long sourceId, @RequestParam("type") FreeformType type,
			@RequestParam("valueText") String valueText, @RequestParam("searchResultCount") String count, Model model) {

		logger.debug("Adding property for source with id: {}", sourceId);

		sourceService.addSourceProperty(sourceId, type, valueText);
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);
		model.addAttribute("sourceTypes", sourceFreeformTypes);
		model.addAttribute("count", count);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_search_result";
	}

	@GetMapping("/delete_source_property/{sourceId}/{sourcePropertyId}/{count}")
	public String deleteSourceProperty(@PathVariable("sourceId") Long sourceId, @PathVariable("sourcePropertyId") Long sourcePropertyId,
			@PathVariable("count") String count, Model model) {

		logger.debug("Deleting source property with id: {}, source id: {}", sourcePropertyId, sourceId);

		sourceService.deleteSourceProperty(sourcePropertyId);
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);
		model.addAttribute("sourceTypes", sourceFreeformTypes);
		model.addAttribute("count", count);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_search_result";
	}

}
