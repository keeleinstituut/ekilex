package eki.ekilex.service;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Arrays;
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

import eki.common.constant.AuthorityOperation;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;

@Component
public class EmailService implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	private static final String USER_ACTIVATION_SUBJECT = "Ekilexi kasutaja registreerimine";
	private static final String USER_ACTIVATION_TEMPLATE = "user-activation";

	private static final String PASSWORD_RECOVERY_SUBJECT = "Ekilexi kasutaja salasõna lähtestamine";
	private static final String PASSWORD_RECOVERY_TEMPLATE = "password-recovery";

	private static final String APPLICATION_SUBMIT_SUBJECT = "Ekilexi kasutaja õiguste taotlus";
	private static final String APPLICATION_SUBMIT_TEMPLATE = "application-submit";

	private static final String APPLICATION_APPROVE_SUBJECT = "Ekilexi kasutaja õiguste taotluse rahuldamine";
	private static final String APPLICATION_REJECT_SUBJECT = "Ekilexi kasutaja õiguste taotluse mitterahuldamine";
	private static final String APPLICATION_APPROVE_OR_REJECT_TEMPLATE = "application-approve-or-reject";

	private static final String USER_PERMISSIONS_SUBJECT = "Ekilexi kasutaja õiguste nimekiri";
	private static final String USER_PERMISSIONS_TEMPLATE = "user-permissions";

	private static final String USER_TERMS_REFUSE_SUBJECT = "Ekilexi kasutaja ei nõustunud kasutustingimustega";
	private static final String USER_TERMS_REFUSE_TEMPLATE = "user-terms-refuse";

	private static final String TERM_SEARCH_RESULT_SUBJECT = "Ekilexi otsingutulemus";
	private static final String TERM_SEARCH_RESULT_TEMPLATE = "term-search-result";

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

	public void sendApplicationSubmitEmail(
			EkiUser user, List<String> emails, String datasetCode, String datasetName, String comment, AuthorityOperation authOp, String datasetPermissionsUrl) {

		Context context = new Context(locale);
		context.setVariable("userName", user.getName());
		context.setVariable("userEmail", user.getEmail());
		context.setVariable("datasetCode", datasetCode);
		context.setVariable("datasetName", datasetName);
		context.setVariable("authOp", authOp);
		context.setVariable("comment", comment);
		context.setVariable("datasetPermissionsUrl", datasetPermissionsUrl);
		sendTextEmail(emails, APPLICATION_SUBMIT_SUBJECT, APPLICATION_SUBMIT_TEMPLATE, context);
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

	public void sendApplicationApprovalEmail(EkiUser applicant, EkiUser reviewer, String datasetName, boolean isApprove) {

		String senderName = reviewer.getName();
		String senderEmail = reviewer.getEmail();
		String email = applicant.getEmail();
		String name = applicant.getName();
		List<DatasetPermission> datasetPermissions = applicant.getDatasetPermissions();
		boolean isEnabled = BooleanUtils.toBoolean(applicant.getEnabled());
		boolean isAdmin = applicant.isAdmin();

		logger.info("User \"{}\" (email: \"{}\") initiated application approval email sending to user \"{}\" (email: \"{}\")", senderName, senderEmail, name, email);

		Context context = new Context(locale);
		context.setVariable("isApprove", isApprove);
		context.setVariable("isEnabled", isEnabled);
		context.setVariable("isAdmin", isAdmin);
		context.setVariable("datasetPermissions", datasetPermissions);
		context.setVariable("datasetName", datasetName);
		if (isApprove) {
			sendTextEmail(email, APPLICATION_APPROVE_SUBJECT, APPLICATION_APPROVE_OR_REJECT_TEMPLATE, context);
		} else {
			sendTextEmail(email, APPLICATION_REJECT_SUBJECT, APPLICATION_APPROVE_OR_REJECT_TEMPLATE, context);
		}
	}

	public void sendTermsRefuseEmail(EkiUser user, List<String> emails) {

		Context context = new Context(locale);
		context.setVariable("userName", user.getName());
		context.setVariable("userEmail", user.getEmail());
		sendTextEmail(emails, USER_TERMS_REFUSE_SUBJECT, USER_TERMS_REFUSE_TEMPLATE, context);
	}

	public void sendTermSearchResult(EkiUser user, String termSearchUrl, String termSearchResultUrl) {

		Context context = new Context(locale);
		context.setVariable("userName", user.getName());
		context.setVariable("termSearchUrl", termSearchUrl);
		context.setVariable("termSearchResultUrl", termSearchResultUrl);
		sendTextEmail(user.getEmail(), TERM_SEARCH_RESULT_SUBJECT, TERM_SEARCH_RESULT_TEMPLATE, context);
	}

	private void sendTextEmail(String to, String subject, String template, Context context) {
		sendTextEmail(Arrays.asList(to), subject, template, context);
	}

	private void sendTextEmail(List<String> toList, String subject, String template, Context context) {

		if (CollectionUtils.isEmpty(toList)) {
			return;
		}

		try {
			String[] toArr = toList.toArray(new String[0]);
			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setSubject(subject);
			helper.setFrom(fromAddress, fromName);
			helper.setTo(toArr);

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
