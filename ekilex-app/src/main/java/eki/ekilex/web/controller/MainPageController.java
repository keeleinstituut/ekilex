package eki.ekilex.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainPageController extends AbstractPageController {

	@RequestMapping(value = MAIN_URI)
	public String main(Authentication authentication, Model model, HttpServletRequest request) {

		return MAIN_PAGE;
	}

}
