package eki.eve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"eki.common", "eki.eve"})
public class EveAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EveAppApplication.class, args);
	}
}
