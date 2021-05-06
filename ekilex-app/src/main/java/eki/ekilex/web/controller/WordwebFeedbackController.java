package eki.ekilex.web.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.FeedbackComment;
import eki.ekilex.data.FeedbackLog;
import eki.ekilex.data.FeedbackLogResult;
import eki.ekilex.service.FeedbackService;
import eki.ekilex.web.bean.WwFeedbackSearchBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes({WebConstant.SESSION_BEAN, WebConstant.WW_FEEDBACK_SEARCH_BEAN})
public class WordwebFeedbackController extends AbstractPublicPageController {

	private static final Logger logger = LoggerFactory.getLogger(WordwebFeedbackController.class);

	@Autowired
	private FeedbackService feedbackService;

	@CrossOrigin
	@PostMapping(SEND_FEEDBACK_URI)
	@ResponseBody
	public String receiveFeedback(@RequestBody FeedbackLog feedbackLog) {

		logger.info("Wordweb feedback received: \"{}\"", feedbackLog);

		String statusMessage;
		if (feedbackService.isValidFeedbackLog(feedbackLog)) {
			statusMessage = feedbackService.createFeedbackLog(feedbackLog);
		} else {
			statusMessage = "error";
		}
		return "{\"status\": \"" + statusMessage + "\"}";
	}

	private WwFeedbackSearchBean getWwFeedbackSearchBean(Model model) {
		WwFeedbackSearchBean wwFeedbackSearchBean = (WwFeedbackSearchBean) model.asMap().get(WW_FEEDBACK_SEARCH_BEAN);
		if (wwFeedbackSearchBean == null) {
			wwFeedbackSearchBean = new WwFeedbackSearchBean();
			model.addAttribute(WW_FEEDBACK_SEARCH_BEAN, wwFeedbackSearchBean);
		}
		return wwFeedbackSearchBean;
	}

	@PreAuthorize("principal.datasetCrudPermissionsExist")
	@GetMapping(WW_FEEDBACK_URI)
	public String init(Model model) {

		WwFeedbackSearchBean wwFeedbackSearchBean = new WwFeedbackSearchBean();
		wwFeedbackSearchBean.setPageNum(1);
		model.addAttribute(WW_FEEDBACK_SEARCH_BEAN, wwFeedbackSearchBean);

		populateModel(model);

		return WW_FEEDBACK_PAGE;
	}

	@PreAuthorize("principal.datasetCrudPermissionsExist")
	@GetMapping(WW_FEEDBACK_URI + "/page/{pageNum}")
	public String page(@PathVariable("pageNum") int pageNum, Model model) {

		WwFeedbackSearchBean wwFeedbackSearchBean = getWwFeedbackSearchBean(model);
		wwFeedbackSearchBean.setPageNum(pageNum);

		populateModel(model);

		return WW_FEEDBACK_PAGE;
	}

	@PostMapping(WW_FEEDBACK_URI + SEARCH_URI)
	public String search(
			@RequestParam(name = "searchFilter", required = false) String searchFilter,
			@RequestParam(name = "notCommentedFilter", required = false) Boolean notCommentedFilter,
			Model model) {

		if (StringUtils.isBlank(searchFilter) && (notCommentedFilter == null)) {
			return "redirect:" + WW_FEEDBACK_URI;
		}
		if (StringUtils.isNotBlank(searchFilter) && StringUtils.length(searchFilter) < 3) {
			return "redirect:" + WW_FEEDBACK_URI;
		}

		WwFeedbackSearchBean wwFeedbackSearchBean = getWwFeedbackSearchBean(model);
		wwFeedbackSearchBean.setSearchFilter(searchFilter);
		wwFeedbackSearchBean.setNotCommentedFilter(notCommentedFilter);
		wwFeedbackSearchBean.setPageNum(1);

		populateModel(model);

		return WW_FEEDBACK_PAGE;
	}

	@PreAuthorize("principal.datasetCrudPermissionsExist")
	@PostMapping(WW_FEEDBACK_URI + "/addcomment")
	public String addFeedbackComment(
			@RequestBody Map<String, String> requestBody,
			Model model) {

		String userName = userContext.getUserName();
		Long feedbackLogId = Long.valueOf(requestBody.get("feedbackId"));
		String comment = requestBody.get("comment");

		feedbackService.createFeedbackLogComment(feedbackLogId, comment, userName);

		List<FeedbackComment> comments = feedbackService.getFeedbackLogComments(feedbackLogId);
		FeedbackLog feedbackLog = new FeedbackLog();
		feedbackLog.setId(feedbackLogId);
		feedbackLog.setFeedbackComments(comments);

		model.addAttribute("feedbackLog", feedbackLog);

		return WW_FEEDBACK_PAGE + PAGE_FRAGMENT_ELEM + "eki_comments";
	}

	@PreAuthorize("principal.datasetCrudPermissionsExist")
	@GetMapping(WW_FEEDBACK_URI + "/deletefeedback")
	public String deleteDatasetPerm(@RequestParam("feedbackId") Long feedbackId, Model model) {

		feedbackService.deleteFeedbackLog(feedbackId);

		populateModel(model);

		return WW_FEEDBACK_PAGE;
	}

	private void populateModel(Model model) {

		WwFeedbackSearchBean wwFeedbackSearchBean = getWwFeedbackSearchBean(model);
		String searchFilter = wwFeedbackSearchBean.getSearchFilter();
		Boolean notCommentedFilter = wwFeedbackSearchBean.getNotCommentedFilter();
		int pageNum = wwFeedbackSearchBean.getPageNum();

		FeedbackLogResult feedbackLogResult = feedbackService.getFeedbackLog(searchFilter, notCommentedFilter, pageNum);
		model.addAttribute("feedbackLogResult", feedbackLogResult);
	}
}
