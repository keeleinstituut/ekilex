package eki.ekilex.web.controller;

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
	public String updateWordSynRelationsStatusDeleted(
			@RequestParam Long wordId,
			@RequestParam String language,
			@RequestParam String datasetCode,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Updating word {} lang {} dataset {} syn relation status to \"DELETED\"", wordId, language, datasetCode);
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		synCudService.updateWordSynRelationsStatusDeleted(wordId, datasetCode, language, isManualEventOnUpdateEnabled);
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

	@PostMapping(SYN_CREATE_MEANING_WORD_WITH_CANDIDATE_DATA_URI + "/{targetMeaningId}/{wordRelationId}")
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String createSynMeaningWordWithCandidateData(
			@PathVariable Long targetMeaningId,
			@PathVariable Long wordRelationId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		UserContextData userContextData = getUserContextData();
		String datasetCode = userContextData.getUserRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		synCudService.createSynMeaningWordWithCandidateData(targetMeaningId, null, wordRelationId, datasetCode, isManualEventOnUpdateEnabled);
		return RESPONSE_OK_VER2;
	}

	@PostMapping(SYN_CREATE_MEANING_WORD_WITH_CANDIDATE_DATA_URI + SELECT_URI)
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String createSynMeaningWordWithCandidateData(
			@RequestParam(name = "wordId", required = false) Long wordId,
			@RequestParam("targetMeaningId") Long targetMeaningId,
			@RequestParam("wordRelationId") Long wordRelationId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		UserContextData userContextData = getUserContextData();
		String datasetCode = userContextData.getUserRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		synCudService.createSynMeaningWordWithCandidateData(targetMeaningId, wordId, wordRelationId, datasetCode, isManualEventOnUpdateEnabled);
		return RESPONSE_OK_VER2;
	}

	@PostMapping(SYN_CREATE_MEANING_WORD_URI)
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String createSynMeaningWord(
			@RequestParam(name = "wordId", required = false) Long wordId,
			@RequestParam("wordValue") String wordValue,
			@RequestParam("targetMeaningId") Long targetMeaningId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		UserContextData userContextData = getUserContextData();
		String datasetCode = userContextData.getUserRoleDatasetCode();
		String wordLang = userContextData.getFullSynCandidateLangCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		synCudService.createSynMeaningWord(targetMeaningId, wordId, wordValue, wordLang, datasetCode, isManualEventOnUpdateEnabled);
		return RESPONSE_OK_VER2;
	}

}
