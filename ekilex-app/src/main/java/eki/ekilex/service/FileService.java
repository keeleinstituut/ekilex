package eki.ekilex.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileService.class);

	@Value("${file.repository.path:}")
	private String fileRepositoryPath;

	public Resource getFileAsResource(String fileName) {

		Path pathToFile = findFilePath(fileName);
		return pathToFile == null ? null : new PathResource(pathToFile);
	}

	private Path findFilePath(String fileName) {

		Path filePath = null;
		try {
			Optional<Path> fileToServe = Files.find(
					Paths.get(fileRepositoryPath),
					4,
					(p, a) -> p.getFileName().toString().startsWith(fileName)).findFirst();
			if (fileToServe.isPresent()) {
				filePath = fileToServe.get();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return filePath;
	}

}
