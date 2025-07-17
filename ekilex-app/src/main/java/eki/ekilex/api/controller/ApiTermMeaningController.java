package eki.ekilex.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
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

import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.TermMeaning;
import eki.ekilex.service.CudService;
import eki.ekilex.service.api.TermMeaningService;

@ConditionalOnWebApplication
@RestController
public class ApiTermMeaningController extends AbstractApiController {

	@Autowired
	private TermMeaningService termMeaningService;

	@Autowired
	private CudService cudService;

	@Order(850)
	@PreAuthorize("@permEval.isDatasetCrudGranted(authentication, #crudRoleDataset, #datasetCode)")
	@GetMapping(API_SERVICES_URI + TERM_MEANING_URI + DETAILS_URI + "/{meaningId}/{datasetCode}")
	@ResponseBody
	public TermMeaning getTermMeaning(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@PathVariable("meaningId") Long meaningId,
			@PathVariable("datasetCode") String datasetCode,
			Authentication authentication,
			HttpServletRequest request) {

		TermMeaning termMeaning = termMeaningService.getTermMeaning(meaningId, datasetCode);
		addRequestStat(authentication, request);
		return termMeaning;
	}

	@Order(851)
	@PreAuthorize("principal.apiCrud "
			+ "&& @permEval.isDatasetCrudGranted(authentication, #crudRoleDataset, #termMeaning.datasetCode) "
			+ "&& @permEval.isMeaningCrudGranted(authentication, #crudRoleDataset, #termMeaning.meaningId)")
	@PostMapping(API_SERVICES_URI + TERM_MEANING_URI + SAVE_URI)
	@ResponseBody
	public ApiResponse saveTermMeaning(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam(value = "isManualEventOnUpdateEnabled", required = false, defaultValue = "true") boolean isManualEventOnUpdateEnabled,
			@RequestBody TermMeaning termMeaning,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			if (CollectionUtils.isEmpty(termMeaning.getWords())) {
				return getOpFailResponse(authentication, request, "Missing words");
			}
			Long meaningId = termMeaningService.saveTermMeaning(termMeaning, crudRoleDataset, isManualEventOnUpdateEnabled);
			return getOpSuccessResponse(authentication, request, "MEANING", meaningId);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

	@Order(852)
	@PreAuthorize("principal.apiCrud "
			+ "&& @permEval.isDatasetCrudGranted(authentication, #crudRoleDataset, #crudRoleDataset) "
			+ "&& @permEval.isMeaningCrudGranted(authentication, #crudRoleDataset, #meaningId)")
	@DeleteMapping(API_SERVICES_URI + TERM_MEANING_URI + DELETE_URI)
	@ResponseBody
	public ApiResponse deleteTermMeaning(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestParam("meaningId") Long meaningId,
			Authentication authentication,
			HttpServletRequest request) {

		try {
			cudService.deleteMeaningAndLexemes(meaningId, crudRoleDataset, MANUAL_EVENT_ON_UPDATE_DISABLED);
			return getOpSuccessResponse(authentication, request);
		} catch (Exception e) {
			return getOpFailResponse(authentication, request, e);
		}
	}

}
