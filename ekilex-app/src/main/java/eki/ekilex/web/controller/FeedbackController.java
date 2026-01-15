package eki.ekilex.web.controller;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.FeedbackType;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.FeedbackLog;
import eki.ekilex.data.FeedbackLogResult;
import eki.ekilex.data.FeedbackSearchFilter;
import eki.ekilex.data.WordSuggestion;
import eki.ekilex.service.FeedbackService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes({WebConstant.SESSION_BEAN, WebConstant.FEEDBACK_SEARCH_FILTER_KEY})
@PreAuthorize("principal.master")
public class FeedbackController extends AbstractPublicPageController {

	private static final int MIN_SEARCH_VALUE_LENGTH = 3;

	@Autowired
	private FeedbackService feedbackService;

	@GetMapping(WW_FEEDBACK_URI)
	public String init(Model model) {

		FeedbackSearchFilter feedbackSearchFilter = new FeedbackSearchFilter();
		feedbackSearchFilter.setPageNum(1);
		model.addAttribute(FEEDBACK_SEARCH_FILTER_KEY, feedbackSearchFilter);

		populateModel(model);

		return WW_FEEDBACK_PAGE;
	}

	@GetMapping(WW_FEEDBACK_URI + SEARCH_URI)
	public String initRedirect(Model model) {

		return REDIRECT_PREF + WW_FEEDBACK_URI;
	}

	@GetMapping(WW_FEEDBACK_URI + "/page/{pageNum}")
	public String page(@PathVariable("pageNum") int pageNum, Model model) {

		FeedbackSearchFilter wwFeedbackSearchBean = getFeedbackSearchFilter(model);
		wwFeedbackSearchBean.setPageNum(pageNum);

		populateModel(model);

		return WW_FEEDBACK_PAGE;
	}

	@PostMapping(WW_FEEDBACK_URI + SEARCH_URI)
	public String search(
			FeedbackSearchFilter feedbackSearchFilter,
			Model model) {

		if (StringUtils.isNotBlank(feedbackSearchFilter.getSearchValue())
				&& StringUtils.length(feedbackSearchFilter.getSearchValue()) < MIN_SEARCH_VALUE_LENGTH) {
			feedbackSearchFilter.setSearchValue(null);
		}

		FeedbackType feedbackType = feedbackSearchFilter.getFeedbackType();
		String searchValue = feedbackSearchFilter.getSearchValue();
		LocalDate created = feedbackSearchFilter.getCreated();
		Boolean notCommented = feedbackSearchFilter.getNotCommented();

		if (StringUtils.isBlank(searchValue)
				&& (feedbackType == null)
				&& (created == null)
				&& (notCommented == null)) {
			return REDIRECT_PREF + WW_FEEDBACK_URI;
		}

		feedbackSearchFilter.setPageNum(1);

		populateModel(model);

		return WW_FEEDBACK_PAGE;
	}

	@PostMapping(WW_FEEDBACK_URI + "/createcomment")
	public String createFeedbackComment(
			@RequestParam("feedbackLogId") Long feedbackLogId,
			@RequestParam("comment") String comment,
			Model model) {

		String userName = userContext.getUserName();

		feedbackService.createFeedbackLogComment(feedbackLogId, comment, userName);
		FeedbackLog feedbackLog = feedbackService.getFeedbackLog(feedbackLogId);
		model.addAttribute("feedbackLog", feedbackLog);

		return WW_FEEDBACK_PAGE + PAGE_FRAGMENT_ELEM + "feedbacklog_row";
	}

	@PostMapping(WW_FEEDBACK_URI + "/savewordsuggestion")
	public String saveWordSuggestion(WordSuggestion wordSuggestion, Model model) {

		EkiUser user = userContext.getUser();
		Long feedbackLogId = wordSuggestion.getFeedbackLogId();
		feedbackService.saveWordSuggestion(user, wordSuggestion);
		FeedbackLog feedbackLog = feedbackService.getFeedbackLog(feedbackLogId);
		model.addAttribute("feedbackLog", feedbackLog);

		return WW_FEEDBACK_PAGE + PAGE_FRAGMENT_ELEM + "feedbacklog_row";
	}

	@GetMapping(WW_FEEDBACK_URI + "/deletefeedback")
	public String deleteFeedback(@RequestParam("feedbackId") Long feedbackId, Model model) {

		feedbackService.deleteFeedbackLog(feedbackId);

		populateModel(model);

		FeedbackSearchFilter wwFeedbackSearchBean = getFeedbackSearchFilter(model);
		int pageNum = wwFeedbackSearchBean.getPageNum();

		return REDIRECT_PREF + WW_FEEDBACK_URI + "/page/" + pageNum;
	}

	private void populateModel(Model model) {

		FeedbackSearchFilter feedbackSearchFilter = getFeedbackSearchFilter(model);
		int pageNum = feedbackSearchFilter.getPageNum();

		FeedbackLogResult feedbackLogResult = feedbackService.getFeedbackLogs(feedbackSearchFilter, pageNum);
		model.addAttribute("feedbackLogResult", feedbackLogResult);
	}

	private FeedbackSearchFilter getFeedbackSearchFilter(Model model) {
		FeedbackSearchFilter feedbackSearchFilter = (FeedbackSearchFilter) model.asMap().get(FEEDBACK_SEARCH_FILTER_KEY);
		if (feedbackSearchFilter == null) {
			feedbackSearchFilter = new FeedbackSearchFilter();
			model.addAttribute(FEEDBACK_SEARCH_FILTER_KEY, feedbackSearchFilter);
		}
		return feedbackSearchFilter;
	}
}
