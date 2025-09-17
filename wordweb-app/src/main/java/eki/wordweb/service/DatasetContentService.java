package eki.wordweb.service;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.wordweb.data.Dataset;
import eki.wordweb.data.DatasetHomeData;
import eki.wordweb.service.db.CommonDataDbService;
import eki.wordweb.service.db.DatasetContentDbService;

@Component
public class DatasetContentService {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private DatasetContentDbService datasetContentDbService;

	@Transactional
	public DatasetHomeData getDatasetHomeData(String datasetCode) {

		Dataset dataset = commonDataDbService.getDataset(datasetCode);
		List<Character> firstLetters = datasetContentDbService.getDatasetFirstLetters(datasetCode);
		boolean isValidDataset = (dataset != null) && CollectionUtils.isNotEmpty(firstLetters);

		DatasetHomeData datasetHomeData = new DatasetHomeData();
		datasetHomeData.setDataset(dataset);
		datasetHomeData.setFirstLetters(firstLetters);
		datasetHomeData.setValidDataset(isValidDataset);
		return datasetHomeData;
	}

	@Transactional
	public List<String> getDatasetWords(String datasetCode, Character firstLetter) {

		return datasetContentDbService.getDatasetWords(datasetCode, firstLetter);
	}
}
