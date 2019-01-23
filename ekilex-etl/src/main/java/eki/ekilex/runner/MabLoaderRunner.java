package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.data.Count;
import eki.ekilex.data.transform.Form;
import eki.ekilex.service.ReportComposer;

@Component
public class MabLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MabLoaderRunner.class);

	private static final String REPORT_ENRICHED_WORDS = "enriched_words";

	private static final String DISCLOSED_MORPH_CODE = "Rpl";
	private static final String EMPTY_FORM_VALUE = "-";
	private static final String DISPLAY_FORM_CLEANUP_CHARS = "̄̆̇’'`´:_–!°¤()[]";
	private static final String WORD_VALUE_POLLUTION_TEST_CHARS = DISPLAY_FORM_CLEANUP_CHARS + FORM_COMPONENT_SEPARATOR;

	private final int defaultHomonymNr = 0;
	private final String dataLang = "est";

	private final String guidExp = "x:G";
	private final String articleHeaderExp = "x:P";
	private final String wordGroupExp = "x:mg";
	private final String wordExp = "x:m";
	private final String articleBodyExp = "x:S";
	private final String paradigmExp = "x:lemp[not(@x:as='ab')]/x:mtg";
	private final String paradigmDataExp = "x:pdgp";
	private final String inflectionTypeNrExp = "x:mt";
	private final String formGroupExp = "x:mvg";
	private final String morphValueExp = "x:vk";
	private final String formExp = "x:parg/x:mv";
	private final String homonymNrAttr = "i";//can't see use for that
	private final String wordClassAttr = "kuvasonaklass";
	private final String inflectionTypeAttr = "kuvamuuttyyp";
	private final String formValueAttr = "kuvavorm";
	private final String soundFileAttr = "kuvaheli";
	private final String formOrderAttr = "ord";
	private final String morphGroup1Attr = "grp2";
	private final String morphGroup2Attr = "grp3";
	private final String morphGroup3Attr = "grp4";
	private final String displayLevelAttr = "lvl";

	private final String inexistentFormValue = "#";
	private final String expectedWordAppendix = "[ne]";
	private final String expectedWordSuffix = "ne";

	private ReportComposer reportComposer;

	@Override
	public String getDataset() {
		return "mab";
	}

	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
	}

	@Override
	void initialise() {
	}

	@Transactional
	public void execute(String[] dataXmlFilePaths, boolean doReports) throws Exception {

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer("mab load report", REPORT_ENRICHED_WORDS);
		}
		start();

		// morph value to code conversion map
		Map<String, String> morphValueCodeMap = composeMorphValueCodeMap(dataLang);
		Document dataDoc;
		Node guidNode, headerNode, contentNode, wordGroupNode, wordNode, firstParadigmDataNode, paradigmDataNode, morphValueNode, inflectionTypeNrNode;
		Element rootElement, guidElement, wordElement, inflectionTypeNrElement, firstParadigmDataElement, paradigmDataElement, formElement, formGroupElement;
		List<Node> articleNodes, paradigmNodes, formGroupNodes, formNodes;
		List<Form> forms;
		List<Long> wordIds;
		Form form;
		String[] components;
		String guid, word, wordAlt, wordClass, sourceMorphCode, destinMorphCode, formValue, displayForm, inflectionTypeNr, inflectionType;
		String formOrderByStr, morphGroup1, morphGroup2, morphGroup3, displayLevelStr, soundFile;
		Integer formOrderBy, displayLevel;
		Long wordId, paradigmId;

		Count totalArticleCount = new Count();
		Count uncleanWordCount = new Count();
		Count wordCount = new Count();
		Count paradigmCount = new Count();
		Count formCount = new Count();

		logger.debug("There are {} volumes in total", dataXmlFilePaths.length);

		int volumeCounter = 0;

		for (String dataXmlFilePath : dataXmlFilePaths) {

			logger.debug("Loading \"{}\"", dataXmlFilePath);
			dataDoc = xmlReader.readDocument(dataXmlFilePath);
			rootElement = dataDoc.getRootElement();
			articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
			int articleCount = articleNodes.size();
			totalArticleCount.increment(articleCount);
			logger.debug("Extracted {} articles", articleCount);

			volumeCounter++;
			long articleCounter = 0;
			long progressIndicator = articleCount / Math.min(articleCount, 100);

			for (Node articleNode : articleNodes) {

				contentNode = articleNode.selectSingleNode(articleBodyExp);
				if (contentNode == null) {
					continue;
				}

				guidNode = articleNode.selectSingleNode(guidExp);
				guidElement = (Element) guidNode;
				guid = guidElement.getTextTrim();
				headerNode = articleNode.selectSingleNode(articleHeaderExp);
				wordGroupNode = headerNode.selectSingleNode(wordGroupExp);
				wordNode = wordGroupNode.selectSingleNode(wordExp);
				wordElement = (Element) wordNode;
				word = wordElement.getTextTrim();

				firstParadigmDataNode = contentNode.selectSingleNode(paradigmExp + "/" + paradigmDataExp);
				firstParadigmDataElement = (Element) firstParadigmDataNode;
				wordClass = firstParadigmDataElement.attributeValue(wordClassAttr);

				wordIds = new ArrayList<>();
				if (StringUtils.endsWith(word, expectedWordAppendix)) {
					word = StringUtils.substringBefore(word, expectedWordAppendix);
					wordId = createWordAndGuid(guid, word, defaultHomonymNr, dataLang, wordClass);
					wordIds.add(wordId);
					wordAlt = word + expectedWordSuffix;
					wordId = createWordAndGuid(guid, wordAlt, defaultHomonymNr, dataLang, wordClass);
					wordIds.add(wordId);
				} else if (StringUtils.contains(word, FORM_COMPONENT_SEPARATOR)) {
					word = StringUtils.remove(word, FORM_COMPONENT_SEPARATOR);
					wordId = createWordAndGuid(guid, word, defaultHomonymNr, dataLang, wordClass);
					wordIds.add(wordId);
				} else if (StringUtils.containsAny(word, WORD_VALUE_POLLUTION_TEST_CHARS)) {
					uncleanWordCount.increment();
					if (doReports) {
						reportComposer.append(REPORT_ENRICHED_WORDS, word);
					}
					continue;
				} else {
					wordId = createWordAndGuid(guid, word, defaultHomonymNr, dataLang, wordClass);
					wordIds.add(wordId);
				}
				wordCount.increment(wordIds.size());

				paradigmNodes = contentNode.selectNodes(paradigmExp);

				for (Node paradigmNode : paradigmNodes) {

					paradigmDataNode = paradigmNode.selectSingleNode(paradigmDataExp);
					inflectionTypeNrNode = paradigmNode.selectSingleNode(inflectionTypeNrExp);
					inflectionTypeNrElement = (Element) inflectionTypeNrNode;
					inflectionTypeNr = inflectionTypeNrElement.getTextTrim();
					inflectionTypeNr = String.valueOf(Integer.valueOf(inflectionTypeNr));

					paradigmDataElement = (Element) paradigmDataNode;
					inflectionType = paradigmDataElement.attributeValue(inflectionTypeAttr);
					formGroupNodes = paradigmDataElement.selectNodes(formGroupExp);

					// compose forms
					forms = new ArrayList<>();

					for (Node formGroupNode : formGroupNodes) {

						morphValueNode = formGroupNode.selectSingleNode(morphValueExp);
						sourceMorphCode = ((Element) morphValueNode).getTextTrim();
						destinMorphCode = morphValueCodeMap.get(sourceMorphCode);
						if (StringUtils.equals(destinMorphCode, DISCLOSED_MORPH_CODE)) {
							continue;
						}

						formGroupElement = (Element) formGroupNode;
						formOrderByStr = formGroupElement.attributeValue(formOrderAttr);
						formOrderBy = Integer.valueOf(formOrderByStr);
						morphGroup1 = formGroupElement.attributeValue(morphGroup1Attr);
						morphGroup2 = formGroupElement.attributeValue(morphGroup2Attr);
						morphGroup3 = formGroupElement.attributeValue(morphGroup3Attr);

						formNodes = formGroupNode.selectNodes(formExp);

						for (Node formNode : formNodes) {

							formElement = (Element) formNode;
							formValue = formElement.attributeValue(formValueAttr);
							displayLevelStr = formElement.attributeValue(displayLevelAttr);
							displayLevel = Integer.valueOf(displayLevelStr);
							displayForm = formElement.getTextTrim();
							displayForm = StringUtils.removeEnd(displayForm, "[");
							boolean noMorphExists = StringUtils.equals(inexistentFormValue, displayForm);
							if (noMorphExists) {
								formValue = EMPTY_FORM_VALUE;
								displayForm = EMPTY_FORM_VALUE;
								components = null;
							} else {
								String cleanDisplayForm = StringUtils.replaceChars(displayForm, DISPLAY_FORM_CLEANUP_CHARS, "");
								components = StringUtils.split(cleanDisplayForm, FORM_COMPONENT_SEPARATOR);
								components = escapeNulls(components);
							}
							soundFile = extractSoundFileName(formElement);

							form = new Form();
							form.setMorphGroup1(morphGroup1);
							form.setMorphGroup2(morphGroup2);
							form.setMorphGroup3(morphGroup3);
							form.setDisplayLevel(displayLevel);
							form.setMorphCode(destinMorphCode);
							form.setMorphExists(new Boolean(!noMorphExists));
							form.setValue(formValue);
							form.setComponents(components);
							form.setDisplayForm(displayForm);
							//formObj.setVocalForm(vocalForm);
							form.setSoundFile(soundFile);
							form.setOrderBy(formOrderBy);

							forms.add(form);
						}
					}

					// sort forms
					forms.sort(Comparator.comparing(Form::getOrderBy));

					// which is word
					locateWord(word, forms);

					// create pradigms and forms for words
					for (Long newWordId : wordIds) {
						paradigmId = createParadigm(newWordId, inflectionTypeNr, inflectionType, false);
						paradigmCount.increment();
						for (Form newWorm : forms) {
							createForm(paradigmId, newWorm);
							formCount.increment();
						}
					}
				}

				// progress
				articleCounter++;
				if (articleCounter % progressIndicator == 0) {
					long progressPercent = articleCounter / progressIndicator;
					logger.debug("vol #{} - {}% - {} articles iterated", volumeCounter, progressPercent, articleCounter);
				}
			}
		}

		if (reportComposer != null) {
			reportComposer.end();
		}

		logger.debug("Found {} articles in total", totalArticleCount.getValue());
		logger.debug("Found {} unclean words", uncleanWordCount.getValue());
		logger.debug("Found {} words", wordCount.getValue());
		logger.debug("Found {} paradigms", paradigmCount.getValue());
		logger.debug("Found {} forms", formCount.getValue());

		end();
	}

	private String[] escapeNulls(String[] components) {
		if (ArrayUtils.isEmpty(components)) {
			return components;
		}
		for (int componentIndex = 0; componentIndex < components.length; componentIndex++) {
			if (StringUtils.equalsIgnoreCase("null", components[componentIndex])) {
				components[componentIndex] = "'null'";
			}
		}
		return components;
	}

	private void locateWord(String word, List<Form> forms) {
		boolean wordFormLocated = false;
		for (Form formObjOfMode : forms) {
			if (wordFormLocated) {
				formObjOfMode.setMode(FormMode.FORM);
			} else if (StringUtils.equals(word, formObjOfMode.getValue())) {
				formObjOfMode.setMode(FormMode.WORD);
				wordFormLocated = true;
			} else {
				formObjOfMode.setMode(FormMode.FORM);
			}
		}
	}

	private String extractSoundFileName(Element element) {
		String name = element.attributeValue(soundFileAttr);
		return isNotBlank(name) ? name + ".mp3" : null;
	}

	private Map<String, String> composeMorphValueCodeMap(String morphLang) throws Exception {

		final String morphType = "ekimorfo";

		String morphSqlScript = "select "
				+ "ml.value \"key\","
				+ "ml.code \"value\" "
				+ "from " + MORPH_LABEL + " ml "
				+ "where "
				+ "ml.lang = :morphLang "
				+ "and ml.type = :morphType";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("morphLang", morphLang);
		paramMap.put("morphType", morphType);
		Map<String, String> morphValueCodeMap = basicDbService.queryListAsMap(morphSqlScript, paramMap);

		if (MapUtils.isEmpty(morphValueCodeMap)) {
			throw new Exception("No morph classifiers are available with that criteria");
		}
		return morphValueCodeMap;
	}

	protected Long createWordAndGuid(String guid, String word, int homonymNr, String lang, String wordClass) throws Exception {
		Long wordId = createWord(word, null, homonymNr, wordClass, lang, null, null, null);
		createWordGuid(wordId, getDataset(), guid);
		return wordId;
	}
}
