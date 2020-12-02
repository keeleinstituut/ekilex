package eki.stat;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"eki.common", "eki.stat.config", "eki.stat.api"})
@EnableCaching
@EnableTransactionManagement
public class StatApplication {

	@Value("${tomcat.ajp.port:0}")
	int ajpPort;

	@Value("${tomcat.ajp.enabled:false}")
	boolean ajpEnabled;

	@Value("${server.servlet.session.timeout:30m}") // default 30 min
	Duration sessionTimeout;

	public static void main(String[] args) {
		System.setProperty("org.jooq.no-logo", "true");
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		SpringApplication.run(StatApplication.class, args);
	}

	// TODO ?

	// @Bean
	// public TomcatServletWebServerFactory servletContainer() {
	// 	TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
	// 	tomcat.getSession().setTimeout(sessionTimeout);
	// 	if (ajpEnabled) {
	// 		Connector ajpConnector = new Connector("AJP/1.3");
	// 		ajpConnector.setPort(ajpPort);
	// 		ajpConnector.setSecure(false);
	// 		ajpConnector.setAllowTrace(false);
	// 		ajpConnector.setScheme("http");
	// 		ajpConnector.setURIEncoding(StandardCharsets.UTF_8.name());
	// 		((AbstractAjpProtocol<?>) ajpConnector.getProtocolHandler()).setSecretRequired(false);
	// 		tomcat.addAdditionalTomcatConnectors(ajpConnector);
	// 	}
	// 	return tomcat;
	// }

}
