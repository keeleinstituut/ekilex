package eki.ekilex.runner;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;

//TODO under construction
@Component
public class VmabLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(VmabLoaderRunner.class);

	@Override
	public String getDataset() {
		return "vm";
	}

	@Override
	public Complexity getComplexity() {
		return null;
	}

	@Override
	public void deleteDatasetData() throws Exception {
		
	}

	@Override
	public void initialise() throws Exception {
	}

	//TODO is it possible to use MabData wrapper?
	public void execute(String dataXmlFilePath, boolean doReports) throws Exception {

		logger.debug("Loading VM...");

		Document dataDoc = xmlReader.readDocument(dataXmlFilePath);

		Element rootElement = dataDoc.getRootElement();

		long articleCount = rootElement.content().stream().filter(node -> node instanceof Element).count();
		List<Node> articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(toList());
		logger.debug("Extracted {} articles", articleCount);
	}
}
