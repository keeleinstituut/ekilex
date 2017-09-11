package eki.eve.config;

import eki.eve.web.interceptor.PageRequestPostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

/**
 * Thymeleaf and Spring MVC configuration.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

	@Autowired
	private PageRequestPostHandler pageRequestPostHandler;

	@Value("${spring.thymeleaf.cache}")
	private Boolean cacheTemplates;

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Bean
	public ITemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("classpath:/view/html/");
		resolver.setSuffix(".html");
		resolver.setCacheable(cacheTemplates);
		return resolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		engine.setEnableSpringELCompiler(true);
		engine.addDialect(new SpringSecurityDialect());
		engine.addDialect(new Java8TimeDialect());
		return engine;
	}

	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		return resolver;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(pageRequestPostHandler).addPathPatterns("/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/view/css/**").addResourceLocations("classpath:/view/css/");
		registry.addResourceHandler("/view/js/**").addResourceLocations("classpath:/view/js/");
		registry.addResourceHandler("/view/img/**").addResourceLocations("classpath:/view/img/");
	}
}
