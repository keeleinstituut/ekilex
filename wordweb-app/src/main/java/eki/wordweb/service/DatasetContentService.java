package eki.wordweb.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.wordweb.data.DatasetHomeData;
import eki.wordweb.data.DatasetStat;
import eki.wordweb.data.DatasetWord;
import eki.wordweb.service.db.DatasetContentDbService;

@Component
public class DatasetContentService {

	private static final int DATASET_WORD_COUNT_LIMIT = 10;

	@Autowired
	private DatasetContentDbService datasetContentDbService;

	@Transactional
	public DatasetHomeData getDatasetHomeData(String datasetCode) {

		DatasetStat dataset = datasetContentDbService.getDatasetStat(datasetCode);
		if (dataset == null) {
			return null;
		}
		List<Character> firstLetters = datasetContentDbService.getDatasetFirstLetters(datasetCode);

		List<DatasetWord> createdMeaningWords = dataset.getCreatedMeaningWords();
		dataset.setCreatedMeaningWords(removeDuplicateValuesAndLimit(createdMeaningWords));
		List<DatasetWord> updatedMeaningWords = dataset.getUpdatedMeaningWords();
		dataset.setUpdatedMeaningWords(removeDuplicateValuesAndLimit(updatedMeaningWords));

		DatasetHomeData datasetHomeData = new DatasetHomeData();
		datasetHomeData.setDataset(dataset);
		datasetHomeData.setFirstLetters(firstLetters);

		return datasetHomeData;
	}

	private List<DatasetWord> removeDuplicateValuesAndLimit(List<DatasetWord> datasetWords) {

		List<DatasetWord> limitedDatasetWords = new ArrayList<>();

		if (CollectionUtils.isEmpty(datasetWords)) {
			return null;
		}
		List<String> uniqueWordValues = new ArrayList<>();

		for (DatasetWord datasetWord : datasetWords) {

			if (limitedDatasetWords.size() >= DATASET_WORD_COUNT_LIMIT) {
				return limitedDatasetWords;
			}
			String wordValue = datasetWord.getValue();
			if (uniqueWordValues.contains(wordValue)) {
				continue;
			}
			uniqueWordValues.add(wordValue);
			limitedDatasetWords.add(datasetWord);
		}
		return limitedDatasetWords;
	}

	@Transactional
	public List<String> getDatasetWords(String datasetCode, Character firstLetter) {

		return datasetContentDbService.getDatasetWords(datasetCode, firstLetter);
	}
}
