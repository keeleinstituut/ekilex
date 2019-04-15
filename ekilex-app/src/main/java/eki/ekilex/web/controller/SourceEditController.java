package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.service.SourceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ConditionalOnWebApplication
@Controller
public class SourceEditController extends AbstractPageController {

	private static final Logger logger = LoggerFactory.getLogger(ModifyController.class);

	@Autowired
	private SourceService sourceService;

	@PostMapping("/edit_source_property")
	public String editSourceProperty(@RequestParam("sourceId") Long sourceId, @RequestParam("sourcePropertyId") Long sourcePropertyId,
			@RequestParam("valueText") String valueText, @RequestParam("searchResultCount") String count, Model model) {

		logger.debug("Editing source property with id: {}, source id: {}", sourcePropertyId, sourceId);

		sourceService.editSourceProperty(sourcePropertyId, valueText);
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);
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
		model.addAttribute("count", count);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_search_result";
	}

	@PostMapping("/edit_source_type")
	public String editSourceType(@RequestParam("sourceId") Long sourceId, @RequestParam("sourceType") SourceType type,
			@RequestParam("searchResultCount") String count, Model model) {

		logger.debug("Editing source type, source id: {}", sourceId);

		sourceService.editSourceType(sourceId, type);
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);
		model.addAttribute("count", count);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_search_result";
	}

	@PostMapping("/add_source")
	@ResponseBody
	public String addSource(@RequestParam("sourceName") String sourceName, @RequestParam("sourceType") SourceType sourceType,
			@RequestParam("type") List<FreeformType> sourcePropertyTypes, @RequestParam("valueText") List<String> valueTexts) {

		logger.debug("Adding new source, source name: {}", sourceName);

		List<SourceProperty> sourceProperties = new ArrayList<>();

		SourceProperty name = new SourceProperty();
		name.setType(FreeformType.SOURCE_NAME);
		name.setValueText(sourceName);
		sourceProperties.add(name);

		for (int i = 0; i < sourcePropertyTypes.size(); i++) {
			FreeformType type = sourcePropertyTypes.get(i);
			String valueText = valueTexts.get(i);
			SourceProperty sourceProperty = new SourceProperty();
			sourceProperty.setType(type);
			sourceProperty.setValueText(valueText);
			sourceProperties.add(sourceProperty);
		}

		String extSourceIdNa = "n/a";
		Long sourceId = sourceService.addSource(sourceType, extSourceIdNa, sourceProperties);

		return String.valueOf(sourceId);
	}

	@GetMapping("validate_source_delete/{sourceId}")
	@ResponseBody
	public String validateSourceDelete(@PathVariable("sourceId") Long sourceId) throws JsonProcessingException {

		logger.debug("Validating source delete, source id: {}", sourceId);

		Map<String, String> response = new HashMap<>();
		if (sourceService.isSourceDeletePossible(sourceId)) {
			response.put("status", "ok");
		} else {
			response.put("status", "invalid");
			response.put("message", "Allikat ei saa kustutada, sest sellele on viidatud.");
		}
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@GetMapping("delete_source/{sourceId}")
	@ResponseBody
	public String deleteSource(@PathVariable("sourceId") Long sourceId) {

		logger.debug("Deleting source with id: {}", sourceId);

		sourceService.deleteSource(sourceId);
		return "ok";
	}

}
