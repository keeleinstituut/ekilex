package eki.ekilex.web.controller;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@ConditionalOnWebApplication
@RestController
public class DataController implements SystemConstant {

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private FileService fileService;

	@RequestMapping(value="/data/app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public AppData getAppData(HttpServletRequest request) {
		return appDataHolder.getAppData(request, POM_PATH);
	}

	@GetMapping("/files/images/{fileName:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveImage(@PathVariable String fileName) {

		Resource resource = fileService.getFileAsResource(fileName);
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}

}
