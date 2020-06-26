package eki.ekilex.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.CreateItemRequest;
import eki.ekilex.data.UpdateItemRequest;
import eki.ekilex.service.ProcessService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ProcessController extends AbstractAuthActionController {

	// TODO move to editController? - yogesh

	private static final Logger logger = LoggerFactory.getLogger(ProcessController.class);

	@Autowired
	private ProcessService processService;

	@PostMapping(CREATE_LEXEME_PROCESS_STATE_URI)
	@ResponseBody
	public String createLexemeProcessState(@RequestBody CreateItemRequest itemData) {

		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();
		logger.debug("Creating process state for lexeme \"{}\"", lexemeId);
		processService.updateLexemeProcessState(lexemeId, processStateCode);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(UPDATE_LEXEME_PROCESS_STATE_URI)
	@ResponseBody
	public String updateLexemeProcessState(@RequestBody UpdateItemRequest itemData) {

		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();
		logger.debug("Updating process state for lexeme \"{}\"", lexemeId);
		processService.updateLexemeProcessState(lexemeId, processStateCode);

		return RESPONSE_OK_VER2;
	}
}
