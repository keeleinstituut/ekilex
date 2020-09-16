package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import eki.common.constant.ActivityEntity;
import eki.common.constant.Complexity;
import eki.common.constant.LexemeType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.RelationStatus;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.LogData;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordLangGroup;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TypeWordRelParam;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordNote;
import eki.ekilex.data.WordRelationDetails;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.SynSearchDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class SynSearchService extends AbstractWordSearchService {

	private static final String RAW_RELATION_CODE = "raw";

	private static final float DEFAULT_LEXEME_WEIGHT = 1;

	@Value("#{${relation.weight.multipliers}}")
	private Map<String, Float> relationWeightMultiplierMap;

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public WordDetails getWordSynDetails(
			Long wordId, String datasetCode, List<String> synCandidateLangCodes, List<String> synMeaningWordLangCodes,
			Tag activeTag, DatasetPermission userRole) throws Exception {

		List<String> datasetCodeList = new ArrayList<>(Collections.singletonList(datasetCode));
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodeList);
		Word word = synSearchDbService.getWordDetails(wordId);
		permCalculator.applyCrud(userRole, word);
		List<NoteSourceTuple> wordNoteSourceTuples = commonDataDbService.getWordNoteSourceTuples(wordId);
		List<WordNote> wordNotes = conversionUtil.composeNotes(WordNote.class, wordId, wordNoteSourceTuples);
		permCalculator.filterVisibility(userRole, wordNotes);
		String wordLang = word.getLang();

		List<WordLexeme> synLexemes = synSearchDbService.getWordPrimarySynonymLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		synLexemes.forEach(lexeme -> populateLexeme(lexeme, wordLang, synMeaningWordLangCodes, userRole));
		lexemeLevelPreseUtil.combineLevels(synLexemes);
		boolean isActiveTagComplete = conversionUtil.isLexemesActiveTagComplete(synLexemes, activeTag);

		List<Relation> relations = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(synCandidateLangCodes)) {
			relations = synSearchDbService.getWordSynRelations(wordId, RAW_RELATION_CODE, datasetCode, synCandidateLangCodes, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
		}
		List<Classifier> allWordRelationTypes = commonDataDbService.getWordRelationTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
		WordRelationDetails wordRelationDetails = conversionUtil.composeWordRelationDetails(relations, null, wordLang, allWordRelationTypes);

		WordDetails wordDetails = new WordDetails();
		word.setNotes(wordNotes);
		wordDetails.setWord(word);
		wordDetails.setLexemes(synLexemes);
		wordDetails.setWordRelationDetails(wordRelationDetails);
		wordDetails.setActiveTagComplete(isActiveTagComplete);

		return wordDetails;
	}

	private void populateLexeme(WordLexeme lexeme, String headwordLanguage, List<String> meaningWordLangs, DatasetPermission userRole) {

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();

		permCalculator.applyCrud(userRole, lexeme);
		List<MeaningWordLangGroup> meaningWordLangGroups = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(meaningWordLangs)) {
			List<LexemeType> lexemeTypes = Arrays.asList(LexemeType.PRIMARY, LexemeType.SECONDARY);
			List<MeaningWord> meaningWords = synSearchDbService.getSynMeaningWords(lexemeId, meaningWordLangs, lexemeTypes);
			meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, headwordLanguage);
		}

		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		permCalculator.filterVisibility(userRole, definitions);

		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
				commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
		permCalculator.filterVisibility(userRole, usages);

		List<String> tags = commonDataDbService.getLexemeTags(lexemeId);

		lexeme.setMeaningWordLangGroups(meaningWordLangGroups);
		lexeme.setUsages(usages);
		lexeme.setTags(tags);
		Meaning meaning = new Meaning();
		meaning.setMeaningId(meaningId);
		meaning.setDefinitions(definitions);
		lexeme.setMeaning(meaning);
	}

	@Transactional
	public void changeRelationStatus(Long relationId, String relationStatus) throws Exception {

		LogData logData;
		if (RelationStatus.DELETED.name().equals(relationStatus)) {
			moveChangedRelationToLast(relationId);
			logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, relationId, relationStatus);
		} else {
			logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, relationId, relationStatus);
		}
		createLifecycleLog(logData);
		Long wordId = activityLogService.getOwnerId(relationId, ActivityEntity.WORD_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("changeRelationStatus", wordId, LifecycleLogOwner.WORD);
		synSearchDbService.changeRelationStatus(relationId, relationStatus);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.WORD_RELATION);
	}

	@Transactional
	public void createSecondarySynLexeme(Long meaningId, Long wordId, String datasetCode, Long existingLexemeId, Long relationId) throws Exception {

		ActivityLogData activityLog;
		List<TypeWordRelParam> typeWordRelParams = synSearchDbService.getWordRelationParams(relationId);
		Float lexemeWeight = getCalculatedLexemeWeight(typeWordRelParams);
		Complexity complexity = lookupDbService.getWordSecondaryLexemesCalculatedComplexity(wordId);

		activityLog = activityLogService.prepareActivityLog("createSecondarySynLexeme", existingLexemeId, LifecycleLogOwner.LEXEME);
		Long lexemeId = synSearchDbService.createLexeme(wordId, meaningId, datasetCode, LexemeType.SECONDARY, lexemeWeight, complexity);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);

		SimpleWord synWord = lookupDbService.getSimpleWord(wordId);
		String synWordValue = synWord.getWordValue();
		LogData matchLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.MEANING_WORD, existingLexemeId, synWordValue);
		createLifecycleLog(matchLogData);

		LogData relationLogData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, relationId, RelationStatus.PROCESSED.name());
		createLifecycleLog(relationLogData);
		Long relationWordId = activityLogService.getOwnerId(relationId, ActivityEntity.WORD_RELATION);
		activityLog = activityLogService.prepareActivityLog("createSecondarySynLexeme", relationWordId, LifecycleLogOwner.WORD);
		synSearchDbService.changeRelationStatus(relationId, RelationStatus.PROCESSED.name());
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.WORD_RELATION);

		Word word = synSearchDbService.getWordDetails(wordId);
		List<MeaningWord> meaningWords = synSearchDbService.getSynMeaningWords(lexemeId, Arrays.asList(word.getLang()), Arrays.asList(LexemeType.PRIMARY));

		for (MeaningWord meaningWord : meaningWords) {
			Long meaningWordId = meaningWord.getWordId();
			Long meaningWordRelationId = synSearchDbService.getRelationId(meaningWordId, wordId, RAW_RELATION_CODE);

			if (meaningWordRelationId != null) {
				LogData oppositeRelationLogData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, meaningWordRelationId,
						RelationStatus.PROCESSED.name());
				createLifecycleLog(oppositeRelationLogData);
				activityLog = activityLogService.prepareActivityLog("createSecondarySynLexeme", meaningWordId, LifecycleLogOwner.WORD);
				synSearchDbService.changeRelationStatus(meaningWordRelationId, RelationStatus.PROCESSED.name());
				activityLogService.createActivityLog(activityLog, meaningWordRelationId, ActivityEntity.WORD_RELATION);
			}
		}
	}

	private void moveChangedRelationToLast(Long relationId) {
		List<Relation> existingRelations = synSearchDbService.getExistingFollowingRelationsForWord(relationId, RAW_RELATION_CODE);

		if (existingRelations.size() > 1) {
			Relation lastRelation = existingRelations.get(existingRelations.size() - 1);
			List<Long> existingOrderByValues = existingRelations.stream().map(Relation::getOrderBy).collect(Collectors.toList());

			cudDbService.updateWordRelationOrderBy(relationId, lastRelation.getOrderBy());
			existingRelations.remove(0);

			existingOrderByValues.remove(existingOrderByValues.size() - 1);

			int relIdx = 0;
			for (Relation relation : existingRelations) {
				cudDbService.updateWordRelationOrderBy(relation.getId(), existingOrderByValues.get(relIdx));
				relIdx++;
			}
		}
	}

	private Float getCalculatedLexemeWeight(List<TypeWordRelParam> typeWordRelParams) {

		if (typeWordRelParams.isEmpty()) {
			return DEFAULT_LEXEME_WEIGHT;
		}

		float dividend = 0;
		float divisor = 0;

		for (TypeWordRelParam typeWordRelParam : typeWordRelParams) {
			String relationParamName = typeWordRelParam.getName();
			Float relationParamValue = typeWordRelParam.getValue();
			Float relationParamWeightMultiplier = relationWeightMultiplierMap.get(relationParamName);

			if (relationParamWeightMultiplier == null) {
				throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Unknown relation weight name: " + relationParamName);
			}

			dividend += (relationParamValue * relationParamWeightMultiplier);
			divisor += relationParamWeightMultiplier;
		}

		return dividend / divisor;
	}
}
