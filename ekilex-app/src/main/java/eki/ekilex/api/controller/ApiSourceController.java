package eki.ekilex.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@RestController
public class ApiSourceController extends AbstractApiController {

	@Autowired
	private SourceService sourceService;

	@Order(201)
	@GetMapping(API_SERVICES_URI + SOURCE_URI + SEARCH_URI + "/{searchFilter}")
	@ResponseBody
	public List<Source> sourceSearch(@PathVariable("searchFilter") String searchFilter) {

		List<Source> sources = sourceService.getSources(searchFilter);
		return sources;
	}

	@Order(202)
	@PreAuthorize("principal.apiCrud && principal.datasetCrudPermissionsExist")
	@PostMapping(API_SERVICES_URI + SOURCE_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createSource(@RequestBody Source source) {

		SourceType sourceType = source.getType();
		List<String> sourceNames = source.getSourceNames();
		List<SourceProperty> sourceProperties = source.getSourceProperties();

		List<SourceProperty> completeSourceProperties = new ArrayList<>();

		if (CollectionUtils.isEmpty(sourceNames) && CollectionUtils.isEmpty(sourceProperties)) {
			return getOpFailResponse("Source has no name nor any properties");
		} else if (CollectionUtils.isEmpty(sourceNames)) {
			boolean sourceHasNoName = sourceProperties.stream().noneMatch(sourceProperty -> FreeformType.SOURCE_NAME.equals(sourceProperty.getType()));
			if (sourceHasNoName) {
				return getOpFailResponse("Source has no name");
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
			Long sourceId = sourceService.createSource(sourceType, completeSourceProperties, null, MANUAL_EVENT_ON_UPDATE_DISABLED);
			return getOpSuccessResponse(sourceId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(203)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceCrudGranted(authentication, #crudRoleDataset, #sourceId)")
	@PostMapping(API_SERVICES_URI + SOURCE_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateSource(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceId") Long sourceId,
			@RequestParam("sourceType") SourceType sourceType) {

		try {
			sourceService.updateSource(sourceId, sourceType, crudRoleDataset);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(204)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceCrudGranted(authentication, #crudRoleDataset, #sourceId)")
	@DeleteMapping(API_SERVICES_URI + SOURCE_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteSource(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceId") Long sourceId) {

		try {
			boolean isValidForDeletion = sourceService.validateSourceDelete(sourceId);
			if (!isValidForDeletion) {
				return getOpFailResponse("Cannot delete, source in use");
			}
			sourceService.deleteSource(sourceId, crudRoleDataset);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(205)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceCrudGranted(authentication, #crudRoleDataset, #sourceId1)")
	@PostMapping(API_SERVICES_URI + SOURCE_URI + JOIN_URI)
	@ResponseBody
	public ApiResponse joinSources(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceId1") Long sourceId1,
			@RequestParam("sourceId2") Long sourceId2) {

		try {
			sourceService.joinSources(sourceId1, sourceId2, crudRoleDataset);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(206)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceCrudGranted(authentication, #crudRoleDataset, #sourceId)")
	@PostMapping(API_SERVICES_URI + SOURCE_PROPERTY_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createSourceProperty(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceId") Long sourceId,
			@RequestParam("type") FreeformType type,
			@RequestParam("valueText") String valueText) {

		valueText = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valueText);
		try {
			sourceService.createSourceProperty(sourceId, type, valueText, crudRoleDataset);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(207)
	@PreAuthorize("principal.apiCrud && @permEval.isSourcePropertyCrudGranted(authentication, #crudRoleDataset, #sourcePropertyId)")
	@PostMapping(API_SERVICES_URI + SOURCE_PROPERTY_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateSourceProperty(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourcePropertyId") Long sourcePropertyId,
			@RequestParam("valueText") String valueText) {

		valueText = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valueText);
		try {
			sourceService.updateSourceProperty(sourcePropertyId, valueText, crudRoleDataset);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(208)
	@PreAuthorize("principal.apiCrud && @permEval.isSourcePropertyCrudGranted(authentication, #crudRoleDataset, #sourcePropertyId)")
	@DeleteMapping(API_SERVICES_URI + SOURCE_PROPERTY_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteSourceProperty(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourcePropertyId") Long sourcePropertyId) {

		try {
			sourceService.deleteSourceProperty(sourcePropertyId, crudRoleDataset);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}
}
