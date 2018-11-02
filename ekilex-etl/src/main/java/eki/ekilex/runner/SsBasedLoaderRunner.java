package eki.ekilex.runner;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.replaceChars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ClassifierName;
import eki.common.data.Count;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.data.transform.Usage;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.MabService;
import eki.ekilex.service.ReportComposer;

public abstract class SsBasedLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(SsBasedLoaderRunner.class);

	private final static String formStrCleanupChars = ".()¤:_|[]̄̆̇\"`´–+=*";
	protected final static String defaultWordMorphCode = "??";
	protected final static String dataLang = "est";
	protected final static String latinLang = "lat";

	protected final static String LEXEME_RELATION_SUB_WORD = "mm";

	protected final static String ARTICLES_REPORT_NAME = "keywords";
	protected final static String DESCRIPTIONS_REPORT_NAME = "keywords_descriptions";
	protected final static String MEANINGS_REPORT_NAME = "keywords_meanings";

	protected ReportComposer reportComposer;
	protected boolean reportingEnabled;
	protected boolean reportingPaused;

	protected Map<String, String> wordTypes;
	protected Map<String, String> displayMorpCodes;
	protected Map<String, String> frequencyGroupCodes;
	protected Map<String, String> posCodes;
	protected Map<String, String> processStateCodes;
	protected Map<String, String> registerCodes;

	protected abstract Map<String, String> xpathExpressions();

	@Autowired
	private MabService mabService;

	@Override
	void initialise() throws Exception {
		wordTypes = loadClassifierMappingsFor(EKI_CLASSIFIER_LIIKTYYP, ClassifierName.WORD_TYPE.name());
		displayMorpCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_VKTYYP, ClassifierName.DISPLAY_MORPH.name());
		frequencyGroupCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_MSAGTYYP);
		posCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_SLTYYP);
		processStateCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_ASTYYP);
		registerCodes = loadClassifierMappingsFor(EKI_CLASSIFIER_STYYP);
	}

	protected void saveRegisters(Long lexemeId, List<String> lexemeRegisterCodes, String reportingId) throws Exception {
		for (String registerCode : lexemeRegisterCodes) {
			String ekilexRegisterCode = registerCodes.get(registerCode);
			if (ekilexRegisterCode == null) {
				writeToLogFile(reportingId, "Tundmatu registri kood", registerCode);
			} else {
				createLexemeRegister(lexemeId, ekilexRegisterCode);
			}
		}
	}

	protected WordToMeaningData findExistingMeaning(Context context, WordData newWord, int level1, List<LexemeToWordData> connectedWords,
			List<String> definitions) {

		List<String> connectedWordValues = connectedWords.stream().map(w -> w.word).collect(toList());
		List<WordToMeaningData> existingMeanings = context.meanings.stream()
				.filter(cachedMeaning -> newWord.value.equals(cachedMeaning.word) &&
						newWord.homonymNr == cachedMeaning.homonymNr &&
						level1 == cachedMeaning.lexemeLevel1 &&
						connectedWordValues.contains(cachedMeaning.meaningWord))
				.collect(toList());
		Optional<WordToMeaningData> existingMeaning;
		if (existingMeanings.size() == 1) {
			return existingMeanings.get(0);
		} else {
			existingMeaning = existingMeanings.stream().filter(meaning -> meaning.meaningDefinitions.containsAll(definitions)).findFirst();
		}
		if (!existingMeaning.isPresent() && !connectedWords.isEmpty()) {
			LexemeToWordData connectedWord = connectedWords.get(0);
			existingMeaning = context.meanings.stream()
					.filter(cachedMeaning -> connectedWord.word.equals(cachedMeaning.meaningWord) &&
							connectedWord.homonymNr == cachedMeaning.meaningHomonymNr &&
							connectedWord.lexemeLevel1 == cachedMeaning.meaningLevel1)
					.findFirst();
		}
		return existingMeaning.orElse(null);
	}

	protected boolean validateMeaning(WordToMeaningData meaningData, List<String> definitions, String reportingId) throws Exception {

		if (meaningData.meaningDefinitions.isEmpty() || definitions.isEmpty()) {
			return true;
		}
		String definition = definitions.isEmpty() ? null : definitions.get(0);
		String meaningDefinition = meaningData.meaningDefinitions.isEmpty() ? null : meaningData.meaningDefinitions.get(0);
		if (Objects.equals(meaningDefinition, definition)) {
			return true;
		}
		//		logger.debug("meanings do not match for word {} | {} | {}", reportingId, definition, meaningDefinition);
		writeToLogFile(MEANINGS_REPORT_NAME, reportingId, "Tähenduse seletused on erinevad", definition + " : " + meaningDefinition);
		return false;
	}

	protected void processLatinTerms(Context context) throws Exception {

		logger.debug("Found {} latin terms <s:ld>.", context.latinTermins.size());
		logger.debug("Processing started.");
		reportingPaused = true;

		Count newLatinTermWordCount = processLexemeToWord(context, context.latinTermins, null, "Ei leitud ladina terminit, loome uue", latinLang);

		reportingPaused = false;
		logger.debug("Latin terms created {}", newLatinTermWordCount.getValue());
		logger.debug("Latin term import done.");
	}

	protected Count processLexemeToWord(Context context, List<LexemeToWordData> items, String defaultWordType, String logMessage, String lang) throws Exception {
		Count newWordCount = new Count();
		for (LexemeToWordData itemData : items) {
			Long wordId;
			int level1 = itemData.lexemeLevel1;
			boolean addLexeme = true;
			Optional<WordData> existingWord = context.importedWords.stream()
					.filter(w -> itemData.word.equals(w.value) && lang.equals(w.language) && itemData.homonymNr == w.homonymNr)
					.findFirst();
			if (!existingWord.isPresent()) {
				String wordType = defaultWordType == null ? itemData.wordType : defaultWordType;
				WordData newWord = createDefaultWordFrom(itemData.word, itemData.displayForm, lang, null, wordType, null);
				context.importedWords.add(newWord);
				newWordCount.increment();
				wordId = newWord.id;
				if (!reportingPaused) {
					logger.debug("new word created : {}", itemData.word);
				}
				writeToLogFile(itemData.reportingId, logMessage, itemData.word);
			} else {
//				logger.debug("word found ; {}", existingWord.get().value);
				wordId = existingWord.get().id;
				if (hasLexemeForMeaning(wordId, itemData.meaningId)) {
					addLexeme = false;
				} else {
					existingWord.get().level1++;
					level1 = existingWord.get().level1;
				}
			}
			if (addLexeme) {
				Lexeme lexeme = new Lexeme();
				lexeme.setWordId(wordId);
				lexeme.setMeaningId(itemData.meaningId);
				lexeme.setLevel1(level1);
				lexeme.setLevel2(1);
				lexeme.setLevel3(1);
				createLexeme(lexeme, getDataset());
			}
		}
		return newWordCount;
	}

	private boolean hasLexemeForMeaning(Long wordId, Long meaningId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("word_id", wordId);
		params.put("meaning_id", meaningId);
		Map<String, Object> result = basicDbService.select(LEXEME, params);
		return MapUtils.isNotEmpty(result);
	}

	protected void processDomains(Node node, Long meaningId, List<String> additionalDomains) throws Exception {

		final String domainOrigin = "bolan";
		final String domainExp = xpathExpressions().get("domain");

		List<String> domainCodes = node == null ? new ArrayList<>() : extractValuesAsStrings(node, domainExp);
		if (additionalDomains != null) {
			domainCodes.addAll(additionalDomains);
		}
		for (String domainCode : domainCodes) {
			Map<String, Object> params = new HashMap<>();
			params.put("meaning_id", meaningId);
			params.put("domain_code", domainCode);
			params.put("domain_origin", domainOrigin);
			basicDbService.createIfNotExists(MEANING_DOMAIN, params);
		}
	}

	protected WordData createDefaultWordFrom(String wordValue, String displayForm, String lang, String displayMorph, String wordType, String aspectType) throws Exception {

		int homonymNr = getWordMaxHomonymNr(wordValue, lang) + 1;
		Word word = new Word(wordValue, lang, null, null, displayForm, null, homonymNr, defaultWordMorphCode, null, wordType);
		word.setDisplayMorph(displayMorph);
		word.setAspectTypeCode(aspectType);
		WordData createdWord = new WordData();
		createdWord.value = wordValue;
		createdWord.displayForm = displayForm;
		createdWord.language = lang;
		createdWord.id = createOrSelectWord(word, null, getDataset(), null);
		return createdWord;
	}

	protected List<LexemeToWordData> extractLexemeMetadata(Node node, String lexemeMetadataExp, String relationTypeAttr, String reportingId) throws Exception {

		final String lexemeLevel1Attr = "t";
		final String homonymNrAttr = "i";
		final String wordTypeAttr = "liik";
		final int defaultLexemeLevel1 = 1;

		List<LexemeToWordData> metadataList = new ArrayList<>();
		List<Node> metadataNodes = node.selectNodes(lexemeMetadataExp);
		for (Node metadataNode : metadataNodes) {
			if (isRestricted(metadataNode)) {
				continue;
			}
			Element metadataElement = (Element) metadataNode;
			LexemeToWordData lexemeMetadata = new LexemeToWordData();
			lexemeMetadata.displayForm = metadataElement.getTextTrim();
			lexemeMetadata.word = cleanUp(lexemeMetadata.displayForm);
			lexemeMetadata.reportingId = reportingId;
			String lexemeLevel1AttrValue = metadataElement.attributeValue(lexemeLevel1Attr);
			if (StringUtils.isBlank(lexemeLevel1AttrValue)) {
				lexemeMetadata.lexemeLevel1 = defaultLexemeLevel1;
			} else {
				lexemeMetadata.lexemeLevel1 = Integer.parseInt(lexemeLevel1AttrValue);
			}
			String homonymNrAttrValue = metadataElement.attributeValue(homonymNrAttr);
			if (StringUtils.isNotBlank(homonymNrAttrValue)) {
				lexemeMetadata.homonymNr = Integer.parseInt(homonymNrAttrValue);
			}
			if (relationTypeAttr != null) {
				lexemeMetadata.relationType = metadataElement.attributeValue(relationTypeAttr);
			}
			String wordTypeAttrValue = metadataElement.attributeValue(wordTypeAttr);
			if (StringUtils.isNotBlank(wordTypeAttrValue)) {
				lexemeMetadata.wordType = wordTypes.get(wordTypeAttrValue);
				if (lexemeMetadata.wordType == null) {
					logger.debug("unknown lexeme type {}", wordTypeAttrValue);
					writeToLogFile(reportingId, "Tundmatu märksõnaliik", wordTypeAttrValue);
				}
			}
			metadataList.add(lexemeMetadata);
		}
		return metadataList;
	}

	protected List<Paradigm> extractParadigms(Node wordGroupNode, WordData word) throws Exception {

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
					.map(v -> v.substring(v.indexOf("-") + 1).trim())
					.collect(Collectors.toList()));
		}

		return formEndings;
	}

	protected Word extractWordData(Node wordGroupNode, WordData wordData, String guid, int index) throws Exception {

		String wordExp = xpathExpressions().get("word");//		final String wordExp = "s:m";
		String wordDisplayMorphExp = xpathExpressions().get("wordDisplayMorph");//	final String wordDisplayMorphExp = "s:vk";
		String wordVocalFormExp = xpathExpressions().get("wordVocalForm"); //final String wordVocalFormExp = "s:hld";
		String homonymNrAttr = "i";
		String wordTypeAttr = "liik";
		String wordFrequencyGroupExp = xpathExpressions().get("wordFrequencyGroup"); //final String wordFrequencyGroupExp = "s:msag";

		Element wordNode = (Element) wordGroupNode.selectNodes(wordExp).get(index);
		if (wordNode.attributeValue(homonymNrAttr) != null) {
			wordData.homonymNr = Integer.parseInt(wordNode.attributeValue(homonymNrAttr));
		}
		if (wordNode.attributeValue(wordTypeAttr) != null) {
			wordData.wordType = wordTypes.get(wordNode.attributeValue(wordTypeAttr));
		}
		String wordDisplayForm = wordNode.getTextTrim();
		String wordValue = cleanUp(wordDisplayForm);
		wordData.value = wordValue;
		wordData.language = dataLang;
		int homonymNr = getWordMaxHomonymNr(wordValue, dataLang) + 1;

		String wordVocalForm = null;
		if (index == 0) {
			Element vocalFormNode = (Element) wordGroupNode.selectSingleNode(wordVocalFormExp);
			if (vocalFormNode != null) {
				wordVocalForm = vocalFormNode.getTextTrim();
			}
		}

		Word word = new Word(wordValue, dataLang, null, null, wordDisplayForm, wordVocalForm, homonymNr, defaultWordMorphCode, guid, wordData.wordType);

		Element wordDisplayMorphNode = (Element) wordGroupNode.selectSingleNode(wordDisplayMorphExp);
		if (wordDisplayMorphNode != null) {
			word.setDisplayMorph(displayMorpCodes.get(wordDisplayMorphNode.getTextTrim()));
			if (displayMorpCodes.get(wordDisplayMorphNode.getTextTrim()) == null && index == 0) {
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

	protected List<String> extractGrammar(Node node) {
		String grammarValueExp = xpathExpressions().get("grammarValue");
		return extractValuesAsStrings(node, grammarValueExp);
	}

	protected List<PosData> extractPosCodes(Node node, String wordPosCodeExp) {

		final String asTyypAttr = "as";

		List<PosData> posCodes = new ArrayList<>();
		List<Node> posCodeNodes = node.selectNodes(wordPosCodeExp);
		for (Node posCodeNode : posCodeNodes) {
			Element posCodeElement = (Element) posCodeNode;
			PosData posData = new PosData();
			posData.code = posCodeElement.getTextTrim();
			posData.processStateCode = posCodeElement.attributeValue(asTyypAttr);
			posCodes.add(posData);
		}
		return posCodes;
	}

	protected String extractReporingId(Node node) {

		String reportingIdExp = xpathExpressions().get("reportingId");

		Element reportingIdNode = (Element) node.selectSingleNode(reportingIdExp);
		String reportingId = reportingIdNode != null ? cleanUp(reportingIdNode.getTextTrim()) : "";
		return reportingId;
	}

	protected String extractGuid(Node node, String articleGuidExp) {
		Element guidNode = (Element) node.selectSingleNode(articleGuidExp);
		return guidNode != null ? StringUtils.lowerCase(guidNode.getTextTrim()) : null;
	}

	protected List<String> extractValuesAsStrings(Node node, String valueExp) {

		List<String> values = new ArrayList<>();
		List<Node> valueNodes = node.selectNodes(valueExp);
		for (Node valueNode : valueNodes) {
			if (!isRestricted(valueNode)) {
				String value = ((Element)valueNode).getTextTrim();
				value = cleanEkiEntityMarkup(value);
				values.add(value);
			}
		}
		return values;
	}

	protected boolean isRestricted(Node node) {

		final String restrictedAttr = "as";
		String restrictedValue = ((Element)node).attributeValue(restrictedAttr);
		return asList("ab", "ap").contains(restrictedValue);
	}

	protected String cleanUp(String value) {
		String cleanedWord = cleanEkiEntityMarkup(value);
		cleanedWord = replaceChars(cleanedWord, formStrCleanupChars, "");
		return cleanedWord;
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

	protected List<WordToMeaningData> convertToMeaningData(List<LexemeToWordData> items, WordData meaningWord, int level1, List<String> definitions) {

		List<WordToMeaningData> meanings = new ArrayList<>();
		for (LexemeToWordData item : items) {
			WordToMeaningData meaning = new WordToMeaningData();
			meaning.meaningId = item.meaningId;
			meaning.meaningWord = meaningWord.value;
			meaning.meaningHomonymNr = meaningWord.homonymNr;
			meaning.meaningLevel1 = level1;
			meaning.meaningDefinitions.addAll(definitions);
			meaning.word = item.word;
			meaning.homonymNr = item.homonymNr;
			meaning.lexemeLevel1 = item.lexemeLevel1;
			meanings.add(meaning);
		}
		return meanings;
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
		List<String> governments = new ArrayList<>();
		String displayMorph;
		int level1 = 1;
		String language;
		String displayForm;
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
		String government;
		List<Usage> usages = new ArrayList<>();
		String reportingId;
		String wordType;
		Long meaningId;
		String register;
		String aspect;
		Long wordId;

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
			newData.register = this.register;
			newData.aspect = this.aspect;
			newData.wordId = this.wordId;
			return newData;
		}
	}

	protected class WordSeries {
		Long groupId;
		List<WordData> words = new ArrayList<>();
	}

	protected class Context {
		Count ssWordCount = new Count();
		Count reusedWordCount = new Count();
		List<WordData> importedWords = new ArrayList<>();
		List<WordData> unionWords = new ArrayList<>();
		List<WordData> subWords = new ArrayList<>();
		List<WordData> derivativeWords = new ArrayList<>();
		List<LexemeToWordData> synonyms = new ArrayList<>();
		List<WordToMeaningData> antonyms = new ArrayList<>();
		List<LexemeToWordData> abbreviations = new ArrayList<>();
		List<LexemeToWordData> abbreviationFullWords = new ArrayList<>();
		List<WordToMeaningData> cohyponyms = new ArrayList<>();
		List<LexemeToWordData> tokens = new ArrayList<>();
		List<LexemeToWordData> latinTermins = new ArrayList<>();
		List<WordToMeaningData> meanings = new ArrayList<>();
		List<WordSeries> series = new ArrayList<>();
	}

}
