package eki.ekilex.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.SystemConstant;

@Component
public class XsdToClassifierCsvRunner implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(XsdToClassifierCsvRunner.class);

	private final static String CLASSIFIER_ALL_CSV_PATH = "./fileresources/csv/classifier-all.csv";

	@Autowired
	private XmlReader xmlReader;

	public void execute(String[] classifierXsdFilePaths) throws Exception {

		verifyClassifierXsdFilePaths(classifierXsdFilePaths);

		StringBuffer classifCsvBuf = new StringBuffer();
		classifCsvBuf.append("EKI liik");
		classifCsvBuf.append(CSV_SEPARATOR);
		classifCsvBuf.append("EKI nimi");
		classifCsvBuf.append(CSV_SEPARATOR);
		classifCsvBuf.append("EKI kood");
		classifCsvBuf.append(CSV_SEPARATOR);
		classifCsvBuf.append("EKI väärtus");
		classifCsvBuf.append(CSV_SEPARATOR);
		classifCsvBuf.append("EKI keel");
		classifCsvBuf.append('\n');

		int classifierCsvRowCount = 0;

		for (String classifierXsdFilePath : classifierXsdFilePaths) {

			logger.debug("Loading \"{}\"", classifierXsdFilePath);

			Document dataDoc = xmlReader.readDocument(classifierXsdFilePath);

			StringBuffer classifRowBuf;

			List<Element> classifierGroupNodes = dataDoc.selectNodes("/xs:schema/xs:simpleType[@name]");
			for (Element classifierGroupNode : classifierGroupNodes) {

				String ekiClassifierType = classifierGroupNode.attributeValue("name");
				String ekiClassifierName = null;
				Element classifierNameNode = (Element) classifierGroupNode.selectSingleNode("xs:annotation/xs:documentation");
				if (classifierNameNode == null) {
					ekiClassifierName = ekiClassifierType;
				} else {
					ekiClassifierName = classifierNameNode.getTextTrim();
				}
				List<Element> classifierNodes = classifierGroupNode.selectNodes("xs:restriction/xs:enumeration");
				for (Element classifierNode : classifierNodes) {
					String ekiClassifierCode = classifierNode.attributeValue("value");

					classifRowBuf = new StringBuffer();
					classifRowBuf.append(ekiClassifierType);
					classifRowBuf.append(CSV_SEPARATOR);
					classifRowBuf.append(ekiClassifierName);
					classifRowBuf.append(CSV_SEPARATOR);
					classifRowBuf.append(ekiClassifierCode);
					classifRowBuf.append(CSV_SEPARATOR);

					List<Element> classifierValueNodes = classifierNode.selectNodes("xs:annotation/xs:documentation");
					for (Element classifierValueNode : classifierValueNodes) {

						String ekiClassifierValueLang = classifierValueNode.attributeValue("lang");
						String ekiClassifierValue = classifierValueNode.getTextTrim();

						if (StringUtils.isNotBlank(ekiClassifierValue)) {
							classifCsvBuf.append(classifRowBuf);
							classifCsvBuf.append(ekiClassifierValue);
							classifCsvBuf.append(CSV_SEPARATOR);
							classifCsvBuf.append(ekiClassifierValueLang);
							classifCsvBuf.append('\n');
							classifierCsvRowCount++;
						}
					}
				}
			}
		}
		String classifierCsvContent = classifCsvBuf.toString();

		FileOutputStream classifierCsvStream = new FileOutputStream(CLASSIFIER_ALL_CSV_PATH);
		IOUtils.write(classifierCsvContent, classifierCsvStream, StandardCharsets.UTF_8);
		classifierCsvStream.flush();
		classifierCsvStream.close();

		logger.debug("Done. Collected {} rows", classifierCsvRowCount);
	}

	private void verifyClassifierXsdFilePaths(String[] classifierXsdFilePaths) throws FileNotFoundException {
		for (String classifierXsdFilePath : classifierXsdFilePaths) {
			File classifierXsdFile = new File(classifierXsdFilePath);
			if (!classifierXsdFile.exists()) {
				throw new FileNotFoundException("No such file exists: \"" + classifierXsdFilePath + "\"");
			}
		}
	}
}
