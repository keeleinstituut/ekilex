package eki.ekilex.runner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.transform.Lexeme;

@Component
public class LexemeLevelsSorterRunner extends AbstractLoaderRunner implements GlobalConstant {

	private static Logger logger = LoggerFactory.getLogger(LexemeLevelsSorterRunner.class);

	private static final String SQL_SELECT_WORD_ID_AND_DATASETS_WHERE_DUPLICATE_LEXEME_LEVELS = "sql/select_word_id_and_datasets_where_duplicate_lexeme_levels.sql";

	private static final String SQL_SELECT_LEXEME_ID_AND_LEVELS_BY_WORD_ID_AND_DATASET = "sql/select_lexeme_id_and_levels_by_word_id_and_dataset.sql";

	private String sqlSelectWordIdAndDatasetsWhereDuplicateLexemeLevels;

	private String getSqlSelectLexemeIdAndLevelsByWordIdAndDataset;

	private List<String> ignoredDatasetCodes;

	@Override
	public String getDataset() {
		return "lexemelevelssort";
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

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_ID_AND_DATASETS_WHERE_DUPLICATE_LEXEME_LEVELS);
		sqlSelectWordIdAndDatasetsWhereDuplicateLexemeLevels = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_ID_AND_LEVELS_BY_WORD_ID_AND_DATASET);
		getSqlSelectLexemeIdAndLevelsByWordIdAndDataset = getContent(resourceFileInputStream);

		ignoredDatasetCodes = new ArrayList<>();
		ignoredDatasetCodes.add(ETYMOLOGY_OWNER_DATASET_CODE);
	}

	@Transactional
	public void execute(boolean doReports) throws Exception {

		this.doReports = doReports;
		start();

		Map<Long, List<String>> wordIdsAndDatasetCodes = getWordIdsAndDatasetCodes();
		logger.debug("Found {} words that have lexemes with unsorted levels", wordIdsAndDatasetCodes.size());
		logger.debug("Sorting started");
		for (Map.Entry<Long, List<String>> wordIdAndDatasetCodes : wordIdsAndDatasetCodes.entrySet()) {
			Long wordId = wordIdAndDatasetCodes.getKey();
			List<String> datasetCodes = wordIdAndDatasetCodes.getValue();
			for (String datasetCode : datasetCodes) {
				List<Lexeme> lexemes = getLexemesToSort(wordId, datasetCode);
				sortLexemeLevels(lexemes);
				updateLexemeLevels(lexemes);
			}
		}

		logger.debug("Sorting finished successfully");
		end();
	}

	private void updateLexemeLevels(List<Lexeme> lexemes) throws Exception {

		Map<String, Object> criteriaParamMap;
		Map<String, Object> valueParamMap;

		for (Lexeme lexeme : lexemes) {
			criteriaParamMap = new HashMap<>();
			criteriaParamMap.put("id", lexeme.getLexemeId());

			valueParamMap = new HashMap<>();
			valueParamMap.put("level1", lexeme.getLevel1());
			valueParamMap.put("level2", lexeme.getLevel2());

			basicDbService.update(LEXEME, criteriaParamMap, valueParamMap);
		}
	}

	private void sortLexemeLevels(List<Lexeme> lexemesToSort) {

		Integer previousLevel1 = null;
		Integer previousLevel2 = null;
		Integer resettedLevel1 = null;
		Integer resettedLevel2 = null;

		for (int currentLexemeIdx = 0; currentLexemeIdx < lexemesToSort.size(); currentLexemeIdx++) {

			Lexeme currentLexeme = lexemesToSort.get(currentLexemeIdx);
			Integer currentLevel1 = currentLexeme.getLevel1();
			Integer currentLevel2 = currentLexeme.getLevel2();

			boolean isLevel1Reset = !Objects.equals(currentLevel1, previousLevel1);
			boolean isLevel1Increase = Objects.equals(currentLevel1, resettedLevel1) && Objects.equals(currentLevel2, resettedLevel2);
			boolean isLevel2Increase = Objects.equals(currentLevel1, previousLevel1) && Objects.equals(currentLevel2, previousLevel2);

			if (isLevel1Reset) {
				resettedLevel1 = currentLevel1;
				resettedLevel2 = currentLevel2;
			} else {
				if (isLevel1Increase) {
					for (int lexemeToIncreaseIdx = currentLexemeIdx; lexemeToIncreaseIdx < lexemesToSort.size(); lexemeToIncreaseIdx++) {
						Lexeme lexemeToIncreaseLevel1 = lexemesToSort.get(lexemeToIncreaseIdx);
						Integer increasedLevel1 = lexemeToIncreaseLevel1.getLevel1() + 1;
						lexemeToIncreaseLevel1.setLevel1(increasedLevel1);
					}
					resettedLevel1 = currentLevel1 + 1;
				} else if (isLevel2Increase) {
					for (int lexemeToIncreaseIdx = currentLexemeIdx; lexemeToIncreaseIdx < lexemesToSort.size(); lexemeToIncreaseIdx++) {
						Lexeme lexemeToIncreaseLevel2 = lexemesToSort.get(lexemeToIncreaseIdx);
						if (Objects.equals(lexemeToIncreaseLevel2.getLevel1(), currentLevel1)) {
							int increasedLevel2 = lexemeToIncreaseLevel2.getLevel2() + 1;
							lexemeToIncreaseLevel2.setLevel2(increasedLevel2);
						}
					}
				}
			}
			previousLevel1 = currentLexeme.getLevel1();
			previousLevel2 = currentLexeme.getLevel2();
		}
	}

	private Map<Long, List<String>> getWordIdsAndDatasetCodes() throws Exception {

		Map<Long, List<String>> wordIdsAndDatasetCodesMap = new HashMap<>();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("ignoredDatasetCodes", ignoredDatasetCodes);
		paramMap.put("lexemeType", LEXEME_TYPE_PRIMARY);
		List<Map<String, Object>> wordIdAndDatasetCodesList = basicDbService.queryList(sqlSelectWordIdAndDatasetsWhereDuplicateLexemeLevels, paramMap);

		for (Map<String, Object> wordIdAndDatasetCodesRow : wordIdAndDatasetCodesList) {
			Long wordId = (Long) wordIdAndDatasetCodesRow.get("word_id");
			PgArray datasetCodesArr = (PgArray) wordIdAndDatasetCodesRow.get("dataset_codes");
			String[] datasetCodesArray = (String[]) datasetCodesArr.getArray();
			List<String> datasetCodesList = Arrays.asList(datasetCodesArray);
			wordIdsAndDatasetCodesMap.put(wordId, datasetCodesList);
		}
		return wordIdsAndDatasetCodesMap;
	}

	private List<Lexeme> getLexemesToSort(Long wordId, String datasetCode) {

		List<Lexeme> lexemes = new ArrayList<>();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("wordId", wordId);
		paramMap.put("datasetCode", datasetCode);
		paramMap.put("lexemeType", LEXEME_TYPE_PRIMARY);
		List<Map<String, Object>> lexemeIdAndLevelsList = basicDbService.queryList(getSqlSelectLexemeIdAndLevelsByWordIdAndDataset, paramMap);

		for (Map<String, Object> lexemeIdAndLevelsRow : lexemeIdAndLevelsList) {
			Long lexemeId = (Long) lexemeIdAndLevelsRow.get("id");
			Integer lexemeLevel1 = (Integer) lexemeIdAndLevelsRow.get("level1");
			Integer lexemeLevel2 = (Integer) lexemeIdAndLevelsRow.get("level2");

			Lexeme lexeme = new Lexeme();
			lexeme.setLexemeId(lexemeId);
			lexeme.setLevel1(lexemeLevel1);
			lexeme.setLevel2(lexemeLevel2);
			lexemes.add(lexeme);
		}
		return lexemes;
	}
}
