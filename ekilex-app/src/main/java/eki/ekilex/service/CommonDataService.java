package eki.ekilex.service;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import eki.ekilex.data.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.CommonDataDbService;

import static java.util.stream.Collectors.groupingBy;

@Component
public class CommonDataService {

	private final static String classifierLabelLangEst = "est";
	private final static String classifierLabelTypeDescrip = "descrip";
	private final static String classifierLabelTypeFull = "full";

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Transactional
	public List<Dataset> getDatasets() {
		return commonDataDbService.getDatasets().into(Dataset.class);
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
	public List<Classifier> getLexemeFrequencyGroups() {
		return commonDataDbService.getLexemeFrequencyGroups().into(Classifier.class);
	}

	@Transactional
	public Word getWord(Long wordId) {
		return commonDataDbService.getWord(wordId).into(Word.class);
	}

	@Transactional
	public List<Classifier> getWordMorphCodes() {
		return commonDataDbService.getWordMorphCodes(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getWordGenders() {
		return commonDataDbService.getWordGenders(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getWordTypes() {
		return commonDataDbService.getWordTypes(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getWordAspects() {
		return commonDataDbService.getWordAspects(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
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
	public List<Classifier> getAllLexemePos() {
		return commonDataDbService.getAllLexemePos(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getLexemeRegisters() {
		return commonDataDbService.getLexemeRegisters(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getLexemeDerivs() {
		return commonDataDbService.getLexemeDerivs(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getLexemeValueStates() {
		return commonDataDbService.getLexemeValueStates(classifierLabelLangEst, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getProcessStates() {
		return commonDataDbService.getProcessStates().into(Classifier.class);
	}

}
