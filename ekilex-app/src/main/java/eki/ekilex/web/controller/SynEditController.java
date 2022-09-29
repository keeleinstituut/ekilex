package eki.ekilex.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.UserContextData;
import eki.ekilex.service.SynCudService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SynEditController extends AbstractPrivateSearchController {

	private static final Logger logger = LoggerFactory.getLogger(SynEditController.class);

	@Autowired
	protected SynCudService synCudService;

	@PostMapping(SYN_RELATION_STATUS_URI)
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String updateRelationStatus(@RequestParam Long id, @RequestParam String status, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Updating syn relation status id {}, new status {}", id, status);
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		synCudService.updateRelationStatus(id, status, isManualEventOnUpdateEnabled);
		return RESPONSE_OK_VER2;
	}

	@PostMapping(SYN_RELATION_STATUS_URI + "/delete")
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String updateWordSynRelationsStatusDeleted(@RequestParam Long wordId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Updating word {} syn relation status to \"DELETED\"", wordId);
		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		String datasetCode = userRole.getDatasetCode();
		List<String> synCandidateLangCodes = userContextData.getPartSynCandidateLangCodes();

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		synCudService.updateWordSynRelationsStatusDeleted(wordId, datasetCode, synCandidateLangCodes, isManualEventOnUpdateEnabled);
		return RESPONSE_OK_VER2;
	}

	@PostMapping(SYN_CREATE_MEANING_RELATION_URI + "/{targetMeaningId}/{sourceMeaningId}/{wordRelationId}")
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String createSynMeaningRelation(
			@PathVariable Long targetMeaningId,
			@PathVariable Long sourceMeaningId,
			@PathVariable Long wordRelationId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		synCudService.createSynMeaningRelation(targetMeaningId, sourceMeaningId, wordRelationId, isManualEventOnUpdateEnabled);
		return RESPONSE_OK_VER2;
	}

}
