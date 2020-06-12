package eki.ekilex.service;

import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.LayerName;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.db.LexSearchDbService;

@Component
public abstract class AbstractWordSearchService extends AbstractSearchService {

	protected final static String classifierLabelLang = "est";
	protected final static String classifierLabelTypeDescrip = "descrip";
	protected final static String classifierLabelTypeFull = "full";

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Transactional
	public WordsResult getWords(
			SearchFilter searchFilter, List<String> datasetCodes, DatasetPermission userRole, LayerName layerName, boolean fetchAll,
			int offset, int maxResultsLimit) throws Exception {

		List<Word> words;
		int wordCount;
		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodes);
			words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, userRole, layerName, fetchAll, offset, maxResultsLimit);
			wordCount = words.size();
			if (!fetchAll && wordCount == maxResultsLimit) {
				wordCount = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);

		boolean showPaging = wordCount > maxResultsLimit;
		result.setShowPaging(showPaging);
		if (showPaging) {
			setPagingData(offset, maxResultsLimit, wordCount, result);
		}
		return result;
	}

	@Transactional
	public WordsResult getWords(
			String searchFilter, List<String> datasetCodes, DatasetPermission userRole, LayerName layerName, boolean fetchAll, int offset, int maxResultsLimit) {

		List<Word> words;
		int wordCount;
		if (StringUtils.isBlank(searchFilter)) {
			words = Collections.emptyList();
			wordCount = 0;
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodes);
			words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, userRole, layerName, fetchAll, offset, maxResultsLimit);
			wordCount = words.size();
			if ((!fetchAll && wordCount == maxResultsLimit) || offset > DEFAULT_OFFSET) {
				wordCount = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
			}
		}
		WordsResult result = new WordsResult();
		result.setWords(words);
		result.setTotalCount(wordCount);

		boolean showPaging = wordCount > maxResultsLimit;
		result.setShowPaging(showPaging);
		if (showPaging) {
			setPagingData(offset, maxResultsLimit, wordCount, result);
		}
		return result;
	}

	public int countWords(String searchFilter, List<String> selectedDatasetCodes) {
		if (StringUtils.isBlank(searchFilter)) {
			return 0;
		}
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		int count = lexSearchDbService.countWords(searchFilter, searchDatasetsRestriction);
		return count;
	}
}
