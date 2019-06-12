package eki.ekilex.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.CodeOriginTuple;
import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.DatasetDbService;

@Component
public class DatasetService {

	@Autowired
	private DatasetDbService datasetDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;


	@Transactional
	public List<Dataset> getDatasets() {
		return datasetDbService.getDatasets();

		// FIXME
		// fill domain labels for selected domains -
		//TODO can be done by some jooq trick or just to take the first label from domain_label?
		// domainCodeOrigins.forEach(
		// 		codeOriginTuple -> codeOriginTuple.setValue(
		// 				String.join(", ", commonDataDbService.getDomainLabels(codeOriginTuple.getCode(), codeOriginTuple.getOrigin()))));

	}

	@Transactional
	public void createDataset(Dataset dataset) {
		datasetDbService.createDataset(dataset);

		addDatasetToSelectedClassifiers(dataset.getCode(), ClassifierName.LANGUAGE, dataset.getSelectedLanguageCodes());
		addDatasetToSelectedClassifiers(dataset.getCode(), ClassifierName.PROCESS_STATE, dataset.getSelectedProcessStateCodes());

		addDatasetToSelectedDomains(dataset);

	}

	private void addDatasetToSelectedClassifiers(String datasetCode, ClassifierName classifierName, List<String> selectedClassifierCodes) {
		if (selectedClassifierCodes != null) {
			for (String code : selectedClassifierCodes) {
				commonDataDbService.addDatasetCodeToClassifier(classifierName, code, datasetCode, null);
			}
		}
	}

	private void addDatasetToSelectedDomains(Dataset dataset) {
		List<CodeOriginTuple> selectedClassifierCodeOrigins = dataset.getSelectedDomainCodeOriginPairs();
		if (selectedClassifierCodeOrigins != null) {
			for (CodeOriginTuple codeOrigin : selectedClassifierCodeOrigins) {
				commonDataDbService.addDatasetCodeToClassifier(ClassifierName.DOMAIN, codeOrigin.getCode(), dataset.getCode(), codeOrigin.getOrigin());
			}
		}
	}

	private void updateSelectedDatasetClassifiers(ClassifierName classifierName, String datasetCode, List<String> selectedClassifierCodes) {

		List<String> existingClassifierCodes = commonDataDbService.getDatasetClassifiers(classifierName, datasetCode)
				.stream()
				.map(Classifier::getCode)
				.collect(Collectors.toList());

		// remove dataset code from Classifier if was unselected
		existingClassifierCodes
				.stream()
				.filter(code -> selectedClassifierCodes == null || !selectedClassifierCodes.contains(code))
				.forEach(code -> commonDataDbService.removeDatasetCodeFromClassifier(classifierName, code, datasetCode, null));

		if (selectedClassifierCodes != null) {
			for (String classifCode : selectedClassifierCodes) {
				if (!existingClassifierCodes.contains(classifCode)) {
					commonDataDbService.addDatasetCodeToClassifier(classifierName, classifCode, datasetCode, null);
				}
			}
		}
	}


	@Transactional
	public void updateDataset(Dataset dataset) {
		datasetDbService.updateDataset(dataset);

		updateSelectedDatasetClassifiers(ClassifierName.LANGUAGE, dataset.getCode(), dataset.getSelectedLanguageCodes());
		updateSelectedDatasetClassifiers(ClassifierName.PROCESS_STATE, dataset.getCode(), dataset.getSelectedProcessStateCodes());
		updateDatasetDomains(dataset);

	}

	private void updateDatasetDomains(Dataset dataset) {

		List<CodeOriginTuple> existingClassifierCodeOrigins = commonDataDbService.getDatasetClassifiers(ClassifierName.DOMAIN, dataset.getCode())
				.stream()
				.map(classifier -> {
					CodeOriginTuple tuple = new CodeOriginTuple();
					tuple.setCode(classifier.getCode());
					tuple.setOrigin(classifier.getOrigin());
					return tuple;
				})
				.collect(Collectors.toList());

		// remove dataset code from Classifier if was unselected
		existingClassifierCodeOrigins
				.stream()
				.filter(codeOriginTuple ->
						dataset.getSelectedDomainCodeOriginPairs() == null || !dataset.getSelectedDomainCodeOriginPairs().contains(codeOriginTuple))
				.forEach(codeOriginTuple ->
						commonDataDbService.removeDatasetCodeFromClassifier(ClassifierName.DOMAIN, codeOriginTuple.getCode(), dataset.getCode(), codeOriginTuple.getOrigin()));

		if (dataset.getSelectedDomainCodeOriginPairs() != null) {
			for (CodeOriginTuple codeOriginTuple : dataset.getSelectedDomainCodeOriginPairs()) {
				if (!existingClassifierCodeOrigins.contains(codeOriginTuple)) {
					commonDataDbService.addDatasetCodeToClassifier(ClassifierName.DOMAIN, codeOriginTuple.getCode(), dataset.getCode(), codeOriginTuple.getOrigin());
				}
			}
		}
	}

	@Transactional
	public void deleteDataset(String datasetCode) {

		// delete classifiers
		removeDatasetClassifiers(ClassifierName.LANGUAGE, datasetCode);
		removeDatasetClassifiers(ClassifierName.PROCESS_STATE, datasetCode);
		removeDatasetClassifiers(ClassifierName.DOMAIN, datasetCode);

		datasetDbService.deleteDataset(datasetCode);
	}

	@Transactional
	public boolean datasetCodeExists(String code) {
		return datasetDbService.datasetCodeExists(code);
	}


	private void removeDatasetClassifiers(ClassifierName classifierName, String datasetCode) {
		List<Classifier> existingClassifiers = commonDataDbService.getDatasetClassifiers(classifierName, datasetCode);
		existingClassifiers.forEach(classifier -> commonDataDbService.removeDatasetCodeFromClassifier(classifierName, classifier.getCode(), datasetCode, classifier.getOrigin()));
	}

}
