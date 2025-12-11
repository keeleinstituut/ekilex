package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

	private static final ClassifierName[] NON_EDITABLE_CLASSIFIER_NAMES = new ClassifierName[] {ClassifierName.LABEL_TYPE};

	private static final ClassifierName[] OS_CLASSIFIER_NAMES = new ClassifierName[] {
			ClassifierName.DISPLAY_MORPH,
			ClassifierName.WORD_TYPE,
			ClassifierName.REGISTER,
			ClassifierName.VALUE_STATE};

	@Autowired
	private ClassifierDbService classifierDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private MaintenanceService maintenanceService;

	public List<String> getEditableClassifierNames() {

		List<String> editableClassifierNames = Arrays.stream(ClassifierName.values())
				.filter(classifName -> !ArrayUtils.contains(NON_EDITABLE_CLASSIFIER_NAMES, classifName))
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

		List<String> labelTypes;
		boolean hasLabel = classifierName.hasLabel();
		boolean isOs = ArrayUtils.contains(OS_CLASSIFIER_NAMES, classifierName);
		if (hasLabel) {
			if (isOs) {
				labelTypes = Arrays.asList(
						CLASSIF_LABEL_TYPE_DESCRIP,
						CLASSIF_LABEL_TYPE_WORDWEB,
						CLASSIF_LABEL_TYPE_OS,
						CLASSIF_LABEL_TYPE_COMMENT);
			} else {
				labelTypes = Arrays.asList(
						CLASSIF_LABEL_TYPE_DESCRIP,
						CLASSIF_LABEL_TYPE_WORDWEB,
						CLASSIF_LABEL_TYPE_COMMENT);
			}
		} else {
			labelTypes = Collections.emptyList();
		}
		List<ClassifierFull> classifiers;
		if (ClassifierName.DOMAIN.equals(classifierName) && StringUtils.isNotBlank(domainOrigin)) {
			classifiers = classifierDbService.getClassifierFulls(classifierName, domainOrigin, labelTypes);
		} else {
			classifiers = classifierDbService.getClassifierFulls(classifierName, labelTypes);
		}
		if (hasLabel && CollectionUtils.isNotEmpty(classifiers)) {
			transformLabels(labelTypes, classifierName, domainOrigin, classifiers);
		}
		return classifiers;
	}

	private void transformLabels(List<String> labelTypes, ClassifierName classifierName, String domainOrigin, List<ClassifierFull> classifiers) {

		boolean hasLabel = classifierName.hasLabel();

		for (ClassifierFull classifier : classifiers) {

			String code = classifier.getCode();
			List<ClassifierLabel> allLabels = classifier.getLabels();
			List<ClassifierLabel> typeLabels = new ArrayList<>();

			Map<String, List<ClassifierLabel>> labelTypeMap = null;
			if (CollectionUtils.isNotEmpty(allLabels)) {
				labelTypeMap = allLabels.stream().collect(Collectors.groupingBy(ClassifierLabel::getType));
			}

			for (String type : labelTypes) {

				ClassifierLabel typeLabel = new ClassifierLabel();
				List<ClassifierLabel> labelsOfType = null;
				if (labelTypeMap != null) {
					labelsOfType = labelTypeMap.get(type);
				}
				if (labelsOfType != null) {
					Map<String, String> labelValueLangMap = labelsOfType.stream()
							.collect(Collectors.toMap(ClassifierLabel::getLang, classifierLabel -> classifierLabel.getValue()));

					typeLabel.setLabelEst(labelValueLangMap.get(LANGUAGE_CODE_EST));
					typeLabel.setLabelEng(labelValueLangMap.get(LANGUAGE_CODE_ENG));
					typeLabel.setLabelRus(labelValueLangMap.get(LANGUAGE_CODE_RUS));
				}
				typeLabel.setClassifierName(classifierName);
				typeLabel.setOrigin(domainOrigin);
				typeLabel.setCode(code);
				typeLabel.setType(type);
				typeLabels.add(typeLabel);
			}

			classifier.setLabels(typeLabels);
			classifier.setHasLabel(hasLabel);
		}
	}

	@Transactional(rollbackFor = Exception.class)
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

	@Transactional(rollbackFor = Exception.class)
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

	@Transactional(rollbackFor = Exception.class)
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
