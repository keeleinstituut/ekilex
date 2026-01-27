package eki.ekilex.cli.runner;

import java.util.Base64;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;

import eki.common.constant.GlobalConstant;
import eki.ekilex.client.SmtpTransport;
import eki.ekilex.constant.SystemConstant;

@Component
public class MsSmtpEmailRunner implements GlobalConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(MsSmtpEmailRunner.class);

	@Autowired
	private Environment env;

	public void execute() throws Exception {

		logger.info("Starting test...");

		String authUrl = env.getProperty("mail.oauth2.auth_url");
		String scope = env.getProperty("mail.oauth2.scope");
		String clientId = env.getProperty("mail.oauth2.client_id");
		String secretValue = env.getProperty("mail.oauth2.secret_value");
		String host = env.getProperty("mail.host");
		String username = env.getProperty("mail.username");

		System.out.println("=========== PROPS ===========");
		System.out.println("authUrl : " + authUrl);
		System.out.println("scope   : " + scope);
		System.out.println("clientId: " + clientId);
		System.out.println("host    : " + host);
		System.out.println("username: " + username);
		System.out.println("=============================");

		String token = getToken(authUrl, scope, clientId, secretValue);
		sendMessage2(host, username, token);
		//readInbox(host, username, token);
	}

	private String getToken(String authUrl, String accessScope, String clientId, String secretValue) {

		String mytoken = null;

		try {
			Set<String> scopes = new HashSet<>();
			scopes.add(accessScope);

			IClientCredential cred = ClientCredentialFactory.createFromSecret(secretValue);
			ConfidentialClientApplication cca = ConfidentialClientApplication.builder(clientId, cred).authority(authUrl).build();
			ClientCredentialParameters parameters = ClientCredentialParameters.builder(scopes).build();
			IAuthenticationResult token = cca.acquireToken(parameters).join();

			mytoken = token.accessToken();

			System.out.println("=========== TOKEN ===========");
			System.out.println("Token  : " + token.accessToken());
			System.out.println("Scope  : " + token.scopes());
			System.out.println("Expires: " + token.expiresOnDate());
			System.out.println("Payload: " + tokenDecodePayload(token.accessToken(), 1));
			System.out.println("=============================");

		} catch (Exception e) {
			System.out.println("Failed to acquire token");
			e.printStackTrace();
		}

		return mytoken;
	}

	private String tokenDecodePayload(String token, int chunkIndex) {
		String[] chunks = token.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[chunkIndex]));
		return payload;
	}

	private void sendMessage2(String host, String userName, String accessToken) {

		if (StringUtils.isBlank(accessToken)) {
			System.out.println("Missing token. Exiting...");
			return;
		}

		System.out.println("Sending test email...");

		String to = "martin.laubre@gmail.com";
		String subject = "Test message";
		String content = "Test content\nRegards";

		Properties props = new Properties();

		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", "smtp.office365.com");
		props.put("mail.smtp.user", userName);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

		props.put("mail.smtp.auth.xoauth2.disable", "false");

		props.put("mail.debug.auth", "true");
		props.put("mail.debug", "true");

		SmtpTransport transport = null;

		try {
			/*
			Session session = Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, accessToken);
				}
			});
			*/
			Session session = Session.getInstance(props);
			Message message = composeMessage(session, userName, to, subject, content);

			transport = new SmtpTransport(session, null);
			logger.info("Connecting to server...");
			transport.connect("smtp.office365.com", userName, accessToken);
			//smtpTransport.connect("smtp.office365.com", userName, accessToken);
			//smtpTransport.issueCommand("AUTH XOAUTH2 " + smtpToken, 235);
			//transport.connect(userName, smtpToken);
			//transport.connect(userName, accessToken);
			//System.out.println("---> connected: " + transport.isConnected());
			//System.out.println("---> url name: " + transport.getURLName());
			logger.info("Sending message...");
			transport.sendMessage(message, message.getAllRecipients());
			System.out.println("...email sent");
		} catch (Exception e) {
			logger.info("...connecting failed");
			e.printStackTrace();
		} finally {
			try {
				transport.close();
			} catch (MessagingException e) {
				System.out.println("Error on close");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void sendMessage(String host, String userName, String accessToken) {

		if (StringUtils.isBlank(accessToken)) {
			System.out.println("Missing token. Exiting...");
			return;
		}

		System.out.println("Sending test email...");

		String to = "test@test.com";
		String subject = "Test message";
		String content = "Test content\nRegards";

		//String encodedToken = tokenEncoder( userName, accessToken ); 

		Properties props = new Properties();

		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", "smtp.office365.com");
		props.put("mail.smtp.user", userName);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

		props.put("mail.smtp.auth.xoauth2.disable", "false");
		//props.put("mail.smtp.sasl.enable", "true");

		/*
		props.put("mail.smtp.auth.xoauth2.disable", "false");
		props.put("mail.smtp.sasl.enable", "true");
		props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", "smtp.office365.com");
		*/

		props.put("mail.debug.auth", "true");
		props.put("mail.debug", "true");

		try {
			/*
			Session session = Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, accessToken);
				}
			});
			*/
			Session session = Session.getInstance(props);
			Message message = composeMessage(session, userName, to, subject, content);
			Transport transport = session.getTransport("smtp");
			com.sun.mail.smtp.SMTPTransport smtpTransport = (com.sun.mail.smtp.SMTPTransport) transport;
			System.out.println("---- connecting....");
			smtpTransport.connect("smtp.office365.com", userName, accessToken);
			//smtpTransport.issueCommand("AUTH XOAUTH2 " + smtpToken, 235);
			//transport.connect(userName, accessToken);
			//System.out.println("---> connected: " + transport.isConnected());
			//System.out.println("---> url name: " + transport.getURLName());
			//transport.sendMessage(message, message.getAllRecipients());
			System.out.println("...email sent");
		} catch (Exception e) {
			System.out.println("...sending email failed");
			e.printStackTrace();
		}

	}

	private Message composeMessage(Session session, String from, String tos, String subject, String content) {
		try {
			InternetAddress[] recipients = InternetAddress.parse(tos);

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, recipients);
			message.setSubject(subject);
			message.setContent(content, "text/plain");

			return message;
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void readInbox(String host, String userName, String accessToken) {

		System.out.println("=== Connecting via Oauth2 ");

		//String encodedToken = tokenEncoder( userName, accessToken ); 

		Properties props = new Properties();

		props.put("mail.imap.ssl.protocols", "TLSv1.2");
		props.put("mail.imap.ssl.enable", "true");
		props.put("mail.imap.ssl.trust", "*");
		props.put("mail.imap.starttls.enable", "true");
		props.put("mail.imap.auth.mechanisms", "XOAUTH2");
		props.put("mail.imap.auth.login.disable", "true");
		props.put("mail.imap.auth.plain.disable", "true");
		props.put("mail.imap.sasl.enable", "true");
		props.put("mail.imap.sasl.mechanisms", "XOAUTH2");
		props.put("mail.imap.sasl.mechanisms.oauth2.oauthToken", accessToken);
		props.put("mail.debug.auth", "true");
		props.put("mail.debug", "true");

		try {
			Session session = Session.getInstance(props);

			System.out.println("=== Connecting to imap ...");

			Store store = session.getStore("imap");

			store.connect(host, userName, accessToken); // The OAuth 2.0 Access Token should be passed as the password, and not base64 encoded 

			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_ONLY);
			int messageCount = inbox.getMessageCount();

			System.out.println("=== Message count in INBOX: " + messageCount);

			// saving a message to file
			//saveMessageToFile(  inbox.getMessage(messageCount-1) , "/tmp/test.eml");

			inbox.close(false);
			store.close();

			System.out.println("=== Disconnected.");

		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}
}
