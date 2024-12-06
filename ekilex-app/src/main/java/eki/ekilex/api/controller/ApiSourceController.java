package eki.ekilex.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceSearchResult;
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
	public SourceSearchResult sourceSearch(
			@PathVariable("searchFilter") String searchFilter,
			Authentication authentication,
			HttpServletRequest request) {

		EkiUser user = userContext.getUser();
		SourceSearchResult sourceSearchResult = sourceService.getSourceSearchResult(searchFilter, user, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);
		addRequestStat(authentication, request);
		return sourceSearchResult;
	}

	@Order(202)
	@GetMapping(API_SERVICES_URI + SOURCE_URI + DETAILS_URI + "/{sourceId}")
	@ResponseBody
	public Source getSource(
			@PathVariable("sourceId") Long sourceId,
			Authentication authentication,
			HttpServletRequest request) {

		Source source = sourceService.getSource(sourceId);
		addRequestStat(authentication, request);
		return source;
	}

	@Order(203)
	@PreAuthorize("principal.apiCrud && @permEval.isDatasetCrudGranted(authentication, #crudRoleDataset, #source.datasetCode)")
	@PostMapping(API_SERVICES_URI + SOURCE_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createSource(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody Source source,
			Authentication authentication,
			HttpServletRequest request) {

		if (StringUtils.isBlank(source.getName())) {
			return getOpFailResponse(authentication, request, "Source has no name");
		}

		cleanupSource(source);

		try {
			Long sourceId = sourceService.createSource(source, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_DISABLED);
			return getOpSuccessResponse(authentication, request, "SOURCE", sourceId);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(204)
	@PreAuthorize("principal.apiCrud "
			+ "&& @permEval.isDatasetCrudGranted(authentication, #crudRoleDataset, #source.datasetCode) "
			+ "&& @permEval.isSourceCrudGranted(authentication, #crudRoleDataset, #source.id)")
	@PostMapping(API_SERVICES_URI + SOURCE_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateSource(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody Source source,
			Authentication authentication,
			HttpServletRequest request) {

		cleanupSource(source);

		try {
			sourceService.updateSource(source, crudRoleDataset);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	private void cleanupSource(Source source) {

		String valuePrese = source.getValuePrese();
		valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
		source.setValuePrese(valuePrese);
	}

	@Order(205)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceCrudGranted(authentication, #crudRoleDataset, #sourceId)")
	@DeleteMapping(API_SERVICES_URI + SOURCE_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteSource(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceId") Long sourceId,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			boolean isValidForDeletion = sourceService.validateSourceDelete(sourceId);
			if (!isValidForDeletion) {
				return getOpFailResponse(authentication, request, "Cannot delete, source in use");
			}
			sourceService.deleteSource(sourceId, crudRoleDataset);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(206)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceCrudGranted(authentication, #crudRoleDataset, #sourceId1)")
	@PostMapping(API_SERVICES_URI + SOURCE_URI + JOIN_URI)
	@ResponseBody
	public ApiResponse joinSources(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceId1") Long sourceId1,
			@RequestParam("sourceId2") Long sourceId2,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			sourceService.joinSources(sourceId1, sourceId2, crudRoleDataset);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}
}
