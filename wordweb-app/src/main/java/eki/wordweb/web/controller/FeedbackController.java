package eki.wordweb.web.controller;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.data.AppResponse;
import eki.common.data.Feedback;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.service.FeedbackService;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class FeedbackController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LanguageContext languageContext;

	// dummy response for hackers
	@GetMapping("/feed")
	public String feedGet() {
		logger.warn("Illegal attempt to access feedback form");
		return "nothing";
	}

	// dummy response for hackers
	@PostMapping("/feed")
	public String feedPost() {
		logger.warn("Illegal attempt to access feedback form");
		return "nothing";
	}

	@PostMapping(FEEDBACK_URI)
	@ResponseBody
	public AppResponse feedback(Feedback feedback, @ModelAttribute(SESSION_BEAN) SessionBean sessionBean) {

		feedback.setLastSearch(sessionBean.getSearchWord());
		AppResponse response = feedbackService.feedback(feedback);
		String messageKey = response.getMessageKey();
		if (StringUtils.isNotBlank(messageKey)) {
			Locale displayLocale = languageContext.getDisplayLocale();
			String messageValue = messageSource.getMessage(messageKey, new Object[0], displayLocale);
			response.setMessageValue(messageValue);
		}
		return response;
	}
}
