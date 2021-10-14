package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ActivityLogHistory;
import eki.ekilex.service.HistoryService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class HistoryController extends AbstractPrivatePageController {

	private static final String HISTORY_TYPE_WORD = "word";

	private static final String HISTORY_TYPE_MEANING = "meaning";	

	@Autowired
	private HistoryService historyService;

	@GetMapping(HISTORY_URI)
	public String history(Model model) {

		model.addAttribute("historyType", null);

		return HISTORY_PAGE;
	}

	@GetMapping(HISTORY_URI + "/{historyType}/{offset}")
	public String historyWord(
			@PathVariable("historyType") String historyType,
			@PathVariable(value = "offset", required = false) Integer offset, 
			Model model) {

		if (offset == null) {
			offset = DEFAULT_OFFSET;
		} else {
			offset = Math.max(offset, DEFAULT_OFFSET);
		}
		List<ActivityLogHistory> activityLogHistory = null;
		int resultCount = 0;
		if (StringUtils.equals(HISTORY_TYPE_WORD, historyType)) {
			activityLogHistory = historyService.getWordsHistory(offset, DEFAULT_MAX_RESULTS_LIMIT);
			resultCount = activityLogHistory.size();
		} else if (StringUtils.equals(HISTORY_TYPE_MEANING, historyType)) {
			activityLogHistory = historyService.getMeaningsHistory(offset, DEFAULT_MAX_RESULTS_LIMIT);
			resultCount = activityLogHistory.size();
		}
		int prevOffset = Math.max(offset - DEFAULT_MAX_RESULTS_LIMIT, 0);
		int nextOffset = 0;
		if (resultCount < DEFAULT_MAX_RESULTS_LIMIT) {
			nextOffset = -1;
		} else {
			nextOffset = offset + DEFAULT_MAX_RESULTS_LIMIT;
		}
		model.addAttribute("historyType", historyType);
		model.addAttribute("prevOffset", prevOffset);
		model.addAttribute("currOffset", offset);
		model.addAttribute("nextOffset", nextOffset);
		model.addAttribute("activityLogHistory", activityLogHistory);

		return HISTORY_PAGE;
	}

}
