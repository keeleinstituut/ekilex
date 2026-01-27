package eki.common.service.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

import eki.common.constant.FeedbackConstant;
import eki.common.constant.FeedbackType;
import eki.common.data.ExtendedFeedback;
import eki.common.data.Feedback;
import eki.common.data.ValidationResult;

@Component
public class FeedbackValidator implements FeedbackConstant {

	private static final int TEXT_MAX_LENGTH = 2000;

	public ValidationResult validate(Feedback feedback) {

		if (feedback == null) {
			return new ValidationResult(false, "message.feedback.empty");
		}
		FeedbackType feedbackType = feedback.getFeedbackType();

		if (FeedbackType.WORD_SUGGESTION.equals(feedbackType)) {

			ExtendedFeedback extendedFeedback = (ExtendedFeedback) feedback;
			String senderEmail = feedback.getSenderEmail();
			String senderName = extendedFeedback.getSenderName();
			String word = extendedFeedback.getWord();
			String definition = extendedFeedback.getDefinition();
			String usage = extendedFeedback.getUsage();

			if (StringUtils.isBlank(word)) {
				return new ValidationResult(false, "message.feedback.missing.word");
			}
			if (StringUtils.isBlank(definition)) {
				return new ValidationResult(false, "message.feedback.missing.definition");
			} else if (StringUtils.length(definition) > TEXT_MAX_LENGTH) {
				return new ValidationResult(false, "message.feedback.invalid.text");
			}
			if (StringUtils.isBlank(usage)) {
				return new ValidationResult(false, "message.feedback.missing.usage");
			}
			if (StringUtils.isBlank(senderName)) {
				return new ValidationResult(false, "message.feedback.missing.name");
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
			String description = feedback.getDescription();
			String senderEmail = feedback.getSenderEmail();
			if (StringUtils.isBlank(description)) {
				return new ValidationResult(false, "message.feedback.missing.description");
			} else if (StringUtils.length(description) > TEXT_MAX_LENGTH) {
				return new ValidationResult(false, "message.feedback.invalid.text");
			}
			if (StringUtils.isNotBlank(senderEmail)) {
				boolean isValidEmail = isValidEmail(senderEmail);
				if (!isValidEmail) {
					return new ValidationResult(false, "message.feedback.invalid.email");
				}
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
