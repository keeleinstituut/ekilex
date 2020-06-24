package eki.ekilex.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.constant.ApiConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.service.SourceService;
import eki.ekilex.service.util.MessageUtil;
import eki.ekilex.web.util.ValueUtil;

@ConditionalOnWebApplication
@RestController
public class ApiSourceController implements SystemConstant, ApiConstant {

	@Autowired
	private SourceService sourceService;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private ValueUtil valueUtil;

	@Order(201)
	@GetMapping(value = API_SERVICES_URI + SOURCE_URI + SEARCH_URI + "/{searchFilter}")
	@ResponseBody
	public List<Source> sourceSearch(@PathVariable("searchFilter") String searchFilter) {

		List<Source> sources = sourceService.getSources(searchFilter);
		return sources;
	}

	@Order(202)
	@GetMapping(value = API_SERVICES_URI + SOURCE_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createSource(@RequestBody Source source) {

		SourceType sourceType = source.getType();
		List<String> sourceNames = source.getSourceNames();
		List<SourceProperty> sourceProperties = source.getSourceProperties();

		List<SourceProperty> completeSourceProperties = new ArrayList<>();

		if (CollectionUtils.isEmpty(sourceNames) && CollectionUtils.isEmpty(sourceProperties)) {
			return new ApiResponse(false, "Source has no name nor any properties");
		} else if (CollectionUtils.isEmpty(sourceNames)) {
			boolean sourceHasNoName = sourceProperties.stream().noneMatch(sourceProperty -> FreeformType.SOURCE_NAME.equals(sourceProperty.getType()));
			if (sourceHasNoName) {
				return new ApiResponse(false, "Source has no name");
			}
		} else {
			for (String sourceName : sourceNames) {
				SourceProperty name = new SourceProperty();
				name.setType(FreeformType.SOURCE_NAME);
				name.setValueText(sourceName);
				completeSourceProperties.add(name);
			}
		}

		if (CollectionUtils.isNotEmpty(sourceProperties)) {
			completeSourceProperties.addAll(sourceProperties);
		}

		completeSourceProperties.forEach(sourceProperty -> {
			String valueText = sourceProperty.getValueText();
			valueText = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valueText);
			sourceProperty.setValueText(valueText);
		});

		try {
			Long sourceId = sourceService.createSource(sourceType, completeSourceProperties);
			String positiveQuote = messageUtil.getPositiveQuote();
			return new ApiResponse(true, positiveQuote, sourceId);
		} catch (Exception e) {
			String message = e.toString();
			return new ApiResponse(false, message);
		}
	}

	@Order(203)
	@GetMapping(value = API_SERVICES_URI + SOURCE_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateSource(
			@RequestParam("sourceId") Long sourceId,
			@RequestParam("sourceType") SourceType sourceType) {

		try {
			sourceService.updateSource(sourceId, sourceType);
			return getPositiveResponse();
		} catch (Exception e) {
			String message = e.toString();
			return new ApiResponse(false, message);
		}
	}

	@Order(204)
	@GetMapping(value = API_SERVICES_URI + SOURCE_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteSource(@RequestParam("sourceId") Long sourceId) {

		try {
			sourceService.deleteSource(sourceId);
			return getPositiveResponse();
		} catch (Exception e) {
			String message = e.toString();
			return new ApiResponse(false, message);
		}
	}

	@Order(205)
	@GetMapping(value = API_SERVICES_URI + SOURCE_URI + JOIN_URI)
	@ResponseBody
	public ApiResponse joinSources(
			@RequestParam("sourceId1") Long sourceId1,
			@RequestParam("sourceId2") Long sourceId2) {

		try {
			sourceService.joinSources(sourceId1, sourceId2);
			return getPositiveResponse();
		} catch (Exception e) {
			String message = e.toString();
			return new ApiResponse(false, message);
		}
	}

	@Order(206)
	@GetMapping(value = API_SERVICES_URI + SOURCE_PROPERTY_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createSourceProperty(
			@RequestParam("sourceId") Long sourceId,
			@RequestParam("type") FreeformType type,
			@RequestParam("valueText") String valueText) {

		valueText = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valueText);
		try {
			sourceService.createSourceProperty(sourceId, type, valueText);
			return getPositiveResponse();
		} catch (Exception e) {
			String message = e.toString();
			return new ApiResponse(false, message);
		}
	}

	@Order(207)
	@GetMapping(value = API_SERVICES_URI + SOURCE_PROPERTY_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateSourceProperty(
			@RequestParam("sourcePropertyId") Long sourcePropertyId,
			@RequestParam("valueText") String valueText) {

		valueText = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valueText);
		try {
			sourceService.updateSourceProperty(sourcePropertyId, valueText);
			return getPositiveResponse();
		} catch (Exception e) {
			String message = e.toString();
			return new ApiResponse(false, message);
		}
	}

	@Order(208)
	@GetMapping(value = API_SERVICES_URI + SOURCE_PROPERTY_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteSourceProperty(@RequestParam("sourcePropertyId") Long sourcePropertyId) {

		try {
			sourceService.deleteSourceProperty(sourcePropertyId);
			return getPositiveResponse();
		} catch (Exception e) {
			String message = e.toString();
			return new ApiResponse(false, message);
		}
	}

	private ApiResponse getPositiveResponse() {
		String positiveQuote = messageUtil.getPositiveQuote();
		return new ApiResponse(true, positiveQuote);
	}
}
