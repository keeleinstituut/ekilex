package eki.ekilex.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.CreateItemRequest;
import eki.ekilex.data.ProcessLog;
import eki.ekilex.data.UpdateItemRequest;
import eki.ekilex.service.ProcessService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ProcessController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(ProcessController.class);

	@Autowired
	private ProcessService processService;

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

	@PostMapping("/create_meaning_process_log")
	@ResponseBody
	public String createMeaningProcessLog(@RequestBody CreateItemRequest itemData) {

		Long meaningId = itemData.getId();
		String commentPrese = itemData.getValue();
		String dataset = itemData.getDataset();
		logger.debug("Creating process log for meaning \"{}\"", meaningId);
		processService.createMeaningProcessLog(meaningId, dataset, commentPrese);

		return "{}";
	}

	@PostMapping("/create_word_process_log")
	@ResponseBody
	public String createWordProcessLog(@RequestBody CreateItemRequest itemData) {

		Long wordId = itemData.getId();
		String commentPrese = itemData.getValue();
		String dataset = itemData.getDataset();
		logger.debug("Creating process log for word \"{}\"", wordId);
		processService.createWordProcessLog(wordId, dataset, commentPrese);

		return "{}";
	}

	@PostMapping("/create_lexeme_process_state")
	@ResponseBody
	public String createLexemeProcessState(@RequestBody CreateItemRequest itemData) {

		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();
		logger.debug("Creating process state for lexeme \"{}\"", lexemeId);
		processService.createLexemeProcessLog(lexemeId, processStateCode);
		processService.updateLexemeProcessState(lexemeId, processStateCode);

		return "{}";
	}

	@PostMapping("/update_lexeme_process_state")
	@ResponseBody
	public String updateLexemeProcessState(@RequestBody UpdateItemRequest itemData) {

		Long lexemeId = itemData.getId();
		String processStateCode = itemData.getValue();
		logger.debug("Updating process state for lexeme \"{}\"", lexemeId);
		processService.createLexemeProcessLog(lexemeId, processStateCode);
		processService.updateLexemeProcessState(lexemeId, processStateCode);

		return "{}";
	}
}
