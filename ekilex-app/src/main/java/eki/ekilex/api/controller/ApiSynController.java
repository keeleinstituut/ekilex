package eki.ekilex.api.controller;

import org.apache.commons.lang3.StringUtils;
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
import eki.ekilex.data.api.WordSynCandidateData;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.SynCandidateService;

/*
@ConditionalOnWebApplication
@RestController
*/
public class ApiSynController extends AbstractApiController {

	@Autowired
	private LookupService lookupService;

	@Autowired
	private SynCandidateService synCandidateService;

	@Order(701)
	@PreAuthorize("principal.apiCrud && @permEval.isDatasetCrudGranted(principal, #crudRoleDataset, #wordSynCandidateData.datasetCode)")
	@PostMapping(value = API_SERVICES_URI + SYN_CANDIDATE_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createWordSynCandidates(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody WordSynCandidateData wordSynCandidateData) {

		try {
			String headwordValue = wordSynCandidateData.getValue();
			String headwordLang = wordSynCandidateData.getLang();
			if (StringUtils.isBlank(headwordValue)) {
				return getOpFailResponse("Missing headword value");
			}
			if (StringUtils.isBlank(headwordLang)) {
				return getOpFailResponse("Missing headword lang");
			}
			boolean headwordExists = lookupService.wordExists(headwordValue, headwordLang);
			if (headwordExists) {
				synCandidateService.createWordSynCandidates(wordSynCandidateData);
				return getOpSuccessResponse();
			} else {
				return getOpFailResponse("Headword does not exist");
			}
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}
}
