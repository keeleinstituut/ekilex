package eki.eve;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@SpringBootApplication(
		scanBasePackages = {"eki.common", "eki.eve"},
		exclude={org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class}
		)
@EnableTransactionManagement
public class EveAppApplication {

	@Value("${tomcat.ajp.port:0}")
	int ajpPort;

	@Value("${tomcat.ajp.enabled:false}")
	boolean ajpEnabled;

	@Value("${server.session.timeout:1800}")  // default 30 min
	int sessionTimeout;

	public static void main(String[] args) {
		SpringApplication.run(EveAppApplication.class, args);
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasenames("classpath:/messages/messages");
		source.setDefaultEncoding(StandardCharsets.UTF_8.name());
		source.setCacheSeconds(10);
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}

	@Bean
	public TomcatServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.getSession().setTimeout(Duration.ofSeconds(sessionTimeout));
		if (ajpEnabled) {
			Connector ajpConnector = new Connector("AJP/1.3");
			ajpConnector.setPort(ajpPort);
			ajpConnector.setSecure(false);
			ajpConnector.setAllowTrace(false);
			ajpConnector.setScheme("http");
			tomcat.addAdditionalTomcatConnectors(ajpConnector);
		}
		return tomcat;
	}

}
