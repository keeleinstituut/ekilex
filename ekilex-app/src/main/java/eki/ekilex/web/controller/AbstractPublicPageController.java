package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.service.UserContext;
import eki.ekilex.service.UserService;

public abstract class AbstractPublicPageController implements WebConstant {

	@Autowired
	protected UserContext userContext;

	@Autowired
	protected UserService userService;
}
