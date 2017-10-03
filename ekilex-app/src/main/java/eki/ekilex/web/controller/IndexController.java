package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@ConditionalOnWebApplication
@Controller
public class IndexController implements WebConstant {

	@RequestMapping("/")
	public String index() {
		return MAIN_PAGE;
	}

}
