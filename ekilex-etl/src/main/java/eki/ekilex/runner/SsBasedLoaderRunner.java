package eki.ekilex.runner;

import eki.common.data.Count;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Government;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.MabService;
import eki.ekilex.service.ReportComposer;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.removePattern;
import static org.apache.commons.lang3.StringUtils.replaceChars;

public abstract class SsBasedLoaderRunner extends AbstractLoaderRunner {

	private final static String formStrCleanupChars = ".()¤:_|[]̄̆̇’\"'`´–+=";
	protected final static String defaultWordMorphCode = "??";
	protected final static String dataLang = "est";
	protected final static String latinLang = "lat";

	private static Logger logger = LoggerFactory.getLogger(SsBasedLoaderRunner.class);

	protected ReportComposer reportComposer;
	protected boolean reportingEnabled;
	protected boolean reportingPaused;

	protected Map<String, String> wordTypes;
	protected Map<String, String> displayMorpCodes;
	protected Map<String, String> frequencyGroupCodes;

	protected abstract Map<String,String> xpathExpressions();

	@Autowired
	private MabService mabService;

	protected List<Paradigm> extractParadigms(Element wordGroupNode, WordData word) throws Exception {

		String morphGroupExp = xpathExpressions().get("morphGroup");

		List<Paradigm> paradigms = new ArrayList<>();
		if (mabService.isMabLoaded() && mabService.paradigmsExist(word.value)) {
			Element morphGroupNode = morphGroupExp == null ? null : (Element) wordGroupNode.selectSingleNode(morphGroupExp);
			List<Paradigm> paradigmsFromMab = fetchParadigmsFromMab(word.value, morphGroupNode);
			if (!paradigmsFromMab.isEmpty()) {
				paradigms.addAll(paradigmsFromMab);
			}
		}
		return paradigms;
	}

