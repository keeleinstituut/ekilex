package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
import eki.ekilex.data.transform.MabData;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.service.ReportComposer;

@Component
public class MabLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MabLoaderRunner.class);

	private static final String REPORT_ENRICHED_WORDS = "enriched_words";

	private static final String DISCLOSED_MORPH_CODE = "Rpl";
	private static final String EMPTY_FORM_VALUE = "-";	

	private final String dataLang = "est";

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
	private final String homonymNrAttr = "i";
	//private final String inflectionTypeAttr = "kuvamuuttyyp";//TODO needs further analyse
	private final String formValueAttr = "kuvavorm";
	private final String soundFileAttr = "kuvaheli";
	private final String formOrderAttr = "ord";
	private final String morphGroup1Attr = "grp2";
	private final String morphGroup2Attr = "grp3";
	private final String morphGroup3Attr = "grp4";
	private final String displayLevelAttr = "lvl";

	private final String inexistentFormValue = "#";
	private final String wordCleanupChars = ".+'`()¤:_";
	private final String expectedWordAppendix = "[ne]";
	private final String expectedWordSuffix = "ne";

	private ReportComposer reportComposer;

	@Override
	String getDataset() {
		return "mab";
	}

	@Override
	void initialise() {
	}

	@Transactional
	public MabData execute(String[] dataXmlFilePaths, boolean doReports) throws Exception {

		logger.debug("Loading MAB...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("mab load report", REPORT_ENRICHED_WORDS);
		}

		// morph value to code conversion map
		Map<String, String> morphValueCodeMap = composeMorphValueCodeMap(dataLang);
		Document dataDoc;
		Element rootElement;
		List<Node> allArticleNodes = new ArrayList<>();
		List<Node> articleNodes;
		for (String dataXmlFilePath : dataXmlFilePaths) {
			logger.debug("Loading \"{}\"", dataXmlFilePath);
			dataDoc = xmlReader.readDocument(dataXmlFilePath);
			rootElement = dataDoc.getRootElement();
			articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
			allArticleNodes.addAll(articleNodes);
		}
		int articleCount = allArticleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		Map<String, List<Paradigm>> wordParadigmsMap = new HashMap<>();

		Node headerNode, contentNode, wordGroupNode, wordNode, paradigmDataNode, morphValueNode, inflectionTypeNrNode;
		Element wordElement, formElement, formGroupElement;
		List<Node> paradigmNodes, formGroupNodes, formNodes;
		List<Paradigm> paradigms, newParadigms;
		List<Form> forms;
		List<String> formValues;
		List<String> words;
		Paradigm paradigmObj;
		Form formObj;
		String word, sourceMorphCode, destinMorphCode, formValue, displayForm, inflectionTypeNr, formOrderByStr, morphGroup1, morphGroup2, morphGroup3, displayLevelStr;
		Integer formOrderBy, displayLevel;

		Count uncleanWordCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Node articleNode : allArticleNodes) {

			contentNode = articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			headerNode = articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNode = headerNode.selectSingleNode(wordGroupExp);
			wordNode = wordGroupNode.selectSingleNode(wordExp);
			wordElement = (Element) wordNode;
			word = wordElement.getTextTrim();

			Integer homonymNr = null;
			String homonymNrAsString = wordElement.attributeValue(homonymNrAttr);
			if (isNotEmpty(homonymNrAsString)) {
				homonymNr = Integer.parseInt(homonymNrAsString);
			}

			words = new ArrayList<>();
			if (StringUtils.endsWith(word, expectedWordAppendix)) {
				word = StringUtils.substringBefore(word, expectedWordAppendix);
				words.add(word);
				word = word + expectedWordSuffix;
				words.add(word);
			} else if (StringUtils.containsAny(word, wordCleanupChars)) {
				uncleanWordCount.increment();
				reportComposer.append(REPORT_ENRICHED_WORDS, word);
				continue;
			} else {
				words.add(word);
			}
			word = words.get(0);

			paradigmNodes = contentNode.selectNodes(paradigmExp);

			newParadigms = new ArrayList<>();

			for (Node paradigmNode : paradigmNodes) {

				paradigmDataNode = paradigmNode.selectSingleNode(paradigmDataExp);
				inflectionTypeNrNode = paradigmNode.selectSingleNode(inflectionTypeNrExp);
				inflectionTypeNr = ((Element) inflectionTypeNrNode).getTextTrim();
				inflectionTypeNr = String.valueOf(Integer.valueOf(inflectionTypeNr));

				formGroupNodes = paradigmDataNode.selectNodes(formGroupExp);

				// compose forms
				forms = new ArrayList<>();
				formValues = new ArrayList<>();

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
						}

						formObj = new Form();
						formObj.setMorphGroup1(morphGroup1);
						formObj.setMorphGroup2(morphGroup2);
						formObj.setMorphGroup3(morphGroup3);
						formObj.setDisplayLevel(displayLevel);
						formObj.setMorphCode(destinMorphCode);
						formObj.setMorphExists(new Boolean(noMorphExists));
						formObj.setValue(formValue);
						//formObj.setComponents(components);
						formObj.setDisplayForm(displayForm);
						//formObj.setVocalForm(vocalForm);
						formObj.setSoundFile(extractSoundFileName(formElement));
						formObj.setOrderBy(formOrderBy);

						forms.add(formObj);
						formValues.add(formValue);
					}
				}

				// sort forms
				forms.sort(Comparator.comparing(Form::getOrderBy));

				// which is word
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

				// compose paradigm
				paradigmObj = new Paradigm();
				paradigmObj.setForms(forms);
				paradigmObj.setFormValues(formValues);
				paradigmObj.setInflectionTypeNr(inflectionTypeNr);
				paradigmObj.setHomonymNr(homonymNr);

				newParadigms.add(paradigmObj);
			}

			// assign morphology to headword(s)
			for (String newWord : words) {
				paradigms = wordParadigmsMap.get(newWord);
				if (paradigms == null) {
					paradigms = new ArrayList<>();
					wordParadigmsMap.put(newWord, paradigms);
				}
				paradigms.addAll(newParadigms);
			}

			// progress
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}

		Map<String, List<String>> formWordsMap = composeFormWordsMap(wordParadigmsMap);

		if (reportComposer != null) {
			reportComposer.end();
		}

		logger.debug("Found {} unclean words", uncleanWordCount.getValue());
		logger.debug("Found {} words", wordParadigmsMap.size());

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));

		return new MabData(wordParadigmsMap, formWordsMap);
	}

	private String extractSoundFileName(Element element) {
		String name = element.attributeValue(soundFileAttr);
		return isNotBlank(name) ? name + ".mp3" : null;
	}

	private Map<String, List<String>> composeFormWordsMap(Map<String, List<Paradigm>> wordParadigmsMap) {

		Map<String, List<String>> formWordsMap = new HashMap<>();
		List<String> wordForms;

		for (String word : wordParadigmsMap.keySet()) {
			wordForms = new ArrayList<>();
			List<Paradigm> paradigms = wordParadigmsMap.get(word);
			for (Paradigm paradigm : paradigms) {
				List<String> formValues = paradigm.getFormValues();
				for (String form : formValues) {
					if (wordForms.contains(form)) {
						continue;
					}
					wordForms.add(form);
					List<String> assignedWords = formWordsMap.get(form);
					if (CollectionUtils.isEmpty(assignedWords)) {
						assignedWords = new ArrayList<>();
						formWordsMap.put(form, assignedWords);
					}
					if (!assignedWords.contains(word)) {
						assignedWords.add(word);
					}
				}
			}
		}
		return formWordsMap;
	}

	private Map<String, String> composeMorphValueCodeMap(String morphLang) throws Exception {

		final String morphType = "ekimorfo";

		String morphSqlScript =
				"select "
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
}
