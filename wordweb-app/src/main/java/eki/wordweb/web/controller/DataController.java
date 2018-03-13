package eki.wordweb.web.controller;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.wordweb.constant.SystemConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@ConditionalOnWebApplication
@RestController
public class DataController implements SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@RequestMapping(value="/data/app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public AppData getAppData(HttpServletRequest request) {
		return appDataHolder.getAppData(request, POM_PATH);
	}

}
