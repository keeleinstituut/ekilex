package eki.ekilex.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.DatasetDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.util.DatasetUtil;
import eki.ekilex.web.util.ClassifierUtil;

@Component
public class DatasetService implements SystemConstant, GlobalConstant {

	@Autowired
	private DatasetDbService datasetDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private DatasetUtil datasetUtil;

	@Autowired
	private MaintenanceService maintenanceService;

	@Transactional
	public List<Dataset> getDatasets() {
		List<Dataset> datasets = datasetDbService.getDatasets();
		datasets = datasetUtil.removePlaceholderDataset(datasets);
		datasets = datasetUtil.resortPriorityDatasets(datasets);
		return datasets;
	}

	@Transactional
	public Dataset getDataset(String datasetCode) {
		Dataset dataset = datasetDbService.getDataset(datasetCode);
		List<Classifier> domains = getDatasetDomains(dataset.getCode());
		List<Classifier> languages = getDatasetClassifiers(ClassifierName.LANGUAGE, dataset.getCode());
		dataset.setDomains(domains);
		dataset.setLanguages(languages);

		if (CollectionUtils.isNotEmpty(domains)) {
			List<String> origins = domains.stream().map(Classifier::getOrigin).distinct().sorted().collect(Collectors.toList());
			dataset.setOrigins(origins);
		}
		return dataset;

	}

	@Transactional
	public void createDataset(Dataset dataset) {
		datasetDbService.createDataset(dataset);
		addDatasetToSelectedClassifiers(dataset);
		maintenanceService.clearDatasetCache();
	}

	@Transactional
	public void updateDataset(Dataset dataset) {
		datasetDbService.updateDataset(dataset);
		updateDatasetSelectedClassifiers(dataset);
		maintenanceService.clearDatasetCache();
	}

	private void updateDatasetSelectedClassifiers(Dataset dataset) {
		removeDatasetFromAllClassifiers(dataset.getCode());
		addDatasetToSelectedClassifiers(dataset);
	}

	@Transactional
	public void deleteDataset(String datasetCode) {
		removeDatasetFromAllClassifiers(datasetCode);
		datasetDbService.removeDatasetFromUserProfile(datasetCode);
		permissionDbService.deleteDatasetPermissions(datasetCode);
		datasetDbService.deleteDataset(datasetCode);
		maintenanceService.clearDatasetCache();
	}

	@Transactional
	public boolean datasetCodeExists(String code) {
		return datasetDbService.datasetCodeExists(code);
	}

	private void addDatasetToSelectedClassifiers(Dataset dataset) {
		datasetDbService.addDatasetToClassifier(ClassifierName.LANGUAGE, dataset.getCode(), dataset.getLanguages());
		datasetDbService.addDatasetToClassifier(ClassifierName.DOMAIN, dataset.getCode(), dataset.getDomains());
	}

	private void removeDatasetFromAllClassifiers(String datasetCode) {
		datasetDbService.removeDatasetFromAllClassifiers(ClassifierName.LANGUAGE, datasetCode);
		datasetDbService.removeDatasetFromAllClassifiers(ClassifierName.DOMAIN, datasetCode);
	}

	private List<Classifier> getDatasetClassifiers(ClassifierName classifierName, String datasetCode) {
		List<Classifier> classifiers = commonDataDbService.getDatasetClassifiers(classifierName, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		classifierUtil.populateClassifierJson(classifiers);
		return classifiers;
	}

	private List<Classifier> getDatasetDomains(String datasetCode) {
		List<Classifier> domains = commonDataDbService.getDatasetDomains(datasetCode);
		classifierUtil.populateClassifierJson(domains);
		return domains;
	}

	@Transactional
	public List<Classifier> getDomains(String originCode) {
		List<Classifier> domains = commonDataDbService.getDomains(originCode, CLASSIF_LABEL_TYPE_DESCRIP);
		classifierUtil.populateClassifierJson(domains);
		return domains;
	}

}
