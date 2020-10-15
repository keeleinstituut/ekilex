package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierFull;
import eki.ekilex.data.ClassifierLabel;
import eki.ekilex.service.db.ClassifierDbService;

@Component
public class ClassifierService implements GlobalConstant {

	@Autowired
	private ClassifierDbService classifierDbService;

	@Transactional
	public ClassifierFull getClassifier(ClassifierName classifierName, String classifierCode) {

		boolean hasLabel = classifierName.hasLabel();
		String name = classifierName.name();

		ClassifierFull classifier = new ClassifierFull();
		classifier.setName(name);
		classifier.setCode(classifierCode);
		classifier.setHasLabel(hasLabel);

		if (!hasLabel) {
			return classifier;
		}

		List<ClassifierLabel> labels = classifierDbService.getClassifierLabels(name, classifierCode);
		List<String> labelTypes = classifierDbService.getLabelTypeCodes();
		List<ClassifierLabel> labelRows = new ArrayList<>();

		for (String labelType : labelTypes) {

			ClassifierLabel labelRow = new ClassifierLabel();
			labelRow.setCode(classifierCode);
			labelRow.setType(labelType);

			Map<String, ClassifierLabel> typeLabelsByLang = labels.stream()
					.filter(label -> labelType.equals(label.getType()))
					.collect(Collectors.toMap(ClassifierLabel::getLang, classifierLabel -> classifierLabel));

			ClassifierLabel labelEst = typeLabelsByLang.get(LANGUAGE_CODE_EST);
			if (labelEst != null) {
				labelRow.setLabelEst(labelEst.getValue());
			}

			ClassifierLabel labelEng = typeLabelsByLang.get(LANGUAGE_CODE_ENG);
			if (labelEng != null) {
				labelRow.setLabelEng(labelEng.getValue());
			}

			ClassifierLabel labelRus = typeLabelsByLang.get(LANGUAGE_CODE_RUS);
			if (labelRus != null) {
				labelRow.setLabelRus(labelRus.getValue());
			}

			labelRows.add(labelRow);
		}

		classifier.setLabels(labelRows);
		return classifier;
	}

	@Transactional
	public ClassifierFull getEmptyClassifier(ClassifierName classifierName) {

		boolean hasLabel = classifierName.hasLabel();
		String name = classifierName.name();

		ClassifierFull classifier = new ClassifierFull();
		classifier.setName(name);
		classifier.setHasLabel(hasLabel);

		if (!hasLabel) {
			return classifier;
		}

		List<String> labelTypes = classifierDbService.getLabelTypeCodes();
		List<ClassifierLabel> labelRows = new ArrayList<>();

		for (String labelType : labelTypes) {
			ClassifierLabel labelRow = new ClassifierLabel();
			labelRow.setType(labelType);
			labelRows.add(labelRow);
		}

		classifier.setLabels(labelRows);
		return classifier;
	}

	@Transactional
	public List<String> getLabelTypeCodes() {
		return classifierDbService.getLabelTypeCodes();
	}

	@Transactional
	public boolean createClassifier(List<ClassifierLabel> classifierLabels) {

		ClassifierLabel label = classifierLabels.get(0);
		String classifierName = label.getClassifierName().name();
		String classifierCode = label.getCode();
		boolean classifierExists = classifierDbService.classifierExists(classifierName, classifierCode);
		if (classifierExists) {
			return false;
		}

		classifierDbService.createClassifier(classifierName, classifierCode);
		for (ClassifierLabel classifierLabel : classifierLabels) {
			if (StringUtils.isNotBlank(classifierLabel.getValue())) {
				classifierDbService.createOrUpdateClassifierLabel(classifierLabel);
			}
		}
		return true;
	}

	@Transactional
	public boolean createClassifier(String classifierName, String classifierCode) {

		boolean classifierExists = classifierDbService.classifierExists(classifierName, classifierCode);
		if (classifierExists) {
			return false;
		}

		classifierDbService.createClassifier(classifierName, classifierCode);
		return true;
	}

	@Transactional
	public void updateClassifier(List<ClassifierLabel> classifierLabels) {

		for (ClassifierLabel classifierLabel : classifierLabels) {
			if (StringUtils.isNotBlank(classifierLabel.getValue())) {
				classifierDbService.createOrUpdateClassifierLabel(classifierLabel);
			} else {
				classifierDbService.deleteClassifierLabel(classifierLabel);
			}
		}
	}

	@Transactional
	public void applyLabelsTextAgg(List<Classifier> classifiers) {

		for (Classifier classifier : classifiers) {
			String classifierName = classifier.getName();
			String classifierCode = classifier.getCode();
			String labelsTextAgg = classifierDbService.getClassifierLabelsAggregation(classifierName, classifierCode);
			classifier.setLabelsTextAgg(labelsTextAgg);
		}
	}
}