	private List<Paradigm> fetchParadigmsFromMab(String wordValue, Element node) throws Exception {

		final String formsNodeExp = xpathExpressions().get("formsNode");
		final String formsNodeExp2 = xpathExpressions().get("formsNode2");

		if (mabService.isSingleHomonym(wordValue)) {
			return mabService.getWordParadigms(wordValue);
		}

		List<String> formEndings = extractFormEndings(node, formsNodeExp);
		formEndings.addAll(extractFormEndings(node, formsNodeExp2));
		if (formEndings.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> morphCodesToCheck = asList("SgG", "Inf", "IndPrSg1");
		long bestFormValuesMatchCount = -1;
		Paradigm matchingParadigm = null;
		for (Paradigm paradigm : mabService.getWordParadigms(wordValue)) {
			long numberOfMachingEndings = paradigm.getForms().stream()
					.filter(form -> morphCodesToCheck.contains(form.getMorphCode())).map(Form::getValue)
					.filter(formValue -> formEndings.stream().anyMatch(formValue::endsWith))
					.count();
			if (numberOfMachingEndings > bestFormValuesMatchCount) {
				bestFormValuesMatchCount = numberOfMachingEndings;
				matchingParadigm = paradigm;
			}
		}
		Integer matchingHomonymNumber = matchingParadigm.getHomonymNr();
		return mabService.getWordParadigmsForHomonym(wordValue, matchingHomonymNumber);
	}

	private List<String> extractFormEndings(Element node, String formsNodeExp) {

		List<String> formEndings = new ArrayList<>();
		if (node == null || formsNodeExp == null) {
			return formEndings;
		}

		Element formsNode = (Element) node.selectSingleNode(formsNodeExp);
		if (formsNode != null) {
			formEndings.addAll(Arrays.stream(formsNode.getTextTrim().split(","))
					.map(v -> v.substring(v.indexOf("-")+1).trim())
					.collect(Collectors.toList()));
		}

		return formEndings;
	}

	protected Word extractWordData(Element wordGroupNode, WordData wordData, String guid) throws Exception {

		String wordExp = xpathExpressions().get("word");//		final String wordExp = "s:m";
		String wordDisplayMorphExp = xpathExpressions().get("wordDisplayMorph");//	final String wordDisplayMorphExp = "s:vk";
		String wordVocalFormExp = xpathExpressions().get("wordVocalForm"); //final String wordVocalFormExp = "s:hld";
		String homonymNrAttr = "i";
		String wordTypeAttr = "liik";
		String wordFrequencyGroupExp = xpathExpressions().get("wordFrequencyGroup"); //final String wordFrequencyGroupExp = "s:msag";

		Element wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
		if (wordNode.attributeValue(homonymNrAttr) != null) {
			wordData.homonymNr = Integer.parseInt(wordNode.attributeValue(homonymNrAttr));
		}
		if (wordNode.attributeValue(wordTypeAttr) != null) {
			wordData.wordType = wordTypes.get(wordNode.attributeValue(wordTypeAttr));
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

		Word word = new Word(wordValue, dataLang, null, null, wordDisplayForm, wordVocalForm, homonymNr, defaultWordMorphCode, guid, wordData.wordType);

		Element wordDisplayMorphNode = (Element) wordGroupNode.selectSingleNode(wordDisplayMorphExp);
		if (wordDisplayMorphNode != null) {
			word.setDisplayMorph(displayMorpCodes.get(wordDisplayMorphNode.getTextTrim()));
			if (displayMorpCodes.get(wordDisplayMorphNode.getTextTrim()) == null) {
				logger.warn("Unknown display morph code : {} : {}", wordDisplayMorphNode.getTextTrim(), wordValue);
			}
		}
		if (wordFrequencyGroupExp != null) {
			Element frequencyGroupNode = (Element) wordGroupNode.selectSingleNode(wordFrequencyGroupExp);
			if (frequencyGroupNode != null) {
				wordData.frequencyGroup = frequencyGroupCodes.get(frequencyGroupNode.getTextTrim());
			}
		}
		wordData.grammars = extractGrammar(wordGroupNode);
		return word;
	}

	protected List<String> extractGrammar(Element node) {
		String grammarValueExp = xpathExpressions().get("reportingId");
		return extractValuesAsStrings(node, grammarValueExp);
	}

	protected List<PosData> extractPosCodes(Element node, String wordPosCodeExp) {

		final String asTyypAttr = "as";

		List<PosData> posCodes = new ArrayList<>();
		List<Element> posCodeNodes = node.selectNodes(wordPosCodeExp);
		for (Element posCodeNode : posCodeNodes) {
			PosData posData = new PosData();
			posData.code = posCodeNode.getTextTrim();
			posData.processStateCode = posCodeNode.attributeValue(asTyypAttr);
			posCodes.add(posData);
		}
		return posCodes;
	}

	protected String extractReporingId(Element node) {

		String reportingIdExp = xpathExpressions().get("reportingId");

		Element reportingIdNode = (Element) node.selectSingleNode(reportingIdExp);
		String reportingId = reportingIdNode != null ? cleanUp(reportingIdNode.getTextTrim()) : "";
		return reportingId;
	}


	protected List<String> extractValuesAsStrings(Element node, String valueExp) {

		List<String> values = new ArrayList<>();
		List<Element> valueNodes = node.selectNodes(valueExp);
		for (Element valueNode : valueNodes) {
			if (!isRestricted(valueNode)) {
				String value = valueNode.getTextTrim();
				values.add(value);
			}
		}
		return values;
	}

	protected boolean isRestricted(Element node) {

		final String restrictedAttr = "as";
		String restrictedValue = node.attributeValue(restrictedAttr);
		return asList("ab", "ap").contains(restrictedValue);
	}

	protected String cleanUp(String value) {
		String cleanedWord = replaceChars(value, formStrCleanupChars, "");
		// FIXME: quick fix for removing subscript tags, better solution would be to use some markup for mathematical and chemical formulas
		return removePattern(cleanedWord, "[&]\\w+[;]");
	}

	protected void writeToLogFile(String reportingId, String message, String values) throws Exception {
		writeToLogFile(null, reportingId, message, values);
	}

	protected void writeToLogFile(String reportFile, String reportingId, String message, String values) throws Exception {
		if (reportingEnabled && !reportingPaused) {
			String logMessage = String.join(String.valueOf(CSV_SEPARATOR), asList(reportingId, message, values));
			if (reportFile == null) {
				reportComposer.append(logMessage);
			} else {
				reportComposer.append(reportFile, logMessage);
			}
		}
	}

	protected void setActivateReport(String reportName) {
		if (reportComposer != null) {
			reportComposer.setActiveStream(reportName);
		}
	}

	protected class CommentData {
		String value;
		String author;
		String createdAt;
	}

	protected class WordData {
		Long id;
		String value;
		int homonymNr = 0;
		String reportingId;
		String wordType;
		List<PosData> posCodes = new ArrayList<>();
		String frequencyGroup;
		List<String> grammars = new ArrayList<>();
		Long meaningId;
		String government;
		String displayMorph;
	}

	protected class PosData {
		String code;
		String processStateCode;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			PosData posData = (PosData) o;
			return Objects.equals(code, posData.code);
		}

		@Override
		public int hashCode() {
			return Objects.hash(code);
		}
	}

	protected class WordToMeaningData {
		String word;
		int homonymNr = 0;
		int lexemeLevel1 = 1;
		Long meaningId;
		List<String> meaningDefinitions = new ArrayList<>();
		String meaningWord;
		int meaningHomonymNr = 0;
		int meaningLevel1 = 1;
	}

	protected class LexemeToWordData {
		Long lexemeId;
		String word;
		String displayForm;
		int lexemeLevel1 = 1;
		int homonymNr = 0;
		String relationType;
		Government government;
		List<Usage> usages = new ArrayList<>();
		String reportingId;
		String wordType;
		Long meaningId;

		LexemeToWordData copy() {
			LexemeToWordData newData = new LexemeToWordData();
			newData.lexemeId = this.lexemeId;
			newData.word = this.word;
			newData.displayForm = this.displayForm;
			newData.lexemeLevel1 = this.lexemeLevel1;
			newData.homonymNr = this.homonymNr;
			newData.relationType = this.relationType;
			newData.government = this.government;
			newData.reportingId = this.reportingId;
			newData.usages.addAll(this.usages);
			newData.wordType = this.wordType;
			newData.meaningId = this.meaningId;
			return newData;
		}
	}

	protected class Context {
		List<WordData> importedWords = new ArrayList<>();
		List<WordData> basicWords = new ArrayList<>();
		List<WordData> subWords = new ArrayList<>();
		List<WordData> derivativeWords = new ArrayList<>();
		Count wordDuplicateCount = new Count();
		List<LexemeToWordData> synonyms = new ArrayList<>();
		List<WordToMeaningData> antonyms = new ArrayList<>();
		List<LexemeToWordData> abbreviations = new ArrayList<>();
		List<LexemeToWordData> abbreviationFullWords = new ArrayList<>();
		List<WordToMeaningData> cohyponyms = new ArrayList<>();
		List<LexemeToWordData> tokens = new ArrayList<>();
		List<LexemeToWordData> formulas = new ArrayList<>();
		List<LexemeToWordData> latinTermins = new ArrayList<>();
		List<WordToMeaningData> meanings = new ArrayList<>();
	}

}
