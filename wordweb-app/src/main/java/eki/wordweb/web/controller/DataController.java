package eki.wordweb.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.service.AudioLinkService;
import eki.wordweb.service.FileService;

@ConditionalOnWebApplication
@RestController
public class DataController implements WebConstant, SystemConstant {

	private static final Logger logger = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private AudioLinkService audioLinkService;

	@Autowired
	private FileService fileService;

	@GetMapping("/data/app")
	@ResponseBody
	public AppData getAppData() {
		return appDataHolder.getAppData();
	}

	@PostMapping(AUDIO_LINK_URI)
	public String getAudioLink(
			@RequestParam String text,
			@RequestParam String serviceId) {
		return audioLinkService.getAudioLink(text, serviceId);
	}

	@GetMapping(FILES_URI + "/{fileId}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String fileId) {

		String fileName = "";
		Resource resource = null;
		try (Stream<Path> dirStream = Files.find(Paths.get(
				System.getProperty("java.io.tmpdir")),
				1,
				(p, a) -> p.getFileName().toString().startsWith(fileId))) {
			Optional<Path> fileToServe = dirStream.findFirst();
			if (fileToServe.isPresent()) {
				resource = new FileSystemResource(fileToServe.get());
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

	@GetMapping(FILES_URI + "/audio/{fileName:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveAudioFile(@PathVariable String fileName) {

		Resource resource = fileService.getAudioFileAsResource(fileName);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}

	@GetMapping(FILES_URI + "/images/{fileName:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveImage(@PathVariable String fileName) {

		Resource resource = fileService.getFileAsResource(fileName);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}

}
