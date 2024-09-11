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

	private static final ClassifierName[] nonEditableClassifNames = new ClassifierName[] {ClassifierName.LABEL_TYPE};

	private static final String[] editableLabelTypes = new String[] {CLASSIF_LABEL_TYPE_DESCRIP, CLASSIF_LABEL_TYPE_WORDWEB};

	@Autowired
	private ClassifierDbService classifierDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private MaintenanceService maintenanceService;

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
		int order = 1;

		if (ClassifierName.DOMAIN.equals(classifierName) && StringUtils.isNotBlank(domainOrigin)) {
			List<String> domainCodes = classifierDbService.getDomainCodes(domainOrigin);
			for (String domainCode : domainCodes) {
				ClassifierFull classifier = getDomainClassifier(domainOrigin, domainCode);
				classifier.setOrder(order++);
				classifiers.add(classifier);
			}
			return classifiers;
		}

		List<String> classifierCodes = classifierDbService.getClassifierCodes(classifierName.name());
		for (String classifierCode : classifierCodes) {
			ClassifierFull classifier = getClassifier(classifierName, classifierCode);
			classifier.setOrder(order++);
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
	public boolean createClassifier(ClassifierFull classifier) {

		String classifierName = classifier.getName();
		String domainOrigin = classifier.getOrigin();
		boolean isDomainClassifier = isDomainClassifier(classifierName, domainOrigin);

		boolean classifierCreated;
		if (isDomainClassifier) {
			classifierCreated = createDomainClassifier(classifier);
		} else {
			classifierCreated = createRegularClassifier(classifier);
		}
		return classifierCreated;
	}

	private boolean createRegularClassifier(ClassifierFull classifier) {

		String classifierCode = classifier.getCode();
		String classifierName = classifier.getName();

		boolean classifierExists = classifierDbService.classifierExists(classifierName, classifierCode);
		if (classifierExists) {
			return false;
		}

		classifierDbService.createClassifier(classifierName, classifierCode);
		updateClassifierOrderBys(classifier);

		maintenanceService.clearClassifCache();
		return true;
	}

	public boolean createDomainClassifier(ClassifierFull classifier) {

		String domainOrigin = classifier.getOrigin();
		String domainCode = classifier.getCode();

		boolean classifierExists = classifierDbService.domainExists(domainOrigin, domainCode);
		if (classifierExists) {
			return false;
		}

		classifierDbService.createDomain(domainOrigin, domainCode);
		updateDomainClassifierOrderBys(classifier);

		maintenanceService.clearClassifCache();
		return true;

	}

	@Transactional
	public void updateClassifier(ClassifierFull classifier) {

		String classifierName = classifier.getName();
		String domainOrigin = classifier.getOrigin();
		boolean isDomainClassifier = isDomainClassifier(classifierName, domainOrigin);
		if (isDomainClassifier) {
			updateDomainClassifierOrderBys(classifier);
		} else {
			updateClassifierOrderBys(classifier);
		}

		List<ClassifierLabel> classifierLabels = classifier.getLabels();
		for (ClassifierLabel classifierLabel : classifierLabels) {
			if (StringUtils.isNotBlank(classifierLabel.getValue())) {
				classifierDbService.createOrUpdateClassifierLabel(classifierLabel);
			} else {
				classifierDbService.deleteClassifierLabel(classifierLabel);
			}
		}
		maintenanceService.clearClassifCache();
	}

	private void updateClassifierOrderBys(ClassifierFull classifier) {

		String classifierName = classifier.getName();
		String classifierCode = classifier.getCode();
		Integer classifierOrder = classifier.getOrder();
		Long oldOrderBy = classifierDbService.getClassifierOrderBy(classifierName, classifierCode);
		Long newOrderBy = classifierDbService.getClassifierOrderByOrMaxOrderBy(classifierName, classifierOrder);

		if (!oldOrderBy.equals(newOrderBy)) {
			boolean isIncrease = newOrderBy > oldOrderBy;
			List<Long> orderByList;

			if (isIncrease) {
				orderByList = classifierDbService.getClassifierOrderByIntervalList(classifierName, oldOrderBy, newOrderBy);
				orderByList.remove(0);
				classifierDbService.reduceClassifierOrderBys(classifierName, orderByList);
			} else {
				orderByList = classifierDbService.getClassifierOrderByIntervalList(classifierName, newOrderBy, oldOrderBy);
				orderByList.remove(orderByList.size() - 1);
				classifierDbService.increaseClassifierOrderBys(classifierName, orderByList);
			}

			classifierDbService.updateClassifierOrderBy(classifierName, classifierCode, newOrderBy);
		}
	}

	private void updateDomainClassifierOrderBys(ClassifierFull classifier) {

		String domainOrigin = classifier.getOrigin();
		String domainCode = classifier.getCode();
		Integer domainOrder = classifier.getOrder();
		Long oldOrderBy = classifierDbService.getDomainOrderBy(domainOrigin, domainCode);
		Long newOrderBy = classifierDbService.getDomainOrderByOrMaxOrderBy(domainOrigin, domainOrder);

		if (!oldOrderBy.equals(newOrderBy)) {
			boolean isIncrease = newOrderBy > oldOrderBy;
			List<Long> orderByList;

			if (isIncrease) {
				orderByList = classifierDbService.getDomainOrderByIntervalList(domainOrigin, oldOrderBy, newOrderBy);
				orderByList.remove(0);
				classifierDbService.reduceDomainOrderBys(domainOrigin, orderByList);
			} else {
				orderByList = classifierDbService.getDomainOrderByIntervalList(domainOrigin, newOrderBy, oldOrderBy);
				orderByList.remove(orderByList.size() - 1);
				classifierDbService.increaseDomainOrderBys(domainOrigin, orderByList);
			}

			classifierDbService.updateDomainOrderBy(domainOrigin, domainCode, newOrderBy);
		}
	}

	@Transactional
	public boolean deleteClassifier(ClassifierFull classifier) {

		String classifierName = classifier.getName();
		String classifierCode = classifier.getCode();
		String domainOrigin = classifier.getOrigin();
		boolean isDomainClassifier = isDomainClassifier(classifierName, domainOrigin);

		try {
			if (isDomainClassifier) {
				classifierDbService.deleteDomain(domainOrigin, classifierCode);
			} else {
				classifierDbService.deleteClassifier(classifierName, classifierCode);
			}
			maintenanceService.clearClassifCache();
			return true;
		} catch (DataAccessException e) {
			// classifier is in use. delete is not possible
			return false;
		}
	}

	private boolean isDomainClassifier(String classifierName, String domainOrigin) {

		return ClassifierName.DOMAIN.name().equals(classifierName) && StringUtils.isNotBlank(domainOrigin);
	}
}
