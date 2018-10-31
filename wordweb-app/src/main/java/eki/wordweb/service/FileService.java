package eki.wordweb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileService.class);

	@Value("${file.repository.path:}")
	private String fileRepositoryPath;

	public Resource getSoundFileAsResource(String fileName) {

		Resource resource = null;
		Path pathToFile = findFilePath(fileName);
		if (pathToFile != null) {
			Path path = doMp3ConversionIfNeeded(pathToFile);
			resource = new FileSystemResource(path);
		}
		return resource;
	}

	public Resource getFileAsResource(String fileName) {

		Path pathToFile = findFilePath(fileName);
		return pathToFile == null ? null : new FileSystemResource(pathToFile);
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

	private Path doMp3ConversionIfNeeded(Path soundFile) {

		if (soundFile.getFileName().toString().endsWith("wav")) {
			String mp3File = soundFile.getFileName().toString().replace(".wav", ".mp3");
			mp3File = System.getProperty("java.io.tmpdir") + "/" + mp3File;
			if (Files.exists(Paths.get(mp3File))) {
				return Paths.get(mp3File);
			}

			String wavFile = soundFile.toString();
			String command = String.format("lame --silent %s %s", wavFile, mp3File);
			if (execute(command)) {
				return Paths.get(mp3File);
			}
		}
		return soundFile;
	}

	private boolean execute(String command) {

		ProcessBuilder builder = new ProcessBuilder();
		builder.command("sh", "-c", command).redirectErrorStream(true);
		int exitCode;
		try {
			Process process = builder.start();
			StreamConsumer outputStreamConsumer = new StreamConsumer(process.getInputStream(), logger::debug);
			Executors.newSingleThreadExecutor().submit(outputStreamConsumer);
			exitCode = process.waitFor();
		} catch (InterruptedException | IOException e) {
			logger.error("Shell execute", e);
			exitCode = 1;
		}
		return exitCode == 0;
	}

	private static class StreamConsumer implements Runnable {

		private InputStream inputStream;
		private Consumer<String> consumer;

		StreamConsumer(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream))
					.lines()
					.forEach(consumer);
		}
	}

}
