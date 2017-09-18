package eki.eve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.charset.StandardCharsets;

@SpringBootApplication(
		scanBasePackages = {"eki.common", "eki.eve"},
		exclude={org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class}
		)
@EnableTransactionManagement
public class EveAppApplication {

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
}
