package eki.wordweb.config;

import eki.wordweb.web.interceptor.PageRequestPostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.UrlPathHelper;

import java.util.Locale;

/**
 * Thymeleaf and Spring MVC configuration.
 */
@ConditionalOnWebApplication
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private PageRequestPostHandler pageRequestPostHandler;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(pageRequestPostHandler).addPathPatterns("/**");
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/view/css/**").addResourceLocations("classpath:/view/css/");
		registry.addResourceHandler("/view/js/**").addResourceLocations("classpath:/view/js/");
		registry.addResourceHandler("/view/img/**").addResourceLocations("classpath:/view/img/");
		registry.addResourceHandler("/view/fonts/**").addResourceLocations("classpath:/view/fonts/");
		registry.addResourceHandler("/view/images/**").addResourceLocations("classpath:/view/images/");
		registry.addResourceHandler("/favicon.png").addResourceLocations("classpath:/view/images/favicon.png");
		registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/view/images/favicon.ico");
		registry.addResourceHandler("/apple-touch-icon.png").addResourceLocations("classpath:/view/images/apple-touch-icon.png");
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.forLanguageTag("et"));
		return localeResolver;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor changeInterceptor = new LocaleChangeInterceptor();
		changeInterceptor.setParamName("lang");
		return changeInterceptor;
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		UrlPathHelper urlPathHelper = new UrlPathHelper();
		urlPathHelper.setUrlDecode(false);
		configurer.setUrlPathHelper(urlPathHelper);
	}

}
