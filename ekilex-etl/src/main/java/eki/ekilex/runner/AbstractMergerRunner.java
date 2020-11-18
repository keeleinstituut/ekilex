package eki.ekilex.runner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.data.util.AbstractRowMapper;
import eki.ekilex.data.transform.WordId;
import eki.ekilex.data.transform.WordLexemeMeaning;
import eki.ekilex.service.MergeService;
import eki.ekilex.service.ReportComposer;

public abstract class AbstractMergerRunner extends AbstractLoaderRunner implements GlobalConstant {

	private static Logger logger = LoggerFactory.getLogger(AbstractMergerRunner.class);

	private static final int DEFAULT_LEXEME_LEVEL = 1;

	protected ReportComposer reportComposer;

	@Autowired
	protected MergeService mergeService;

	protected List<List<Long>> collectSimilarWordIdSets(Map<Long, WordId> simWordIdMap) {

		for (WordId wordIdObj : simWordIdMap.values()) {
			Long wordId = wordIdObj.getWordId();
			List<Long> combSimWordIds = new ArrayList<>();
			combSimWordIds.add(wordId);
			appendSimWordIds(combSimWordIds, wordIdObj, simWordIdMap);
			if (combSimWordIds.size() > 1) {
				combSimWordIds = combSimWordIds.stream().sorted().collect(Collectors.toList());
			}
			wordIdObj.setSimWordIds(combSimWordIds);
		}
		List<List<Long>> simWordIdsSets = simWordIdMap.values().stream().map(WordId::getSimWordIds).distinct().collect(Collectors.toList());
		return simWordIdsSets;
	}

	protected void appendSimWordIds(List<Long> srcSimWordIds, WordId currWordIdObj, Map<Long, WordId> simWordIdMap) {

		List<Long> currSimWordIds = currWordIdObj.getSimWordIds();
		for (Long currSimWordId : currSimWordIds) {
			if (srcSimWordIds.contains(currSimWordId)) {
				continue;
			}
			srcSimWordIds.add(currSimWordId);
			WordId currSimWordIdObj = simWordIdMap.get(currSimWordId);
			appendSimWordIds(srcSimWordIds, currSimWordIdObj, simWordIdMap);
		}
	}

	protected void mergeLexemeData(Long targetWordId, Long sourceWordId, Map<String, Count> updateCountMap, Map<String, Count> deleteCountMap) throws Exception {

		List<WordLexemeMeaning> sourceWordLexemes = mergeService.getLexemesByWord(sourceWordId);
		for (WordLexemeMeaning sourceWordLexeme : sourceWordLexemes) {
			Long sourceWordLexemeId = sourceWordLexeme.getLexemeId();
			Long sourceWordLexemeMeaningId = sourceWordLexeme.getMeaningId();
			String sourceWordLexemeDatasetCode = sourceWordLexeme.getDatasetCode();

			Long targetWordLexemeId = mergeService.getLexemeId(targetWordId, sourceWordLexemeMeaningId, sourceWordLexemeDatasetCode);
			boolean lexemeExists = targetWordLexemeId != null;

			if (lexemeExists) {
				Complexity sourceWordLexemeComplexity = sourceWordLexeme.getComplexity();
				boolean isUpdateTargetLexemeToSimple = Complexity.SIMPLE.equals(sourceWordLexemeComplexity);
				mergeService.moveLexemesData(targetWordLexemeId, Arrays.asList(sourceWordLexemeId), isUpdateTargetLexemeToSimple, updateCountMap);
				mergeService.deleteLexemes(Arrays.asList(sourceWordLexemeId), deleteCountMap);
			} else {
				Integer currentMaxLevel = mergeService.getWordLexemesMaxFirstLevel(targetWordId);
				int level1 = currentMaxLevel + 1;
				mergeService.reassignLexemeToWordAndUpdateLevels(targetWordId, sourceWordLexemeId, level1, DEFAULT_LEXEME_LEVEL, updateCountMap);
			}
		}
	}

	protected void logCounts(String description, Map<String, Count> countMap) {
		logger.info(description);
		for (Map.Entry<String, Count> countEntry : countMap.entrySet()) {
			logger.info("{} : {}", countEntry.getKey(), countEntry.getValue().getValue());
		}
	}

	protected class WordStress extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long formId;

		private String value;

		private String valuePrese;

		private String displayForm;

		public WordStress(Long formId, String value, String valuePrese, String displayForm) {
			this.formId = formId;
			this.value = value;
			this.valuePrese = valuePrese;
			this.displayForm = displayForm;
		}

		public Long getFormId() {
			return formId;
		}

		public String getValue() {
			return value;
		}

		public String getValuePrese() {
			return valuePrese;
		}

		public String getDisplayForm() {
			return displayForm;
		}

	}

	protected class WordStressRowMapper extends AbstractRowMapper implements RowMapper<WordStress> {

		@Override
		public WordStress mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long formId = rs.getObject("form_id", Long.class);
			String value = rs.getString("value");
			String valuePrese = rs.getString("value_prese");
			String displayForm = rs.getString("display_form");

			WordStress wordStress = new WordStress(formId, value, valuePrese, displayForm);
			return wordStress;
		}
	}

	protected void appendToReport(boolean doReports, String reportName, String ... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}
}
