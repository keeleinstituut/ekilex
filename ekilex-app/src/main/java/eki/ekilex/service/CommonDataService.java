package eki.ekilex.service;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.Word;
import eki.ekilex.service.db.CommonDataDbService;

@Component
public class CommonDataService {

	private final static String classifierLabelLangEst = "est";
	private final static String classifierLabelTypeDescrip = "descrip";
	private final static String classifierLabelTypeFull = "full";

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
		return commonDataDbService.getLanguages(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getDomainsInUse() {
		return commonDataDbService.getDomainsInUse().into(Classifier.class);
	}

	@Transactional
	public Map<String, List<Classifier>> getDomainsInUseByOrigin() {
		List<Classifier> domains = commonDataDbService.getDomainsInUse().into(Classifier.class);
		return domains.stream().collect(groupingBy(Classifier::getOrigin));
	}

	@Transactional
	public Map<String, List<Classifier>> getAllDomainsByOrigin() {
		List<Classifier> domains = commonDataDbService.getDomains().into(Classifier.class);
		return domains.stream().collect(groupingBy(Classifier::getOrigin));
	}

	@Transactional
	public List<Classifier> getFrequencyGroups() {
		return commonDataDbService.getFrequencyGroups().into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getMorphs() {
		return commonDataDbService.getMorphs(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getGenders() {
		return commonDataDbService.getGenders(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getWordTypes() {
		return commonDataDbService.getWordTypes(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getAspects() {
		return commonDataDbService.getAspects(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getWordRelationTypes() {
		return commonDataDbService.getWordRelationTypes(classifierLabelLangEst, classifierLabelTypeFull).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getLexemeRelationTypes() {
		return commonDataDbService.getLexemeRelationTypes(classifierLabelLangEst, classifierLabelTypeFull).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getMeaningRelationTypes() {
		return commonDataDbService.getMeaningRelationTypes(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getPoses() {
		return commonDataDbService.getPoses(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getRegisters() {
		return commonDataDbService.getRegisters(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getDerivs() {
		return commonDataDbService.getDerivs(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getValueStates() {
		return commonDataDbService.getValueStates(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getProcessStates() {
		return commonDataDbService.getProcessStates().into(Classifier.class);
	}

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
		if (ClassifierName.POS.equals(classifierName)) {
			return getPoses();
		}
		if (ClassifierName.REGISTER.equals(classifierName)) {
			return getRegisters();
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
		return commonDataDbService.getWord(wordId).into(Word.class);
	}
}
