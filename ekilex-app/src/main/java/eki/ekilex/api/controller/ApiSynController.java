package eki.ekilex.api.controller;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
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
import eki.ekilex.data.api.SynCandidacy;
import eki.ekilex.data.api.SynCandidateWord;
import eki.ekilex.service.DatasetService;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.SynCandidateService;

@ConditionalOnWebApplication
@RestController
public class ApiSynController extends AbstractApiController {

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private LookupService lookupService;

	@Autowired
	private SynCandidateService synCandidateService;

	@Order(701)
	@PreAuthorize("principal.apiCrud && @permEval.isDatasetCrudGranted(authentication, #crudRoleDataset, #synCandidacy.synCandidateDatasetCode)")
	@PostMapping(API_SERVICES_URI + SYN_CANDIDATE_URI + CREATE_URI)
	@ResponseBody
	public ApiResponse createSynCandidacy(
			@RequestParam("crudRoleDataset") String crudRoleDataset,
			@RequestBody SynCandidacy synCandidacy) {

		try {
			String headwordValue = synCandidacy.getHeadwordValue();
			String headwordLang = synCandidacy.getHeadwordLang();
			String datasetCode = synCandidacy.getSynCandidateDatasetCode();
			List<SynCandidateWord> synCandidateWords = synCandidacy.getSynCandidateWords();
			if (StringUtils.isBlank(headwordValue)) {
				return getOpFailResponse("Missing headword value");
			}
			if (StringUtils.isBlank(headwordLang)) {
				return getOpFailResponse("Missing headword lang");
			}
			if (StringUtils.isBlank(datasetCode)) {
				return getOpFailResponse("Missing dataset code");
			}
			if (CollectionUtils.isEmpty(synCandidateWords)) {
				return getOpFailResponse("Missing synonym/match candidates");
			}
			boolean datasetExists = datasetService.datasetExists(datasetCode);
			if (!datasetExists) {
				return getOpFailResponse("Dataset does not exist");
			}
			boolean headwordExists = lookupService.wordExists(headwordValue, headwordLang);
			if (!headwordExists) {
				return getOpFailResponse("Headword does not exist");
			}
			synCandidateService.createFullSynCandidacy(synCandidacy, crudRoleDataset);
			return getOpSuccessResponse();
		} catch (Exception e) {
			return getOpFailResponse(e);
		}
	}
}
