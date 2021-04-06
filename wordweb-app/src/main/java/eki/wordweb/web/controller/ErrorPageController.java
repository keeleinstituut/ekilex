package eki.wordweb.web.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.service.util.LanguageContext;

@ConditionalOnWebApplication
@Controller
public class ErrorPageController implements WebConstant {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LanguageContext languageContext;

	@RequestMapping(value = "error/{error}")
	public String showError(@PathVariable("error") String error, Model model) {
		Locale displayLocale = languageContext.getDisplayLocale();
		String errorDescription = messageSource.getMessage("error." + error, new Object[0], displayLocale);
		model.addAttribute("errorName", error);
		model.addAttribute("errorDescription", errorDescription);
		return ERROR_PAGE;
	}
}
