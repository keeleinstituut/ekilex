package eki.ekilex.manual;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.constant.SystemConstant;

/*
 * valdkond: v_tyyp
 * sõnaliik: sl_tyyp
 * vormi märgend: vk_tyyp
 * register=stiil: s_tyyp
 */
public class XsdToClassifier implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(XsdToClassifier.class);

	private static final String CLASSIF_KEY_PLACEHOLDER = "{classifKey}";
	private static final String CLASSIF_LANG_PLACEHOLDER = "{lang}";
	private static final String CLASSIF_XPATH = "//xs:simpleType[@name='" + CLASSIF_KEY_PLACEHOLDER + "']/xs:restriction/xs:enumeration";
	private static final String CLASSIF_LANG_XPATH = "//xs:simpleType/xs:restriction/xs:enumeration/xs:annotation/xs:documentation[boolean(@xml:lang) and text()]";
	private static final String CLASSIF_DOMAIN_KEY = "v_tyyp";
	private static final String CLASSIF_POS_TYPE_KEY = "sl_tyyp";
	private static final String CLASSIF_POS_CAT_KEY = "vk_tyyp";
	private static final String CLASSIF_REGISTER_KEY = "s_tyyp";

	public static void main(String[] args) throws Exception {

		final String origDataFilesRootPath = "/projects/eki/data/bolan";
		final String classifDomainFilePath = "./fileresources/csv/classifier-domain-bolan_" + CLASSIF_LANG_PLACEHOLDER + ".csv";
		final String classifPosTypeFilePath = "./fileresources/csv/classifier-postype-bolan_" + CLASSIF_LANG_PLACEHOLDER + ".csv";
		final String classifPosCatFilePath = "./fileresources/csv/classifier-poscat-bolan_" + CLASSIF_LANG_PLACEHOLDER + ".csv";
		final String classifRegisterFilePath = "./fileresources/csv/classifier-register-bolan_" + CLASSIF_LANG_PLACEHOLDER + ".csv";

		long t1, t2;
		t1 = System.currentTimeMillis();

		File origDataFilesRootFolder = new File(origDataFilesRootPath);
		List<File> origDataFiles = collectOrigDataFiles(origDataFilesRootFolder);

		SAXReader origDataDocParser = new SAXReader();

		List<Document> origDataDocs = new ArrayList<>();
		Document origDataDoc;

		for (File origDataFile : origDataFiles) {
			origDataDoc = origDataDocParser.read(origDataFile);
			origDataDocs.add(origDataDoc);
		}

		List<String> classifLangs = collectClassifierLanguages(origDataDocs);

		composeClassifierFile(origDataDocs, CLASSIF_DOMAIN_KEY, classifLangs, classifDomainFilePath);
		composeClassifierFile(origDataDocs, CLASSIF_POS_TYPE_KEY, classifLangs, classifPosTypeFilePath);
		composeClassifierFile(origDataDocs, CLASSIF_POS_CAT_KEY, classifLangs, classifPosCatFilePath);
		composeClassifierFile(origDataDocs, CLASSIF_REGISTER_KEY, classifLangs, classifRegisterFilePath);

		t2 = System.currentTimeMillis();

		System.out.println("Done transforming classifiers at " + (t2 - t1) + " ms");
	}

	private static List<String> collectClassifierLanguages(List<Document> origDataDocs) {

		List<String> languages = new ArrayList<>();
		List<Element> classifNodes;
		Attribute langAttribute;
		String lang;

		for (Document origDataDoc : origDataDocs) {
			classifNodes = origDataDoc.selectNodes(CLASSIF_LANG_XPATH);
			for (Element classifNode : classifNodes) {
				langAttribute = classifNode.attribute("lang");
				lang = langAttribute.getValue();
				if (!languages.contains(lang)) {
					languages.add(lang);
				}
			}
		}
		return languages;
	}

	private static void composeClassifierFile(List<Document> origDataDocs, String classifKey, List<String> classifLangs, String classifierFilePath) throws Exception {

		List<Element> classifNodes;
		Attribute classifCodeAttr, classifLangAttr;
		List<Element> classifNameNodes;
		int classifNodeCount;
		Map<String, String> classifierMap;
		String classifLang, classifCode, classifName, existingClassifName;
		String classifXpath = StringUtils.replace(CLASSIF_XPATH, CLASSIF_KEY_PLACEHOLDER, classifKey);

		List<String> classifierOrderList = new ArrayList<>();
		Map<String, Map<String, String>> classifierLangMap = new HashMap<>();
		for (String lang : classifLangs) {
			classifierMap = new HashMap<>();
			classifierLangMap.put(lang, classifierMap);
		}

		for (Document origDataDoc : origDataDocs) {

			classifNodes = origDataDoc.selectNodes(classifXpath);
			classifNodeCount = classifNodes.size();
			if (classifNodeCount > 0) {
				for (Element classifNode : classifNodes) {
					classifCodeAttr = classifNode.attribute("value");
					classifCode = classifCodeAttr.getValue();
					if (!classifierOrderList.contains(classifCode)) {
						classifierOrderList.add(classifCode);
					}
					classifNameNodes = classifNode.selectNodes("xs:annotation/xs:documentation[text()]");
					for (Element classifNameNode : classifNameNodes) {
						classifLangAttr = classifNameNode.attribute("lang");
						classifLang = classifLangAttr.getValue();
						classifName = classifNameNode.getTextTrim();
						classifierMap = classifierLangMap.get(classifLang);
						existingClassifName = classifierMap.get(classifCode);
						if (StringUtils.isNotBlank(existingClassifName) &&
						   !StringUtils.equals(classifName, existingClassifName) &&
						    StringUtils.length(existingClassifName) > StringUtils.length(classifName)) {
							logger.warn("Duplicate value found for classifier \"{}\" = \"{}\" -> \"{}\"", classifCode, classifName, existingClassifName);
							classifName = existingClassifName;
						}
						classifierMap.put(classifCode, classifName);
					}
				}
			}
		}

		for (String lang : classifLangs) {
			classifierMap = classifierLangMap.get(lang);
			if (MapUtils.isNotEmpty(classifierMap)) {
				composeClassifierFile(classifierMap, classifierOrderList, classifierFilePath, lang);
			}
		}
	}

	private static void composeClassifierFile(Map<String, String> classifierMap, List<String> classifierOrderList, String classifierFilePath, String lang) throws Exception {

		String classifierLangFilePath = StringUtils.replace(classifierFilePath, CLASSIF_LANG_PLACEHOLDER, lang);
		File classifierFile = new File(classifierLangFilePath);
		FileOutputStream classifierFileOutputStream = new FileOutputStream(classifierFile);
		String classifierFileLine;
		for (String classifierCode : classifierOrderList) {
			String classifierName = classifierMap.get(classifierCode);
			if (StringUtils.isNotBlank(classifierName)) {
				classifierFileLine = classifierCode + CSV_SEPARATOR + CSV_EMPTY_CELL + CSV_SEPARATOR + classifierName + '\n';
				IOUtils.write(classifierFileLine, classifierFileOutputStream, UTF_8);
			}
		}
		classifierFileOutputStream.flush();
		classifierFileOutputStream.close();
	}

	private static List<File> collectOrigDataFiles(File origDataFilesRootFolder) {

		List<File> origDataFilesList = new ArrayList<File>();
		collectOrigDataFiles(origDataFilesRootFolder, origDataFilesList);

		return origDataFilesList;
	}

	private static void collectOrigDataFiles(File fileOrFolder, List<File> filesList) {

		if (fileOrFolder.isDirectory()) {
			File[] listedFilesOrFolders = fileOrFolder.listFiles();
			if (ArrayUtils.isEmpty(listedFilesOrFolders)) {
				return;
			}
			for (File listedFileOrFolder : listedFilesOrFolders) {
				collectOrigDataFiles(listedFileOrFolder, filesList);
			}
		} else if (fileOrFolder.isFile()
				&& StringUtils.endsWithIgnoreCase(fileOrFolder.getName(), ".xsd")) {
			filesList.add(fileOrFolder);
		}
	}
}
