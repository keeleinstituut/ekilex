package eki.wordweb.service;

import eki.wordweb.data.CorporaSentence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class CorporaServiceEst extends CorporaService {

	@Value("${corpora.service.est.url:}")
	private String serviceUrl;

	@Value("${corpora.service.est.corpname:}")
	private String corpNames;

	private Random seedGenerator = new Random();

	protected URI composeCorporaUri(String sentence) {

		if (isBlank(serviceUrl)) {
			return null;
		} else {
			return UriComponentsBuilder.fromUriString(serviceUrl)
					.queryParam("command", "query")
					.queryParam("corpus", corpNames)
					.queryParam("start", 0)
					.queryParam("end", 25)
					.queryParam("cqp", parseSentenceToQueryString(sentence))
					.queryParam("defaultcontext", "1+sentence")
					.queryParam("show", "pos")
					.queryParam("sort", "random")
					.queryParam("random_seed", abs(seedGenerator.nextInt()))
					.build()
					.toUri();
		}
	}

	protected List<CorporaSentence> parseResponse(Map<String, Object> response) {

		List<CorporaSentence> sentences = new ArrayList<>();
		if (response.isEmpty() || (response.containsKey("hits") && (int) response.get("hits") == 0) || !response.containsKey("kwic")) {
			return sentences;
		}
		for (Map<String, Object> kwic : (List<Map<String, Object>>) response.get("kwic")) {
			Map<String, Object> match = (Map<String, Object>) kwic.get("match");
			int startPos = (int) match.get("start");
			int endPos = (int) match.get("end");
			int index = 0;
			CorporaSentence sentence = new CorporaSentence();
			for (Map<String, Object> token : (List<Map<String, Object>>) kwic.get("tokens")) {
				String word = parseWord(token);
				if (index < startPos) {
					sentence.setLeftPart(sentence.getLeftPart() + word);
				} else if (index >= endPos) {
					sentence.setRightPart(sentence.getRightPart() + word);
				} else {
					sentence.setMiddlePart(sentence.getMiddlePart() + word);
				}
				index++;
			}
			sentences.add(sentence);
		}
		return sentences;
	}

	private String parseWord(Map<String, Object> token) {
		String word = (String) token.get("word");
		String pos = (String) token.get("pos");
		word = isPunctuation(pos) ? word : " " + word;
		return word;
	}

	private boolean isPunctuation(String word) {
		return "Z".equals(word);
	}

}
