package eki.ekilex.api.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.data.api.ApiResponse;
import eki.ekilex.data.api.TermMeaning;
import eki.ekilex.service.api.TermMeaningService;

@ConditionalOnWebApplication
@RestController
public class ApiTermMeaningController extends AbstractApiController {

	@Autowired
	private TermMeaningService termMeaningService;

	@Order(804)
	@PreAuthorize("@permEval.isDatasetCrudGranted(principal, #crudRoleDataset, #datasetCode)")
	@GetMapping(API_SERVICES_URI + TERM_MEANING_URI + DETAILS_URI + "/{meaningId}/{datasetCode}")
	@ResponseBody
	public TermMeaning getTermMeaning(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@PathVariable("meaningId") Long meaningId,
			@PathVariable("datasetCode") String datasetCode) {

		return termMeaningService.getTermMeaning(meaningId, datasetCode);
	}

	@Order(805)
	@PreAuthorize("principal.apiCrud "
			+ "&& @permEval.isDatasetCrudGranted(principal, #crudRoleDataset, #termMeaning.datasetCode) "
			+ "&& @permEval.isMeaningCrudGranted(principal, #crudRoleDataset, #termMeaning.meaningId)")
	@PostMapping(API_SERVICES_URI + TERM_MEANING_URI + SAVE_URI)
	@ResponseBody
	public ApiResponse saveTermMeaning(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody TermMeaning termMeaning) {

		try {
			if (CollectionUtils.isEmpty(termMeaning.getWords())) {
				return getOpFailResponse("Missing words");
			}
			Long meaningId = termMeaningService.saveTermMeaning(termMeaning, crudRoleDataset);
			return getOpSuccessResponse(meaningId);
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}
}
