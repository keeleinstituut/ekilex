package eki.wordweb.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

import org.apache.commons.lang3.StringUtils;
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

import eki.common.util.CodeGenerator;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Word;

@Service
public class SpeechSynthesisService implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(SpeechSynthesisService.class);

	private static final String SYNTH_COMMAND_STRING = "bin/synthts_et -lex dct/et.dct -lexd dct/et3.dct -o %s -f %s -m htsvoices/eki_et_tnu.htsvoice -r 1.1";

	private static final String LANG_EST = "est";

	@Value("${server.servlet.context-path:}")
	private String contextPath;

	@Value("${speech.synthesizer.path:}")
	private String synthesizerPath;

	@Value("${speech.synthesizer.service.url:}")
	private String synthesizerUrl;

	public String urlToSoundSource(String words) throws Exception {
		if (!isEnabled()) {
			return null;
		}
		if (isNotBlank(synthesizerPath)) {
			return urlFromIntegratedSpeechSynthesizer(words);
		}
		return urlFromEkiPublicService(words);
	}

	public String urlToSoundSource(Word word) throws Exception {
		if (StringUtils.equals(LANG_EST, word.getLang())) {
			return urlToSoundSource(word.getWord());
		}
		return null;
	}

	public boolean isEnabled() {
		return isNotBlank(synthesizerPath) || isNotBlank(synthesizerUrl);
	}

	private String urlFromIntegratedSpeechSynthesizer(String words) {

		String fileId = CodeGenerator.generateUniqueId();
		String tempDirPath = System.getProperty("java.io.tmpdir");
		String sourceFile = tempDirPath + "/" + fileId + ".txt";
		String wavFile = tempDirPath + "/" + fileId + ".wav";
		String mp3File = tempDirPath + "/" + fileId + ".mp3";
		try {
			Files.write(Paths.get(sourceFile), words.getBytes());
			String command = String.format(SYNTH_COMMAND_STRING, wavFile, sourceFile);
			if (execute(command)) {
				command = String.format("lame --silent %s %s", wavFile, mp3File);
				if (execute(command)) {
					Files.delete(Paths.get(wavFile));
				}
			} else {
				fileId = null;
			}
			Files.delete(Paths.get(sourceFile));
		} catch (Exception e) {
			logger.error(e.getMessage());
			fileId = null;
		}
		return fileId == null ? null : contextPath + FILES_URI + "/" + fileId;
	}

	private String urlFromEkiPublicService(String words) throws Exception {

		URI url = UriComponentsBuilder.fromUriString(synthesizerUrl)
				.queryParam("haal", 15) // 14 <- female voice
				.queryParam("tekst", words)
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
