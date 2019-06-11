package eki.ekilex.runner;

import java.io.InputStream;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import eki.common.constant.DbConstant;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.exception.DataLoadingException;
import eki.ekilex.data.transform.Freeform;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.runner.util.FreeformRowMapper;

@Component
public class LexemeMergerRunner extends AbstractLoaderRunner implements DbConstant {

	private static Logger logger = LoggerFactory.getLogger(LexemeMergerRunner.class);

	private static final String SQL_SELECT_WORD_LEXEME_MEANING_IDS_PATH = "sql/select_word_lexeme_meaning_ids.sql";

	private String sqlSelectWordLexemeMeaningIds;

	private String lexemeMergeName;

	@Override
	public String getDataset() {
		return lexemeMergeName;
	}

	@Transactional
	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
	}

	@Override
	public void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_LEXEME_MEANING_IDS_PATH);
		sqlSelectWordLexemeMeaningIds = getContent(resourceFileInputStream);

	}

	@Transactional
	public void execute(String lexemeMergeName, List<String> lexemeMergeDatasets) throws Exception {
		this.lexemeMergeName = lexemeMergeName;
		start();

		logger.debug("Merging {} lexemes into {}", lexemeMergeDatasets, lexemeMergeName);

		createDatasetIfNotExists(lexemeMergeName);

		List<WordMeaningPair> wordMeaningPairs = getWordMeaningPairs(lexemeMergeDatasets);

		int wordMeaningPairCount = wordMeaningPairs.size();
		logger.debug("Merging lexemes between {} word meaning pairs", wordMeaningPairCount);

		Count tooManyFreqGroupCodeCount = new Count();
		Count tooManyValueStateCodeCount = new Count();

		long wordMeaningPairCounter = 0;
		long progressIndicator = wordMeaningPairCount / Math.min(wordMeaningPairCount, 100);

		for (WordMeaningPair wordMeaningPair : wordMeaningPairs) {

			List<Long> lexemeIds = wordMeaningPair.getLexemeIds();
			List<FullLexeme> allLexemes = new ArrayList<>();
			for (Long lexemeId : lexemeIds) {
				FullLexeme lexeme = getFullLexeme(lexemeId);
				allLexemes.add(lexeme);
			}

			FullLexeme sumLexeme = new FullLexeme();
			sumLexeme.setWordId(wordMeaningPair.getWordId());
			sumLexeme.setMeaningId(wordMeaningPair.getMeaningId());
			sumLexeme.setDatasetCode(lexemeMergeName);
			sumLexeme.setProcessStateCode(PROCESS_STATE_PUBLIC);

			if (CollectionUtils.size(allLexemes) == 1) {
				//copy single to single lexeme
				FullLexeme lexeme = allLexemes.get(0);
				sumLexeme.setFrequencyGroupCode(lexeme.getFrequencyGroupCode());
				sumLexeme.setCorpusFrequency(lexeme.getCorpusFrequency());
				sumLexeme.setLevel1(lexeme.getLevel1());
				sumLexeme.setLevel2(lexeme.getLevel2());
				sumLexeme.setLevel3(lexeme.getLevel3());
				sumLexeme.setValueStateCode(lexeme.getValueStateCode());
				sumLexeme.setFreeforms(lexeme.getFreeforms());
			} else {
				//compare and copy sum into single lexeme
				composeSumLexeme(allLexemes, sumLexeme, tooManyFreqGroupCodeCount, tooManyValueStateCodeCount);
				composeSumLexemeFreeforms(allLexemes, sumLexeme);
			}
			Long sumLexemeId = createLexeme(sumLexeme);
			createFreeforms(sumLexemeId, sumLexeme.getFreeforms());

			// TODO freeform hierarchy
			// TODO lexeme_frequency
			// TODO lexeme_register
			// TODO lexeme_pos
			// TODO lexeme_deriv
			// TODO lexeme_region
			// TODO lexeme_lifecycle_log ??
			// TODO lexeme_process_log ??
			// TODO lex_relation
			// TODO lex_colloc_pos_group
			// TODO lex_colloc_rel_group
			// TODO lex_colloc
			// TODO lexeme_source_link
			// TODO freeform_source_link
			// TODO collocation
			// TODO collocation_freeform

			// progress
			wordMeaningPairCounter++;
			if (wordMeaningPairCounter % progressIndicator == 0) {
				long progressPercent = wordMeaningPairCounter / progressIndicator;
				logger.debug("{}% - {} word meaning pairs iterated", progressPercent, wordMeaningPairCounter);
			}
		}

		logger.info("Found {} competing freq group lexemes", tooManyFreqGroupCodeCount.getValue());
		logger.info("Found {} competing value state lexemes", tooManyValueStateCodeCount.getValue());

		if (true) {
			throw new DataLoadingException("Intentional interruption to avoid transaction commit");
		}

		end();
	}

	private void composeSumLexemeFreeforms(List<FullLexeme> allLexemes, FullLexeme sumLexeme) {

		List<Freeform> sumFreeforms = new ArrayList<>();
		for (FullLexeme lexeme : allLexemes) {
			List<Freeform> srcFreeforms = lexeme.getFreeforms();
			if (CollectionUtils.isEmpty(srcFreeforms)) {
				continue;
			}
			if (CollectionUtils.isEmpty(sumFreeforms)) {
				sumFreeforms.addAll(srcFreeforms);
			} else {
				for (Freeform srcFreeform : srcFreeforms) {
					boolean freeformExists = sumFreeforms.stream()
							.filter(sumFreeform -> Objects.equals(sumFreeform.getType(), srcFreeform.getType()))
							.anyMatch(sumFreeform -> {
								if (StringUtils.equals(sumFreeform.getValueText(), srcFreeform.getValueText())
										|| Objects.equals(sumFreeform.getValueDate(), srcFreeform.getValueDate())) {
									return true;
								}
								return false;
							});
					if (!freeformExists) {
						sumFreeforms.add(srcFreeform);
					}
				}
			}
		}
		sumLexeme.setFreeforms(sumFreeforms);
	}

	private void composeSumLexeme(List<FullLexeme> allLexemes, Lexeme sumLexeme, Count tooManyFreqGroupCodeCount, Count tooManyValueStateCodeCount) throws Exception {

		List<Lexeme> lexemesSortedByLevels = allLexemes.stream()
				.sorted(Comparator
						.comparing(Lexeme::getLevel1)
						.thenComparing(Lexeme::getLevel2)
						.thenComparing(Lexeme::getLevel3))
				.collect(Collectors.toList());
		Lexeme firstLevelLexeme = lexemesSortedByLevels.get(0);
		Integer sumLevel1 = firstLevelLexeme.getLevel1();
		Integer sumLevel2 = firstLevelLexeme.getLevel2();
		Integer sumLevel3 = firstLevelLexeme.getLevel3();
		List<String> frequencyGroupCodeCandidates = allLexemes.stream()
				.filter(lexeme -> StringUtils.isNotBlank(lexeme.getFrequencyGroupCode()))
				.map(Lexeme::getFrequencyGroupCode)
				.distinct()
				.collect(Collectors.toList());
		String sumFrequencyGroupCode;
		if (CollectionUtils.isEmpty(frequencyGroupCodeCandidates)) {
			sumFrequencyGroupCode = null;
		} else if (frequencyGroupCodeCandidates.size() > 1) {
			//logger.debug("More than one freq group codes: {}", frequencyGroupCodeCandidates);
			tooManyFreqGroupCodeCount.increment();
			sumFrequencyGroupCode = frequencyGroupCodeCandidates.get(0);
		} else {
			sumFrequencyGroupCode = frequencyGroupCodeCandidates.get(0);
		}
		Float sumCorpusFrequency = allLexemes.stream()
				.filter(lexeme -> lexeme.getCorpusFrequency() != null)
				.map(Lexeme::getCorpusFrequency)
				.findFirst().orElse(null);
		List<String> valueStateCodeCandidates = allLexemes.stream()
				.filter(lexeme -> StringUtils.isNotBlank(lexeme.getValueStateCode()))
				.map(Lexeme::getValueStateCode)
				.distinct()
				.collect(Collectors.toList());
		String sumValueStateCode;
		if (CollectionUtils.isEmpty(valueStateCodeCandidates)) {
			sumValueStateCode = null;
		} else if (valueStateCodeCandidates.size() > 1) {
			logger.debug("More than one value state codes: {}", valueStateCodeCandidates);
			tooManyValueStateCodeCount.increment();
			sumValueStateCode = valueStateCodeCandidates.get(0);
		} else {
			sumValueStateCode = valueStateCodeCandidates.get(0);
		}
		sumLexeme.setFrequencyGroupCode(sumFrequencyGroupCode);
		sumLexeme.setCorpusFrequency(sumCorpusFrequency);
		sumLexeme.setLevel1(sumLevel1);
		sumLexeme.setLevel2(sumLevel2);
		sumLexeme.setLevel3(sumLevel3);
		sumLexeme.setValueStateCode(sumValueStateCode);
	}

	private void createDatasetIfNotExists(String datasetCode) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("code", datasetCode);
		Map<String, Object> dataset = basicDbService.select(DATASET, paramMap);
		if (dataset == null) {
			paramMap.put("name", datasetCode);
			basicDbService.createWithoutId(DATASET, paramMap);
		}
	}

	private List<WordMeaningPair> getWordMeaningPairs(List<String> datasetCodes) throws Exception {

		//TODO sort lexemes by dataset
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("datasetCodes", datasetCodes);
		paramMap.put("processState", PROCESS_STATE_PUBLIC);
		List<WordMeaningPair> wordMeaningPairs = basicDbService.getResults(sqlSelectWordLexemeMeaningIds, paramMap, new WordMeaningPairRowMapper());
		return wordMeaningPairs;
	}

	private void createFreeforms(Long sumLexemeId, List<Freeform> freeforms) throws Exception {

		for (Freeform freeform : freeforms) {
			if (freeform.getValueText() != null) {
				createLexemeFreeform(sumLexemeId, freeform.getType(), freeform.getValueText(), freeform.getLangCode());				
			} else if (freeform.getValueDate() != null) {
				createLexemeFreeform(sumLexemeId, freeform.getType(), freeform.getValueDate(), freeform.getLangCode());
			} else {
				// other data types?
			}
		}
	}

	private FullLexeme getFullLexeme(Long lexemeId) throws Exception {

		Map<String, Object> paramMap;
		String sql;

		paramMap = new HashMap<>();
		paramMap.put("id", lexemeId);
		sql = "select * from " + LEXEME + " where id = :id";
		FullLexeme lexeme = basicDbService.getSingleResult(sql, paramMap, new FullLexemeRowMapper());

		paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		sql = "select ff.* from " + FREEFORM + " ff, " + LEXEME_FREEFORM + " lff where lff.freeform_id = ff.id and lff.lexeme_id = :lexemeId order by ff.order_by";
		List<Freeform> freeforms = basicDbService.getResults(sql, paramMap, new FreeformRowMapper());
		lexeme.setFreeforms(freeforms);

		//TODO freeform hierarchy?

		return lexeme;
	}

	class WordMeaningPair extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long wordId;

		private Long meaningId;

		private List<Long> lexemeIds;

		public WordMeaningPair() {
		}

		public WordMeaningPair(Long wordId, Long meaningId, List<Long> lexemeIds) {
			this.wordId = wordId;
			this.meaningId = meaningId;
			this.lexemeIds = lexemeIds;
		}

		public Long getWordId() {
			return wordId;
		}

		public void setWordId(Long wordId) {
			this.wordId = wordId;
		}

		public Long getMeaningId() {
			return meaningId;
		}

		public void setMeaningId(Long meaningId) {
			this.meaningId = meaningId;
		}

		public List<Long> getLexemeIds() {
			return lexemeIds;
		}

		public void setLexemeIds(List<Long> lexemeIds) {
			this.lexemeIds = lexemeIds;
		}
	}

	class FullLexeme extends Lexeme {

		private static final long serialVersionUID = 1L;

		private List<Freeform> freeforms;

		public List<Freeform> getFreeforms() {
			return freeforms;
		}

		public void setFreeforms(List<Freeform> freeforms) {
			this.freeforms = freeforms;
		}
	}

	class WordMeaningPairRowMapper implements RowMapper<WordMeaningPair> {

		@Override
		public WordMeaningPair mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long wordId = rs.getLong("word_id");
			Long meaningId = rs.getLong("meaning_id");
			Array lexemeIdsPgArr = rs.getArray("lexeme_ids");
			Long[] lexemeIdsArr = (Long[]) lexemeIdsPgArr.getArray();
			List<Long> lexemeIds = Arrays.asList(lexemeIdsArr);
			WordMeaningPair wordMeaningPair = new WordMeaningPair(wordId, meaningId, lexemeIds);
			return wordMeaningPair;
		}
	}

	class FullLexemeRowMapper implements RowMapper<FullLexeme> {

		@Override
		public FullLexeme mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long lexemeId = rs.getLong("id");
			Long wordId = rs.getLong("word_id");
			Long meaningId = rs.getLong("meaning_id");
			String datasetCode = rs.getString("dataset_code");
			String frequencyGroupCode = rs.getString("frequency_group_code");
			Float corpusFrequency = rs.getFloat("corpus_frequency");
			Integer level1 = rs.getInt("level1");
			Integer level2 = rs.getInt("level2");
			Integer level3 = rs.getInt("level3");
			String valueStateCode = rs.getString("value_state_code");
			String processStateCode = rs.getString("process_state_code");
			Long orderBy = rs.getLong("order_by");

			FullLexeme lexeme = new FullLexeme();
			lexeme.setLexemeId(lexemeId);
			lexeme.setWordId(wordId);
			lexeme.setMeaningId(meaningId);
			lexeme.setDatasetCode(datasetCode);
			lexeme.setFrequencyGroupCode(frequencyGroupCode);
			lexeme.setCorpusFrequency(corpusFrequency);
			lexeme.setLevel1(level1);
			lexeme.setLevel2(level2);
			lexeme.setLevel3(level3);
			lexeme.setValueStateCode(valueStateCode);
			lexeme.setProcessStateCode(processStateCode);
			lexeme.setOrderBy(orderBy);
			return lexeme;
		}

	}
}
