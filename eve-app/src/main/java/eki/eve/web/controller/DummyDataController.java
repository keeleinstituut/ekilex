package eki.eve.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.eve.constant.SystemConstant;

//this is just a sample, not actual service
@RestController
public class DummyDataController {

	@Autowired
	private AppDataHolder appDataHolder;

	@RequestMapping(value = "/data/app", method = RequestMethod.GET)
	public ResponseEntity<AppData> getAppData(HttpServletRequest request) {

		AppData appData = appDataHolder.getAppData(request, SystemConstant.POM_PATH);

		return new ResponseEntity<>(appData, HttpStatus.OK);
	}
}
