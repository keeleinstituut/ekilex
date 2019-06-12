package eki.ekilex.service;

import java.util.List;

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

	private void addDatasetToSelectedClassifiers(ClassifierName classifierName, String datasetCode, List<Classifier> selectedClassifiers) {
		if (selectedClassifiers != null) {
			for (Classifier classifier : selectedClassifiers) {
				commonDataDbService.addDatasetCodeToClassifier(classifierName, classifier.getCode(), datasetCode, classifier.getOrigin());
			}
		}

	}
	private void addDatasetToSelectedClassifiers(Dataset dataset) {
		addDatasetToSelectedClassifiers(ClassifierName.DOMAIN, dataset.getCode(), dataset.getSelectedDomains());
		addDatasetToSelectedClassifiers(ClassifierName.LANGUAGE, dataset.getCode(), dataset.getSelectedLanguages());
		addDatasetToSelectedClassifiers(ClassifierName.PROCESS_STATE, dataset.getCode(), dataset.getSelectedProcessStates());
	}

	@Transactional
	public void updateDataset(Dataset dataset) {
		datasetDbService.updateDataset(dataset);
		updateDatasetSelectedClassifiers(dataset);
	}

	private  void  updateDatasetSelectedClassifiers(Dataset dataset) {
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
