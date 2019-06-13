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
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import eki.common.constant.DbConstant;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.ekilex.data.transform.Freeform;
import eki.ekilex.data.transform.FreeformSourceLink;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.LexemeClassifier;
import eki.ekilex.data.transform.LexemeFrequency;
import eki.ekilex.data.transform.LexemeRelation;
import eki.ekilex.data.transform.LexemeSourceLink;
import eki.ekilex.runner.util.FreeformRowMapper;
import eki.ekilex.runner.util.FreeformSourceLinkRowMapper;
import eki.ekilex.runner.util.LexemeClassifierRowMapper;
import eki.ekilex.runner.util.LexemeFrequencyRowMapper;
import eki.ekilex.runner.util.LexemeRelationRowMapper;
import eki.ekilex.runner.util.LexemeSourceLinkRowMapper;

@Component
public class LexemeMergerRunner extends AbstractLoaderRunner implements DbConstant {

	private static Logger logger = LoggerFactory.getLogger(LexemeMergerRunner.class);

	private static final String SQL_SELECT_WORD_LEXEME_MEANING_IDS_PATH = "sql/select_word_lexeme_meaning_ids.sql";

	private static final String SQL_SELECT_LEXEME_FREEFORMS_PATH = "sql/select_lexeme_freeforms.sql";

	private String sqlSelectWordLexemeMeaningIds;

	private String sqlSelectLexemeFreeforms;

	private String sqlSelectLexeme = "select * from " + LEXEME + " where id = :id";

	private String sqlSelectFreeformChildren = "select ff.*, false as children_exist, false as source_links_exist from " + FREEFORM + " ff where ff.parent_id = :freeformId order by ff.order_by";

	private String sqlSelectLexemeFrequencies = "select lf.* from " + LEXEME_FREQUENCY + " lf where lf.lexeme_id = :lexemeId";

	private String sqlSelectLexemeRegisters = "select c.lexeme_id, c.register_code as code from " + LEXEME_REGISTER + " c where c.lexeme_id = :lexemeId order by c.order_by";

	private String sqlSelectLexemePoses = "select c.lexeme_id, c.pos_code as code from " + LEXEME_POS + " c where c.lexeme_id = :lexemeId order by c.order_by";

	private String sqlSelectLexemeDerivs = "select c.lexeme_id, c.deriv_code as code from " + LEXEME_DERIV + " c where c.lexeme_id = :lexemeId order by c.id";

	private String sqlSelectLexemeRegions = "select c.lexeme_id, c.region_code as code from " + LEXEME_REGION + " c where c.lexeme_id = :lexemeId order by c.id";

	private String sqlSelectLexemeSourceLinks = "select lsl.* from " + LEXEME_SOURCE_LINK + " lsl where lsl.lexeme_id = :lexemeId order by lsl.order_by";

