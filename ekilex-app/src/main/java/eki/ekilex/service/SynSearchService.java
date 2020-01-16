package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eki.common.constant.DbConstant;
import eki.common.constant.LayerName;
import eki.common.constant.LexemeType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.RelationStatus;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.LexemeData;
import eki.ekilex.data.LogData;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordLangGroup;
import eki.ekilex.data.RelationParam;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.SynRelationParamTuple;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.data.WordSynLexeme;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.ProcessDbService;

@Component
public class SynSearchService extends AbstractWordSearchService {

	private static final String RAW_RELATION_CODE = "raw";

	private static final float DEFAULT_LEXEME_WEIGHT = 1;

	@Value("#{${relation.weight.multipliers}}")
	private Map<String, Float> relationWeightMultiplierMap;

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Autowired
	private ProcessDbService processDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Autowired
	private LookupDbService lookupDbService;

	@Transactional
	public WordSynDetails getWordSynDetails(Long wordId, String datasetCode, LayerName layerName, List<String> candidateLangs, List<String> meaningWordLangs) {

		List<String> datasetCodeList = new ArrayList<>(Collections.singletonList(datasetCode));
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodeList);
		WordSynDetails wordDetails = synSearchDbService.getWordDetails(wordId);
		List<LexemeData> lexemeDatas = processDbService.getLexemeDatas(wordId, datasetCode, layerName);
		boolean isSynLayerComplete = lexemeDatas.stream().allMatch(lexemeData -> StringUtils.equals(DbConstant.PROCESS_STATE_COMPLETE, lexemeData.getLayerProcessStateCode()));
		Integer wordProcessLogCount = processDbService.getLogCountForWord(wordId);
		String mainWordLang = wordDetails.getLanguage();

		List<WordSynLexeme> synLexemes = synSearchDbService.getWordPrimarySynonymLexemes(wordId, searchDatasetsRestriction, layerName);

		if (LayerName.BILING_RUS == layerName) {
			synLexemes.forEach(lexeme -> populateLexeme(lexeme, mainWordLang, meaningWordLangs));
		} else {
			synLexemes.forEach(lexeme -> populateLexeme(lexeme, mainWordLang, Collections.singletonList(mainWordLang)));
		}

		lexemeLevelPreseUtil.combineLevels(synLexemes);

		List<SynRelation> relations = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(candidateLangs)) {
			List<SynRelationParamTuple> relationTuples = synSearchDbService
					.getWordSynRelations(wordId, RAW_RELATION_CODE, datasetCode, candidateLangs, classifierLabelLang, classifierLabelTypeDescrip);
			relations = conversionUtil.composeSynRelations(relationTuples);
		}

		wordDetails.setLexemes(synLexemes);
		wordDetails.setRelations(relations);
		wordDetails.setSynLayerComplete(isSynLayerComplete);
		wordDetails.setWordProcessLogCount(wordProcessLogCount);

		return wordDetails;
	}

	private void populateLexeme(WordSynLexeme lexeme, String mainWordLanguage, List<String> meaningWordLangs) {

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();

		List<MeaningWordLangGroup> meaningWordLangGroups = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(meaningWordLangs)) {
			List<MeaningWord> meaningWords = synSearchDbService.getSynMeaningWords(lexemeId, meaningWordLangs);
			meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, mainWordLanguage);
		}

		List<Classifier> lexemePos = commonDataDbService.getLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<DefinitionRefTuple> definitionRefTuples =
				commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);

		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
				commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);

		lexeme.setPos(lexemePos);
		lexeme.setMeaningWordLangGroups(meaningWordLangGroups);
		lexeme.setDefinitions(definitions);
		lexeme.setUsages(usages);
	}

	@Transactional
	public void changeRelationStatus(Long relationId, String relationStatus) {

		LogData logData;
		if (RelationStatus.DELETED.name().equals(relationStatus)) {
			moveChangedRelationToLast(relationId);
			logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, relationId, relationStatus);
		} else {
			logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, relationId, relationStatus);
		}
		createLifecycleLog(logData);
		synSearchDbService.changeRelationStatus(relationId, relationStatus);
	}

	@Transactional
	public void createSecondarySynLexeme(Long meaningId, Long wordId, String datasetCode, Long existingLexemeId, Long relationId) {

		List<RelationParam> relationParams = synSearchDbService.getWordRelationParams(relationId);
		Float lexemeWeight = getCalculatedLexemeWeight(relationParams);

		Long lexemeId = synSearchDbService.createLexeme(wordId, meaningId, datasetCode, LexemeType.SECONDARY, lexemeWeight, existingLexemeId);
		String synWordValue = lookupDbService.getWordValue(wordId);
		LogData matchLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.MEANING_WORD, existingLexemeId, synWordValue);
		createLifecycleLog(matchLogData);

		LogData relationLogData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, relationId, RelationStatus.PROCESSED.name());
		createLifecycleLog(relationLogData);
		synSearchDbService.changeRelationStatus(relationId, RelationStatus.PROCESSED.name());

		WordSynDetails wordDetails = synSearchDbService.getWordDetails(wordId);
		List<MeaningWord> meaningWords = synSearchDbService.getSynMeaningWords(lexemeId, Collections.singletonList(wordDetails.getLanguage()));

		for (MeaningWord meaningWord : meaningWords) {
			Long meaningWordRelationId = synSearchDbService.getRelationId(meaningWord.getWordId(), wordId, RAW_RELATION_CODE);

			if (meaningWordRelationId != null) {
				LogData oppositeRelationLogData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, meaningWordRelationId,
						RelationStatus.PROCESSED.name());
				createLifecycleLog(oppositeRelationLogData );
				synSearchDbService.changeRelationStatus(meaningWordRelationId, RelationStatus.PROCESSED.name());
			}
		}
	}

	private void moveChangedRelationToLast(Long relationId) {
		List<SynRelation> existingRelations = synSearchDbService.getExistingFollowingRelationsForWord(relationId, RAW_RELATION_CODE);

		if (existingRelations.size() > 1) {
			SynRelation lastRelation = existingRelations.get(existingRelations.size() - 1);
			List<Long> existingOrderByValues = existingRelations.stream().map(SynRelation::getOrderBy).collect(Collectors.toList());

			cudDbService.updateWordRelationOrderBy(relationId, lastRelation.getOrderBy());
			existingRelations.remove(0);

			existingOrderByValues.remove(existingOrderByValues.size() - 1);

			int relIdx = 0;
			for (SynRelation relation : existingRelations) {
				cudDbService.updateWordRelationOrderBy(relation.getId(), existingOrderByValues.get(relIdx));
				relIdx++;
			}
		}
	}

	private Float getCalculatedLexemeWeight(List<RelationParam> relationParams) {

		if (relationParams.isEmpty()) {
			return DEFAULT_LEXEME_WEIGHT;
		}

		float dividend = 0;
		float divisor = 0;

		for (RelationParam relationParam : relationParams) {
			String relationParamName = relationParam.getName();
			Float relationParamValue = relationParam.getValue();
			Float relationParamWeightMultiplier = relationWeightMultiplierMap.get(relationParamName);

			dividend += (relationParamValue * relationParamWeightMultiplier);
			divisor += relationParamWeightMultiplier;
		}

		return dividend / divisor;
	}
}
