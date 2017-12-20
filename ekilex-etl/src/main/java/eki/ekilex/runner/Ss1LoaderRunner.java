package eki.ekilex.runner;

import eki.common.data.Count;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.ReportComposer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.replaceChars;

@Component
public class Ss1LoaderRunner extends AbstractLoaderRunner {

	private final static String dataLang = "est";
	private final static String dataset = "ss1";
	private final static String formStrCleanupChars = ".()¤:_|[]̄̆̇’\"'`´,;–+=";
	private final String defaultWordMorphCode = "SgN";

	private final static String sqlWordLexemesByDataset = "select l.* from " + LEXEME + " l where l.word_id = :wordId and l.dataset_code = :dataset";

	private final static String LEXEME_RELATION_BASIC_WORD = "head";

	private final static String ARTICLES_REPORT_NAME = "keywords";
	private final static String BASIC_WORDS_REPORT_NAME = "basic_words";

	private static Logger logger = LoggerFactory.getLogger(PsvLoaderRunner.class);

	private ReportComposer reportComposer;
	private boolean reportingEnabled;

	private Map<String, String> lexemeTypes;

	@Override
	void initialise() throws Exception {
		lexemeTypes = loadClassifierMappingsFor(EKI_CLASSIFIER_LIIKTYYP);
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
			context.importedWords.addAll(newWords);
		}
		logger.debug("total {} articles iterated", articleCounter);

		processBasicWords(context);

		logger.debug("Found {} word duplicates", context.wordDuplicateCount);

		if (reportComposer != null) {
			reportComposer.end();
		}
		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	private void processBasicWords(Context context) throws Exception {

		logger.debug("Found {} basic words.", context.basicWords.size());
		setActivateReport(BASIC_WORDS_REPORT_NAME);
		writeToLogFile("Märksõna põhisõna seoste töötlus <s:ps>", "", "");

		for (WordData basicWord : context.basicWords) {
			List<WordData> existingWords = context.importedWords.stream().filter(w -> basicWord.value.equals(w.value)).collect(Collectors.toList());
			Long wordId = getWordIdFor(basicWord.value, basicWord.homonymNr, existingWords, basicWord.reportingId);
			if (!existingWords.isEmpty() && wordId != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("wordId", basicWord.id);
				params.put("dataset", dataset);
				List<Map<String, Object>> secondaryWordLexemes = basicDbService.queryList(sqlWordLexemesByDataset, params);
				for (Map<String, Object> secondaryWordLexeme : secondaryWordLexemes) {
					params.put("wordId", wordId);
					List<Map<String, Object>> lexemes = basicDbService.queryList(sqlWordLexemesByDataset, params);
					for (Map<String, Object> lexeme : lexemes) {
						createLexemeRelation((Long) secondaryWordLexeme.get("id"), (Long) lexeme.get("id"), LEXEME_RELATION_BASIC_WORD);
					}
				}
			}
		}
		logger.debug("Basic words processing done.");
	}

	private void processArticleContent(String reportingId, Element contentNode, List<WordData> newWords, Context context) {
	}

	private void processArticleHeader(String reportingId, Element headerNode, List<WordData> newWords, Context context,
			Map<String, List<Paradigm>> wordParadigmsMap, String guid) throws Exception {

		final String wordGroupExp = "s:mg";
		final String wordPosCodeExp = "s:sl";
		final String posAsTyypAttr = "as";

		List<Element> wordGroupNodes = headerNode.selectNodes(wordGroupExp);
		for (Element wordGroupNode : wordGroupNodes) {
			WordData wordData = new WordData();
			wordData.reportingId = reportingId;

			Word word = extractWordData(wordGroupNode, wordData, guid);
			if (word != null) {
				List<Paradigm> paradigms = extractParadigms(wordGroupNode, wordData, wordParadigmsMap);
				wordData.id = saveWord(word, paradigms, dataset, context.wordDuplicateCount);
			}

			List<WordData> basicWordsOfTheWord = extractBasicWords(wordGroupNode, wordData.id, reportingId);
			context.basicWords.addAll(basicWordsOfTheWord);

			List<Element> posCodeNodes = wordGroupNode.selectNodes(wordPosCodeExp);
			for (Element posCodeNode : posCodeNodes) {
				PosData posData = new PosData();
				posData.code = posCodeNode.getTextTrim();
				posData.processStateCode = posCodeNode.attributeValue(posAsTyypAttr);
				wordData.posCodes.add(posData);
			}

			newWords.add(wordData);
		}
	}

	private Word extractWordData(Element wordGroupNode, WordData wordData, String guid) throws Exception {

		final String wordExp = "s:m";
		final String wordDisplayMorphExp = "s:vk";
		final String wordVocalFormExp = "s:hld";
		final String homonymNrAttr = "i";
		final String lexemeTypeAttr = "liik";

		Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
		if (wordNode.attributeValue(homonymNrAttr) != null) {
			wordData.homonymNr = Integer.parseInt(wordNode.attributeValue(homonymNrAttr));
		}
		if (wordNode.attributeValue(lexemeTypeAttr) != null) {
			wordData.lexemeType = lexemeTypes.get(wordNode.attributeValue(lexemeTypeAttr));
		}
		String wordDisplayForm = wordNode.getTextTrim();
		String wordValue = cleanUp(wordDisplayForm);
		wordData.value = wordValue;
		int homonymNr = getWordMaxHomonymNr(wordValue, dataLang) + 1;

		String wordVocalForm = null;
		Element vocalFormNode = (Element) wordGroupNode.selectSingleNode(wordVocalFormExp);
		if (vocalFormNode != null) {
			wordVocalForm = vocalFormNode.getTextTrim();
		}

		Word word = new Word(wordValue, dataLang, null, null, wordDisplayForm, wordVocalForm, homonymNr, defaultWordMorphCode, guid);

		Element wordDisplayMorphNode = (Element) wordGroupNode.selectSingleNode(wordDisplayMorphExp);
		if (wordDisplayMorphNode != null) {
			word.setDisplayMorph(wordDisplayMorphNode.getTextTrim());
		}
		return word;
	}

