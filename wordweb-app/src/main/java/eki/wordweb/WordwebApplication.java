package eki.wordweb;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eki.common.service.TransportService;

@SpringBootApplication
@ComponentScan(basePackages = {"eki.common", "eki.wordweb"}, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {TransportService.class}))
@EnableCaching
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class WordwebApplication {

	@Value("${tomcat.ajp.port:0}")
	int ajpPort;

	@Value("${tomcat.ajp.enabled:false}")
	boolean ajpEnabled;

	@Value("${server.servlet.session.timeout:30m}") // default 30 min
	Duration sessionTimeout;

	public static void main(String[] args) {
		System.setProperty("org.jooq.no-logo", "true");
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		System.setProperty("jsse.enableSNIExtension", "false");
		SpringApplication.run(WordwebApplication.class, args);
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
		tomcat.getSession().setTimeout(sessionTimeout);
		if (ajpEnabled) {
			Connector ajpConnector = new Connector("AJP/1.3");
			ajpConnector.setPort(ajpPort);
			ajpConnector.setSecure(false);
			ajpConnector.setAllowTrace(false);
			ajpConnector.setScheme("http");
			ajpConnector.setURIEncoding(StandardCharsets.UTF_8.name());
			((AbstractAjpProtocol<?>) ajpConnector.getProtocolHandler()).setSecretRequired(false);
			tomcat.addAdditionalTomcatConnectors(ajpConnector);
		}
		return tomcat;
	}

}
