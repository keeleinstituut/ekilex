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

	private final static String classifierLabelLang = "est";
	private final static String classifierLabelTypeDescrip = "descrip";

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Transactional
	public List<Dataset> getDatasets() {
		return commonDataDbService.getDatasets().into(Dataset.class);
	}

	@Transactional
	public List<Classifier> getLanguages() {
		return commonDataDbService.getLanguages(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
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
		return commonDataDbService.getWordMorphCodes(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getWordGenders() {
		return commonDataDbService.getLexemeGenders(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getWordTypes() {
		return commonDataDbService.getWordTypes(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
	}

	@Transactional
	public List<Classifier> getWordAspects() {
		return commonDataDbService.getWordAspects(classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
	}

}
