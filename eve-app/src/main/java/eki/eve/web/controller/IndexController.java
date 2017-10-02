package eki.eve.web.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@ConditionalOnWebApplication
@Controller
public class IndexController {

	@RequestMapping("/")
	public String index() {
		return "index";
	}

}
