package eki.ekilex.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.data.Classifier;
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
		List<Dataset> datasets = datasetDbService.getDatasets();

		for (Dataset dataset : datasets) {
			List<Classifier> domains = commonDataDbService.getDatasetClassifiers(ClassifierName.DOMAIN, dataset.getCode());
			List<Classifier> languages = commonDataDbService.getDatasetClassifiers(ClassifierName.LANGUAGE, dataset.getCode());
			List<Classifier> processStates = commonDataDbService.getDatasetClassifiers(ClassifierName.PROCESS_STATE, dataset.getCode());

			//TODO - this need some refactoring and thinking
			domains.forEach(domain -> domain.setValue(String.join(", ", commonDataDbService.getDomainLabels(domain.getCode(), domain.getOrigin()))));

			dataset.setSelectedLanguages(languages);
			dataset.setSelectedProcessStates(processStates);
			dataset.setSelectedDomains(domains);
		}

		return datasets;

	}

	@Transactional
	public void createDataset(Dataset dataset) {
		datasetDbService.createDataset(dataset);

		addDatasetToSelectedClassifiers(dataset);
	}

	private void addDatasetToSelectedClassifiers(Dataset dataset) {
		List<Classifier> selectedDomains = dataset.getSelectedDomains();
		if (selectedDomains != null) {
			for (Classifier classifier : selectedDomains) {
				commonDataDbService.addDatasetCodeToClassifier(ClassifierName.DOMAIN, classifier.getCode(), dataset.getCode(), classifier.getOrigin());
			}
		}

		List<Classifier> selectedLanguages = dataset.getSelectedLanguages();
		if (selectedLanguages != null) {
			for (Classifier classifier : selectedLanguages) {
				commonDataDbService.addDatasetCodeToClassifier(ClassifierName.LANGUAGE, classifier.getCode(), dataset.getCode(), classifier.getOrigin());
			}
		}

		List<Classifier> selectedProcessStates = dataset.getSelectedProcessStates();
		if (selectedProcessStates != null) {
			for (Classifier classifier : selectedProcessStates) {
				commonDataDbService.addDatasetCodeToClassifier(ClassifierName.PROCESS_STATE, classifier.getCode(), dataset.getCode(), classifier.getOrigin());
			}
		}

	}

	private void updateSelectedDatasetClassifiersByCode(ClassifierName classifierName, String datasetCode, List<String> selectedClassifierCodes) {

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

		updateDatasetSelectedClassifiers(dataset.getCode(), dataset.getSelectedDomains(), ClassifierName.DOMAIN);
		updateDatasetSelectedClassifiers(dataset.getCode(), dataset.getSelectedLanguages(), ClassifierName.LANGUAGE);
		updateDatasetSelectedClassifiers(dataset.getCode(), dataset.getSelectedProcessStates(), ClassifierName.PROCESS_STATE);

	}

	private void updateDatasetSelectedClassifiers(String datasetCode, List<Classifier> selectedClassifiers, ClassifierName classifierName) {

		List<Classifier> previousDatasetClassifiers = commonDataDbService.getDatasetClassifiers(classifierName, datasetCode);
		previousDatasetClassifiers
				.stream()
				.filter(c -> selectedClassifiers == null || !selectedClassifiers.contains(c))
				.forEach(c -> commonDataDbService.removeDatasetCodeFromClassifier(classifierName, c.getCode(), datasetCode, c.getOrigin()));

		if (selectedClassifiers != null) {
			for (Classifier classifier : selectedClassifiers) {
				if (!previousDatasetClassifiers.contains(classifier)) {
					commonDataDbService.addDatasetCodeToClassifier(classifierName, classifier.getCode(), datasetCode, classifier.getOrigin());
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
