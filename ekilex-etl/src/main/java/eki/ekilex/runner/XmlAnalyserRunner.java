package eki.ekilex.runner;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class XmlAnalyserRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(XmlAnalyserRunner.class);

	@Override
	void initialise() throws Exception {
		//Nothing
	}

	@Transactional
	public void execute(String dataXmlFilePath, String[] xPathExpressions) throws Exception {

		logger.debug("Starting analysing...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		Document dataDoc = readDocument(dataXmlFilePath);
		List<Element> elementNodes;
		List<String> elementPaths;
		int elementCount;

		for (String xPathExpression : xPathExpressions) {

			elementNodes = dataDoc.selectNodes(xPathExpression);
			elementCount = elementNodes.size();
			logger.debug(xPathExpression);
			logger.debug("Elements count: {}", elementCount);
			logger.debug("All locations:");

			elementPaths = new ArrayList<>();
			String nodePath;

			for (Element elementNode : elementNodes) {
				nodePath = elementNode.getPath();
				if (!elementPaths.contains(nodePath)) {
					elementPaths.add(nodePath);
				}
			}

			for (String elementPath : elementPaths) {
				System.out.println(elementPath);
			}

		}

		t2 = System.currentTimeMillis();
		logger.debug("Done analysing in {} ms", (t2 - t1));
	}

}
