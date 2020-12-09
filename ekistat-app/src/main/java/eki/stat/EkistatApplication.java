package eki.stat;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {
		"eki.common",
		"eki.stat.config",
		"eki.stat.api",
		"eki.stat.service"})
@EnableCaching
@EnableTransactionManagement
public class EkistatApplication {

	@Value("${server.servlet.session.timeout:30m}") // default 30 min
	Duration sessionTimeout;

	public static void main(String[] args) {
		System.setProperty("org.jooq.no-logo", "true");
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		SpringApplication.run(EkistatApplication.class, args);
	}
}