	private List<Paradigm> extractParadigms(Element wordGroupNode, WordData word, Map<String, List<Paradigm>> wordParadigmsMap) {

		final String morphGroupExp = "s:mfp/s:mtg";
		final String inflectionTypeNrExp = "s:mt";

		List<Paradigm> paradigms = new ArrayList<>();
		boolean isAddForms = !wordParadigmsMap.isEmpty();
		List<Element> morphGroupNodes = wordGroupNode.selectNodes(morphGroupExp);
		for (Element morphGroupNode : morphGroupNodes) {
			Element inflectionTypeNrNode = (Element) morphGroupNode.selectSingleNode(inflectionTypeNrExp);
			if (inflectionTypeNrNode != null) {
				Paradigm paradigm = new Paradigm();
				paradigm.setInflectionTypeNr(inflectionTypeNrNode.getTextTrim());
				if (isAddForms) {
					Paradigm paradigmFromMab = fetchParadigmFromMab(word.value, paradigm.getInflectionTypeNr(), morphGroupNode, wordParadigmsMap);
					if (paradigmFromMab != null) {
						paradigm.setForms(paradigmFromMab.getForms());
					}
				}
				paradigms.add(paradigm);
			} else {
				if (isAddForms) {
					Paradigm paradigmFromMab = fetchParadigmFromMab(word.value, null, morphGroupNode, wordParadigmsMap);
					if (paradigmFromMab != null) {
						paradigms.add(paradigmFromMab);
					}
				}
			}
		}
		return paradigms;
	}

	private List<WordData> extractBasicWords(Element node, Long wordId, String reportingId) {

		final String basicWordExp = "s:ps";
		final String homonymNrAttr = "i";

		List<WordData> basicWords = new ArrayList<>();
		List<Element> basicWordNodes = node.selectNodes(basicWordExp);
		for (Element basicWordNode : basicWordNodes) {
			WordData basicWord = new WordData();
			basicWord.id = wordId;
			basicWord.value = cleanUp(basicWordNode.getTextTrim());
			basicWord.reportingId = reportingId;
			if (basicWordNode.attributeValue(homonymNrAttr) != null) {
				basicWord.homonymNr = Integer.parseInt(basicWordNode.attributeValue(homonymNrAttr));
			}
			basicWords.add(basicWord);
		}
		return basicWords;
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

	private Long getWordIdFor(String wordValue, int homonymNr, List<WordData> words, String reportingId) throws Exception {

		Long wordId = null;
		if (words.size() == 1) {
			wordId = words.get(0).id;
		} else if (words.size() > 1) {
			Optional<WordData> matchingWord = words.stream().filter(w -> w.homonymNr == homonymNr).findFirst();
			if (matchingWord.isPresent()) {
				wordId = matchingWord.get().id;
			}
		}
		if (wordId == null) {
			logger.debug("No matching word was found for {} word {}, {}", reportingId, wordValue, homonymNr);
			writeToLogFile(reportingId, "Ei leitud sihtsõna", wordValue + " : " + homonymNr);
		}
		return wordId;
	}

	private Paradigm fetchParadigmFromMab(String wordValue, String inflectionTypeNr, Element node, Map<String, List<Paradigm>> wordParadigmsMap) {

		final String formsNodeExp = "s:mv";

		List<Paradigm> paradigms = wordParadigmsMap.get(wordValue);
		if (CollectionUtils.isEmpty(paradigms)) {
			return null;
		}

		if (isNotEmpty(inflectionTypeNr)) {
			long nrOfParadigmsMatchingInflectionType = paradigms.stream().filter(p -> Objects.equals(p.getInflectionTypeNr(), inflectionTypeNr)).count();
			if (nrOfParadigmsMatchingInflectionType == 1) {
				return paradigms.stream().filter(p -> Objects.equals(p.getInflectionTypeNr(), inflectionTypeNr)).findFirst().get();
			}
		}

		Element formsNode = (Element) node.selectSingleNode(formsNodeExp);
		if (formsNode == null) {
			return null;
		}
		// FIXME: 20.12.2017 its actually lot more complicated logic, change it when we get documentation about it
		List<String> formValues = Arrays.stream(formsNode.getTextTrim().split(",")).map(String::trim).collect(Collectors.toList());
		List<String> mabFormValues;
		Collection<String> formValuesIntersection;
		int bestFormValuesMatchCount = 0;
		Paradigm matchingParadigm = null;
		for (Paradigm paradigm : paradigms) {
			mabFormValues = paradigm.getFormValues();
			formValuesIntersection = CollectionUtils.intersection(formValues, mabFormValues);
			if (formValuesIntersection.size() > bestFormValuesMatchCount) {
				bestFormValuesMatchCount = formValuesIntersection.size();
				matchingParadigm = paradigm;
			}
		}
		return matchingParadigm;
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

	private void setActivateReport(String reportName) {
		if (reportComposer != null) {
			reportComposer.setActiveStream(reportName);
		}
	}

	private class WordData {
		Long id;
		String value;
		int homonymNr = 0;
		String reportingId;
		String lexemeType;
		List<PosData> posCodes = new ArrayList<>();
	}

	private class PosData {
		String code;
		String processStateCode;
	}

	private class Context {
		List<WordData> importedWords = new ArrayList<>();
		List<WordData> basicWords = new ArrayList<>();
		Count wordDuplicateCount = new Count();
	}

}
