package eki.ekilex.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.LayerName;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.CreateItemRequest;
import eki.ekilex.data.ProcessLog;
import eki.ekilex.data.UpdateItemRequest;
import eki.ekilex.data.UserContextData;
import eki.ekilex.service.ProcessService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ProcessController extends AbstractAuthActionController {

	private static final Logger logger = LoggerFactory.getLogger(ProcessController.class);

	@Autowired
	private ProcessService processService;

	@GetMapping("/lexemeprocesslog:{lexemeId}")
	public String lexemeProcessLogLink(@PathVariable("lexemeId") Long lexemeId, Model model) {

		logger.debug("Requested process log for lexeme, lexeme id \"{}\"", lexemeId);
		List<ProcessLog> processLog = processService.getLogForLexeme(lexemeId);
		model.addAttribute("processLog", processLog);

		return PROCESS_LOG_VIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@PostMapping(CREATE_LEXEME_PROCESS_STATE_URI)
	@ResponseBody
	public String createLexemeProcessState(@RequestBody CreateItemRequest itemData) {

		String userName = userContext.getUserName();
		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();
		logger.debug("Creating process state for lexeme \"{}\"", lexemeId);
		processService.updateLexemeProcessState(lexemeId, userName, processStateCode);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(UPDATE_LEXEME_PROCESS_STATE_URI)
	@ResponseBody
	public String updateLexemeProcessState(@RequestBody UpdateItemRequest itemData) {

		String userName = userContext.getUserName();
		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();
		logger.debug("Updating process state for lexeme \"{}\"", lexemeId);
		processService.updateLexemeProcessState(lexemeId, userName, processStateCode);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(UPDATE_LAYER_COMPLETE_URI + "/{wordId}")
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String updateLayerProcessStateComplete(@PathVariable Long wordId) {

		UserContextData userContextData = getUserContextData();
		String userName = userContextData.getUserName();
		LayerName layerName = userContextData.getLayerName();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();

		logger.debug("Updating {} layer process state complete for word \"{}\"", layerName, wordId);
		processService.updateLayerProcessStateComplete(wordId, userName, userRoleDatasetCode, layerName);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(UPDATE_LAYER_PROCESS_STATE_URI)
	@ResponseBody
	public String updateSynLayerProcessState(@RequestBody UpdateItemRequest itemData) {

		UserContextData userContextData = getUserContextData();
		String userName = userContextData.getUserName();
		LayerName layerName = userContextData.getLayerName();

		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();

		logger.debug("Updating {} layer process state for lexeme \"{}\"", layerName, lexemeId);
		processService.updateSynProcessState(lexemeId, userName, processStateCode, layerName);

		return RESPONSE_OK_VER2;
	}
}
