package eki.common.service.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import eki.common.constant.FeedbackConstant;
import eki.common.constant.FeedbackType;
import eki.common.data.Feedback;
import eki.common.data.ValidationResult;

@Component
public class FeedbackValidator implements FeedbackConstant {

	public ValidationResult validate(Feedback feedback) {

		if (feedback == null) {
			return new ValidationResult(false, "message.feedback.empty");
		}
		FeedbackType feedbackType = feedback.getFeedbackType();
		String senderEmail = feedback.getSenderEmail();
		String word = feedback.getWord();
		String description = feedback.getDescription();
		if (FeedbackType.WORD_SUGGESTION.equals(feedbackType)) {
			if (StringUtils.isBlank(word)) {
				return new ValidationResult(false, "message.feedback.missing.word");
			}
			if (StringUtils.isBlank(description)) {
				return new ValidationResult(false, "message.feedback.missing.description.wordsuggestion");
			}
			if (StringUtils.isBlank(senderEmail)) {
				return new ValidationResult(false, "message.feedback.missing.email");
			} else {
				boolean isValidEmail = isValidEmail(senderEmail);
				if (!isValidEmail) {
					return new ValidationResult(false, "message.feedback.invalid.email");
				}
			}
		} else {
			if (StringUtils.isBlank(description)) {
				return new ValidationResult(false, "message.feedback.missing.description.generic");
			}
		}
		return new ValidationResult(true);
	}

	private boolean isValidEmail(String email) {
		email = StringUtils.lowerCase(email);
		EmailValidator emailValidator = EmailValidator.getInstance();
		boolean isValidEmail = emailValidator.isValid(email);
		return isValidEmail;
	}
}
