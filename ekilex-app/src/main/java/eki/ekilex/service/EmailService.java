package eki.ekilex.service;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Value("${email.from.address}")
	private String fromAddress;

	@Value("${email.from.name}")
	private String fromName;

	@Value("${email.sending.enabled:false}")
	private boolean isEmailSendingEnabled;

	private JavaMailSender emailSender;

	public EmailService(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}

	public boolean isEnabled() {
		return isEmailSendingEnabled;
	}

	public void sendUserActivationEmail(String email, String activationLink) {

		String subject = "Ekilexi kasutaja registreerimine";
		String content = "Kasutaja aktiveerimiseks mine lingile: " + "<a href='" + activationLink + "'>" + activationLink + "</a>";
		sendEmail(email, subject, content);
	}

	public void sendPasswordRecoveryEmail(String email, String passwordRecoveryLink) {

		String subject = "Ekilexi kasutaja salasõna lähtestamine";
		String content = "Kasutaja salasõna lähtestamiseks mine lingile: " + "<a href='" + passwordRecoveryLink + "'>" + passwordRecoveryLink + "</a>";
		sendEmail(email, subject, content);
	}

	private void sendEmail(String to, String subject, String content) {
		sendEmail(Collections.singletonList(to), Collections.emptyList(), subject, content);
	}

	private void sendEmail(List<String> toList, List<String> ccList, String subject, String content) {
		if (CollectionUtils.isEmpty(toList)) {
			return;
		}
		try {
			MimeMessage message = emailSender.createMimeMessage();
			// pass 'true' to the constructor to create a multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(toList.toArray(new String[0]));
			if (CollectionUtils.isNotEmpty(ccList)) {
				helper.setCc(ccList.toArray(new String[0]));
			}
			helper.setSubject(subject);
			helper.setFrom(fromAddress, fromName);
			helper.setText(content, true);

			if (isEmailSendingEnabled) {
				emailSender.send(message);
			}

			logMessage(message, content);
		} catch (MessagingException | IOException e) {
			logger.error("send email error : ", e);
		}
	}

	private void logMessage(MimeMessage message, String content) throws MessagingException {
		List<String> recipents = Stream.of(message.getAllRecipients())
				.map(a -> a.toString())
				.collect(toList());
		if (isEmailSendingEnabled) {
			logger.info("email sent to {}; with content : {}", String.join(",", recipents), content);
		} else {
			logger.info("email generated to {}; with content : {}", String.join(",", recipents), content);
		}
	}

}
