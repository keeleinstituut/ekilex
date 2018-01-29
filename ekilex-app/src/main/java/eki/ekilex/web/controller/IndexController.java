package eki.ekilex.web.controller;

import eki.ekilex.constant.WebConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class IndexController implements WebConstant {

	@RequestMapping("/")
	public String index() {
		return MAIN_PAGE;
	}

}
