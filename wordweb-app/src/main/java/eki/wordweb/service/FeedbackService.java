package eki.wordweb.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.FeedbackConstant;
import eki.common.data.AppResponse;
import eki.common.data.Feedback;
import eki.common.data.ValidationResult;
import eki.common.service.util.FeedbackValidator;

@Component
public class FeedbackService extends AbstractRemoteRequestService implements FeedbackConstant {

	private static final String EKILEX_APP_KEY_HEADER_NAME = "ekilex-app-key";

	@Value("${wordweb.feedback.service.url}")
	private String feedbackServiceUrl;

	@Value("${wordweb.app.key:}")
	private String appKey;

	@Autowired
	private FeedbackValidator feedbackValidator;

	public AppResponse feedback(Feedback feedback) {

		ValidationResult validationResult = feedbackValidator.validate(feedback);

		if (!validationResult.isValid()) {
			return new AppResponse(FEEDBACK_ERROR, validationResult.getMessageKey());
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String messageBody = objectMapper.writeValueAsString(feedback);
			String responseBody = requestPostWithBody(feedbackServiceUrl, EKILEX_APP_KEY_HEADER_NAME, appKey, messageBody);
			if (StringUtils.isBlank(responseBody)) {
				return new AppResponse(FEEDBACK_ERROR, "message.feedback.failing.service");
			}
			AppResponse appResponse = objectMapper.readValue(responseBody, AppResponse.class);
			return appResponse;
		} catch (Exception e) {
			return new AppResponse(FEEDBACK_ERROR, "message.feedback.failing.service");
		}
	}
}
