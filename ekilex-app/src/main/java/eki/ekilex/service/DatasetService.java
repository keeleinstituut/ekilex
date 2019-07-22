package eki.ekilex.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.DatasetDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.web.util.ClassifierUtil;

@Component
public class DatasetService implements SystemConstant {

	@Autowired
	private DatasetDbService datasetDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Transactional
	public List<Dataset> getDatasets() {
		return datasetDbService.getDatasets();
	}

	@Transactional
	public Dataset getDataset(String datasetCode) {
		Dataset dataset = datasetDbService.getDataset(datasetCode);
		List<Classifier> domains = getDatasetDomains(dataset.getCode());
		List<Classifier> languages = getDatasetClassifiers(ClassifierName.LANGUAGE, dataset.getCode());
		List<Classifier> processStates = getDatasetClassifiers(ClassifierName.PROCESS_STATE, dataset.getCode());
		dataset.setDomains(domains);
		dataset.setLanguages(languages);
		dataset.setProcessStates(processStates);

		if (CollectionUtils.isNotEmpty(domains)) {
			dataset.setOrigins(
					domains
							.stream()
							.map(Classifier::getOrigin)
							.distinct()
							.sorted()
							.collect(Collectors.toList()));
		}
		return dataset;

	}


		@Transactional
	public void createDataset(Dataset dataset) {
		datasetDbService.createDataset(dataset);
		addDatasetToSelectedClassifiers(dataset);
	}

	@Transactional
	public void updateDataset(Dataset dataset) {
		datasetDbService.updateDataset(dataset);
		updateDatasetSelectedClassifiers(dataset);
	}

	private void updateDatasetSelectedClassifiers(Dataset dataset) {
		removeDatasetFromAllClassifiers(dataset.getCode());
		addDatasetToSelectedClassifiers(dataset);
	}

	@Transactional
	public void deleteDataset(String datasetCode) {
		removeDatasetFromAllClassifiers(datasetCode);

		permissionDbService.deleteDatasetPermissions(datasetCode);
		datasetDbService.deleteDataset(datasetCode);
	}

	@Transactional
	public boolean datasetCodeExists(String code) {
		return datasetDbService.datasetCodeExists(code);
	}

	private void addDatasetToSelectedClassifiers(Dataset dataset) {
		datasetDbService.addDatasetToClassifier(ClassifierName.LANGUAGE, dataset.getCode(), dataset.getLanguages());
		datasetDbService.addDatasetToClassifier(ClassifierName.PROCESS_STATE, dataset.getCode(), dataset.getProcessStates());
		datasetDbService.addDatasetToClassifier(ClassifierName.DOMAIN, dataset.getCode(), dataset.getDomains());
	}

	private void removeDatasetFromAllClassifiers(String datasetCode) {
		datasetDbService.removeDatasetFromAllClassifiers(ClassifierName.LANGUAGE, datasetCode);
		datasetDbService.removeDatasetFromAllClassifiers(ClassifierName.PROCESS_STATE, datasetCode);
		datasetDbService.removeDatasetFromAllClassifiers(ClassifierName.DOMAIN, datasetCode);
	}

	private List<Classifier> getDatasetClassifiers(ClassifierName classifierName, String datasetCode) {
		List<Classifier> classifiers = commonDataDbService.getDatasetClassifiers(classifierName, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		populateClassifierJson(classifiers);

		return classifiers;
	}

	private List<Classifier> getDatasetDomains(String datasetCode) {
		List<Classifier> domains = commonDataDbService.getDatasetDomains(datasetCode);
		populateClassifierJson(domains);

		return domains;
	}

	@Transactional
	public List<Classifier> findDomainsByOrigin(String originCode) {
		List<Classifier> domains = commonDataDbService.findDomainsByOriginCode(originCode, CLASSIF_LABEL_TYPE_DESCRIP);
		populateClassifierJson(domains);

		return domains;
	}

	private void populateClassifierJson(List<Classifier> classifiers) {
		classifiers.forEach(c -> c.setJsonStr(classifierUtil.toJson(c)));
	}

}
