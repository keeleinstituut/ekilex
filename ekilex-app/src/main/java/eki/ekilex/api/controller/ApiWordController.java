package eki.ekilex.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.EkiUser;
import eki.ekilex.data.WordForum;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.WordClassifier;
import eki.ekilex.data.api.WordFreeform;
import eki.ekilex.data.api.WordRelation;
import eki.ekilex.service.CudService;

@ConditionalOnWebApplication
@RestController
public class ApiWordController extends AbstractApiController {

	@Autowired
	private CudService cudService;

	@Order(603)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(authentication, #crudRoleDataset, #wordType.wordId)")
	@PostMapping(API_SERVICES_URI + WORD_TYPE_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordType(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordClassifier wordType,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			Long wordId = wordType.getWordId();
			String typeCode = wordType.getClassifierCode();
			cudService.createWordType(wordId, typeCode, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(604)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(authentication, #crudRoleDataset, #wordType.wordId)")
	@DeleteMapping(API_SERVICES_URI + WORD_TYPE_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteWordType(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordClassifier wordType,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			Long wordId = wordType.getWordId();
			String typeCode = wordType.getClassifierCode();
			cudService.deleteWordType(wordId, typeCode, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(605)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(authentication, #crudRoleDataset, #wordRelation.wordId)")
	@PostMapping(API_SERVICES_URI + WORD_RELATION_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordRelation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordRelation wordRelation,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			Long wordId = wordRelation.getWordId();
			Long targetWordId = wordRelation.getTargetWordId();
			String relationTypeCode = wordRelation.getRelationTypeCode();
			String oppositeRelationTypeCode = wordRelation.getOppositeRelationTypeCode();
			cudService.createWordRelation(wordId, targetWordId, relationTypeCode, oppositeRelationTypeCode, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(606)
	@PreAuthorize("principal.apiCrud && @permEval.isWordRelationCrudGranted(authentication, #crudRoleDataset, #relationId)")
	@DeleteMapping(API_SERVICES_URI + WORD_RELATION_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteWordRelation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("relationId") Long relationId,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			cudService.deleteWordRelation(relationId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(607)
	@PreAuthorize("principal.apiCrud")
	@PostMapping(API_SERVICES_URI + WORD_FORUM_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordForum(
			@RequestBody WordForum wordForum,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			EkiUser user = userContext.getUser();
			Long wordId = wordForum.getWordId();
			String valuePrese = wordForum.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.createWordForum(wordId, valuePrese, user);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(608)
	@PreAuthorize("principal.apiCrud && @permEval.isWordForumCrudGranted(authentication, #wordForum.id)")
	@PostMapping(API_SERVICES_URI + WORD_FORUM_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateWordForum(
			@RequestBody WordForum wordForum,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			EkiUser user = userContext.getUser();
			Long wordForumId = wordForum.getId();
			String valuePrese = wordForum.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.updateWordForum(wordForumId, valuePrese, user);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(609)
	@PreAuthorize("principal.apiCrud && @permEval.isWordForumCrudGranted(authentication, #wordForumId)")
	@DeleteMapping(API_SERVICES_URI + WORD_FORUM_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteWordForum(
			@RequestParam("wordForumId") Long wordForumId,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			cudService.deleteWordForum(wordForumId);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(610)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(authentication, #crudRoleDataset, #odWordRecommendation.wordId)")
	@PostMapping(API_SERVICES_URI + OD_WORD_RECOMMENDATION + CREATE_URI)
	@ResponseBody
	public ApiResponse createOdWordRecommendation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordFreeform odWordRecommendation,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			Long wordId = odWordRecommendation.getWordId();
			String valuePrese = odWordRecommendation.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.createOdWordRecommendation(wordId, valuePrese, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(611)
	@PreAuthorize("principal.apiCrud && @permEval.isFreeformCrudGranted(authentication, #crudRoleDataset, #odWordRecommendation.freeformId)")
	@PostMapping(API_SERVICES_URI + OD_WORD_RECOMMENDATION + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateOdWordRecommendation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordFreeform odWordRecommendation,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			Long freeformId = odWordRecommendation.getFreeformId();
			String valuePrese = odWordRecommendation.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.updateOdWordRecommendation(freeformId, valuePrese, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(612)
	@PreAuthorize("principal.apiCrud && @permEval.isFreeformCrudGranted(authentication, #crudRoleDataset, #odWordRecommendationId)")
	@DeleteMapping(API_SERVICES_URI + OD_WORD_RECOMMENDATION + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteOdWordRecommendation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("odWordRecommendationId") Long odWordRecommendationId,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			cudService.deleteOdWordRecommendation(odWordRecommendationId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

}
