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

	@PostMapping(EDIT_SOURCE_PROPERTY_URI)
	public String editSourceProperty(@RequestParam("sourceId") Long sourceId, @RequestParam("sourcePropertyId") Long sourcePropertyId,
			@RequestParam("type") FreeformType type, @RequestParam("valueText") String valueText, @RequestParam("searchResultCount") String count,
			Model model) {

		logger.debug("Editing source property with id: {}, source id: {}", sourcePropertyId, sourceId);

		sourceService.editSourceProperty(sourcePropertyId, type, valueText);
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);
		model.addAttribute("count", count);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + SOURCE_SEARCH_RESULT;
	}

	@PostMapping(ADD_SOURCE_PROPERTY_URI)
	public String addSourceProperty(@RequestParam("sourceId") Long sourceId, @RequestParam("type") FreeformType type,
			@RequestParam("valueText") String valueText, @RequestParam("searchResultCount") String count, Model model) {

		logger.debug("Adding property for source with id: {}", sourceId);

		sourceService.addSourceProperty(sourceId, type, valueText);
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);
		model.addAttribute("count", count);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + SOURCE_SEARCH_RESULT;
	}

	@GetMapping(DELETE_SOURCE_PROPERTY_URI + "/{sourceId}/{sourcePropertyId}/{sourcePropertyType}/{count}")
	public String deleteSourceProperty(@PathVariable("sourceId") Long sourceId, @PathVariable("sourcePropertyId") Long sourcePropertyId,
			@PathVariable("sourcePropertyType") FreeformType type, @PathVariable("count") String count, Model model) {

		logger.debug("Deleting source property with id: {}, source id: {}", sourcePropertyId, sourceId);

		sourceService.deleteSourceProperty(sourcePropertyId, type);
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);
		model.addAttribute("count", count);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + SOURCE_SEARCH_RESULT;
	}

	@PostMapping(EDIT_SOURCE_TYPE_URI)
	public String editSourceType(@RequestParam("sourceId") Long sourceId, @RequestParam("sourceType") SourceType type,
			@RequestParam("searchResultCount") String count, Model model) {

		logger.debug("Editing source type, source id: {}", sourceId);

		sourceService.editSourceType(sourceId, type);
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);
		model.addAttribute("count", count);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + SOURCE_SEARCH_RESULT;
	}

	@PostMapping(ADD_SOURCE_URI)
	@ResponseBody
	public String addSource(@RequestParam("sourceName") String sourceName, @RequestParam("sourceType") SourceType sourceType,
			@RequestParam("type") List<FreeformType> sourcePropertyTypes, @RequestParam("valueText") List<String> valueTexts) {

		logger.debug("Adding new source, source name: {}", sourceName);

		List<SourceProperty> sourceProperties = new ArrayList<>();

		SourceProperty name = new SourceProperty();
		name.setType(FreeformType.SOURCE_NAME);
		name.setValueText(sourceName);
		sourceProperties.add(name);

		if (!valueTexts.isEmpty()) {
			for (int i = 0; i < sourcePropertyTypes.size(); i++) {
				FreeformType type = sourcePropertyTypes.get(i);
				String valueText = valueTexts.get(i);
				if (!valueText.isEmpty()) {
					SourceProperty sourceProperty = new SourceProperty();
					sourceProperty.setType(type);
					sourceProperty.setValueText(valueText);
					sourceProperties.add(sourceProperty);
				}
			}
		}

		Long sourceId = sourceService.addSource(sourceType, sourceProperties);

		return String.valueOf(sourceId);
	}

	@GetMapping(VALIDATE_SOURCE_DELETE_URI + "/{sourceId}")
	@ResponseBody
	public String validateSourceDelete(@PathVariable("sourceId") Long sourceId) throws JsonProcessingException {

		logger.debug("Validating source delete, source id: {}", sourceId);

		Map<String, String> response = new HashMap<>();
		if (sourceService.validateSourceDelete(sourceId)) {
			response.put("status", "ok");
		} else {
			response.put("status", "invalid");
			response.put("message", "Allikat ei saa kustutada, sest sellele on viidatud.");
		}
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@GetMapping(DELETE_SOURCE_URI + "/{sourceId}")
	@ResponseBody
	public String deleteSource(@PathVariable("sourceId") Long sourceId) {

		logger.debug("Deleting source with id: {}", sourceId);

		sourceService.deleteSource(sourceId);
		return "ok";
	}

	@PostMapping(SOURCE_JOIN_URI)
	public String joinSources(@RequestParam("sourceId") Long sourceId, @RequestParam("previousSearch") String previousSearch, Model model) {

		Source firstSource = sourceService.getSource(sourceId);
		model.addAttribute("firstSource", firstSource);
		model.addAttribute("previousSearch", previousSearch);

		return SOURCE_JOIN_PAGE;
	}

	@PostMapping(JOIN_SOURCES_URI)
	public String joinSources(@RequestParam("firstSourceId") Long firstSourceId, @RequestParam("secondSourceId") Long secondSourceId) {

		sourceService.joinSources(firstSourceId, secondSourceId);
		return "redirect:" + SOURCE_SEARCH_URI + "/" + firstSourceId;
	}

	@PostMapping(SEARCH_SOURCES_URI)
	public String searchSources(@RequestParam("firstSourceId") Long firstSourceId, @RequestParam(name = "searchFilter", required = false) String searchFilter,
			@RequestParam("previousSearch") String previousSearch, Model model) {

		Source firstSource = sourceService.getSource(firstSourceId);
		List<Source> sources = sourceService.findSourcesToJoin(searchFilter, firstSource);
		model.addAttribute("firstSource", firstSource);
		model.addAttribute("sources", sources);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("previousSearch", previousSearch);

		return SOURCE_JOIN_PAGE;
	}

}
