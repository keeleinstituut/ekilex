package eki.ekilex.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.SystemConstant;

@Component
public class XmlReader implements SystemConstant {

	public Document readDocument(String dataXmlFilePath) throws Exception {

		SAXReader dataDocParser = new SAXReader();
		File dataDocFile = new File(dataXmlFilePath);
		FileInputStream dataDocFileInputStream = new FileInputStream(dataDocFile);
		InputStreamReader dataDocInputReader = new InputStreamReader(dataDocFileInputStream, UTF_8);
		Document dataDoc = dataDocParser.read(dataDocInputReader);
		dataDocInputReader.close();
		dataDocFileInputStream.close();
		return dataDoc;
	}
}
