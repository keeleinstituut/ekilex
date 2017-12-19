package eki.ekilex.runner;

import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.service.ReportComposer;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.replaceChars;

@Component
public class Ss1LoaderRunner extends AbstractLoaderRunner {

	private final static String dataLang = "est";
	private final static String dataset = "ss1";
	private final static String formStrCleanupChars = ".()¤:_|[]/̄̆̇’\"'`´,;–+=";

	private final static String ARTICLES_REPORT_NAME = "keywords";

	private static Logger logger = LoggerFactory.getLogger(PsvLoaderRunner.class);

	private ReportComposer reportComposer;
	private boolean reportingEnabled;

	@Override
	void initialise() {
	}

	@Transactional
	public void execute(
			String dataXmlFilePath,
			Map<String, List<Paradigm>> wordParadigmsMap,
			boolean isAddReporting) throws Exception {

		final String articleHeaderExp = "s:P";
		final String articleBodyExp = "s:S";

		logger.info("Starting import");
		long t1, t2;
		t1 = System.currentTimeMillis();

		reportingEnabled = isAddReporting;
		if (reportingEnabled) {
			reportComposer = new ReportComposer("SS1 import", ARTICLES_REPORT_NAME);
		}

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();

		long articleCount = rootElement.content().stream().filter(o -> o instanceof Element).count();
		long progressIndicator = articleCount / Math.min(articleCount, 100);
		long articleCounter = 0;
		logger.debug("{} articles found", articleCount);

		Context context = new Context();

		writeToLogFile("Artiklite töötlus", "", "");
		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(o -> o instanceof Element).collect(toList());
		for (Element articleNode : articleNodes) {
			String guid = extractGuid(articleNode);
			String reportingId = extractReporingId(articleNode);
			List<WordData> newWords = new ArrayList<>();

			Element headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			processArticleHeader(reportingId, headerNode, newWords, context, wordParadigmsMap, guid);

			Element contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode != null) {
				processArticleContent(reportingId, contentNode, newWords, context);
			}
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}
		logger.debug("{} articles iterated", articleCounter);

		if (reportComposer != null) {
			reportComposer.end();
		}
		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	private void processArticleHeader(String reportingId, Element headerNode, List<WordData> newWords, Context context,
			Map<String, List<Paradigm>> wordParadigmsMap, String guid) {
	}

	private void processArticleContent(String reportingId, Element contentNode, List<WordData> newWords, Context context) {
	}

	private String extractGuid(Element node) {

		final String articleGuidExp = "s:G";

		Element guidNode = (Element) node.selectSingleNode(articleGuidExp);
		return guidNode != null ? StringUtils.lowerCase(guidNode.getTextTrim()) : null;
	}

	private String extractReporingId(Element node) {

		final String reportingIdExp = "s:P/s:mg/s:m"; // use first word as id for reporting

		Element reportingIdNode = (Element) node.selectSingleNode(reportingIdExp);
		String reportingId = reportingIdNode != null ? cleanUp(reportingIdNode.getTextTrim()) : "";
		return reportingId;
	}

	private String cleanUp(String value) {
		return replaceChars(value, formStrCleanupChars, "");
	}

	private void writeToLogFile(String reportingId, String message, String values) throws Exception {
		if (reportingEnabled) {
			String logMessage = String.join(String.valueOf(CSV_SEPARATOR), asList(reportingId, message, values));
			reportComposer.append(logMessage);
		}
	}

	private class WordData {
		Long id;
		String value;
		int homonymNr = 0;
		String reportingId;
	}

	private class Context {
		List<WordData> importedWords = new ArrayList<>();
	}

}

