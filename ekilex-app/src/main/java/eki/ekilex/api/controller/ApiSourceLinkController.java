package eki.ekilex.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.constant.ReferenceOwner;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.service.SourceLinkService;

@ConditionalOnWebApplication
@RestController
public class ApiSourceLinkController extends AbstractApiController {

	@Autowired
	private SourceLinkService sourceLinkService;

	@Order(301)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, #sourceLink)")
	@PostMapping(API_SERVICES_URI + SOURCE_LINK_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody SourceLink sourceLink) {

		try {
			String sourceLinkName = sourceLink.getName();
			sourceLinkName = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(sourceLinkName);
			sourceLink.setName(sourceLinkName);
			Long sourceLinkId = sourceLinkService.createSourceLink(sourceLink, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			if (sourceLinkId == null) {
				return getOpFailResponse("Invalid or unsupported source link composition");
			}
			return getOpSuccessResponse(sourceLinkId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(302)
	@PreAuthorize("principal.apiCrud && @permEval.isSourceLinkCrudGranted(authentication, #crudRoleDataset, #sourceLinkOwner, #sourceLinkId)")
	@DeleteMapping(API_SERVICES_URI + SOURCE_LINK_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteSourceLink(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("sourceLinkOwner") ReferenceOwner sourceLinkOwner,
			@RequestParam("sourceLinkId") Long sourceLinkId) {

		try {
			sourceLinkService.deleteSourceLink(sourceLinkOwner, sourceLinkId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}
}
