package eki.stat;

import java.time.ZoneId;
import java.util.TimeZone;

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

	public static void main(String[] args) {
		System.setProperty("org.jooq.no-logo", "true");
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Tallinn")));
		SpringApplication.run(EkistatApplication.class, args);
	}
}
