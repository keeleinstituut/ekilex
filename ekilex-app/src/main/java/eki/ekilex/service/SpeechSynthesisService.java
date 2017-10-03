package eki.ekilex.service;

import eki.common.util.CodeGenerator;
import eki.ekilex.data.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class SpeechSynthesisService {

	private static final Logger logger = LoggerFactory.getLogger(SpeechSynthesisService.class);

	@Value("${server.contextPath:}")
	private String contextPath;

	@Value("${speech.synthesizer.path:}")
	private String synthesizerPath;

	@Value("${speech.synthesizer.service.url:}")
	private String synthesizerUrl;

	public String urlToSoundSource(Word word) {
		if (!isEnabled() || !"est".equals(word.getLanguage())) {
			return null;
		}
		if (isNotBlank(synthesizerPath)) {
			return urlFromIntegratedSpeechSynthesizer(word);
		}
		return urlFromEkiPublicService(word);
	}

	public boolean isEnabled() {
		return isNotBlank(synthesizerPath) || isNotBlank(synthesizerUrl);
	}

	private String urlFromIntegratedSpeechSynthesizer(Word word) {
		String fileId = CodeGenerator.generateUniqueId();
		String sourceFile = System.getProperty("java.io.tmpdir") + "/" + fileId + ".txt";
		String wavFile = System.getProperty("java.io.tmpdir") + "/" + fileId + ".wav";
		String mp3File = System.getProperty("java.io.tmpdir") + "/" + fileId + ".mp3";
		try {
			Files.write(Paths.get(sourceFile), word.getValue().getBytes());
			String command = String.format("bin/synthts_et -lex dct/et.dct -lexd dct/et3.dct -o %s -f %s -m htsvoices/eki_et_tnu.htsvoice -r 1.5", wavFile, sourceFile);
			if (!execute(command)) {
				fileId = null;
			} else {
				command = String.format("lame --silent %s %s", wavFile, mp3File);
				if (execute(command)) {
					Files.delete(Paths.get(wavFile));
				}
			}
			Files.delete(Paths.get(sourceFile));
		} catch (IOException | InterruptedException e) {
			logger.error(e.getMessage());
			fileId = null;
		}
		return fileId == null ? null : contextPath + "/files/" + fileId;
	}

	private String urlFromEkiPublicService(Word word) {
		URI url= UriComponentsBuilder.fromUriString(synthesizerUrl)
				.queryParam("haal", 15)  // 14 <- female voice
				.queryParam("tekst", word.getValue())
				.build()
				.toUri();
		String responseAsString = doGetRequest(url);
		Map<String, Object> response = Collections.emptyMap();
		if (responseAsString != null) {
			JsonParser jsonParser = JsonParserFactory.getJsonParser();
			response = jsonParser.parseMap(responseAsString);
		}
		return response.containsKey("mp3url") ? (String) response.get("mp3url") : null;
	}

	private String doGetRequest(URI url) {
		HttpHeaders headers = new HttpHeaders();

		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return response == null ? null : response.getBody();
	}

	private boolean execute(String command) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command("sh", "-c", command)
				.directory(new File(synthesizerPath))
				.redirectErrorStream(true);
		Process process = builder.start();
		StreamConsumer outputStreamConsumer = new StreamConsumer(process.getInputStream(), logger::debug);
		Executors.newSingleThreadExecutor().submit(outputStreamConsumer);
		int exitCode = process.waitFor();
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
