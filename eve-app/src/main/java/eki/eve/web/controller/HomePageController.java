package eki.eve.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eki.eve.constant.WebConstant;

@Controller
public class HomePageController implements WebConstant {

	@RequestMapping(value = HOME_URI)
	public String main(Model model, HttpServletRequest request) {

		return HOME_PAGE;
	}

}
