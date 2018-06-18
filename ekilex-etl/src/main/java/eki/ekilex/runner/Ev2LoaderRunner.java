package eki.ekilex.runner;

import javax.transaction.Transactional;

import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Word;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
public class Ev2LoaderRunner extends SsBasedLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(Ev2LoaderRunner.class);

	@Override
	void initialise() throws Exception {
		wordTypes = loadClassifierMappingsFor(EKI_CLASSIFIER_LIIKTYYP);
		displayMorpCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_VKTYYP);
		frequencyGroupCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_MSAGTYYP);
	}

	@Override
	protected Map<String, String> xpathExpressions() {
		Map<String, String> experssions = new HashMap<>();
		experssions.put("reportingId", "x:P/x:mg/x:m"); // use first word as id for reporting
		experssions.put("word", "x:m");
		experssions.put("wordDisplayMorph", "x:vk");
		experssions.put("wordVocalForm", "x:hld");
		experssions.put("grammarValue", "x:gki");
		return experssions;
	}

	@Override
	String getDataset() {
		return "ev2";
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataXmlFilePath2, boolean doReports) throws Exception {

		long t1, t2;
		t1 = System.currentTimeMillis();
		logger.debug("Loading EV2...");

		reportingEnabled = doReports;
		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		Document dataDoc2 = xmlReader.readDocument(dataXmlFilePath2);
		Element rootElement = dataDoc.getRootElement();
		Element rootElement2 = dataDoc2.getRootElement();

		writeToLogFile("Artiklite töötlus", "", "");
		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(o -> o instanceof Element).collect(toList());
		articleNodes.addAll((List<Element>) rootElement2.content().stream().filter(o -> o instanceof Element).collect(toList()));

		long articleCount = articleNodes.size();
		logger.debug("{} articles found", articleCount);

		Context context = new Context();
		long progressIndicator = articleCount / Math.min(articleCount, 100);
		long articleCounter = 0;
		for (Element articleNode : articleNodes) {
			processArticle(articleNode, context);
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}
		logger.debug("total {} articles iterated", articleCounter);

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	@Transactional
	void processArticle(Element articleNode, Context context) throws Exception {

		final String articleHeaderExp = "x:P";
		final String articleBodyExp = "x:S";

		String reportingId = extractReporingId(articleNode);
		List<WordData> newWords = new ArrayList<>();

		Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
		processArticleHeader(reportingId, headerNode, newWords, context, null);

		Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
		if (contentNode != null) {
			processArticleContent(reportingId, contentNode, newWords, context);
		}
		context.importedWords.addAll(newWords);
	}

	private void processArticleHeader(String reportingId, Element headerNode, List<WordData> newWords, Context context, String guid) throws Exception {
		final String wordGroupExp = "x:mg";
		final String wordPosCodeExp = "x:sl";
		final String wordGrammarPosCodesExp = "x:grg/x:sl";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();
			wordData.reportingId = reportingId;

			Word word = extractWordData(wordGroupNode, wordData, guid);
			if (word != null) {
				List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData);
				wordData.id = createWord(word, paradigms, getDataset(), context.wordDuplicateCount);
			}

			List<PosData> posCodes = extractPosCodes(wordGroupNode, wordPosCodeExp);
			wordData.posCodes.addAll(posCodes);
			posCodes = extractPosCodes(wordGroupNode, wordGrammarPosCodesExp);
			wordData.posCodes.addAll(posCodes);

			newWords.add(wordData);
		}
	}

	private void processArticleContent(String reportingId, Element contentNode, List<WordData> newWords, Context context) {
	}

}
