package eki.ekilex.runner;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.data.Count;

@Component
public class FrequencyUpdateRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(FrequencyUpdateRunner.class);

	private static final String LEXEME_FREQUENCY_MODULE = "lexfreq";

	private static final String FORM_FREQUENCY_MODULE = "formfreq";

	private static final String SQL_INSERT_LEXEME_FREQUENCY_BY_POS_WHERE_NOT_EXISTS_PATH = "sql/insert_lexeme_freq_by_pos_where_not_exists.sql";

	private static final String SQL_INSERT_LEXEME_FREQUENCY_NO_POS_WHERE_NOT_EXISTS_PATH = "sql/insert_lexeme_freq_no_pos_where_not_exists.sql";

	private static final String SQL_INSERT_FORM_FREQUENCY_WHERE_EXISTS_WORD_PATH = "sql/insert_form_freq_where_exists_word.sql";

	private static final int FREQ_DECIMAL_PLACES = 7;

	private final String sqlDeleteLexemeFrequencyForSource = "delete from " + LEXEME_FREQUENCY + " where source_name = :sourceName";

	private final String sqlDeleteFormFrequencyForSource = "delete from " + FORM_FREQUENCY + " where source_name = :sourceName";

	private String sqlInsertLexemeFrequencyByPosWhereNotExists;

	private String sqlInsertLexemeFrequencyNoPosWhereNotExists;

	private String sqlInsertFormFrequencyWhereExistsWord;

	private String module;

	@Override
	public String getDataset() {
		return module;
	}

	@Override
	public Complexity getComplexity() {
		return null;
	}

	public String getLexemeFrequencyModule() {
		return LEXEME_FREQUENCY_MODULE;
	}

	public String getFormFrequencyModule() {
		return FORM_FREQUENCY_MODULE;
	}

	@Override
	public void deleteDatasetData() throws Exception {

	}

	@Override
	void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_INSERT_LEXEME_FREQUENCY_BY_POS_WHERE_NOT_EXISTS_PATH);
		sqlInsertLexemeFrequencyByPosWhereNotExists = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_INSERT_LEXEME_FREQUENCY_NO_POS_WHERE_NOT_EXISTS_PATH);
		sqlInsertLexemeFrequencyNoPosWhereNotExists = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_INSERT_FORM_FREQUENCY_WHERE_EXISTS_WORD_PATH);
		sqlInsertFormFrequencyWhereExistsWord = getContent(resourceFileInputStream);
	}

	@Transactional
	public void executeLexemeFrequencyUpdate(String lexemeFrequencyFilePath) throws Exception {
		this.module = LEXEME_FREQUENCY_MODULE;

		start();

		File lexemeFrequencyFile = new File(lexemeFrequencyFilePath);
		String sourceName = StringUtils.substringBefore(lexemeFrequencyFile.getName(), ".");
		List<String> lexemeFreqLines = readFileLines(lexemeFrequencyFilePath);
		int dataRowCount = lexemeFreqLines.size();

		if (dataRowCount == 0) {
			logger.debug("Lexeme frequencies file is empty");
			end();
			return;
		}

		logger.debug("There are {} lexeme frequency rows in source \"{}\"", dataRowCount, sourceName);

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("sourceName", sourceName);
		int lexemeFrequencyDeleteCount = basicDbService.executeScript(sqlDeleteLexemeFrequencyForSource, tableRowParamMap);
		logger.debug("Deleted {} lexeme frequency records of source \"{}\"", lexemeFrequencyDeleteCount, sourceName);

		long dataRowCounter = 0;
		long progressIndicator = dataRowCount / Math.min(dataRowCount, 100);

		Count totalInsertCount = new Count();
		Count existingLexemeByPosCount = new Count();
		Count missingLexemeByPosCount = new Count();
		Count existingLexemeNoPosCount = new Count();
		Count missingLexemeNoPosCount = new Count();

		for (String lexemeFreqLine : lexemeFreqLines) {

			if (StringUtils.isBlank(lexemeFreqLine)) {
				continue;
			}

			String[] lexemeFreqParts = StringUtils.split(lexemeFreqLine, CSV_SEPARATOR);
			int rank = Integer.parseInt(lexemeFreqParts[0]);
			String wordValue = lexemeFreqParts[1];
			String posCode = lexemeFreqParts[2];
			BigDecimal frequency = new BigDecimal(lexemeFreqParts[3]);
			frequency = frequency.setScale(FREQ_DECIMAL_PLACES, BigDecimal.ROUND_HALF_UP);

			//TODO log if already exists

			tableRowParamMap.clear();
			tableRowParamMap.put("sourceName", sourceName);
			tableRowParamMap.put("rank", rank);
			tableRowParamMap.put("frequency", frequency);
			tableRowParamMap.put("posCode", posCode);
			tableRowParamMap.put("wordValue", wordValue);

			int insertCount;

			insertCount = basicDbService.executeScript(sqlInsertLexemeFrequencyByPosWhereNotExists, tableRowParamMap);
			if (insertCount == 0) {
				missingLexemeByPosCount.increment();
			} else {
				existingLexemeByPosCount.increment();
				totalInsertCount.increment(insertCount);
			}

			tableRowParamMap.clear();
			tableRowParamMap.put("sourceName", sourceName);
			tableRowParamMap.put("rank", rank);
			tableRowParamMap.put("frequency", frequency);
			tableRowParamMap.put("wordValue", wordValue);

			insertCount = basicDbService.executeScript(sqlInsertLexemeFrequencyNoPosWhereNotExists, tableRowParamMap);
			if (insertCount == 0) {
				missingLexemeNoPosCount.increment();
			} else {
				existingLexemeNoPosCount.increment();
				totalInsertCount.increment(insertCount);
			}

			// progress
			dataRowCounter++;
			if (dataRowCounter % progressIndicator == 0) {
				long progressPercent = dataRowCounter / progressIndicator;
				logger.debug("{}% - {} rows iterated", progressPercent, dataRowCounter);
			}
		}

		logger.debug("Missing lexeme by POS count: {}", missingLexemeByPosCount.getValue());
		logger.debug("Existing lexeme by POS count: {}", existingLexemeByPosCount.getValue());
		logger.debug("Missing lexeme wo POS count: {}", missingLexemeNoPosCount.getValue());
		logger.debug("Existing lexeme wo POS count: {}", existingLexemeNoPosCount.getValue());
		logger.debug("Total lexeme frequency insert count: {}", totalInsertCount.getValue());

		end();
	}

	@Transactional
	public void executeFormFrequencyUpdate(String formFrequencyFilePath) throws Exception {
		this.module = FORM_FREQUENCY_MODULE;

		start();

		File formFrequencyFile = new File(formFrequencyFilePath);
		String sourceName = StringUtils.substringBefore(formFrequencyFile.getName(), ".");
		List<String> formFreqLines = readFileLines(formFrequencyFilePath);
		int dataRowCount = formFreqLines.size();

		if (dataRowCount == 0) {
			logger.debug("Form frequencies file is empty");
			end();
			return;
		}

		logger.debug("There are {} form frequency rows in source \"{}\"", dataRowCount, sourceName);

		Map<String, Object> tableRowParamMap = new HashMap<>();

		List<Map<String, Object>> morphRows = basicDbService.selectAll(MORPH, tableRowParamMap);
		List<String> supportedMorphCodes = morphRows.stream().map(row -> row.get("code").toString()).collect(Collectors.toList());

		logger.debug("There are {} supported morph codes", supportedMorphCodes.size());

		tableRowParamMap.put("sourceName", sourceName);
		int formFrequencyDeleteCount = basicDbService.executeScript(sqlDeleteFormFrequencyForSource, tableRowParamMap);

		logger.debug("Deleted {} form frequency records of source \"{}\"", formFrequencyDeleteCount, sourceName);

		long dataRowCounter = 0;
		long progressIndicator = dataRowCount / Math.min(dataRowCount, 100);

		Count totalInsertCount = new Count();
		Count unsupportedMorphCount = new Count();

		for (String formFreqLine : formFreqLines) {

			if (StringUtils.isBlank(formFreqLine)) {
				continue;
			}

			String[] formFreqParts = StringUtils.split(formFreqLine, CSV_SEPARATOR);
			int rank = Integer.parseInt(formFreqParts[0]);
			String formValue = formFreqParts[1];
			String wordValue = formFreqParts[2];
			String morphCodesStr = formFreqParts[3];
			String[] morphCodes = StringUtils.split(morphCodesStr, ',');
			BigDecimal frequency = new BigDecimal(formFreqParts[4]);
			frequency = frequency.setScale(FREQ_DECIMAL_PLACES, BigDecimal.ROUND_HALF_UP);

			for (String morphCode : morphCodes) {
				if (!supportedMorphCodes.contains(morphCode)) {
					unsupportedMorphCount.increment();
					continue;
				}

				tableRowParamMap.clear();
				tableRowParamMap.put("sourceName", sourceName);
				tableRowParamMap.put("wordValue", wordValue);
				tableRowParamMap.put("morphCode", morphCode);
				tableRowParamMap.put("formValue", formValue);
				tableRowParamMap.put("rank", rank);
				tableRowParamMap.put("frequency", frequency);
	
				int insertCount = basicDbService.executeScript(sqlInsertFormFrequencyWhereExistsWord, tableRowParamMap);
				if (insertCount > 0) {
					totalInsertCount.increment();
				}
			}

			// progress
			dataRowCounter++;
			if (dataRowCounter % progressIndicator == 0) {
				long progressPercent = dataRowCounter / progressIndicator;
				logger.debug("{}% - {} rows iterated", progressPercent, dataRowCounter);
			}
		}

		logger.debug("Total form frequency insert count: {}", totalInsertCount.getValue());
		logger.debug("Unsupported morph code row count: {}", unsupportedMorphCount.getValue());

		end();
	}

}
