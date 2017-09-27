package eki.eve.web.controller;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.eve.constant.SystemConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class DataController implements SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@RequestMapping(value="/data/app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public AppData getAppData(HttpServletRequest request) {
		return appDataHolder.getAppData(request, POM_PATH);
	}

	@GetMapping("/files/{fileId}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String fileId) {
		String wavFile = System.getProperty("java.io.tmpdir") + fileId + ".wav";
		Resource resource = null;
		try {
			resource = new ByteArrayResource(Files.readAllBytes(Paths.get(wavFile)));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + ".wav" + "\"")
				.body(resource);
	}

}
