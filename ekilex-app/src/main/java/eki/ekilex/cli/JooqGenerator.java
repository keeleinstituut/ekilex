package eki.ekilex.cli;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"eki.ekilex.data"}, exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class JooqGenerator implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(JooqGenerator.class);

	@Value("${spring.datasource.main.driver-class-name}")
	private String mainDataSourceDriver;

	@Value("${spring.datasource.main.url}")
	private String mainDataSourceUrl;

	@Value("${spring.datasource.main.username}")
	private String mainDataSourceUsername;

	@Value("${spring.datasource.main.password}")
	private String mainDataSourcePassword;

	@Value("${spring.datasource.arch.driver-class-name}")
	private String archDataSourceDriver;

	@Value("${spring.datasource.arch.url}")
	private String archDataSourceUrl;

	@Value("${spring.datasource.arch.username}")
	private String archDataSourceUsername;

	@Value("${spring.datasource.arch.password}")
	private String archDataSourcePassword;

	@Autowired
	private ConfigurableApplicationContext context;

	//mvn spring-boot:run -P jooq -D spring-boot.run.profiles=dev
	public static void main(String[] args) {
		logger.info("Application starting up");
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication application = new SpringApplication(JooqGenerator.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
		logger.info("Application finished");
	}

	@Override
	public void run(String... args) throws Exception {

		try {
			runGenerator(mainDataSourceDriver, mainDataSourceUrl, mainDataSourceUsername, mainDataSourcePassword, "eki.ekilex.data.db.main");
			runGenerator(archDataSourceDriver, archDataSourceUrl, archDataSourceUsername, archDataSourcePassword, "eki.ekilex.data.db.arch");
		} catch (Exception e) {
			logger.error("Fatal error", e);
		} finally {
			context.close();
		}
	}

	private void runGenerator(String dataSourceDriver, String dataSourceUrl, String dataSourceUsername, String dataSourcePassword, String targetPackageName) throws Exception {

		final String dataSourceSchema = "public";
		final String databaseName = "org.jooq.meta.postgres.PostgresDatabase";
		final String targetSourceDirectory = "src/main/java";

		logger.info("Generating JOOQ proxies based on \"{}\"", dataSourceUrl);

		Configuration configuration = new Configuration()
				.withJdbc(new Jdbc()
						.withDriver(dataSourceDriver)
						.withUrl(dataSourceUrl)
						.withUser(dataSourceUsername)
						.withPassword(dataSourcePassword))
				.withGenerator(new Generator()
						.withDatabase(new Database()
								.withName(databaseName)
								.withIncludes(".*")
								.withIncludeIndexes(Boolean.FALSE)
								.withIncludeSystemIndexes(Boolean.FALSE)
								.withInputSchema(dataSourceSchema))
						.withGenerate(new Generate()
								.withJavaTimeTypes(Boolean.FALSE))
						.withTarget(new Target()
								.withPackageName(targetPackageName)
								.withDirectory(targetSourceDirectory)));

		GenerationTool.generate(configuration);
	}
}
