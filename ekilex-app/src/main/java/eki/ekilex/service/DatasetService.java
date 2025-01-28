package eki.ekilex.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformOwner;
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
	public boolean datasetExists(String code) {
		return datasetDbService.datasetExists(code);
	}

	@Transactional
	public List<Dataset> getDatasets() {
		List<Dataset> datasets = datasetDbService.getDatasets();
		datasets = datasetUtil.resortPriorityDatasets(datasets);
		return datasets;
	}

	@Transactional
	public Dataset getDataset(String datasetCode) {

		Dataset dataset = datasetDbService.getDataset(datasetCode);

		List<Classifier> domains = commonDataDbService.getDatasetClassifiers(ClassifierName.DOMAIN, datasetCode, CLASSIF_LABEL_LANG_EST);
		List<Classifier> languages = commonDataDbService.getDatasetClassifiers(ClassifierName.LANGUAGE, datasetCode, CLASSIF_LABEL_LANG_EST);
		List<Classifier> wordFreeformTypes = commonDataDbService.getFreeformTypes(datasetCode, FreeformOwner.WORD, CLASSIF_LABEL_LANG_EST);
		List<Classifier> lexemeFreeformTypes = commonDataDbService.getFreeformTypes(datasetCode, FreeformOwner.LEXEME, CLASSIF_LABEL_LANG_EST);
		List<Classifier> meaningFreeformTypes = commonDataDbService.getFreeformTypes(datasetCode, FreeformOwner.MEANING, CLASSIF_LABEL_LANG_EST);
		List<Classifier> definitionFreeformTypes = commonDataDbService.getFreeformTypes(datasetCode, FreeformOwner.DEFINITION, CLASSIF_LABEL_LANG_EST);

		List<String> origins = null;
		if (CollectionUtils.isNotEmpty(domains)) {
			origins = domains.stream().map(Classifier::getOrigin).distinct().sorted().collect(Collectors.toList());
		}

		classifierUtil.populateClassifierJson(domains);
		classifierUtil.populateClassifierJson(languages);
		classifierUtil.populateClassifierJson(wordFreeformTypes);
		classifierUtil.populateClassifierJson(lexemeFreeformTypes);
		classifierUtil.populateClassifierJson(meaningFreeformTypes);
		classifierUtil.populateClassifierJson(definitionFreeformTypes);

		dataset.setOrigins(origins);
		dataset.setDomains(domains);
		dataset.setLanguages(languages);
		dataset.setWordFreeformTypes(wordFreeformTypes);
		dataset.setLexemeFreeformTypes(lexemeFreeformTypes);
		dataset.setMeaningFreeformTypes(meaningFreeformTypes);
		dataset.setDefinitionFreeformTypes(definitionFreeformTypes);

		return dataset;

	}

	@Transactional
	public List<Classifier> getDomains(String originCode) {

		List<Classifier> domains = commonDataDbService.getDomains(originCode, CLASSIF_LABEL_LANG_EST);
		classifierUtil.populateClassifierJson(domains);
		return domains;
	}

	@Transactional(rollbackOn = Exception.class)
	public void createDataset(Dataset dataset) {

		datasetDbService.createDataset(dataset);
		addDatasetToSelectedClassifiers(dataset);
		maintenanceService.clearDatasetCache();
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateDataset(Dataset dataset) {

		datasetDbService.updateDataset(dataset);
		removeDatasetFromAllClassifiers(dataset.getCode());
		addDatasetToSelectedClassifiers(dataset);
		maintenanceService.clearDatasetCache();
	}

	@Transactional(rollbackOn = Exception.class)
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

	private void removeDatasetFromAllClassifiers(String datasetCode) {

		datasetDbService.removeDatasetFromClassifiers(ClassifierName.FREEFORM_TYPE, datasetCode);
		datasetDbService.removeDatasetFromClassifiers(ClassifierName.LANGUAGE, datasetCode);
		datasetDbService.removeDatasetFromClassifiers(ClassifierName.DOMAIN, datasetCode);
	}

	private void addDatasetToSelectedClassifiers(Dataset dataset) {

		String datasetCode = dataset.getCode();

		datasetDbService.addDatasetToClassifiers(ClassifierName.FREEFORM_TYPE, datasetCode, dataset.getWordFreeformTypes(), FreeformOwner.WORD);
		datasetDbService.addDatasetToClassifiers(ClassifierName.FREEFORM_TYPE, datasetCode, dataset.getLexemeFreeformTypes(), FreeformOwner.LEXEME);
		datasetDbService.addDatasetToClassifiers(ClassifierName.FREEFORM_TYPE, datasetCode, dataset.getMeaningFreeformTypes(), FreeformOwner.MEANING);
		datasetDbService.addDatasetToClassifiers(ClassifierName.FREEFORM_TYPE, datasetCode, dataset.getDefinitionFreeformTypes(), FreeformOwner.DEFINITION);
		datasetDbService.addDatasetToClassifiers(ClassifierName.LANGUAGE, datasetCode, dataset.getLanguages());
		datasetDbService.addDatasetToClassifiers(ClassifierName.DOMAIN, datasetCode, dataset.getDomains());
	}

}
