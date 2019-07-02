package eki.ekilex.service;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.Word;
import eki.ekilex.service.db.CommonDataDbService;

@Component
public class CommonDataService implements SystemConstant {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Transactional
	public List<Dataset> getDatasets() {
		return commonDataDbService.getDatasets();
	}

	@Transactional
	public List<String> getDatasetCodes() {
		List<String> datasetCodes = getDatasets().stream().map(Dataset::getCode).collect(Collectors.toList());
		return datasetCodes;
	}

	@Transactional
	public List<Classifier> getLanguages() {
		return commonDataDbService.getLanguages(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public Map<String, List<Classifier>> getDomainsInUseByOrigin() {
		List<Classifier> domains = commonDataDbService.getDomainsInUse();
		return domains.stream().collect(groupingBy(Classifier::getOrigin));
	}


	@Transactional
	public Map<String, List<Classifier>> getDatasetDomainsByOrigin(String datasetCode) {
		List<Classifier> domains = commonDataDbService.getDatasetClassifiers(ClassifierName.DOMAIN, datasetCode,
					CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		return domains.stream().collect(groupingBy(Classifier::getOrigin));
	}

	@Transactional
	public Map<String, List<Classifier>> getAllDomainsByOrigin() {
		List<Classifier> domains = domains = commonDataDbService.getDomains();
		return domains.stream().collect(groupingBy(Classifier::getOrigin));
	}

	@Transactional
	public List<Classifier> getFrequencyGroups() {
		return commonDataDbService.getFrequencyGroups();
	}

	@Transactional
	public List<Classifier> getMorphs() {
		return commonDataDbService.getMorphs(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getGenders() {
		return commonDataDbService.getGenders(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getWordTypes() {
		return commonDataDbService.getWordTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getAspects() {
		return commonDataDbService.getAspects(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getWordRelationTypes() {
		return commonDataDbService.getWordRelationTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
	}

	@Transactional
	public List<Classifier> getLexemeRelationTypes() {
		return commonDataDbService.getLexemeRelationTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
	}

	@Transactional
	public List<Classifier> getMeaningRelationTypes() {
		return commonDataDbService.getMeaningRelationTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getDefinitionTypes() {
		return commonDataDbService.getDefinitionTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getPoses() {
		return commonDataDbService.getPoses(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getRegisters() {
		return commonDataDbService.getRegisters(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getRegions() {
		return commonDataDbService.getRegions();
	}

	@Transactional
	public List<Classifier> getDerivs() {
		return commonDataDbService.getDerivs(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getValueStates() {
		return commonDataDbService.getValueStates(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getProcessStates() {
		return commonDataDbService.getProcessStates();
	}

	@Transactional
	public List<Classifier> getProcessStatesByDataset(String datasetCode) {
		return commonDataDbService.getDatasetClassifiers(ClassifierName.PROCESS_STATE, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getClassifiers(ClassifierName classifierName) {
		if (classifierName == null) {
			return null;
		}
		if (ClassifierName.LANGUAGE.equals(classifierName)) {
			return getLanguages();
		}
		if (ClassifierName.FREQUENCY_GROUP.equals(classifierName)) {
			return getFrequencyGroups();
		}
		if (ClassifierName.MORPH.equals(classifierName)) {
			return getMorphs();
		}
		if (ClassifierName.GENDER.equals(classifierName)) {
			return getGenders();
		}
		if (ClassifierName.WORD_TYPE.equals(classifierName)) {
			return getWordTypes();
		}
		if (ClassifierName.ASPECT.equals(classifierName)) {
			return getAspects();
		}
		if (ClassifierName.WORD_REL_TYPE.equals(classifierName)) {
			return getWordRelationTypes();
		}
		if (ClassifierName.LEX_REL_TYPE.equals(classifierName)) {
			return getLexemeRelationTypes();
		}
		if (ClassifierName.MEANING_REL_TYPE.equals(classifierName)) {
			return getMeaningRelationTypes();
		}
		if (ClassifierName.DEFINITION_TYPE.equals(classifierName)) {
			return getDefinitionTypes();
		}
		if (ClassifierName.POS.equals(classifierName)) {
			return getPoses();
		}
		if (ClassifierName.REGISTER.equals(classifierName)) {
			return getRegisters();
		}
		if (ClassifierName.REGION.equals(classifierName)) {
			return getRegions();
		}
		if (ClassifierName.DERIV.equals(classifierName)) {
			return getDerivs();
		}
		if (ClassifierName.VALUE_STATE.equals(classifierName)) {
			return getValueStates();
		}
		if (ClassifierName.PROCESS_STATE.equals(classifierName)) {
			return getProcessStates();
		}
		return null;
	}

	@Transactional
	public Word getWord(Long wordId) {
		return commonDataDbService.getWord(wordId);
	}

	@Transactional
	public List<Classifier> findDomainsByValue(String searchValue) {
		return commonDataDbService.findDomainsByValue(searchValue);
	}

	@Transactional
	public List<Classifier> getDatasetLanguages(String datasetCode) {
		return commonDataDbService.getDatasetClassifiers(ClassifierName.LANGUAGE, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

}
