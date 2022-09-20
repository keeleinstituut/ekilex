package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ActivityLog;
import eki.ekilex.service.core.ActivityLogService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ActivityLogController implements WebConstant {

	@Autowired
	private ActivityLogService activityLogService;

	@GetMapping("/wordactivitylog:{wordId}")
	public String wordActivityLog(@PathVariable("wordId") Long wordId, Model model) {
		List<ActivityLog> wordActivityLog = activityLogService.getWordActivityLog(wordId);
		model.addAttribute("activityLog", wordActivityLog);
		return ACTIVITYLOGVIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@GetMapping("/meaningactivitylog:{meaningId}")
	public String meaningActivityLog(@PathVariable("meaningId") Long meaningId, Model model) {
		List<ActivityLog> wordActivityLog = activityLogService.getMeaningActivityLog(meaningId);
		model.addAttribute("activityLog", wordActivityLog);
		return ACTIVITYLOGVIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}

	@GetMapping("/sourceactivitylog:{sourceId}")
	public String sourceActivityLog(@PathVariable("sourceId") Long sourceId, Model model) {
		List<ActivityLog> wordActivityLog = activityLogService.getSourceActivityLog(sourceId);
		model.addAttribute("activityLog", wordActivityLog);
		return ACTIVITYLOGVIEW_PAGE + PAGE_FRAGMENT_ELEM + "details";
	}
}
