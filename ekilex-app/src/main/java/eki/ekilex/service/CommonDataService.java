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
		List<Classifier> freeformTypes = getDefaultClassifiers(ClassifierName.FREEFORM_TYPE);
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
		Map<String, String> languageIso2Map = languages.stream()
				.collect(Collectors.toMap(
						Classifier::getCode,
						classifier -> {
							if (StringUtils.isNotBlank(classifier.getValue())) {
								return classifier.getValue();
							}
							return classifier.getCode();
						}));
		return languageIso2Map;
	}

	@Transactional
	public Map<String, List<Classifier>> getDomainsInUseByOrigin() {
		List<Classifier> domains = commonDataDbService.getDomainsInUse(CLASSIF_LABEL_LANG_EST);
		applyValueCompilations(domains);
		return domains.stream().collect(groupingBy(Classifier::getOrigin));
	}

	@Transactional
	public Map<String, List<Classifier>> getDatasetDomainsByOrigin(String datasetCode) {
		List<Classifier> domains = commonDataDbService.getDatasetClassifiers(ClassifierName.DOMAIN, datasetCode, CLASSIF_LABEL_LANG_EST);
		applyValueCompilations(domains);
		Map<String, List<Classifier>> datasetDomainsByOrigin = domains.stream().collect(groupingBy(Classifier::getOrigin));
		return datasetDomainsByOrigin;
	}

	@Transactional
	public List<Classifier> getDomains(String origin) {
		List<Classifier> domains = commonDataDbService.getDomains(origin, CLASSIF_LABEL_LANG_EST);
		applyValueCompilations(domains);
		return domains;
	}

	@Transactional
	public List<Classifier> getLanguages() {
		return getDefaultClassifiers(ClassifierName.LANGUAGE);
	}

	@Transactional
	public List<Classifier> getMorphs() {
		return getDefaultClassifiers(ClassifierName.MORPH);
	}

	@Transactional
	public List<Classifier> getGenders() {
		return getDefaultClassifiers(ClassifierName.GENDER);
	}

	@Transactional
	public List<Classifier> getWordTypes() {
		return getDefaultClassifiers(ClassifierName.WORD_TYPE);
	}

	@Transactional
	public List<Classifier> getAspects() {
		return getDefaultClassifiers(ClassifierName.ASPECT);
	}

	@Transactional
	public List<Classifier> getWordRelationTypes() {
		return getDefaultClassifiers(ClassifierName.WORD_REL_TYPE);
	}

	@Transactional
	public List<Classifier> getLexemeRelationTypes() {
		return getDefaultClassifiers(ClassifierName.LEX_REL_TYPE);
	}

	@Transactional
	public List<Classifier> getMeaningRelationTypes() {
		return getDefaultClassifiers(ClassifierName.MEANING_REL_TYPE);
	}

	@Transactional
	public List<Classifier> getDefinitionTypes() {
		return getDefaultClassifiers(ClassifierName.DEFINITION_TYPE);
	}

	@Transactional
	public List<Classifier> getPoses() {
		return getDefaultClassifiers(ClassifierName.POS);
	}

	@Transactional
	public List<Classifier> getRegisters() {
		return getDefaultClassifiers(ClassifierName.REGISTER);
	}

	@Transactional
	public List<Classifier> getRegions() {
		return getDefaultClassifiers(ClassifierName.REGION);
	}

	@Transactional
	public List<Classifier> getDerivs() {
		return getDefaultClassifiers(ClassifierName.DERIV);
	}

	@Transactional
	public List<Classifier> getValueStates() {
		return getDefaultClassifiers(ClassifierName.VALUE_STATE);
	}

	@Transactional
	public List<Classifier> getProficiencyLevels() {
		return getDefaultClassifiers(ClassifierName.PROFICIENCY_LEVEL);
	}

	@Transactional
	public List<Classifier> getSemanticTypes() {
		return getDefaultClassifiers(ClassifierName.SEMANTIC_TYPE);
	}

	@Transactional
	public List<Classifier> getDisplayMorphs() {
		return getDefaultClassifiers(ClassifierName.DISPLAY_MORPH);
	}

	@Transactional
	public List<Classifier> getGovernmentTypes() {
		return getDefaultClassifiers(ClassifierName.GOVERNMENT_TYPE);
	}

	@Transactional
	public List<Classifier> getEtymologyTypes() {
		return getDefaultClassifiers(ClassifierName.ETYMOLOGY_TYPE);
	}

	@Transactional
	public List<Classifier> getPosGroups() {
		return getDefaultClassifiers(ClassifierName.POS_GROUP);
	}

	@Transactional
	public List<Classifier> getRelGroups() {
		return getDefaultClassifiers(ClassifierName.REL_GROUP);
	}

	@Transactional
	public List<Classifier> getUsageTypes() {
		return getDefaultClassifiers(ClassifierName.USAGE_TYPE);
	}

	@Transactional
	public List<Classifier> getVariantTypes() {
		return getDefaultClassifiers(ClassifierName.VARIANT_TYPE);
	}

	@Transactional
	public List<Classifier> getClassifiers(ClassifierName classifierName) {
		if (classifierName == null) {
			return null;
		}
		return getDefaultClassifiers(classifierName);
	}

	@Transactional
	public List<Classifier> getDatasetLanguages(String datasetCode) {
		return commonDataDbService.getDatasetClassifiers(ClassifierName.LANGUAGE, datasetCode, CLASSIF_LABEL_LANG_EST);
	}

	private List<Classifier> getDefaultClassifiers(ClassifierName classifierName) {
		List<Classifier> classifiers = commonDataDbService.getDefaultClassifiers(classifierName, CLASSIF_LABEL_LANG_EST);
		applyValueCompilations(classifiers);
		return classifiers;
	}

	private void applyValueCompilations(List<Classifier> classifiers) {
		final String separator = " - ";
		for (Classifier classifier : classifiers) {
			String origin = classifier.getOrigin();
			String code = classifier.getCode();
			String value = classifier.getValue();
			String comment = classifier.getComment();
			String valueComment = value;
			String originCodeValueCommentShort = null;
			String originCodeValueCommentFull = null;
			if (StringUtils.isNotBlank(comment)) {
				valueComment = valueComment + separator + comment;
			}
			if (StringUtils.isNotBlank(origin)) {
				originCodeValueCommentFull = StringUtils.join(origin, separator, code, separator, value);
				originCodeValueCommentShort = StringUtils.abbreviate(originCodeValueCommentFull, 100);
				if (StringUtils.isNotBlank(comment)) {
					originCodeValueCommentShort = originCodeValueCommentShort + separator + comment;
					originCodeValueCommentFull = originCodeValueCommentFull + separator + comment;
				}
			}
			classifier.setValueComment(valueComment);
			classifier.setOriginCodeValueCommentShort(originCodeValueCommentShort);
			classifier.setOriginCodeValueCommentFull(originCodeValueCommentFull);
		}
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
