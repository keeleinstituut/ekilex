package eki.eve.web.controller;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.eve.constant.SystemConstant;
import eki.eve.service.SoundFileService;
import eki.eve.service.SpeechSynthesisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@ConditionalOnWebApplication
@RestController
public class DataController implements SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	SpeechSynthesisService speechSynthesisService;

	@Autowired
	SoundFileService soundFileService;

	@RequestMapping(value="/data/app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public AppData getAppData(HttpServletRequest request) {
		return appDataHolder.getAppData(request, POM_PATH);
	}

	@GetMapping("/files/{fileId}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String fileId) {

		String fileName = "";
		Resource resource = null;
		try {
			Optional<Path> fileToServe = Files.find(
					Paths.get(System.getProperty("java.io.tmpdir")),
					1,
					(p,a) -> p.getFileName().toString().startsWith(fileId)).findFirst();
			if (fileToServe.isPresent()) {
				resource = new ByteArrayResource(Files.readAllBytes(fileToServe.get()));
				fileName = fileToServe.get().getFileName().toString();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}

	@PostMapping("/generate_voice")
	public String generateSoundFileUrl(@RequestParam String words) {
		return speechSynthesisService.urlToSoundSource(words);
	}

	@GetMapping("/sounds/{fileName}")
	@ResponseBody
	public ResponseEntity<Resource> serveSoundFile(@PathVariable String fileName) {

		Resource resource = soundFileService.getSoundFileAsResource(fileName);
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}
