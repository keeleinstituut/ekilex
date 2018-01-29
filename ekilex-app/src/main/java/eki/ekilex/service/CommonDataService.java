package eki.ekilex.service;

import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Dataset;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.service.db.CommonDataDbService;

@Component
public class CommonDataService {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Transactional
	public List<Dataset> getDatasets() {
		return commonDataDbService.getDatasets().into(Dataset.class);
	}

	@Transactional
	public List<Word> findWords(SearchFilter searchFilter, List<String> datasets) throws Exception {

		return commonDataDbService.findWords(searchFilter, datasets).into(Word.class);
	}

	@Transactional
	public List<Word> findWords(String searchFilter, List<String> datasets) {
		if (StringUtils.isBlank(searchFilter)) {
			return Collections.emptyList();
		}
		return commonDataDbService.findWords(searchFilter, datasets).into(Word.class);
	}
}
