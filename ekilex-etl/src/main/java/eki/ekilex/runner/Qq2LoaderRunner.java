package eki.ekilex.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.SystemConstant;

@Component
public class Qq2LoaderRunner implements InitializingBean, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(Qq2LoaderRunner.class);

	private static final String TRANSFORM_MORPH_DERIV_FILE_PATH = "./fileresources/csv/transform-morph-deriv.csv";

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

	public void execute(String dataXmlFilePath) throws Exception {

		final String wordHeaderExp = "/x:sr/x:A/x:P";

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

		List<Element> wordHeaderNodes = dataDoc.selectNodes(wordHeaderExp);
		logger.debug("Extracted {} word headers", wordHeaderNodes.size());

		List<Element> wordGroupNodes, grammarNodes;
		Element wordNode, morphNode;
		String word, grammarLang, sourceMorphCode, destinMorphCode, destinDerivCode;

		for (Element wordHeaderNode : wordHeaderNodes) {
			wordGroupNodes = wordHeaderNode.selectNodes("x:mg");
			for (Element wordGroupNode : wordGroupNodes) {
				wordNode = (Element) wordGroupNode.selectSingleNode("x:m");
				word = wordNode.attributeValue("O");
				morphNode = (Element) wordGroupNode.selectSingleNode("x:vk");
				sourceMorphCode = null;
				destinMorphCode = null;
				if (morphNode != null) {
					sourceMorphCode = morphNode.getText();
					destinMorphCode = morphToMorphMap.get(sourceMorphCode);
					destinDerivCode = morphToDerivMap.get(sourceMorphCode);
				}
				grammarNodes = wordGroupNode.selectNodes("x:grg/x:gki");
				for (Element grammarNode : grammarNodes) {
					grammarLang = grammarNode.attributeValue("lang");
					System.out.println(word);
					System.out.println("- (" + grammarLang + ") " + grammarNode.getText());
				}
			}
		}

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private List<String> getContentLines(String resourceFilePath) throws Exception {
		InputStream resourceInputStream = new FileInputStream(resourceFilePath);
		List<String> contentLines = IOUtils.readLines(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return contentLines;
	}
}
