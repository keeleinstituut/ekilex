package eki.ekilex.api.controller;

import javax.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
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

import eki.ekilex.data.Freeform;
import eki.ekilex.data.LexemeFreeform;
import eki.ekilex.data.MeaningFreeform;
import eki.ekilex.data.WordFreeform;
import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.service.api.FreeformService;
@Tag(name = "Freeforms", description = "Operations with freeforms")
@ConditionalOnWebApplication
@RestController
public class ApiFreeformController extends AbstractApiController {

	@Autowired
	private FreeformService freeformService;

	@Order(750)
	@GetMapping(API_SERVICES_URI + FREEFORM_URI + DETAILS_URI + "/{freeformId}")
	@ResponseBody
	public Freeform getFreeform(
			@PathVariable("freeformId") Long freeformId,
			Authentication authentication,
			HttpServletRequest request) {

		Freeform freeform = freeformService.getFreeform(freeformId);
		addRequestStat(authentication, request);
		return freeform;
	}

	@Order(751)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(authentication, #crudRoleDataset, #freeform.wordId)")
	@PostMapping(API_SERVICES_URI + WORD_FREEFORM_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordFreeform(
			@RequestBody WordFreeform freeform,
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			Long freeformId = freeformService.createWordFreeform(freeform, crudRoleDataset);
			return getOpSuccessResponse(authentication, request, "FREEFORM", freeformId);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(752)
	@PreAuthorize("principal.apiCrud && @permEval.isLexemeCrudGranted(authentication, #crudRoleDataset, #freeform.lexemeId)")
	@PostMapping(API_SERVICES_URI + LEXEME_FREEFORM_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createLexemeFreeform(
			@RequestBody LexemeFreeform freeform,
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			Long freeformId = freeformService.createLexemeFreeform(freeform, crudRoleDataset);
			return getOpSuccessResponse(authentication, request, "FREEFORM", freeformId);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(753)
	@PreAuthorize("principal.apiCrud && @permEval.isMeaningCrudGranted(authentication, #crudRoleDataset, #freeform.meaningId)")
	@PostMapping(API_SERVICES_URI + MEANING_FREEFORM_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createMeaningFreeform(
			@RequestBody MeaningFreeform freeform,
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			Long freeformId = freeformService.createMeaningFreeform(freeform, crudRoleDataset);
			return getOpSuccessResponse(authentication, request, "FREEFORM", freeformId);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(754)
	@PreAuthorize("principal.apiCrud && @permEval.isFreeformCrudGranted(authentication, #crudRoleDataset, #freeform.id)")
	@PostMapping(API_SERVICES_URI + FREEFORM_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateFreeform(
			@RequestBody Freeform freeform,
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			freeformService.updateFreeform(freeform, crudRoleDataset);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(755)
	@PreAuthorize("principal.apiCrud && @permEval.isFreeformCrudGranted(authentication, #crudRoleDataset, #freeformId)")
	@DeleteMapping(API_SERVICES_URI + FREEFORM_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteFreeform(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("freeformId") Long freeformId,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			freeformService.deleteFreeform(freeformId, crudRoleDataset);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}
}
