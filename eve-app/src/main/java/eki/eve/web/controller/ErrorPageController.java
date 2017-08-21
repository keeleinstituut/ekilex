package eki.eve.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import eki.eve.constant.WebConstant;

@Controller
public class ErrorPageController implements WebConstant {

	@Autowired
	protected MessageSource messageSource;

	@RequestMapping(value = "/error/{error}")
	public String showError(@PathVariable("error") String error, Model model, HttpServletRequest request) throws Exception {

		String errorDescription = messageSource.getMessage("error." + error, new Object[0], LocaleContextHolder.getLocale());

		model.addAttribute("errorName", error);
		model.addAttribute("errorDescription", errorDescription);

		return ERROR_PAGE;
	}
}
