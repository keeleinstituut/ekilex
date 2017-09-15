package eki.ekilex.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.TableName;
import eki.common.service.db.BasicDbService;
import eki.ekilex.constant.SystemConstant;

@Component
public class Qq2LoaderRunner implements InitializingBean, SystemConstant, TableName {

	private static Logger logger = LoggerFactory.getLogger(Qq2LoaderRunner.class);

	private static final String TRANSFORM_MORPH_DERIV_FILE_PATH = "./fileresources/csv/transform-morph-deriv.csv";

	@Autowired
	private BasicDbService basicDbService;

	private Map<String, String> morphToMorphMap;

	private Map<String, String> morphToDerivMap;

	@Override
	public void afterPropertiesSet() throws Exception {

		List<String> morphDerivMapLines = getContentLines(TRANSFORM_MORPH_DERIV_FILE_PATH);
		morphToMorphMap = new HashMap<>();
		morphToDerivMap = new HashMap<>();
		for (String morphDerivMapLine : morphDerivMapLines) {
			if (StringUtils.isBlank(morphDerivMapLine)) {
				continue;
			}
			String[] morphDerivMapLineParts = StringUtils.split(morphDerivMapLine, CSV_SEPARATOR);
			String sourceMorphCode = morphDerivMapLineParts[0];
			String destinMorphCode = morphDerivMapLineParts[1];
			String destinDerivCode = morphDerivMapLineParts[2];
			morphToMorphMap.put(sourceMorphCode, destinMorphCode);
			if (!StringUtils.equals(destinDerivCode, String.valueOf(CSV_EMPTY_CELL))) {
				morphToDerivMap.put(sourceMorphCode, destinDerivCode);
			}
		}
	}

