package eki.ekilex.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class XmlAnalyserRunner extends AbstractLoaderCommons {

	private static Logger logger = LoggerFactory.getLogger(XmlAnalyserRunner.class);

	@Transactional
	public void execute(String dataXmlFilePath, String[] xPathExpressions) throws Exception {

		logger.debug("Starting analysing...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);
		List<Node> elementNodes;
		List<String> elementPaths;
		List<String> elementValues;
		int elementCount;

		for (String xPathExpression : xPathExpressions) {

			elementNodes = dataDoc.selectNodes(xPathExpression);
			elementCount = elementNodes.size();
			logger.debug(xPathExpression);
			logger.debug("Elements count: {}", elementCount);
			logger.debug("All locations:");

			elementPaths = new ArrayList<>();
			elementValues = new ArrayList<>();
			String nodePath, nodeValue;

			for (Node elementNode : elementNodes) {
				nodePath = elementNode.getPath();
				if (!elementPaths.contains(nodePath)) {
					elementPaths.add(nodePath);
				}
				nodeValue = ((Element) elementNode).getTextTrim();
				if (!elementValues.contains(nodeValue)) {
					elementValues.add(nodeValue);
				}
			}

			System.out.println("---------- paths ---------");
			for (String elementPath : elementPaths) {
				System.out.println(elementPath);
			}

			System.out.println("---------- values ---------");
			Collections.sort(elementValues);
			for (String elementValue : elementValues) {
				System.out.println(elementValue);
			}

		}

		t2 = System.currentTimeMillis();
		logger.debug("Done analysing in {} ms", (t2 - t1));
	}

}
