package eki.ekilex.api.controller;

import org.apache.commons.lang3.StringUtils;
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

import eki.ekilex.data.EkiUser;
import eki.ekilex.data.MeaningForum;
import eki.ekilex.data.Response;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.MeaningRelation;
import eki.ekilex.data.api.MeaningTag;
import eki.ekilex.service.CudService;

@ConditionalOnWebApplication
@RestController
public class ApiMeaningController extends AbstractApiController {

	@Autowired
	private CudService cudService;

	@Order(801)
	@PreAuthorize("principal.apiCrud")
	@PostMapping(API_SERVICES_URI + MEANING_FORUM_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createMeaningForum(@RequestBody MeaningForum meaningForum) {

		try {
			EkiUser user = userContext.getUser();
			Long meaningId = meaningForum.getMeaningId();
			String valuePrese = meaningForum.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.createMeaningForum(meaningId, valuePrese, user);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(802)
	@PreAuthorize("principal.apiCrud && @permEval.isMeaningForumCrudGranted(authentication, #meaningForum.id)")
	@PostMapping(API_SERVICES_URI + MEANING_FORUM_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateMeaningForum(@RequestBody MeaningForum meaningForum) {

		try {
			EkiUser user = userContext.getUser();
			Long meaningForumId = meaningForum.getId();
			String valuePrese = meaningForum.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.updateMeaningForum(meaningForumId, valuePrese, user);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(803)
	@PreAuthorize("principal.apiCrud && @permEval.isMeaningForumCrudGranted(authentication, #meaningForumId)")
	@DeleteMapping(API_SERVICES_URI + MEANING_FORUM_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteMeaningForum(@RequestParam("meaningForumId") Long meaningForumId) {

		try {
			cudService.deleteMeaningForum(meaningForumId);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(804)
	@PreAuthorize("principal.apiCrud && @permEval.isMeaningCrudGranted(authentication, #crudRoleDataset, #meaningRelation.meaningId)")
	@PostMapping(API_SERVICES_URI + MEANING_RELATION_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createMeaningRelation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody MeaningRelation meaningRelation) {

		try {
			Long meaningId = meaningRelation.getMeaningId();
			Long targetMeaningId = meaningRelation.getTargetMeaningId();
			String relationTypeCode = meaningRelation.getRelationTypeCode();
			String oppositeRelationTypeCode = meaningRelation.getOppositeRelationTypeCode();
			cudService.createMeaningRelation(meaningId, targetMeaningId, relationTypeCode, oppositeRelationTypeCode, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(805)
	@PreAuthorize("principal.apiCrud && @permEval.isMeaningRelationCrudGranted(authentication, #crudRoleDataset, #relationId)")
	@DeleteMapping(API_SERVICES_URI + MEANING_RELATION_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteMeaningRelation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("relationId") Long relationId) {

		try {
			Response response = new Response();
			response = cudService.deleteMeaningRelation(relationId, response, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			String opMessage = response.getMessage();
			if (StringUtils.isBlank(opMessage)) {
				return getOpSuccessResponse();
			} else {
				return new ApiResponse(true, opMessage);
			}
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(806)
	@PreAuthorize("principal.apiCrud && @permEval.isMeaningCrudGranted(authentication, #crudRoleDataset, #meaningTag.meaningId)")
	@PostMapping(API_SERVICES_URI + MEANING_TAG_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createMeaningTag(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody MeaningTag meaningTag) {

		try {
			Long meaningId = meaningTag.getMeaningId();
			String tagName = meaningTag.getTagName();
			cudService.createMeaningTag(meaningId, tagName, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(807)
	@PreAuthorize("principal.apiCrud && @permEval.isMeaningCrudGranted(authentication, #crudRoleDataset, #meaningTag.meaningId)")
	@PostMapping(API_SERVICES_URI + MEANING_TAG_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteMeaningTag(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody MeaningTag meaningTag) {

		try {
			Long meaningId = meaningTag.getMeaningId();
			String tagName = meaningTag.getTagName();
			cudService.deleteMeaningTag(meaningId, tagName, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}
}
