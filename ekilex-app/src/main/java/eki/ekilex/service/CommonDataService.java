package eki.ekilex.service;

import static java.util.stream.Collectors.groupingBy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformConstant;
import eki.common.constant.FreeformOwner;
import eki.common.constant.GlobalConstant;
import eki.common.constant.TagType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.Origin;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.util.DatasetUtil;

// only common use data aggregation!
@Component
public class CommonDataService implements InitializingBean, SystemConstant, GlobalConstant, FreeformConstant {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private DatasetUtil datasetUtil;

	private List<String> technicalFreeformTypesCodes;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream = classLoader.getResourceAsStream(TECHNICAL_FF_TYPE_CODES_FILE_PATH);
		List<String> resourceFileLines = IOUtils.readLines(resourceFileInputStream, UTF_8);
		resourceFileInputStream.close();

		technicalFreeformTypesCodes = new ArrayList<>();
		for (String resourceFileLine : resourceFileLines) {
			if (StringUtils.isBlank(resourceFileLine)) {
				continue;
			}
			String freeformTypeCode = StringUtils.trim(resourceFileLine);
			technicalFreeformTypesCodes.add(freeformTypeCode);
		}
	}

	@Transactional
	public List<Classifier> getAvailableFreeformTypes() {
		List<Classifier> freeformTypes = commonDataDbService.getDefaultClassifiers(ClassifierName.FREEFORM_TYPE, CLASSIF_LABEL_LANG_EST);
		freeformTypes = removeTechnicalFreeformTypes(freeformTypes);
		return freeformTypes;
	}

	@Transactional
	public List<Classifier> getFreeformTypes(FreeformOwner freeformOwner) {
		List<Classifier> freeformTypes = commonDataDbService.getFreeformTypes(freeformOwner, CLASSIF_LABEL_LANG_EST);
		if (CollectionUtils.isEmpty(freeformTypes)) {
			return freeformTypes;
		}
		freeformTypes = removeTechnicalFreeformTypes(freeformTypes);
		return freeformTypes;
	}

	@Transactional
	public List<Classifier> getFreeformTypes(String datasetCode, FreeformOwner freeformOwner) {
		List<Classifier> freeformTypes = commonDataDbService.getFreeformTypes(datasetCode, freeformOwner, CLASSIF_LABEL_LANG_EST);
		if (CollectionUtils.isEmpty(freeformTypes)) {
			return freeformTypes;
		}
		freeformTypes = removeTechnicalFreeformTypes(freeformTypes);
		return freeformTypes;
	}

	private List<Classifier> removeTechnicalFreeformTypes(List<Classifier> freeformTypes) {
		freeformTypes.removeIf(classifier -> technicalFreeformTypesCodes.contains(classifier.getCode()));
		return freeformTypes;
	}

	@Transactional
	public List<Dataset> getAllDatasets() {
		return commonDataDbService.getAllDatasets();
	}

	@Transactional
	public List<Dataset> getVisibleDatasets() {
		List<Dataset> datasets = commonDataDbService.getVisibleDatasets();
		datasets = datasetUtil.resortPriorityDatasets(datasets);
		return datasets;
	}

	@Transactional
	public List<Dataset> getVisibleDatasetsWithOwner() {
		List<Dataset> datasets = commonDataDbService.getVisibleDatasetsWithOwner();
		datasets = datasetUtil.resortPriorityDatasets(datasets);
		return datasets;
	}

	@Transactional
	public List<String> getVisibleDatasetCodes() {
		List<String> datasetCodes = getVisibleDatasets().stream().map(Dataset::getCode).collect(Collectors.toList());
		return datasetCodes;
	}

	@Transactional
	public Map<String, String> getLanguageIso2Map() {
		List<Classifier> languages = commonDataDbService.getClassifiers(ClassifierName.LANGUAGE, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_ISO2);
		return languages.stream()
				.collect(Collectors.toMap(Classifier::getCode, classifier -> StringUtils.isNotBlank(classifier.getValue()) ? classifier.getValue() : classifier.getCode()));
	}

	@Transactional
	public Map<String, List<Classifier>> getDomainsInUseByOrigin() {
		List<Classifier> domains = commonDataDbService.getDomainsInUse(CLASSIF_LABEL_LANG_EST);
		return domains.stream().collect(groupingBy(Classifier::getOrigin));
	}

	@Transactional
	public Map<String, List<Classifier>> getDatasetDomainsByOrigin(String datasetCode) {
		List<Classifier> domains = commonDataDbService.getDatasetClassifiers(ClassifierName.DOMAIN, datasetCode, CLASSIF_LABEL_LANG_EST);
		Map<String, List<Classifier>> datasetDomainsByOrigin = domains.stream().collect(groupingBy(Classifier::getOrigin));
		return datasetDomainsByOrigin;
	}

	@Transactional
	public List<Classifier> getDomains(String origin) {
		List<Classifier> domains = commonDataDbService.getDomains(origin, CLASSIF_LABEL_LANG_EST);
		return domains;
	}

	@Transactional
	public List<Classifier> getLanguages() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.LANGUAGE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getMorphs() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.MORPH, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getGenders() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.GENDER, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getWordTypes() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.WORD_TYPE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getAspects() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.ASPECT, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getWordRelationTypes() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.WORD_REL_TYPE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getLexemeRelationTypes() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.LEX_REL_TYPE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getMeaningRelationTypes() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.MEANING_REL_TYPE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getDefinitionTypes() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.DEFINITION_TYPE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getPoses() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.POS, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getRegisters() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.REGISTER, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getRegions() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.REGION, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getDerivs() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.DERIV, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getValueStates() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.VALUE_STATE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getProficiencyLevels() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.PROFICIENCY_LEVEL, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getSemanticTypes() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.SEMANTIC_TYPE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getDisplayMorphs() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.DISPLAY_MORPH, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getGovernmentTypes() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.GOVERNMENT_TYPE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getEtymologyTypes() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.ETYMOLOGY_TYPE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getPosGroups() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.POS_GROUP, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getRelGroups() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.REL_GROUP, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getUsageTypes() {
		return commonDataDbService.getDefaultClassifiers(ClassifierName.USAGE_TYPE, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getClassifiers(ClassifierName classifierName) {
		if (classifierName == null) {
			return null;
		}
		return commonDataDbService.getDefaultClassifiers(classifierName, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Classifier> getDatasetLanguages(String datasetCode) {
		return commonDataDbService.getDatasetClassifiers(ClassifierName.LANGUAGE, datasetCode, CLASSIF_LABEL_LANG_EST);
	}

	@Transactional
	public List<Origin> getDomainOrigins() {
		List<Origin> allDomainOrigins = commonDataDbService.getDomainOrigins();
		//hack to beautify specific code into label
		final String ekiCodePrefix = "eki ";
		final String ekiLabelPrefix = "EKI ";
		for (Origin origin : allDomainOrigins) {
			String code = origin.getCode();
			String label = origin.getLabel();
			if (StringUtils.isBlank(label)) {
				if (StringUtils.startsWith(code, ekiCodePrefix)) {
					label = StringUtils.replace(code, ekiCodePrefix, ekiLabelPrefix);
				} else {
					label = code;
				}
				origin.setLabel(label);
			}
		}
		return allDomainOrigins;
	}

	@Transactional
	public List<String> getAllTags() {
		return commonDataDbService.getTags();
	}

	@Transactional
	public List<String> getLexemeTags() {
		return commonDataDbService.getTags(TagType.LEX.name());
	}

	@Transactional
	public List<String> getMeaningTags() {
		return commonDataDbService.getTags(TagType.MEANING.name());
	}

	@Transactional
	public List<String> getWordTags() {
		return commonDataDbService.getTags(TagType.WORD.name());
	}
}
