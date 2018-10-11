package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
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

	private final String dataLang = "est";

	private ReportComposer reportComposer;

	@Override
	String getDataset() {
		return "mab";
	}

	@Override
	void initialise() {
	}

	@Transactional
	public MabData execute(String dataXmlFilePath, boolean doReports) throws Exception {

		logger.debug("Loading MAB...");

		final String articleHeaderExp = "x:P";
		final String wordGroupExp = "x:mg";
		final String wordExp = "x:m";
		final String articleBodyExp = "x:S";
		final String rootBaseExp = "x:lemp/x:mtg";
		final String formGroupExp = "x:pdgp/x:mvg";
		final String morphValueExp = "x:vk";
		final String formExp = "x:parg/x:mv";
		final String inflectionTypeNrExp = "x:mt";
		final String homonymNrAttr = "i";

		final String wordCleanupChars = ".+'`()¤:_";//probably () only
		final String formCleanupChars = "`´[]#";
		final String expectedWordAppendix = "(ne)";
		final String expectedWordSuffix = "ne";

		long t1, t2;
		t1 = System.currentTimeMillis();

		if (doReports) {
			reportComposer = new ReportComposer("mab load report", REPORT_ENRICHED_WORDS);
		}

		// morph value to code conversion map
		Map<String, String> morphValueCodeMap = composeMorphValueCodeMap(dataLang);

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();

		long articleCount = rootElement.content().stream().filter(node -> node instanceof Element).count();
		List<Element> articleNodes = (List<Element>) rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
		logger.debug("Extracted {} articles", articleCount);

		Map<String, List<Paradigm>> wordParadigmsMap = new HashMap<>();

		Element headerNode, contentNode, wordGroupNode, wordNode, morphValueNode, inflectionTypeNrNode;
		List<Element> rootBaseNodes, formGroupNodes, formNodes;
		List<Paradigm> paradigms, newParadigms;
		List<Form> forms;
		List<String> formValues;
		List<String> words;
		Paradigm paradigmObj;
		Form formObj;
		String word, sourceMorphCode, destinMorphCode, form, displayForm, inflectionTypeNr;

		Count uncleanWordCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Element articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNode = (Element) headerNode.selectSingleNode(wordGroupExp);
			wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			word = wordNode.getTextTrim();

			Integer homonymNr = null;
			String homonymNrAsString = wordNode.attributeValue(homonymNrAttr);
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

			rootBaseNodes = contentNode.selectNodes(rootBaseExp);

			newParadigms = new ArrayList<>();

			for (Element rootBaseNode : rootBaseNodes) {

				formGroupNodes = rootBaseNode.selectNodes(formGroupExp);
				inflectionTypeNrNode = (Element) rootBaseNode.selectSingleNode(inflectionTypeNrExp);
				inflectionTypeNr = inflectionTypeNrNode.getTextTrim();
				inflectionTypeNr = String.valueOf(Integer.parseInt(inflectionTypeNr));

				forms = new ArrayList<>();
				formValues = new ArrayList<>();
				boolean isWord = true;
				FormMode mode = FormMode.WORD;

				for (Element formGroupNode : formGroupNodes) {

					morphValueNode = (Element) formGroupNode.selectSingleNode(morphValueExp);
					sourceMorphCode = morphValueNode.getTextTrim();
					destinMorphCode = morphValueCodeMap.get(sourceMorphCode);

					formNodes = formGroupNode.selectNodes(formExp);

					for (Element formNode : formNodes) {

						displayForm = formNode.getTextTrim();
						form = StringUtils.replaceChars(displayForm, formCleanupChars, "");

						if (StringUtils.isBlank(form)) {
							continue;
						}

						formObj = new Form();
						formObj.setValue(form);
						formObj.setDisplayForm(displayForm);
						formObj.setMorphCode(destinMorphCode);
						formObj.setMode(mode);

						forms.add(formObj);
						formValues.add(form);
					}
					// TODO ask - first is the word?
					if (isWord) {
						isWord = false;
						mode = FormMode.FORM;
					}
				}

				paradigmObj = new Paradigm();
				paradigmObj.setForms(forms);
				paradigmObj.setFormValues(formValues);
				paradigmObj.setInflectionTypeNr(inflectionTypeNr);
				paradigmObj.setHomonymNr(homonymNr);

				newParadigms.add(paradigmObj);
			}

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