	private String sqlSelectFreeformSourceLinks = "select ffsl.* from " + FREEFORM_SOURCE_LINK + " ffsl where ffsl.freeform_id = :freeformId order by ffsl.order_by";

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

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEME_FREEFORMS_PATH);
		sqlSelectLexemeFreeforms = getContent(resourceFileInputStream);
	}

	@Transactional
	public void execute(String lexemeMergeName, List<String> lexemeMergeDatasets) throws Exception {
		this.lexemeMergeName = lexemeMergeName;
		start();

		logger.debug("Merging {} lexemes into {}", lexemeMergeDatasets, lexemeMergeName);

		createDatasetIfNotExists(lexemeMergeName);

		List<WordMeaningPair> wordMeaningPairs = getWordMeaningPairs(lexemeMergeDatasets);
		Map<Long, Long> sumLexemeIdMap = new HashMap<>();

		int wordMeaningPairCount = wordMeaningPairs.size();
		logger.debug("Merging lexemes between {} word meaning pairs", wordMeaningPairCount);

		Count tooManyFreqGroupCodeCount = new Count();
		Count tooManyValueStateCodeCount = new Count();
		Count summableLexemeCount = new Count();
		Count sumLexemeCount = new Count();
		Count summableLexemeFreeformCount = new Count();
		Count sumLexemeFreeformCount = new Count();
		Count summableLexemeFreeformSourceLinkCount = new Count();
		Count sumLexemeFreeformSourceLinkCount = new Count();
		Count summableLexemeSourceLinkCount = new Count();
		Count sumLexemeSourceLinkCount = new Count();
		Count summableLexemeRelationCount = new Count();
		Count sumLexemeRelationCount = new Count();

		Map<String, Count> countsMap = new HashMap<>();
		countsMap.put("tooManyFreqGroupCodeCount", tooManyFreqGroupCodeCount);
		countsMap.put("tooManyValueStateCodeCount", tooManyValueStateCodeCount);
		countsMap.put("summableLexemeCount", summableLexemeCount);
		countsMap.put("sumLexemeCount", sumLexemeCount);
		countsMap.put("summableLexemeFreeformCount", summableLexemeFreeformCount);
		countsMap.put("sumLexemeFreeformCount", sumLexemeFreeformCount);
		countsMap.put("summableLexemeFreeformSourceLinkCount", summableLexemeFreeformSourceLinkCount);
		countsMap.put("sumLexemeFreeformSourceLinkCount", sumLexemeFreeformSourceLinkCount);
		countsMap.put("summableLexemeSourceLinkCount", summableLexemeSourceLinkCount);
		countsMap.put("sumLexemeSourceLinkCount", sumLexemeSourceLinkCount);
		countsMap.put("summableLexemeRelationCount", summableLexemeRelationCount);
		countsMap.put("sumLexemeRelationCount", sumLexemeRelationCount);

		long wordMeaningPairCounter = 0;
		long progressIndicator = wordMeaningPairCount / Math.min(wordMeaningPairCount, 100);

		for (WordMeaningPair wordMeaningPair : wordMeaningPairs) {

			List<Long> lexemeIds = wordMeaningPair.getLexemeIds();
			List<FullLexeme> allLexemes = new ArrayList<>();
			for (Long lexemeId : lexemeIds) {
				FullLexeme lexeme = getFullLexeme(lexemeId, countsMap);
				allLexemes.add(lexeme);
			}

			FullLexeme sumLexeme = new FullLexeme();
			sumLexeme.setWordId(wordMeaningPair.getWordId());
			sumLexeme.setMeaningId(wordMeaningPair.getMeaningId());
			sumLexeme.setDatasetCode(lexemeMergeName);
			sumLexeme.setProcessStateCode(PROCESS_STATE_PUBLIC);

			if (CollectionUtils.size(allLexemes) == 1) {
				//copy single to single lexeme
				FullLexeme singleLexeme = allLexemes.get(0);
				composeSumLexeme(sumLexeme, singleLexeme);
			} else {
				//compare and copy sum into single lexeme
				composeSumLexeme(sumLexeme, allLexemes, countsMap);
			}
			Long sumLexemeId = createLexeme(sumLexeme);
			sumLexemeCount.increment();
			for (Long lexemeId : lexemeIds) {
				sumLexemeIdMap.put(lexemeId, sumLexemeId);
				summableLexemeCount.increment();
			}

			createLexemeFreeformsAndSourceLinks(sumLexemeId, allLexemes, countsMap);
			createLexemeFrequencies(sumLexemeId, allLexemes);
			createLexemeRegisters(sumLexemeId, allLexemes);
			createLexemePoses(sumLexemeId, allLexemes);
			createLexemeDerivs(sumLexemeId, allLexemes);
			createLexemeRegions(sumLexemeId, allLexemes);
			createLexemeSourceLinks(sumLexemeId, allLexemes, countsMap);

			// TODO lex_colloc_pos_group
			// TODO lex_colloc_rel_group
			// TODO lex_colloc
			// TODO collocation
			// TODO collocation_freeform
			// TODO lexeme_lifecycle_log
			// TODO lexeme_process_log

			// progress
			wordMeaningPairCounter++;
			if (wordMeaningPairCounter % progressIndicator == 0) {
				long progressPercent = wordMeaningPairCounter / progressIndicator;
				logger.debug("{}% - {} word meaning pairs iterated", progressPercent, wordMeaningPairCounter);
			}
		}

		List<LexemeRelation> lexemeRelations = getLexemeRelations(sumLexemeIdMap.keySet());
		summableLexemeRelationCount.increment(lexemeRelations.size());
		createLexemeRelations(sumLexemeIdMap, lexemeRelations, countsMap);

		logger.info("Collected {} lexemes to sum", summableLexemeCount.getValue());
		logger.info("Created {} sum lexemes", sumLexemeCount.getValue());
		logger.info("Found {} competing freq group lexemes", tooManyFreqGroupCodeCount.getValue());
		logger.info("Found {} competing value state lexemes", tooManyValueStateCodeCount.getValue());
		logger.info("Collected {} lexeme freeforms", summableLexemeFreeformCount.getValue());
		logger.info("Created {} lexeme freeforms", sumLexemeFreeformCount.getValue());
		logger.info("Collected {} lexeme freeform source links", summableLexemeFreeformSourceLinkCount.getValue());
		logger.info("Created {} lexeme freeform source links", sumLexemeFreeformSourceLinkCount.getValue());
		logger.info("Collected {} lexeme source links", summableLexemeSourceLinkCount.getValue());
		logger.info("Created {} lexeme source links", sumLexemeSourceLinkCount.getValue());
		logger.info("Found {} lexeme relations", summableLexemeRelationCount.getValue());
		logger.info("Created {} lexeme relations", sumLexemeRelationCount.getValue());

		end();
	}

	private void composeSumLexeme(FullLexeme sumLexeme, FullLexeme lexeme) {
		sumLexeme.setFrequencyGroupCode(lexeme.getFrequencyGroupCode());
		sumLexeme.setCorpusFrequency(lexeme.getCorpusFrequency());
		sumLexeme.setLevel1(lexeme.getLevel1());
		sumLexeme.setLevel2(lexeme.getLevel2());
		sumLexeme.setLevel3(lexeme.getLevel3());
		sumLexeme.setValueStateCode(lexeme.getValueStateCode());
		sumLexeme.setFreeforms(lexeme.getFreeforms());
		sumLexeme.setLexemeFrequencies(lexeme.getLexemeFrequencies());
		sumLexeme.setLexemeRegisters(lexeme.getLexemeRegisters());
		sumLexeme.setLexemePoses(lexeme.getLexemePoses());
		sumLexeme.setLexemeDerivs(lexeme.getLexemeDerivs());
		sumLexeme.setLexemeRegions(lexeme.getLexemeRegions());
		sumLexeme.setLexemeSourceLinks(lexeme.getLexemeSourceLinks());
	}

	private void composeSumLexeme(Lexeme sumLexeme, List<FullLexeme> allLexemes, Map<String, Count> countsMap) throws Exception {

		Count tooManyFreqGroupCodeCount = countsMap .get("tooManyFreqGroupCodeCount");
		Count tooManyValueStateCodeCount = countsMap .get("tooManyValueStateCodeCount");

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

	private FullLexeme getFullLexeme(Long lexemeId, Map<String, Count> countsMap) throws Exception {

		Count summableLexemeFreeformCount = countsMap.get("summableLexemeFreeformCount");
		Count summableLexemeFreeformSourceLinkCount = countsMap.get("summableLexemeFreeformSourceLinkCount");
		Count summableLexemeSourceLinkCount = countsMap.get("summableLexemeSourceLinkCount");

		Map<String, Object> paramMap;
		FreeformRowMapper freeformRowMapper = new FreeformRowMapper();
		FreeformSourceLinkRowMapper freeformSourceLinkRowMapper = new FreeformSourceLinkRowMapper();

		paramMap = new HashMap<>();
		paramMap.put("id", lexemeId);
		FullLexeme lexeme = basicDbService.getSingleResult(sqlSelectLexeme, paramMap, new FullLexemeRowMapper());

		paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		List<Freeform> freeforms = basicDbService.getResults(sqlSelectLexemeFreeforms, paramMap, freeformRowMapper);
		lexeme.setFreeforms(freeforms);
		summableLexemeFreeformCount.increment(freeforms.size());

		List<Freeform> children;
		List<FreeformSourceLink> sourceLinks;

		for (Freeform freeform : freeforms) {
			if (freeform.isChildrenExist()) {
				paramMap = new HashMap<>();
				paramMap.put("freeformId", freeform.getFreeformId());
				children = basicDbService.getResults(sqlSelectFreeformChildren, paramMap, freeformRowMapper);
				freeform.setChildren(children);
				summableLexemeFreeformCount.increment(children.size());
			}
			if (freeform.isSourceLinksExist()) {
				paramMap = new HashMap<>();
				paramMap.put("freeformId", freeform.getFreeformId());
				sourceLinks = basicDbService.getResults(sqlSelectFreeformSourceLinks, paramMap, freeformSourceLinkRowMapper);
				freeform.setSourceLinks(sourceLinks);
				summableLexemeFreeformSourceLinkCount.increment(sourceLinks.size());
			}
		}

		paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		List<LexemeSourceLink> lexemeSourceLinks = basicDbService.getResults(sqlSelectLexemeSourceLinks, paramMap, new LexemeSourceLinkRowMapper());
		lexeme.setLexemeSourceLinks(lexemeSourceLinks);
		summableLexemeSourceLinkCount.increment(lexemeSourceLinks.size());

		paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		List<LexemeFrequency> lexemeFrequencies = basicDbService.getResults(sqlSelectLexemeFrequencies, paramMap, new LexemeFrequencyRowMapper());
		lexeme.setLexemeFrequencies(lexemeFrequencies);

		paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		List<LexemeClassifier> lexemeRegisters = basicDbService.getResults(sqlSelectLexemeRegisters, paramMap, new LexemeClassifierRowMapper());
		lexeme.setLexemeRegisters(lexemeRegisters);

		paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		List<LexemeClassifier> lexemePoses = basicDbService.getResults(sqlSelectLexemePoses, paramMap, new LexemeClassifierRowMapper());
		lexeme.setLexemePoses(lexemePoses);

		paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		List<LexemeClassifier> lexemeDerivs = basicDbService.getResults(sqlSelectLexemeDerivs, paramMap, new LexemeClassifierRowMapper());
		lexeme.setLexemeDerivs(lexemeDerivs);

		paramMap = new HashMap<>();
		paramMap.put("lexemeId", lexemeId);
		List<LexemeClassifier> lexemeRegions = basicDbService.getResults(sqlSelectLexemeRegions, paramMap, new LexemeClassifierRowMapper());
		lexeme.setLexemeRegions(lexemeRegions);

		return lexeme;
	}

	private List<LexemeRelation> getLexemeRelations(Set<Long> lexemeIds) throws Exception {

		List<Long> allLexemeIdList = new ArrayList<>(lexemeIds);
		List<LexemeRelation> lexemeRelations = new ArrayList<>();

		Map<String, Object> paramMap;
		String sql = "select r.lexeme1_id, r.lexeme2_id, r.lex_rel_type_code, r.order_by from " + LEXEME_RELATION + " r where r.lexeme1_id in (:lexemeIds) order by r.order_by";

		int lexemeIdCount = lexemeIds.size();
		final int lexemeSublistLength = 100;
		int lexemeIdSublistFromIndex = 0;
		int lexemeIdSublistToIndex;
		List<Long> lexemeIdSublist;
		List<LexemeRelation> lexemeRelationSublist;

		while (lexemeIdSublistFromIndex < lexemeIdCount) {
			lexemeIdSublistToIndex = lexemeIdSublistFromIndex + lexemeSublistLength;
			lexemeIdSublistToIndex = Math.min(lexemeIdSublistToIndex, lexemeIdCount);
			lexemeIdSublist = allLexemeIdList.subList(lexemeIdSublistFromIndex, lexemeIdSublistToIndex);
			paramMap = new HashMap<>();
			paramMap.put("lexemeIds", lexemeIdSublist);
			lexemeRelationSublist = basicDbService.getResults(sql, paramMap, new LexemeRelationRowMapper());
			if (CollectionUtils.isNotEmpty(lexemeRelationSublist)) {
				lexemeRelations.addAll(lexemeRelationSublist);
			}
			lexemeIdSublistFromIndex = lexemeIdSublistToIndex;
		}
		lexemeRelations = lexemeRelations.stream().sorted(Comparator.comparing(LexemeRelation::getOrderBy)).collect(Collectors.toList());
		return lexemeRelations;
	}

	private void createLexemeFreeformsAndSourceLinks(Long sumLexemeId, List<FullLexeme> allLexemes, Map<String, Count> countsMap) throws Exception {

		Count sumLexemeFreeformCount = countsMap.get("sumLexemeFreeformCount");
		Count sumLexemeFreeformSourceLinkCount = countsMap.get("sumLexemeFreeformSourceLinkCount");

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
		sumLexemeFreeformCount.increment(sumFreeforms.size());

		for (Freeform freeform : sumFreeforms) {
			Long freeformId = null;
			if (freeform.getValueText() != null) {
				freeformId = createLexemeFreeform(sumLexemeId, freeform.getType(), freeform.getValueText(), freeform.getLangCode());
			} else if (freeform.getValueDate() != null) {
				freeformId = createLexemeFreeform(sumLexemeId, freeform.getType(), freeform.getValueDate(), freeform.getLangCode());
			} else {
				// other data types?
			}
			if (freeform.isChildrenExist()) {
				List<Freeform> children = freeform.getChildren();
				createNestedFreeforms(freeformId, children);
				sumLexemeFreeformCount.increment(children.size());
			}
			if (freeform.isSourceLinksExist()) {
				List<FreeformSourceLink> sourceLinks = freeform.getSourceLinks();
				sumLexemeFreeformSourceLinkCount.increment(sourceLinks.size());
				for (FreeformSourceLink sourceLink : sourceLinks) {
					createFreeformSourceLink(freeformId, sourceLink.getType(), sourceLink.getSourceId(), sourceLink.getName(), sourceLink.getValue());
				}
			}
		}
	}

	private void createNestedFreeforms(Long parentId, List<Freeform> freeforms) throws Exception {

		for (Freeform freeform : freeforms) {
			if (freeform.getValueText() != null) {
				createFreeformTextOrDate(parentId, freeform.getType(), freeform.getValueText(), freeform.getLangCode());
			} else if (freeform.getValueDate() != null) {
				createFreeformTextOrDate(parentId, freeform.getType(), freeform.getValueDate(), freeform.getLangCode());
			}
		}
	}

	private void createLexemeFrequencies(Long sumLexemeId, List<FullLexeme> allLexemes) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("lexeme_id", sumLexemeId);
		for (FullLexeme lexeme : allLexemes) {
			List<LexemeFrequency> lexemeFrequencies = lexeme.getLexemeFrequencies();
			if (CollectionUtils.isNotEmpty(lexemeFrequencies)) {
				for (LexemeFrequency lexemeFrequency : lexemeFrequencies) {
					paramMap.put("source_name", lexemeFrequency.getSourceName());
					paramMap.put("created_on", lexemeFrequency.getCreatedOn());
					paramMap.put("rank", lexemeFrequency.getRank());
					paramMap.put("value", lexemeFrequency.getValue());
					basicDbService.createIfNotExists(LEXEME_FREQUENCY, paramMap);
				}
				// first available set is created
				return;
			}
		}
	}

	private void createLexemeRegisters(Long sumLexemeId, List<FullLexeme> allLexemes) throws Exception {

		for (FullLexeme lexeme : allLexemes) {
			List<LexemeClassifier> lexemeClassifiers = lexeme.getLexemeRegisters();
			if (CollectionUtils.isNotEmpty(lexemeClassifiers)) {
				for (LexemeClassifier lexemeClassifier : lexemeClassifiers) {
					createLexemeRegister(sumLexemeId, lexemeClassifier.getCode());
				}
			}
		}
	}

	private void createLexemePoses(Long sumLexemeId, List<FullLexeme> allLexemes) throws Exception {

		for (FullLexeme lexeme : allLexemes) {
			List<LexemeClassifier> lexemeClassifiers = lexeme.getLexemePoses();
			if (CollectionUtils.isNotEmpty(lexemeClassifiers)) {
				for (LexemeClassifier lexemeClassifier : lexemeClassifiers) {
					createLexemePos(sumLexemeId, lexemeClassifier.getCode());
				}
			}
		}
	}

	private void createLexemeDerivs(Long sumLexemeId, List<FullLexeme> allLexemes) throws Exception {

		for (FullLexeme lexeme : allLexemes) {
			List<LexemeClassifier> lexemeClassifiers = lexeme.getLexemeDerivs();
			if (CollectionUtils.isNotEmpty(lexemeClassifiers)) {
				for (LexemeClassifier lexemeClassifier : lexemeClassifiers) {
					createLexemeDeriv(sumLexemeId, lexemeClassifier.getCode());
				}
			}
		}
	}

	private void createLexemeRegions(Long sumLexemeId, List<FullLexeme> allLexemes) throws Exception {

		for (FullLexeme lexeme : allLexemes) {
			List<LexemeClassifier> lexemeClassifiers = lexeme.getLexemeRegions();
			if (CollectionUtils.isNotEmpty(lexemeClassifiers)) {
				for (LexemeClassifier lexemeClassifier : lexemeClassifiers) {
					createLexemeRegion(sumLexemeId, lexemeClassifier.getCode());
				}
			}
		}
	}

	private void createLexemeSourceLinks(Long sumLexemeId, List<FullLexeme> allLexemes, Map<String, Count> countsMap) throws Exception {

		Count sumLexemeSourceLinkCount = countsMap.get("sumLexemeSourceLinkCount");

		for (FullLexeme lexeme : allLexemes) {
			List<LexemeSourceLink> sourceLinks = lexeme.getLexemeSourceLinks();
			if (CollectionUtils.isNotEmpty(sourceLinks)) {
				for (LexemeSourceLink sourceLink : sourceLinks) {
					Long sourceLinkId = createLexemeSourceLinkIfNotExists(sumLexemeId, sourceLink);
					if (sourceLinkId != null) {
						sumLexemeSourceLinkCount.increment();
					}
				}
			}
		}
	}

	private Long createLexemeSourceLinkIfNotExists(Long sumLexemeId, LexemeSourceLink sourceLink) throws Exception {

		Long sourceId = sourceLink.getSourceId();
		ReferenceType type = sourceLink.getType();
		String name = sourceLink.getName();
		String value = sourceLink.getValue();

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", sumLexemeId);
		tableRowParamMap.put("source_id", sourceId);
		tableRowParamMap.put("type", type.name());
		if (StringUtils.isNotBlank(name)) {
			tableRowParamMap.put("name", name);
		}
		if (StringUtils.isNotBlank(value)) {
			tableRowParamMap.put("value", value);
		}
		Long sourceLinkId = basicDbService.createIfNotExists(LEXEME_SOURCE_LINK, tableRowParamMap);

		if (sourceLinkId != null) {
			createLifecycleLog(LifecycleLogOwner.LEXEME, sumLexemeId, LifecycleEventType.CREATE, LifecycleEntity.LEXEME_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, value);
		}

		return sourceLinkId;
	}

	private void createLexemeRelations(Map<Long, Long> sumLexemeIdMap, List<LexemeRelation> lexemeRelations, Map<String, Count> countsMap) throws Exception {

		logger.debug("Creating {} lexeme relations for {} lexemes", lexemeRelations.size(), sumLexemeIdMap.size());

		Count sumLexemeRelationCount = countsMap.get("sumLexemeRelationCount");

		for (LexemeRelation lexemeRelation : lexemeRelations) {
			Long lexeme1Id = lexemeRelation.getLexeme1Id();
			Long lexeme2Id = lexemeRelation.getLexeme2Id();
			String lexemeRelationTypeCode = lexemeRelation.getLexemeRelationTypeCode();
			Long sumLexeme1Id = sumLexemeIdMap.get(lexeme1Id);
			Long sumLexeme2Id = sumLexemeIdMap.get(lexeme2Id);
			Long lexemeRelationId = createLexemeRelation(sumLexeme1Id, sumLexeme2Id, lexemeRelationTypeCode);
			if (lexemeRelationId != null) {
				sumLexemeRelationCount.increment();
			}
		}

		logger.debug("Done with lexeme relations");
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

		private List<LexemeFrequency> lexemeFrequencies;

		private List<LexemeClassifier> lexemeRegisters;

		private List<LexemeClassifier> lexemePoses;

		private List<LexemeClassifier> lexemeDerivs;

		private List<LexemeClassifier> lexemeRegions;

		private List<LexemeSourceLink> lexemeSourceLinks;

		public List<Freeform> getFreeforms() {
			return freeforms;
		}

		public void setFreeforms(List<Freeform> freeforms) {
			this.freeforms = freeforms;
		}

		public List<LexemeFrequency> getLexemeFrequencies() {
			return lexemeFrequencies;
		}

		public void setLexemeFrequencies(List<LexemeFrequency> lexemeFrequencies) {
			this.lexemeFrequencies = lexemeFrequencies;
		}

		public List<LexemeClassifier> getLexemeRegisters() {
			return lexemeRegisters;
		}

		public void setLexemeRegisters(List<LexemeClassifier> lexemeRegisters) {
			this.lexemeRegisters = lexemeRegisters;
		}

		public List<LexemeClassifier> getLexemePoses() {
			return lexemePoses;
		}

		public void setLexemePoses(List<LexemeClassifier> lexemePoses) {
			this.lexemePoses = lexemePoses;
		}

		public List<LexemeClassifier> getLexemeDerivs() {
			return lexemeDerivs;
		}

		public void setLexemeDerivs(List<LexemeClassifier> lexemeDerivs) {
			this.lexemeDerivs = lexemeDerivs;
		}

		public List<LexemeClassifier> getLexemeRegions() {
			return lexemeRegions;
		}

		public void setLexemeRegions(List<LexemeClassifier> lexemeRegions) {
			this.lexemeRegions = lexemeRegions;
		}

		public List<LexemeSourceLink> getLexemeSourceLinks() {
			return lexemeSourceLinks;
		}

		public void setLexemeSourceLinks(List<LexemeSourceLink> lexemeSourceLinks) {
			this.lexemeSourceLinks = lexemeSourceLinks;
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
