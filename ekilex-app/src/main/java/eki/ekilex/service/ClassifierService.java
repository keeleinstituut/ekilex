package eki.ekilex.service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ClassifierFull;
import eki.ekilex.data.ClassifierLabel;
import eki.ekilex.data.Origin;
import eki.ekilex.service.db.ClassifierDbService;
import eki.ekilex.service.db.CommonDataDbService;

@Component
public class ClassifierService implements GlobalConstant, SystemConstant {

	@Autowired
	private ClassifierDbService classifierDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private MaintenanceService maintenanceService;

	private final ClassifierName[] nonEditableClassifNames = new ClassifierName[] {ClassifierName.LABEL_TYPE};

	private final String[] editableLabelTypes = new String[] {CLASSIF_LABEL_TYPE_DESCRIP, CLASSIF_LABEL_TYPE_WORDWEB};

	public List<String> getEditableClassifierNames() {

		EnumSet<ClassifierName> allClassifierNames = EnumSet.allOf(ClassifierName.class);
		List<String> editableClassifierNames = allClassifierNames.stream()
				.filter(classifName -> !ArrayUtils.contains(nonEditableClassifNames, classifName))
				.map(ClassifierName::name)
				.collect(Collectors.toList());

		return editableClassifierNames;
	}

	@Transactional
	public List<String> getDomainOriginCodes() {

		List<Origin> allDomainOrigins = commonDataDbService.getDomainOrigins();
		List<String> allDomainOriginCodes = allDomainOrigins.stream().map(Origin::getCode).collect(Collectors.toList());
		return allDomainOriginCodes;
	}

	@Transactional
	public List<ClassifierFull> getClassifiers(ClassifierName classifierName, String domainOrigin) {

		List<ClassifierFull> classifiers = new ArrayList<>();

		if (ClassifierName.DOMAIN.equals(classifierName) && StringUtils.isNotBlank(domainOrigin)) {
			List<String> domainCodes = classifierDbService.getDomainCodes(domainOrigin);
			for (String domainCode : domainCodes) {
				ClassifierFull classifier = getDomainClassifier(domainOrigin, domainCode);
				classifiers.add(classifier);
			}
			return classifiers;
		}

		List<String> classifierCodes = classifierDbService.getClassifierCodes(classifierName.name());
		for (String classifierCode : classifierCodes) {
			ClassifierFull classifier = getClassifier(classifierName, classifierCode);
			classifiers.add(classifier);
		}
		return classifiers;
	}

	private ClassifierFull getClassifier(ClassifierName classifierName, String classifierCode) {

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
		List<ClassifierLabel> labelRows = new ArrayList<>();

		for (String labelType : editableLabelTypes) {

			ClassifierLabel labelRow = new ClassifierLabel();
			labelRow.setCode(classifierCode);
			labelRow.setType(labelType);

			Map<String, ClassifierLabel> typeLabelsByLang = labels.stream()
					.filter(label -> labelType.equals(label.getType()))
					.collect(Collectors.toMap(ClassifierLabel::getLang, classifierLabel -> classifierLabel));

			setLabelLangValues(labelRow, typeLabelsByLang);
			labelRows.add(labelRow);
		}

		classifier.setLabels(labelRows);
		return classifier;
	}

	private ClassifierFull getDomainClassifier(String domainOrigin, String domainCode) {

		ClassifierFull classifier = new ClassifierFull();
		classifier.setName(ClassifierName.DOMAIN.name());
		classifier.setOrigin(domainOrigin);
		classifier.setCode(domainCode);

		List<ClassifierLabel> labels = classifierDbService.getDomainLabels(domainOrigin, domainCode, CLASSIF_LABEL_TYPE_DESCRIP);
		List<ClassifierLabel> labelRows = new ArrayList<>();

		ClassifierLabel labelRow = new ClassifierLabel();
		labelRow.setCode(domainCode);
		labelRow.setType(CLASSIF_LABEL_TYPE_DESCRIP);

		Map<String, ClassifierLabel> typeLabelsByLang = labels.stream().collect(Collectors.toMap(ClassifierLabel::getLang, classifierLabel -> classifierLabel));

		setLabelLangValues(labelRow, typeLabelsByLang);
		labelRows.add(labelRow);
		classifier.setLabels(labelRows);
		return classifier;
	}

	private void setLabelLangValues(ClassifierLabel labelRow, Map<String, ClassifierLabel> typeLabelsByLang) {

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
	}

	@Transactional
	public boolean createClassifier(String classifierName, String classifierCode, String existingClassifierCode, boolean createBeforeExisting) {

		boolean classifierExists = classifierDbService.classifierExists(classifierName, classifierCode);
		if (classifierExists) {
			return false;
		}

		Long existingClassifierOrderBy = classifierDbService.getClassifierOrderBy(classifierName, existingClassifierCode);
		Long newClassifierOrderby = createBeforeExisting ? existingClassifierOrderBy : existingClassifierOrderBy + 1;
		classifierDbService.increaseClassifiersOrderBy(classifierName, newClassifierOrderby);
		classifierDbService.createClassifier(classifierName, classifierCode, newClassifierOrderby);
		maintenanceService.clearClassifCache();
		return true;
	}

	@Transactional
	public boolean createDomainClassifier(String domainOriginCode, String classifierCode, String existingClassifierCode, boolean createBeforeExisting) {

		boolean classifierExists = classifierDbService.domainClassifierExists(domainOriginCode, classifierCode);
		if (classifierExists) {
			return false;
		}

		Long existingClassifierOrderBy = classifierDbService.getDomainClassifierOrderBy(domainOriginCode, existingClassifierCode);
		Long newClassifierOrderby = createBeforeExisting ? existingClassifierOrderBy : existingClassifierOrderBy + 1;
		classifierDbService.increaseDomainClassifiersOrderBy(newClassifierOrderby);
		classifierDbService.createDomainClassifier(domainOriginCode, classifierCode, newClassifierOrderby);
		maintenanceService.clearClassifCache();
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
		maintenanceService.clearClassifCache();
	}

	@Transactional
	public boolean deleteClassifier(String classifierName, String classifierCode) {

		try {
			classifierDbService.deleteClassifier(classifierName, classifierCode);
			return true;
		} catch (DataAccessException e) {
			// classifier is in use. delete is not possible
			return false;
		}
	}

	@Transactional
	public boolean deleteDomainClassifier(String domainOriginCode, String classifierCode) {

		try {
			classifierDbService.deleteDomainClassifier(domainOriginCode, classifierCode);
			return true;
		} catch (DataAccessException e) {
			// classifier is in use. delete is not possible
			return false;
		}
	}
}
