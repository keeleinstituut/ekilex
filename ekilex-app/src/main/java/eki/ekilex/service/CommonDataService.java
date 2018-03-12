package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.service.db.CommonDataDbService;

import static java.util.stream.Collectors.toList;

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
		List<Classifier> domainsWithEstonianLabels = commonDataDbService.getDomainsInUseForLanguage("est").into(Classifier.class);
		List<Classifier> domainsWithEnglishLabels = commonDataDbService.getDomainsInUseForLanguage("eng").into(Classifier.class);
		List<Classifier> domainsNotInEstonianList =
				domainsWithEnglishLabels.stream()
						.filter(d -> domainsWithEstonianLabels.stream().noneMatch(e -> e.getCode().equals(d.getCode()) && e.getOrigin().equals(d.getOrigin())))
						.collect(toList());
		domainsWithEstonianLabels.addAll(domainsNotInEstonianList);
		domainsWithEstonianLabels.sort((d, o) -> {
			if (d.getOrigin().compareTo(o.getOrigin()) == 0) {
				return d.getCode().compareTo(o.getCode());
			}
			return d.getOrigin().compareTo(o.getOrigin());
		});
		return domainsWithEstonianLabels;
	}

}
