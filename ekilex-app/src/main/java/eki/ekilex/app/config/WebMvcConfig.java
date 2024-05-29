package eki.ekilex.app.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.web.interceptor.PageRequestPostHandler;

/**
 * Thymeleaf and Spring MVC configuration.
 */
@ConditionalOnWebApplication
@Configuration
public class WebMvcConfig implements WebMvcConfigurer, WebConstant {

	@Autowired
	private PageRequestPostHandler pageRequestPostHandler;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(pageRequestPostHandler).addPathPatterns("/**");
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(VIEW_RESOURCES_URI + "/css/**").addResourceLocations("classpath:/view/css/");
		registry.addResourceHandler(VIEW_RESOURCES_URI + "/js/**").addResourceLocations("classpath:/view/js/");
		registry.addResourceHandler(VIEW_RESOURCES_URI + "/img/**").addResourceLocations("classpath:/view/img/");
		registry.addResourceHandler(VIEW_RESOURCES_URI + "/fonts/**").addResourceLocations("classpath:/view/fonts/");
		registry.addResourceHandler(VIEW_RESOURCES_URI + "/proto/**").addResourceLocations("classpath:/view/proto/");
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
		changeInterceptor.setParamName("uilang");
		return changeInterceptor;
	}
}
