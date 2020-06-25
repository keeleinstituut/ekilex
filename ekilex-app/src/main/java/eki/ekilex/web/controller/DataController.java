package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.service.FileService;

@ConditionalOnWebApplication
@RestController
public class DataController implements SystemConstant, WebConstant {

	@Autowired
	private FileService fileService;

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
