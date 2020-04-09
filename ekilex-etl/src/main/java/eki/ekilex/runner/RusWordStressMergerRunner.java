package eki.ekilex.runner;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.data.Count;
import eki.ekilex.service.ReportComposer;

@Component
public class RusWordStressMergerRunner extends AbstractMergerRunner {

	private static Logger logger = LoggerFactory.getLogger(RusWordStressMergerRunner.class);

	private static final char DISPLAY_FORM_STRESS_CHAR = '"';

	private static final String STRESS_MARKUP_BEGINNING = "<eki-stress>";

	private static final String STRESS_MARKUP_END = "</eki-stress>";

	private static final String SQL_SELECT_RUS_WORDS_STRESS_DATA = "sql/select_rus_words_stress_data.sql";

	private static final String REPORT_RUS_STRESS_MERGE_FAILED_WORDS = "rus_stress_merge_failed_words";

	private String sqlSelectRusWordsStressData;

	@Override
	String getDataset() {
		return "ruswordstressmerge";
	}

	@Override
	Complexity getLexemeComplexity() {
		return null;
	}

	@Override
	Complexity getDefinitionComplexity() {
		return null;
	}

	@Override
	Complexity getFreeformComplexity() {
		return null;
	}

	@Override
	void deleteDatasetData() {
	}

	@Override
	void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_RUS_WORDS_STRESS_DATA);
		sqlSelectRusWordsStressData = getContent(resourceFileInputStream);
	}

	@Transactional
	public void execute(boolean doReports) throws Exception {

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " loader", REPORT_RUS_STRESS_MERGE_FAILED_WORDS);
		}
		start();

		Count properStressCount = new Count();
		Count mergedStressCount = new Count();
		Count failedStressdMergeCount = new Count();
		List<WordStress> wordsStressList = getWordsStressData();
		int stressCount = wordsStressList.size();
		logger.debug("Starting to merge {} word stress", stressCount);

		long stressCounter = 0;
		long progressIndicator = stressCount / Math.min(stressCount, 100);

		for (WordStress wordStress : wordsStressList) {

			Long formId = wordStress.getFormId();
			String value = wordStress.getValue();
			String displayForm = wordStress.getDisplayForm();
			String convertedValuePrese = convertToValuePrese(displayForm);
			String existingValuePrese = wordStress.getValuePrese();

			if (StringUtils.equals(existingValuePrese, convertedValuePrese)) {
				properStressCount.increment();
			} else {
				boolean isExistingValuePreseDecorated = isDecorated(existingValuePrese);
				if (isExistingValuePreseDecorated) {
					appendToReport(doReports, REPORT_RUS_STRESS_MERGE_FAILED_WORDS, value, displayForm, existingValuePrese);
					failedStressdMergeCount.increment();
				} else {
					mergeService.updateFormValuePrese(formId, convertedValuePrese);
					mergedStressCount.increment();
				}
			}

			stressCounter++;
			if (stressCounter % progressIndicator == 0) {
				long progressPercent = stressCounter / progressIndicator;
				logger.debug("{}% - {} word stress iterated", progressPercent, stressCounter);
			}
		}

		logger.debug("{} word stress is already valid", properStressCount.getValue());
		logger.debug("{} word stress merged", mergedStressCount.getValue());
		logger.debug("Failed to merge {} word stress", failedStressdMergeCount.getValue());

		end();
	}

	private List<WordStress> getWordsStressData() throws Exception {

		List<WordStress> wordStressList = basicDbService.getResults(sqlSelectRusWordsStressData, null, new WordStressRowMapper());
		return wordStressList;
	}

	private String convertToValuePrese(String displayForm) {

		StringBuilder valuePreseBuilder = new StringBuilder();
		char[] displayFormChars = displayForm.toCharArray();

		boolean surroundWithStressMarkup = false;
		for (char displayFormChar : displayFormChars) {
			if (surroundWithStressMarkup) {
				surroundWithStressMarkup(valuePreseBuilder, displayFormChar);
				surroundWithStressMarkup = false;
				continue;
			}

			boolean isStressMark = DISPLAY_FORM_STRESS_CHAR == displayFormChar;
			if (isStressMark) {
				surroundWithStressMarkup = true;
				continue;
			}

			valuePreseBuilder.append(displayFormChar);
		}
		return valuePreseBuilder.toString();
	}

	private void surroundWithStressMarkup(StringBuilder builder, char displayFormChar) {

		builder.append(STRESS_MARKUP_BEGINNING);
		builder.append(displayFormChar);
		builder.append(STRESS_MARKUP_END);
	}

	private boolean isDecorated(String text) {

		Pattern pattern = Pattern.compile("<eki-[^>]*>.*?</eki-[^>]*>");
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

}
