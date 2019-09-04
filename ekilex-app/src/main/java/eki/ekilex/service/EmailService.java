package eki.ekilex.service;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;

@Component
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	private static final String USER_ACTIVATION_SUBJECT = "Ekilexi kasutaja registreerimine";
	private static final String USER_ACTIVATION_TEMPLATE = "user-activation";

	private static final String PASSWORD_RECOVERY_SUBJECT = "Ekilexi kasutaja salasõna lähtestamine";
	private static final String PASSWORD_RECOVERY_TEMPLATE = "password-recovery";

	private static final String APPLICATION_SUBMIT_SUBJECT = "Ekilexi kasutaja õiguste taotlus";
	private static final String APPLICATION_SUBMIT_TEMPLATE = "application-submit";

	private static final String ADDITIONAL_APPLICATION_SUBMIT_SUBJECT = "Ekilexi kasutaja õiguste juurdetaotlemine";
	private static final String ADDITIONAL_APPLICATION_SUBMIT_TEMPLATE = "additional-application-submit";

	private static final String USER_PERMISSIONS_SUBJECT = "Ekilexi kasutaja õiguste nimekiri";
	private static final String USER_PERMISSIONS_TEMPLATE = "user-permissions";

	@Value("${email.from.address}")
	private String fromAddress;

	@Value("${email.from.name}")
	private String fromName;

	@Value("${email.sending.enabled:false}")
	private boolean isEmailSendingEnabled;

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	@Qualifier("emailTemplateEngine")
	private TemplateEngine textTemplateEngine;

	private Locale locale = new Locale("est");

	public boolean isEnabled() {
		return isEmailSendingEnabled;
	}

	public void sendUserActivationEmail(String email, String activationLink) {

		Context context = new Context(locale);
		context.setVariable("activationLink", activationLink);
		sendTextEmail(email, USER_ACTIVATION_SUBJECT, USER_ACTIVATION_TEMPLATE, context);
	}

	public void sendPasswordRecoveryEmail(String email, String passwordRecoveryLink) {

		Context context = new Context(locale);
		context.setVariable("passwordRecoveryLink", passwordRecoveryLink);
		sendTextEmail(email, PASSWORD_RECOVERY_SUBJECT, PASSWORD_RECOVERY_TEMPLATE, context);
	}

	public void sendApplicationSubmitEmail(List<String> emails, EkiUser user, List<String> datasets, String comment, boolean isAdditionalApplication) {

		String joinedDatasets = null;
		if (CollectionUtils.isNotEmpty(datasets)) {
			joinedDatasets = String.join(", ", datasets);
		}
		Context context = new Context(locale);
		context.setVariable("userName", user.getName());
		context.setVariable("userEmail", user.getEmail());
		context.setVariable("datasets", joinedDatasets);
		context.setVariable("comment", comment);
		if (isAdditionalApplication) {
			sendTextEmail(emails, Collections.emptyList(), ADDITIONAL_APPLICATION_SUBMIT_SUBJECT, ADDITIONAL_APPLICATION_SUBMIT_TEMPLATE, context);
		} else {
			sendTextEmail(emails, Collections.emptyList(), APPLICATION_SUBMIT_SUBJECT, APPLICATION_SUBMIT_TEMPLATE, context);
		}
	}

	public void sendPermissionsEmail(EkiUser receiver, EkiUser sender) {

		String senderName = sender.getName();
		String senderEmail = sender.getEmail();
		String email = receiver.getEmail();
		String name = receiver.getName();
		boolean isEnabled = BooleanUtils.toBoolean(receiver.getEnabled());
		boolean isAdmin = receiver.isAdmin();
		List<DatasetPermission> datasetPermissions = receiver.getDatasetPermissions();

		logger.info("User \"{}\" (email: \"{}\") initiated permissions email sending to user \"{}\" (email: \"{}\")", senderName, senderEmail, name, email);

		Context context = new Context(locale);
		context.setVariable("isEnabled", isEnabled);
		context.setVariable("isAdmin", isAdmin);
		context.setVariable("datasetPermissions", datasetPermissions);
		sendTextEmail(email, USER_PERMISSIONS_SUBJECT, USER_PERMISSIONS_TEMPLATE, context);
	}

	private void sendTextEmail(String to, String subject, String template, Context context) {
		sendTextEmail(Collections.singletonList(to), Collections.emptyList(), subject, template, context);
	}

	private void sendTextEmail(List<String> toList, List<String> ccList, String subject, String template, Context context) {

		if (CollectionUtils.isEmpty(toList)) {
			return;
		}

		try {
			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setSubject(subject);
			helper.setFrom(fromAddress, fromName);
			helper.setTo(toList.toArray(new String[0]));
			if (CollectionUtils.isNotEmpty(ccList)) {
				helper.setCc(ccList.toArray(new String[0]));
			}

			String textContent = textTemplateEngine.process(template, context);
			helper.setText(textContent);

			if (isEmailSendingEnabled) {
				emailSender.send(mimeMessage);
			}

			logMessage(mimeMessage, textContent);
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
