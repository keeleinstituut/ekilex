package eki.ekilex.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.data.Count;
import eki.ekilex.data.transform.Form;
import eki.ekilex.data.transform.Paradigm;

@Component
public class MabLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MabLoaderRunner.class);

	@Override
	void initialise() throws Exception {

	}

	public Map<String, List<Paradigm>> execute(String dataXmlFilePath, String dataLang) throws Exception {

		logger.debug("Loading MAB...");

		final String articleExp = "/x:sr/x:A";
		final String articleHeaderExp = "x:P";
		final String wordGroupExp = "x:mg";
		final String wordExp = "x:m";
		final String articleBodyExp = "x:S";
		final String paradigmBlockExp = "x:lemp/x:mtg/x:pdgp";

		final String wordCleanupChars = ".+'`()¤:_";//probably () only
		final String formCleanupChars = "`´[]#";

		long t1, t2;
		t1 = System.currentTimeMillis();

		dataLang = unifyLang(dataLang);
		Document dataDoc = readDocument(dataXmlFilePath);

		List<Element> articleNodes = dataDoc.selectNodes(articleExp);
		int articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleCount);

		Map<String, List<Paradigm>> wordParadigmsMap = new HashMap<>();

		Element headerNode, contentNode, wordGroupNode, wordNode, morphValueNode;
		List<Element> paradigmBlockNodes, formGroupNodes, formNodes;
		List<Paradigm> paradigms, newParadigms;
		List<Form> forms;
		List<String> formValues;
		Paradigm paradigmObj;
		Form formObj;
		String word, morphCode, form;

		Count uncleanWordCount = new Count();

		int articleCounter = 0;
		int progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Element articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNode = (Element) headerNode.selectSingleNode(wordGroupExp);
			wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
			word = wordNode.getTextTrim();

			if (StringUtils.containsAny(word, wordCleanupChars)) {
				uncleanWordCount.increment();
			}

			paradigmBlockNodes = contentNode.selectNodes(paradigmBlockExp);

			newParadigms = new ArrayList<>();

			for (Element paradigmBlockNode : paradigmBlockNodes) {

				formGroupNodes = paradigmBlockNode.selectNodes("x:mvg");

				forms = new ArrayList<>();
				formValues = new ArrayList<>();

				for (Element formGroupNode : formGroupNodes) {

					morphValueNode = (Element) formGroupNode.selectSingleNode("x:vk");
					morphCode = morphValueNode.getTextTrim();

					formNodes = formGroupNode.selectNodes("x:parg/x:mv");

					for (Element formNode : formNodes) {

						morphCode = formNode.attributeValue("pvk");
						form = formNode.getTextTrim();
						form = StringUtils.replaceChars(form, formCleanupChars, "");

						formObj = new Form();
						formObj.setMorphCode(morphCode);
						formObj.setValue(form);

						forms.add(formObj);
						formValues.add(form);
					}
				}

				paradigmObj = new Paradigm();
				paradigmObj.setWord(word);
				paradigmObj.setForms(forms);
				paradigmObj.setFormValues(formValues);

				newParadigms.add(paradigmObj);
			}

			paradigms = wordParadigmsMap.get(word);
			if (paradigms == null) {
				paradigms = new ArrayList<>();
				wordParadigmsMap.put(word, paradigms);
			}
			paradigms.addAll(newParadigms);

			// progress
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				int progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}

		logger.debug("Found {} unclean words", uncleanWordCount);

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));

		return wordParadigmsMap;
	}
}
