package eki.ekilex.runner;

import eki.ekilex.service.TermekiService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnBean(name = "dataSourceTermeki")
public class TermekiFilePumpRunner {

	private static Logger logger = LoggerFactory.getLogger(TermekiFilePumpRunner.class);

	@Autowired
	private TermekiService termekiService;

	@Value("${file.repository.path:}")
	private String fileRepositoryPath;

	@Value("${termeki.file.service.url:}")
	private String termekiFileServiceUrl;

	public void importFiles() {

		if (StringUtils.isEmpty(fileRepositoryPath)) {
			logger.error("file.repository.path property can't be empty");
			return;
		}
		logger.info("Starting image file import");

		int existingFilesCount = 0;
		int newFilesCount = 0;
		List<Map<String, Object>> imageIds = termekiService.getImageIds();
		logger.info("Found {} image files", imageIds.size());
		for (Map<String, Object> imageFileId : imageIds) {
			String imageId = imageFileId.get("image_id").toString();
			Path pathToImageInStorage = Paths.get(fileRepositoryPath + "/termeki/" + imageId);
			if (Files.exists(pathToImageInStorage)) {
				existingFilesCount++;
			} else {
				newFilesCount++;
				loadAndSaveFile(imageId, pathToImageInStorage);
			}
			logger.info("file {} imported", imageId);
		}
		logger.info("Images found {}, images already present {}, images imported {}", imageIds.size(), existingFilesCount, newFilesCount);
	}

	private void loadAndSaveFile(String imageId, Path imagePath) {

		try (InputStream in = new URL(termekiFileServiceUrl + "/" + imageId).openStream()){
			Files.copy(in, imagePath);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

}
