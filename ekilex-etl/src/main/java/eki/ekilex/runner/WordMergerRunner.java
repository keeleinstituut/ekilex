package eki.ekilex.runner;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DbConstant;

@Component
public class WordMergerRunner extends AbstractLoaderRunner implements DbConstant {

	private static Logger logger = LoggerFactory.getLogger(WordMergerRunner.class);

	private static final String SQL_SELECT_WORD_JOIN_CANDIDATES_VALUE_AND_WORD_IDS = "sql/select_word_join_candidates_value_and_word_ids.sql";

	private String sqlSelectWordJoinCandidatesValueAndWordIds;

	private Map<String, List<Long>> joinCandidates;

	private List<String> excludedWordTypeCodes;

	@Override
	public String getDataset() {
		// TODO ?
		return "word merger";
	}

	@Override
	public Complexity getLexemeComplexity() {
		return null;
	}

	@Override
	public Complexity getDefinitionComplexity() {
		return null;
	}

	@Override
	public Complexity getFreeformComplexity() {
		return null;
	}

	@Override
	public void deleteDatasetData() {
	}

	@Override
	public void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_JOIN_CANDIDATES_VALUE_AND_WORD_IDS);
		sqlSelectWordJoinCandidatesValueAndWordIds = getContent(resourceFileInputStream);
		joinCandidates = new HashMap<>();
		excludedWordTypeCodes = new ArrayList<>();
		excludedWordTypeCodes.add(WORD_TYPE_CODE_PREFIXOID);
		excludedWordTypeCodes.add(WORD_TYPE_CODE_SUFFIXOID);
	}

	@Transactional
	public void execute(String mergedLexDatasetCode, boolean doReports) throws Exception {

		getJoinCandidates(mergedLexDatasetCode);

		// TODO otsida sama nimetuse järgi, kas on selline keelend, millel on superi lekseem (kui selliseid keelendeid on mitu, siis ei sobi)
		this.doReports = doReports;

		if (doReports) {
			// TODO
		}

	}

	private void getJoinCandidates(String mergedLexDatasetCode) throws SQLException {
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lang", LANGUAGE_CODE_EST);
		tableRowParamMap.put("datasetType", DATASET_TYPE_TERM);
		tableRowParamMap.put("excludedDatasetCode", mergedLexDatasetCode);
		tableRowParamMap.put("excludedWordTypeCodes", excludedWordTypeCodes);
		List<Map<String, Object>> valueAndWordIds = basicDbService.queryList(sqlSelectWordJoinCandidatesValueAndWordIds, tableRowParamMap);
		for (Map<String, Object> row : valueAndWordIds) {
			String value = (String) row.get("value");
			PgArray wordIdsArr = (PgArray) row.get("word_ids");
			Long[] wordIdsArray = (Long[]) wordIdsArr.getArray();
			List<Long> wordIdsList = Arrays.asList(wordIdsArray);
			joinCandidates.put(value, wordIdsList);
		}
	}
}
