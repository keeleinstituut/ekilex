package eki.eve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		scanBasePackages = {"eki.common", "eki.eve"},
		exclude={org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class}
		)
public class EveAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EveAppApplication.class, args);
	}
}
