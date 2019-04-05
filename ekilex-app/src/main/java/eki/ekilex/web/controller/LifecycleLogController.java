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
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.LifecycleLog;
import eki.ekilex.service.LifecycleLogService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LifecycleLogController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(LifecycleLogController.class);

	@Autowired
	private LifecycleLogService lifecycleLogService;

	@GetMapping("/wordlifecyclelog:{wordId}")
	public String wordLifecycleLogLink(@PathVariable("wordId") String wordIdStr, Model model) {

		logger.debug("Requested lifecycle log for word \"{}\"", wordIdStr);
		Long wordId = Long.valueOf(wordIdStr);
		List<LifecycleLog> lifecycleLog = lifecycleLogService.getLogForWord(wordId);
		model.addAttribute("lifecycleLog", lifecycleLog);

		return LIFECYCLELOGVIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@GetMapping("/meaninglifecyclelog:{meaningId}")
	public String meaningLifecycleLogLink(@PathVariable("meaningId") String meaningIdStr, Model model) {

		logger.debug("Requested lifecycle log for meaning \"{}\"", meaningIdStr);
		Long meaningId = Long.valueOf(meaningIdStr);
		List<LifecycleLog> lifecycleLog = lifecycleLogService.getLogForMeaning(meaningId);
		model.addAttribute("lifecycleLog", lifecycleLog);

		return LIFECYCLELOGVIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}
}
