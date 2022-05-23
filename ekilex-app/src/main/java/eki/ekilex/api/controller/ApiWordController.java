package eki.ekilex.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.Word;
import eki.ekilex.data.api.WordClassifier;
import eki.ekilex.data.api.WordFreeform;
import eki.ekilex.data.api.WordRelation;
import eki.ekilex.service.CudService;
import eki.ekilex.service.api.WordService;

@ConditionalOnWebApplication
@RestController
public class ApiWordController extends AbstractApiController {

	@Autowired
	private WordService wordService;

	@Autowired
	private CudService cudService;

	@Order(601)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCreateGranted(principal, #crudRoleDataset, #word)")
	@PostMapping(value = API_SERVICES_URI + WORD_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWord(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody Word word) {

		try {
			Long wordId = wordService.createWord(word, MANUAL_EVENT_ON_UPDATE_ENABLED);
			if (wordId == null) {
				return getOpFailResponse("Invalid or unsupported word composition");
			}
			return getOpSuccessResponse(wordId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(602)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(principal, #crudRoleDataset, #wordType)")
	@PostMapping(value = API_SERVICES_URI + WORD_TYPE_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordType(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordClassifier wordType) {

		try {
			Long wordId = wordType.getWordId();
			String typeCode = wordType.getClassifierCode();
			cudService.createWordType(wordId, typeCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(603)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(principal, #crudRoleDataset, #wordRelation)")
	@PostMapping(value = API_SERVICES_URI + WORD_RELATION_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordRelation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordRelation wordRelation) {

		try {
			Long wordId = wordRelation.getWordId();
			Long targetWordId = wordRelation.getTargetWordId();
			String relationTypeCode = wordRelation.getRelationTypeCode();
			String oppositeRelationTypeCode = wordRelation.getOppositeRelationTypeCode();
			cudService.createWordRelation(wordId, targetWordId, relationTypeCode, oppositeRelationTypeCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(604)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(principal, #crudRoleDataset, #wordNote)")
	@PostMapping(value = API_SERVICES_URI + WORD_NOTE_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordNote(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordFreeform wordNote) {

		try {
			Long wordId = wordNote.getWordId();
			String valuePrese = wordNote.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.createWordNote(wordId, valuePrese, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(605)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(principal, #crudRoleDataset, #odWordRecommendation)")
	@PostMapping(value = API_SERVICES_URI + OD_WORD_RECOMMENDATION + CREATE_URI)
	@ResponseBody
	public ApiResponse createOdWordRecommendation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordFreeform odWordRecommendation) {

		try {
			Long wordId = odWordRecommendation.getWordId();
			String valuePrese = odWordRecommendation.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.createOdWordRecommendation(wordId, valuePrese, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(606)
	@PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(principal, #crudRoleDataset, #word)")
	@PostMapping(value = API_SERVICES_URI + WORD_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateWord(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody Word word) {

		try {
			wordService.updateWord(word, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(607)
	@PreAuthorize("principal.apiCrud && @permEval.isWordFreeformCrudGranted(principal, #crudRoleDataset, #wordNote)")
	@PostMapping(value = API_SERVICES_URI + WORD_NOTE_URI + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateWordNote(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordFreeform wordNote) {

		try {
			Long wordNoteId = wordNote.getFreeformId();
			String valuePrese = wordNote.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.updateWordNote(wordNoteId, valuePrese, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(608)
	@PreAuthorize("principal.apiCrud && @permEval.isWordFreeformCrudGranted(principal, #crudRoleDataset, #odWordRecommendation)")
	@PostMapping(value = API_SERVICES_URI + OD_WORD_RECOMMENDATION + UPDATE_URI)
	@ResponseBody
	public ApiResponse updateOdWordRecommendation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordFreeform odWordRecommendation) {

		try {
			Long freeformId = odWordRecommendation.getFreeformId();
			String valuePrese = odWordRecommendation.getValuePrese();
			valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
			cudService.updateOdWordRecommendation(freeformId, valuePrese, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(609)
	@PreAuthorize("principal.apiCrud && @permEval.isLexemeCrudGranted(principal, #crudRoleDataset, #lexemeId)")
	@PostMapping(value = API_SERVICES_URI + LEXEME_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteLexeme(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("lexemeId") Long lexemeId) {

		try {
			cudService.deleteLexeme(lexemeId, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

   @Order(610)
   @PreAuthorize("principal.apiCrud && @permEval.isWordCrudGranted(principal, #crudRoleDataset, #wordType)")
   @PostMapping(value = API_SERVICES_URI + WORD_TYPE_URI + DELETE_URI)
   @ResponseBody
   public ApiResponse deleteWordType(
		   @RequestParam("crudRoleDataset") String crudRoleDataset,
		   @RequestBody WordClassifier wordType) {

	   try {
		   Long wordId = wordType.getWordId();
		   String typeCode = wordType.getClassifierCode();
		   cudService.deleteWordType(wordId, typeCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		   return getOpSuccessResponse();
	   } catch (Exception e) {
		   return getOpFailResponse(e);
	   }
   }

	@Order(611)
	@PreAuthorize("principal.apiCrud && @permEval.isWordRelationCrudGranted(principal, #crudRoleDataset, #relationId)")
	@PostMapping(value = API_SERVICES_URI + WORD_RELATION_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteWordRelation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("relationId") Long relationId) {

		try {
			cudService.deleteWordRelation(relationId, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(612)
	@PreAuthorize("principal.apiCrud && @permEval.isWordFreeformCrudGranted(principal, #crudRoleDataset, #wordNoteId)")
	@PostMapping(value = API_SERVICES_URI + WORD_NOTE_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteWordNote(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("wordNoteId") Long wordNoteId) {

		try {
			cudService.deleteWordNote(wordNoteId, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

	@Order(613)
	@PreAuthorize("principal.apiCrud && @permEval.isWordFreeformCrudGranted(principal, #crudRoleDataset, #odWordRecommendationId)")
	@PostMapping(value = API_SERVICES_URI + OD_WORD_RECOMMENDATION + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteOdWordRecommendation(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("odWordRecommendationId") Long odWordRecommendationId) {

		try {
			cudService.deleteOdWordRecommendation(odWordRecommendationId, MANUAL_EVENT_ON_UPDATE_ENABLED);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}

}
