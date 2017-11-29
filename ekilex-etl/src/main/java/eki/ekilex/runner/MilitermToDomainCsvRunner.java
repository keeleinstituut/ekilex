package eki.ekilex.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.ekilex.data.transform.ClassifierMapping;

@Component
public class MilitermToDomainCsvRunner extends AbstractDomainRunner {

	private static Logger logger = LoggerFactory.getLogger(MilitermToDomainCsvRunner.class);

	private static final String DOMAIN_EKI_ORIGIN = "militerm";

	private static final String DOMAIN_LANG_ENG = "eng";

	private static final String DOMAIN_LANG_EST = "est";

	@Override
	void initialise() throws Exception {

	}

	public void execute(String sourceCsvFilePath) throws Exception {

		File sourceCsvFile = new File(sourceCsvFilePath);
		List<String> sourceCsvLines = readFileLines(sourceCsvFile);
		sourceCsvLines.remove(0);

		List<ClassifierMapping> sourceClassifiers = loadSourceClassifiers(sourceCsvLines);
		List<ClassifierMapping> existingClassifiers = loadExistingDomainClassifierMappings();
		List<ClassifierMapping> targetClassifiers = merge(sourceClassifiers, existingClassifiers);
		targetClassifiers.sort(Comparator.comparing(ClassifierMapping::getEkiOrigin).thenComparing(ClassifierMapping::getOrder));

		writeDomainClassifierCsvFile(targetClassifiers);

		logger.debug("Done. Recompiled {} rows", targetClassifiers.size());
	}

	private List<ClassifierMapping> loadSourceClassifiers(List<String> sourceCsvLines) {

		List<String> classifierOrderList = new ArrayList<>();
		Map<String, String> classifierEngValueMap = new HashMap<>();
		Map<String, String> classifierEstValueMap = new HashMap<>();
		Map<String, String> classifierHierarchyMap = new HashMap<>();
		String[] csvCells;
		for (String csvLine : sourceCsvLines) {
			csvCells = StringUtils.split(csvLine, CSV_SEPARATOR);
			String code = csvCells[0];
			String engValue = csvCells[1];
			String estValue = csvCells[2];
			classifierOrderList.add(code);
			classifierEngValueMap.put(code, engValue);
			classifierEstValueMap.put(code, estValue);
		}
		String suggestedParentClassifierCode;
		int subClassifierCodeLength;
		for (String subClassifierCode : classifierOrderList) {
			subClassifierCodeLength = subClassifierCode.length();
			suggestedParentClassifierCode = StringUtils.left(subClassifierCode, subClassifierCodeLength - 1);
			if (classifierEngValueMap.containsKey(suggestedParentClassifierCode)) {
				classifierHierarchyMap.put(subClassifierCode, suggestedParentClassifierCode);
			}
		}
		List<ClassifierMapping> loadedClassifiers = new ArrayList<>();
		ClassifierMapping classifier;
		int order = 0;
		for (String classifierCode : classifierOrderList) {
			String parentClassifierCode = classifierHierarchyMap.get(classifierCode);
			if (StringUtils.isBlank(parentClassifierCode)) {
				parentClassifierCode = String.valueOf(CSV_EMPTY_CELL);
			}

			order++;
			classifier = composeClassifier(classifierCode, parentClassifierCode, classifierEngValueMap, DOMAIN_LANG_ENG, order);
			loadedClassifiers.add(classifier);

			order++;
			classifier = composeClassifier(classifierCode, parentClassifierCode, classifierEstValueMap, DOMAIN_LANG_EST, order);
			loadedClassifiers.add(classifier);
		}
		return loadedClassifiers;
	}

	private ClassifierMapping composeClassifier(String classifierCode, String parentClassifierCode, Map<String, String> classifierValueMap, String classifierValueLang, int order) {

		String classifierValue = classifierValueMap.get(classifierCode);
		String ekiKey = composeRow(CLASSIFIER_KEY_SEPARATOR, DOMAIN_EKI_ORIGIN, classifierCode, parentClassifierCode, classifierValue, classifierValueLang);
		ClassifierMapping classifier = new ClassifierMapping();
		classifier.setEkiOrigin(DOMAIN_EKI_ORIGIN);
		classifier.setEkiCode(classifierCode);
		classifier.setEkiParentCode(parentClassifierCode);
		classifier.setEkiValue(classifierValue);
		classifier.setEkiValueLang(classifierValueLang);
		classifier.setOrder(order);
		classifier.setEkiKey(ekiKey);
		return classifier;
	}
}
