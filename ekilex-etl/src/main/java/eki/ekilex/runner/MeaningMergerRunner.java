package eki.ekilex.runner;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.ekilex.data.transform.WordLexemeMeaning;
import eki.ekilex.service.MergeService;

@Component
public class MeaningMergerRunner extends AbstractLoaderRunner implements GlobalConstant {

	private static Logger logger = LoggerFactory.getLogger(MeaningMergerRunner.class);

	private static final String SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_PS = "sql/select_meaning_join_candidates_for_ps.sql";

	private static final String SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_COL = "sql/select_meaning_join_candidates_for_col.sql";

	private static final String SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_QQ = "sql/select_meaning_join_candidates_for_qq.sql";

	private static final String SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_EV = "sql/select_meaning_join_candidates_for_ev.sql";

	private List<String> sqlSelectMeaningJoinCandidatesForDatasets;

	private String compoundDatasetCode;

	@Autowired
	private MergeService mergeService;

	@Override
	public String getDataset() {
		return compoundDatasetCode;
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
	public void deleteDatasetData() throws Exception {
	}

	@Override
	public void initialise() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;
		String sqlSelectMeaningJoinCandidatesForDataset;

		sqlSelectMeaningJoinCandidatesForDatasets = new ArrayList<>();

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_PS);
		sqlSelectMeaningJoinCandidatesForDataset = getContent(resourceFileInputStream);
		sqlSelectMeaningJoinCandidatesForDatasets.add(sqlSelectMeaningJoinCandidatesForDataset);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_COL);
		sqlSelectMeaningJoinCandidatesForDataset = getContent(resourceFileInputStream);
		sqlSelectMeaningJoinCandidatesForDatasets.add(sqlSelectMeaningJoinCandidatesForDataset);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_EV);
		sqlSelectMeaningJoinCandidatesForDataset = getContent(resourceFileInputStream);
		sqlSelectMeaningJoinCandidatesForDatasets.add(sqlSelectMeaningJoinCandidatesForDataset);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANING_JOIN_CANDIDATES_FOR_QQ);
		sqlSelectMeaningJoinCandidatesForDataset = getContent(resourceFileInputStream);
		sqlSelectMeaningJoinCandidatesForDatasets.add(sqlSelectMeaningJoinCandidatesForDataset);
	}

	@Transactional
	public void execute(String compoundDatasetCode) throws Exception {
		this.compoundDatasetCode = compoundDatasetCode;

		start();

		Map<Long, List<MeaningJoinCandidate>> meaningJoinCandidatesMap = collectMeaningJoinCandidates(compoundDatasetCode);
		int joinedMeaningCount = meaningJoinCandidatesMap.size();

		logger.debug("Joined meaning count {}", joinedMeaningCount);

		Map<String, Count> updateCountMap = mergeService.getUpdateCountMap();
		Map<String, Count> deleteCountMap = mergeService.getDeleteCountMap();

		Map<Long, Long> resolvedCompMeaningIdMap = new HashMap<>();
		List<Long> failedDeleteMeaningIds = new ArrayList<>();

		long joinedMeaningCounter = 0;
		long progressIndicator = joinedMeaningCount / Math.min(joinedMeaningCount, 100);

		for (Entry<Long, List<MeaningJoinCandidate>> meaningJoinCandidatesEntry : meaningJoinCandidatesMap.entrySet()) {

			Long ssMeaningId = meaningJoinCandidatesEntry.getKey();
			List<MeaningJoinCandidate> meaningJoinCandidates = meaningJoinCandidatesEntry.getValue();
			meaningJoinCandidates = filterDuplicateJoins(ssMeaningId, meaningJoinCandidates, resolvedCompMeaningIdMap);

			List<Long> simpleWordIds = meaningJoinCandidates.stream().filter(MeaningJoinCandidate::isOverrideComplexity).map(MeaningJoinCandidate::getWordId).collect(Collectors.toList());
			List<Long> compMeaningIds = meaningJoinCandidates.stream().map(MeaningJoinCandidate::getCompMeaningId).distinct().collect(Collectors.toList());
			List<Long> allMeaningIds = new ArrayList<>();
			allMeaningIds.add(ssMeaningId);
			allMeaningIds.addAll(compMeaningIds);

			List<WordLexemeMeaning> allLexemes = mergeService.getLexemesByMeanings(allMeaningIds, compoundDatasetCode);
			Map<Long, List<WordLexemeMeaning>> mergingLexemesByWordIdMap = allLexemes.stream().collect(Collectors.groupingBy(WordLexemeMeaning::getWordId));

			//merge, move, delete lexemes
			for (Entry<Long, List<WordLexemeMeaning>> mergingLexemeEntries : mergingLexemesByWordIdMap.entrySet()) {

				Long targetWordId = mergingLexemeEntries.getKey();
				List<WordLexemeMeaning> mergingLexemes = mergingLexemeEntries.getValue();
				Long ssLexemeId = mergingLexemes.stream().filter(mergingLexeme -> mergingLexeme.getMeaningId().equals(ssMeaningId)).map(WordLexemeMeaning::getLexemeId).findFirst().orElse(null);

				Long targetLexemeId;
				if (ssLexemeId == null) {

					//force move one to ss
					WordLexemeMeaning forcedSsLexeme = mergingLexemes.get(0);
					targetLexemeId = forcedSsLexeme.getLexemeId();
					mergeService.reassignLexemeToMeaning(ssMeaningId, targetLexemeId);
				} else {
					targetLexemeId = ssLexemeId;
				}
				List<Long> sourceMergingLexemeIds = mergingLexemes.stream()
						.map(WordLexemeMeaning::getLexemeId).filter(mergingLexemeId -> !mergingLexemeId.equals(targetLexemeId))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(sourceMergingLexemeIds)) {
					boolean isOverrideComplexity = simpleWordIds.contains(targetWordId);
					mergeService.moveLexemesData(targetLexemeId, sourceMergingLexemeIds, isOverrideComplexity, updateCountMap);
					mergeService.deleteLexemes(sourceMergingLexemeIds, deleteCountMap);
				}
			}

			//merge, delete meanings
			mergeService.moveMeaningsData(ssMeaningId, compMeaningIds, updateCountMap);
			mergeService.deleteMeanings(compMeaningIds, failedDeleteMeaningIds, deleteCountMap);

			// progress
			joinedMeaningCounter++;
			if (joinedMeaningCounter % progressIndicator == 0) {
				long progressPercent = joinedMeaningCounter / progressIndicator;
				logger.debug("{}% - {} target meanings iterated", progressPercent, joinedMeaningCounter);
			}
		}

		logger.info(">>>> Update counts are as following:");
		for (Entry<String, Count> updateCountEntry : updateCountMap.entrySet()) {
			logger.info("{} : {}", updateCountEntry.getKey(), updateCountEntry.getValue().getValue());
		}

		logger.info(">>>> Delete counts are as following:");
		for (Entry<String, Count> deleteCountEntry : deleteCountMap.entrySet()) {
			logger.info("{} : {}", deleteCountEntry.getKey(), deleteCountEntry.getValue().getValue());
		}

		logger.info(">>>> Failed meaning delete count: {}", failedDeleteMeaningIds.size());
		if (CollectionUtils.isNotEmpty(failedDeleteMeaningIds)) {
			logger.debug("Meaning ids: {}", failedDeleteMeaningIds);
		}

		end();
	}

	private List<MeaningJoinCandidate> filterDuplicateJoins(Long ssMeaningId, List<MeaningJoinCandidate> compMeaningJoinCandidates, Map<Long, Long> resolvedCompMeaningIdMap) {
		List<MeaningJoinCandidate> filteredCompMeaningJoinCandidates = new ArrayList<>();
		for (MeaningJoinCandidate compMeaningJoinCandidate : compMeaningJoinCandidates) {
			Long compMeaningId = compMeaningJoinCandidate.getCompMeaningId();
			Long resolvingSsMeaningId = resolvedCompMeaningIdMap.get(compMeaningId);
			if (resolvingSsMeaningId == null) {
				resolvingSsMeaningId = ssMeaningId;
				resolvedCompMeaningIdMap.put(compMeaningId, resolvingSsMeaningId);
			}
			if (!resolvingSsMeaningId.equals(ssMeaningId)) {
				//logger.debug("Component meaning {} is already joined with {}, skipping {}", compMeaningId, resolvingSsMeaningId, ssMeaningId);
				continue;
			}
			filteredCompMeaningJoinCandidates.add(compMeaningJoinCandidate);
		}
		return filteredCompMeaningJoinCandidates;
	}

	private Map<Long, List<MeaningJoinCandidate>> collectMeaningJoinCandidates(String datasetCode) throws Exception {

		Map<Long, List<MeaningJoinCandidate>> joinableWordMeaningPairMap = new HashMap<>();

		for (String sqlSelectMeaningJoinCandidatesForDataset : sqlSelectMeaningJoinCandidatesForDatasets) {

			List<MeaningJoinCandidate> joinCandidates = getJoinCandidatesForDataset(sqlSelectMeaningJoinCandidatesForDataset, datasetCode);
			logger.debug("{} join candidates collected for a dataset", joinCandidates.size());

			for (MeaningJoinCandidate joinCandidate : joinCandidates) {
				Long ssMeaningId = joinCandidate.getSsMeaningId();
				List<MeaningJoinCandidate> wordMeaningJoinCandidates = joinableWordMeaningPairMap.get(ssMeaningId);
				if (wordMeaningJoinCandidates == null) {
					wordMeaningJoinCandidates = new ArrayList<>();
					joinableWordMeaningPairMap.put(ssMeaningId, wordMeaningJoinCandidates);
				}
				wordMeaningJoinCandidates.add(joinCandidate);
			}
		}
		return joinableWordMeaningPairMap;
	}

	private List<MeaningJoinCandidate> getJoinCandidatesForDataset(String sqlSelectMeaningJoinCandidatesForDataset, String datasetCode) throws Exception {
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("datasetCode", datasetCode);
		List<MeaningJoinCandidate> joinCandidates = basicDbService.getResults(sqlSelectMeaningJoinCandidatesForDataset, tableRowParamMap, new MeaningJoinCandidateRowMapper());
		return joinCandidates;
	}

	class MeaningJoinCandidate extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long wordId;

		private String word;

		private Long ssLexemeId;

		private Long ssMeaningId;

		private Long compLexemeId;

		private Long compMeaningId;

		private boolean overrideComplexity;

		public Long getWordId() {
			return wordId;
		}

		public void setWordId(Long wordId) {
			this.wordId = wordId;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public Long getSsLexemeId() {
			return ssLexemeId;
		}

		public void setSsLexemeId(Long ssLexemeId) {
			this.ssLexemeId = ssLexemeId;
		}

		public Long getSsMeaningId() {
			return ssMeaningId;
		}

		public void setSsMeaningId(Long ssMeaningId) {
			this.ssMeaningId = ssMeaningId;
		}

		public Long getCompLexemeId() {
			return compLexemeId;
		}

		public void setCompLexemeId(Long compLexemeId) {
			this.compLexemeId = compLexemeId;
		}

		public Long getCompMeaningId() {
			return compMeaningId;
		}

		public void setCompMeaningId(Long compMeaningId) {
			this.compMeaningId = compMeaningId;
		}

		public boolean isOverrideComplexity() {
			return overrideComplexity;
		}

		public void setOverrideComplexity(boolean overrideComplexity) {
			this.overrideComplexity = overrideComplexity;
		}

	}

	class MeaningJoinCandidateRowMapper implements RowMapper<MeaningJoinCandidate> {

		@Override
		public MeaningJoinCandidate mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long wordId = rs.getObject("word_id", Long.class);
			String word = rs.getObject("word", String.class);
			Long ssLexemeId = rs.getObject("ss_lexeme_id", Long.class);
			Long ssMeaningId = rs.getObject("ss_meaning_id", Long.class);
			Long compLexemeId = rs.getObject("comp_lexeme_id", Long.class);
			Long compMeaningId = rs.getObject("comp_meaning_id", Long.class);
			boolean isOverrideComplexity = rs.getBoolean("is_override_complexity");
			MeaningJoinCandidate meaningJoinCandidate = new MeaningJoinCandidate();
			meaningJoinCandidate.setWordId(wordId);
			meaningJoinCandidate.setWord(word);
			meaningJoinCandidate.setSsLexemeId(ssLexemeId);
			meaningJoinCandidate.setSsMeaningId(ssMeaningId);
			meaningJoinCandidate.setCompLexemeId(compLexemeId);
			meaningJoinCandidate.setCompMeaningId(compMeaningId);
			meaningJoinCandidate.setOverrideComplexity(isOverrideComplexity);
			return meaningJoinCandidate;
		}
	}
}
