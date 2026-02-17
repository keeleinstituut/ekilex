package eki.wwexam.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.UrlPathHelper;

import eki.wwexam.web.interceptor.PageRequestPostHandler;

@ConditionalOnWebApplication
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private PageRequestPostHandler pageRequestPostHandler;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(pageRequestPostHandler).addPathPatterns("/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/view/css/**").addResourceLocations("classpath:/view/css/");
		registry.addResourceHandler("/view/js/**").addResourceLocations("classpath:/view/js/");
		registry.addResourceHandler("/view/img/**").addResourceLocations("classpath:/view/img/");
		registry.addResourceHandler("/view/fonts/**").addResourceLocations("classpath:/view/fonts/");
		registry.addResourceHandler("/view/images/**").addResourceLocations("classpath:/view/images/");
		registry.addResourceHandler("/view/webfonts/**").addResourceLocations("classpath:/view/webfonts/");
		registry.addResourceHandler("/favicon.png").addResourceLocations("classpath:/view/images/favicon.png");
		registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/view/images/favicon.ico");
		registry.addResourceHandler("/apple-touch-icon.png").addResourceLocations("classpath:/view/images/apple-touch-icon.png");
		registry.addResourceHandler("/favicon-32x32-light.png").addResourceLocations("classpath:/view/images/favicon-light/favicon-16x16-light.png");
		registry.addResourceHandler("/favicon-32x32-light.png").addResourceLocations("classpath:/view/images/favicon-light/favicon-32x32-light.png");
		registry.addResourceHandler("/favicon-light.ico").addResourceLocations("classpath:/view/images/favicon-light/favicon-light.ico");
		registry.addResourceHandler("/apple-touch-icon-light.png").addResourceLocations("classpath:/view/images/favicon-light/apple-touch-icon-light.png");
		registry.addResourceHandler("/favicon-32x32-os.png").addResourceLocations("classpath:/view/images/favicon-os/favicon-16x16-os.png");
		registry.addResourceHandler("/favicon-32x32-os.png").addResourceLocations("classpath:/view/images/favicon-os/favicon-32x32-os.png");
		registry.addResourceHandler("/favicon-os.ico").addResourceLocations("classpath:/view/images/favicon-os/favicon-os.ico");
		registry.addResourceHandler("/apple-touch-icon-os.png").addResourceLocations("classpath:/view/images/favicon-os/apple-touch-icon-os.png");
	}

	@Bean
	LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.forLanguageTag("et"));
		return localeResolver;
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		UrlPathHelper urlPathHelper = new UrlPathHelper();
		urlPathHelper.setUrlDecode(false);
		configurer.setUrlPathHelper(urlPathHelper);
	}

}
