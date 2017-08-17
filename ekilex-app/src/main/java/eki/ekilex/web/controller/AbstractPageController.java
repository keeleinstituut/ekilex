package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import eki.ekilex.constant.WebConstant;

public abstract class AbstractPageController implements WebConstant {

	@Autowired
	protected MessageSource messageSource;

}