	public void execute(String dataXmlFilePath, String dataLang) throws Exception {

		final String articleExp = "/x:sr/x:A";
		final String defaultWordMorphCode = "SgN";

		logger.debug("Starting loading QQ2...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		SAXReader dataDocParser = new SAXReader();

		File dataDocFile = new File(dataXmlFilePath);
		FileInputStream dataDocFileInputStream = new FileInputStream(dataDocFile);
		InputStreamReader dataDocInputReader = new InputStreamReader(dataDocFileInputStream, UTF_8);
		Document dataDoc = dataDocParser.read(dataDocInputReader);
		dataDocInputReader.close();
		dataDocFileInputStream.close();

		/*
		String simplifiedPath = "//x:xr";
		logFullPaths(dataDoc, simplifiedPath);
		*/

		List<Element> articleNodes = dataDoc.selectNodes(articleExp);
		int articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleNodes.size());

		Map<String, Map<Integer, Long>> wordHomonymWordIdMap = new HashMap<>();
		Map<Integer, Long> wordIdMap;
		Map<Long, Long> wordIdParadigmIdMap = new HashMap<>();

		Map<String, Serializable> tableRowParamMap;
		Element headerNode, contentNode;
		List<Element> wordGroupNodes, grammarNodes;
		Element wordNode, wordVocalFormNode, morphNode, rectionNode;
		String word, homonymNrStr, wordDisplayForm, wordVocalForm, rection, grammarLang, grammar, sourceMorphCode, destinMorphCode, destinDerivCode;
		int homonymNr;
		Long wordId, paradigmId, formId;

		int articleCounter = 0;
		int progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Element articleNode : articleNodes) {

			headerNode = (Element) articleNode.selectSingleNode("x:P");
			wordGroupNodes = headerNode.selectNodes("x:mg");
			for (Element wordGroupNode : wordGroupNodes) {
				wordNode = (Element) wordGroupNode.selectSingleNode("x:m");
				word = wordNode.attributeValue("O");
				homonymNrStr = wordNode.attributeValue("i");
				if (StringUtils.isBlank(homonymNrStr)) {
					homonymNr = 1;
				} else {
					homonymNr = Integer.parseInt(homonymNrStr);
					word = StringUtils.substringBefore(word, homonymNrStr);
				}
				wordDisplayForm = wordNode.getTextTrim();
				wordVocalFormNode = (Element) wordGroupNode.selectSingleNode("x:hld");
				if (wordVocalFormNode == null) {
					wordVocalForm = null;
				} else {
					wordVocalForm = wordVocalFormNode.getTextTrim();
				}
				morphNode = (Element) wordGroupNode.selectSingleNode("x:vk");
				if (morphNode == null) {
					destinMorphCode = defaultWordMorphCode;
					destinDerivCode = null;
				} else {
					sourceMorphCode = morphNode.getTextTrim();
					destinMorphCode = morphToMorphMap.get(sourceMorphCode);
					destinDerivCode = morphToDerivMap.get(sourceMorphCode);
				}
				rectionNode = (Element) wordGroupNode.selectSingleNode("x:r");
				if (rectionNode == null) {
					rection = null;
				} else {
					rection = rectionNode.getTextTrim();
				}
				grammarNodes = wordGroupNode.selectNodes("x:grg/x:gki");
				for (Element grammarNode : grammarNodes) {
					grammarLang = grammarNode.attributeValue("lang");
					grammar = grammarNode.getTextTrim();
				}

				// save...
				wordIdMap = wordHomonymWordIdMap.get(word);
				if (wordIdMap == null) {
					wordIdMap = new HashMap<>();
					wordHomonymWordIdMap.put(word, wordIdMap);
				}
				wordId = wordIdMap.get(homonymNr);
				if (wordId == null) {

					// word
					tableRowParamMap = new HashMap<>();
					tableRowParamMap.put("lang", dataLang);
					tableRowParamMap.put("morph_code", destinMorphCode);
					tableRowParamMap.put("homonym_nr", homonymNr);
					wordId = basicDbService.create(WORD, tableRowParamMap);
					wordIdMap.put(homonymNr, wordId);

					// paradigm
					tableRowParamMap = new HashMap<>();
					tableRowParamMap.put("word_id", wordId);
					paradigmId = basicDbService.create(PARADIGM, tableRowParamMap);

					// form
					tableRowParamMap = new HashMap<>();
					tableRowParamMap.put("paradigm_id", paradigmId);
					tableRowParamMap.put("morph_code", destinMorphCode);
					tableRowParamMap.put("value", word);
					tableRowParamMap.put("display_form", wordDisplayForm);
					tableRowParamMap.put("vocal_form", wordVocalForm);
					tableRowParamMap.put("is_word", Boolean.TRUE);
					basicDbService.create(FORM, tableRowParamMap);
				} else {
					System.out.println("Already exists: " + word + " (" + homonymNr + ")");
				}

				/*
				System.out.println("word:    " + word);
				System.out.println("homon #: " + homonymNr);
				System.out.println("displ f: " + wordDisplayForm);
				System.out.println("vocal f: " + wordVocalForm);
				System.out.println("morph  : " + destinMorphCode);
				System.out.println("-----------------------------");
				*/
				articleCounter++;
				if (articleCounter % progressIndicator == 0) {
					logger.debug("{} articles iterated", articleCounter);
				}
			}
			contentNode = (Element) articleNode.selectSingleNode("x:S");
		}

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private void logFullPaths(Document dataDoc, String simplifiedPath) {
		List<Element> synNodes = dataDoc.selectNodes(simplifiedPath);
		List<String> synPaths = new ArrayList<>();
		for (Element synNode : synNodes) {
			if (!synPaths.contains(synNode.getPath())) {
				synPaths.add(synNode.getPath());
			}
		}
		for (String synPath : synPaths) {
			System.out.println(synPath);
		}
	}

	private List<String> getContentLines(String resourceFilePath) throws Exception {
		InputStream resourceInputStream = new FileInputStream(resourceFilePath);
		List<String> contentLines = IOUtils.readLines(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return contentLines;
	}
}
