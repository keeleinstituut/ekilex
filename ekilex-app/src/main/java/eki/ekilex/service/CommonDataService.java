package eki.ekilex.service;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.CommonDataDbService;

import static java.util.stream.Collectors.groupingBy;

@Component
public class CommonDataService {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Transactional
	public List<Dataset> getDatasets() {
		return commonDataDbService.getDatasets().into(Dataset.class);
	}

	@Transactional
	public List<Classifier> getLanguages() {
		return commonDataDbService.getLanguages().into(Classifier.class);
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

}
