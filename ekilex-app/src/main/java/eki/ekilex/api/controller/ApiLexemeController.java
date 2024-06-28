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

import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.LexemeClassifier;
import eki.ekilex.data.api.LexemeTag;
import eki.ekilex.service.CudService;

@ConditionalOnWebApplication
@RestController
public class ApiLexemeController extends AbstractApiController {

	@Autowired
	private CudService cudService;

	@Order(901)
	@PreAuthorize("principal.apiCrud && @permEval.isLexemeCrudGranted(authentication, #crudRoleDataset, #lexemePos.lexemeId)")
	@PostMapping(API_SERVICES_URI + LEXEME_POS_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createLexemePos(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody LexemeClassifier lexemePos) {

		try {
			Long lexemeId = lexemePos.getLexemeId();
			String posCode = lexemePos.getClassifierCode();
			cudService.createLexemePos(lexemeId, posCode, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(902)
	@PreAuthorize("principal.apiCrud && @permEval.isLexemeCrudGranted(authentication, #crudRoleDataset, #lexemePos.lexemeId)")
	@DeleteMapping(API_SERVICES_URI + LEXEME_POS_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteLexemePos(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody LexemeClassifier lexemePos) {

		try {
			Long lexemeId = lexemePos.getLexemeId();
			String posCode = lexemePos.getClassifierCode();
			cudService.deleteLexemePos(lexemeId, posCode, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(903)
	@PreAuthorize("principal.apiCrud && @permEval.isLexemeCrudGranted(authentication, #crudRoleDataset, #lexemeTag.lexemeId)")
	@PostMapping(API_SERVICES_URI + LEXEME_TAG_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createLexemeTag(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody LexemeTag lexemeTag) {

		try {
			Long lexemeId = lexemeTag.getLexemeId();
			String tagName = lexemeTag.getTagName();
			cudService.createLexemeTag(lexemeId, tagName, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(904)
	@PreAuthorize("principal.apiCrud && @permEval.isLexemeCrudGranted(authentication, #crudRoleDataset, #lexemeTag.lexemeId)")
	@PostMapping(API_SERVICES_URI + LEXEME_TAG_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteLexemeTag(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody LexemeTag lexemeTag) {

		try {
			Long lexemeId = lexemeTag.getLexemeId();
			String tagName = lexemeTag.getTagName();
			cudService.deleteLexemeTag(lexemeId, tagName, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}


	@Order(920)
	@PreAuthorize("principal.apiCrud && @permEval.isLexemeCrudGranted(authentication, #crudRoleDataset, #lexemeId)")
	@DeleteMapping(API_SERVICES_URI + LEXEME_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteLexeme(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("lexemeId") Long lexemeId) {

		try {
			cudService.deleteLexeme(lexemeId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}
}
