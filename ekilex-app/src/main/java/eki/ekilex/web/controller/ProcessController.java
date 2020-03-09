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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.LayerName;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.CreateItemRequest;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.ProcessLog;
import eki.ekilex.data.UpdateItemRequest;
import eki.ekilex.service.ProcessService;
import eki.ekilex.service.UserProfileService;
import eki.ekilex.service.UserService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ProcessController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(ProcessController.class);

	@Autowired
	private ProcessService processService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserProfileService userProfileService;

	@GetMapping("/meaningprocesslog:{meaningId}")
	public String meaningProcessLogLink(@PathVariable("meaningId") Long meaningId, Model model) {

		logger.debug("Requested process log for meaning \"{}\"", meaningId);
		List<ProcessLog> processLog = processService.getLogForMeaning(meaningId);
		model.addAttribute("processLog", processLog);

		return PROCESS_LOG_VIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@GetMapping("/wordprocesslog:{wordId}")
	public String wordProcessLogLink(@PathVariable("wordId") Long wordId, Model model) {

		logger.debug("Requested process log for word \"{}\"", wordId);
		List<ProcessLog> processLog = processService.getLogForWord(wordId);
		model.addAttribute("processLog", processLog);

		return PROCESS_LOG_VIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@GetMapping("/lexemeandwordprocesslog:{lexemeId}")
	public String lexemeAndWordProcessLogLink(@PathVariable("lexemeId") Long lexemeId, Model model) {

		logger.debug("Requested process log for lexeme and word, lexeme id \"{}\"", lexemeId);
		List<ProcessLog> processLog = processService.getLogForLexemeAndWord(lexemeId);
		model.addAttribute("processLog", processLog);

		return PROCESS_LOG_VIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@GetMapping("/lexemeandmeaningprocesslog:{lexemeId}")
	public String lexemeAndMeaningProcessLogLink(@PathVariable("lexemeId") Long lexemeId, Model model) {

		logger.debug("Requested process log for lexeme and meaning, lexeme id \"{}\"", lexemeId);
		List<ProcessLog> processLog = processService.getLogForLexemeAndMeaning(lexemeId);
		model.addAttribute("processLog", processLog);

		return PROCESS_LOG_VIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@PostMapping(CREATE_MEANING_PROCESS_LOG_URI)
	@ResponseBody
	public String createMeaningProcessLog(
			@RequestBody CreateItemRequest itemData,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		String userName = userService.getAuthenticatedUser().getName();
		String datasetCode = sessionBean.getUserRole().getDatasetCode();
		Long meaningId = itemData.getId();
		String commentPrese = itemData.getValue();
		logger.debug("Creating process log for meaning \"{}\"", meaningId);
		processService.createMeaningProcessLog(meaningId, userName, commentPrese, datasetCode);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(CREATE_WORD_PROCESS_LOG_URI)
	@ResponseBody
	public String createWordProcessLog(
			@RequestBody CreateItemRequest itemData,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		String userName = userService.getAuthenticatedUser().getName();
		String datasetCode = sessionBean.getUserRole().getDatasetCode();
		Long wordId = itemData.getId();
		String commentPrese = itemData.getValue();
		logger.debug("Creating process log for word \"{}\"", wordId);
		processService.createWordProcessLog(wordId, userName, commentPrese, datasetCode);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(CREATE_LEXEME_PROCESS_STATE_URI)
	@ResponseBody
	public String createLexemeProcessState(@RequestBody CreateItemRequest itemData) {

		String userName = userService.getAuthenticatedUser().getName();
		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();
		logger.debug("Creating process state for lexeme \"{}\"", lexemeId);
		processService.updateLexemeProcessState(lexemeId, userName, processStateCode);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(UPDATE_LEXEME_PROCESS_STATE_URI)
	@ResponseBody
	public String updateLexemeProcessState(@RequestBody UpdateItemRequest itemData) {

		String userName = userService.getAuthenticatedUser().getName();
		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();
		logger.debug("Updating process state for lexeme \"{}\"", lexemeId);
		processService.updateLexemeProcessState(lexemeId, userName, processStateCode);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(UPDATE_LAYER_COMPLETE_URI + "/{wordId}")
	@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
	@ResponseBody
	public String updateLayerProcessStateComplete(
			@PathVariable Long wordId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		EkiUser user = userService.getAuthenticatedUser();
		String userName = user.getName();
		Long userId = user.getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		LayerName layerName = userProfile.getPreferredLayerName();
		String datasetCode = sessionBean.getUserRole().getDatasetCode();

		logger.debug("Updating {} layer process state complete for word \"{}\"", layerName, wordId);
		processService.updateLayerProcessStateComplete(wordId, userName, datasetCode, layerName);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(UPDATE_LAYER_PROCESS_STATE_URI)
	@ResponseBody
	public String updateSynLayerProcessState(@RequestBody UpdateItemRequest itemData) {

		EkiUser user = userService.getAuthenticatedUser();
		String userName = user.getName();
		Long userId = user.getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		LayerName layerName = userProfile.getPreferredLayerName();
		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();

		logger.debug("Updating {} layer process state for lexeme \"{}\"", layerName, lexemeId);
		processService.updateSynProcessState(lexemeId, userName, processStateCode, layerName);

		return RESPONSE_OK_VER2;
	}
}
