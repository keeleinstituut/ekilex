package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

import eki.common.constant.ActivityEntity;
import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleLogOwner;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ActivityLog;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.DefSourceAndNoteSourceTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.Image;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.TypeActivityLogDiff;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordGroup;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIds;
import eki.ekilex.data.WordNote;
import eki.ekilex.data.WordRelationDetails;
import eki.ekilex.service.db.ActivityLogDbService;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.util.ConversionUtil;

@Component
public class ActivityLogService implements SystemConstant {

	private static final String ACTIVITY_LOG_DIFF_FIELD_NAME = "diff";

	@Autowired
	protected UserContext userContext;

	@Autowired
	private ActivityLogDbService activityLogDbService;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	public ActivityLogData prepareActivityLog(String functName, Long ownerId, LifecycleLogOwner ownerName) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLogData = new ActivityLogData();
		activityLogData.setEventBy(userName);
		activityLogData.setFunctName(functName);
		activityLogData.setOwnerId(ownerId);
		activityLogData.setOwnerName(ownerName);

		WordLexemeMeaningIds prevWlmIds;

		if (LifecycleLogOwner.LEXEME.equals(ownerName)) {
			Long lexemeId = new Long(ownerId);
			String prevData = getLexemeDetailsJson(lexemeId);
			prevWlmIds = activityLogDbService.getWordMeaningIds(lexemeId);
			activityLogData.setPrevData(prevData);
			activityLogData.setPrevWlmIds(prevWlmIds);
		} else if (LifecycleLogOwner.WORD.equals(ownerName)) {
			Long wordId = new Long(ownerId);
			String prevData = getWordDetailsJson(wordId);
			prevWlmIds = activityLogDbService.getLexemeMeaningIds(wordId);
			activityLogData.setPrevData(prevData);
			activityLogData.setPrevWlmIds(prevWlmIds);
		} else if (LifecycleLogOwner.MEANING.equals(ownerName)) {
			Long meaningId = new Long(ownerId);
			String prevData = getMeaningDetailsJson(meaningId);
			prevWlmIds = activityLogDbService.getLexemeWordIds(meaningId);
			activityLogData.setPrevData(prevData);
			activityLogData.setPrevWlmIds(prevWlmIds);
		} else if (LifecycleLogOwner.SOURCE.equals(ownerName)) {
			// TODO implement
		}
		return activityLogData;
	}

	public ActivityLog createActivityLog(ActivityLogData activityLogData, Long entityId, ActivityEntity entityName) throws Exception {

		activityLogData.setEntityId(entityId);
		activityLogData.setEntityName(entityName);
		LifecycleLogOwner ownerName = activityLogData.getOwnerName();
		Long ownerId = activityLogData.getOwnerId();
		WordLexemeMeaningIds currWlmIds = null;
		String currData = null;

		if (LifecycleLogOwner.LEXEME.equals(ownerName)) {
			Long lexemeId = new Long(ownerId);
			currWlmIds = activityLogDbService.getWordMeaningIds(lexemeId);
			currData = getLexemeDetailsJson(lexemeId);
			activityLogData.setCurrWlmIds(currWlmIds);
			activityLogData.setCurrData(currData);
			handleWlmActivityLog(activityLogData);
		} else if (LifecycleLogOwner.WORD.equals(ownerName)) {
			Long wordId = new Long(ownerId);
			currWlmIds = activityLogDbService.getLexemeMeaningIds(wordId);
			currData = getWordDetailsJson(wordId);
			activityLogData.setCurrWlmIds(currWlmIds);
			activityLogData.setCurrData(currData);
			handleWlmActivityLog(activityLogData);
		} else if (LifecycleLogOwner.MEANING.equals(ownerName)) {
			Long meaningId = new Long(ownerId);
			currWlmIds = activityLogDbService.getLexemeWordIds(meaningId);
			currData = getMeaningDetailsJson(meaningId);
			activityLogData.setCurrWlmIds(currWlmIds);
			activityLogData.setCurrData(currData);
			handleWlmActivityLog(activityLogData);
		} else if (LifecycleLogOwner.SOURCE.equals(ownerName)) {
			// TODO implement
		}

		return activityLogData;
	}

	private void handleWlmActivityLog(ActivityLogData activityLogData) throws Exception {

		calcDiffs(activityLogData);

		WordLexemeMeaningIds prevWlmIds = activityLogData.getPrevWlmIds();
		WordLexemeMeaningIds currWlmIds = activityLogData.getCurrWlmIds();

		List<Long> lexemeIds = collectIds(prevWlmIds.getLexemeIds(), currWlmIds.getLexemeIds());
		List<Long> wordIds = collectIds(prevWlmIds.getWordIds(), currWlmIds.getWordIds());
		List<Long> meaningIds = collectIds(prevWlmIds.getMeaningIds(), currWlmIds.getMeaningIds());

		Long activityLogId = activityLogDbService.create(activityLogData);
		activityLogDbService.createLexemesLog(activityLogId, lexemeIds);
		activityLogDbService.createWordsLog(activityLogId, wordIds);
		activityLogDbService.createMeaningsLog(activityLogId, meaningIds);

		activityLogData.setId(activityLogId);
	}

	private List<Long> collectIds(List<Long> ids1, List<Long> ids2) {
		List<Long> ids = new ArrayList<>();
		ids.addAll(ids1);
		ids.addAll(ids2);
		ids = ids.stream().distinct().collect(Collectors.toList());
		return ids;
	}

	private void calcDiffs(ActivityLog activityLog) throws Exception {

		String prevData = activityLog.getPrevData();
		String currData = activityLog.getCurrData();

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode prevDataNode = objectMapper.readTree(prevData);
		JsonNode currDataNode = objectMapper.readTree(currData);

		JsonNode prevDataDiffNode = JsonDiff.asJson(currDataNode, prevDataNode);
		JsonNode currDataDiffNode = JsonDiff.asJson(prevDataNode, currDataNode);

		String prevDataDiffJson = "{\"" + ACTIVITY_LOG_DIFF_FIELD_NAME + "\": " + prevDataDiffNode.toString() + "}";
		String currDataDiffJson = "{\"" + ACTIVITY_LOG_DIFF_FIELD_NAME + "\": " + currDataDiffNode.toString() + "}";

		List<TypeActivityLogDiff> prevDiffs = composeActivityLogDiffs(objectMapper, prevDataDiffJson);
		List<TypeActivityLogDiff> currDiffs = composeActivityLogDiffs(objectMapper, currDataDiffJson);

		activityLog.setPrevDiffs(prevDiffs);
		activityLog.setCurrDiffs(currDiffs);
	}

	private List<TypeActivityLogDiff> composeActivityLogDiffs(ObjectMapper objectMapper, String diffJson) throws Exception {

		Map<String, Object> diffMap = objectMapper.readValue(diffJson, new TypeReference<Map<String, Object>>() {
		});
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> diffList = (List<Map<String, Object>>) diffMap.get(ACTIVITY_LOG_DIFF_FIELD_NAME);
		List<TypeActivityLogDiff> activityLogDiffs = new ArrayList<>();

		for (Map<String, Object> diffRow : diffList) {
			String diffOp = diffRow.get("op").toString();
			String diffPath = diffRow.get("path").toString();
			Object diffValueObj = diffRow.get("value");
			String diffValue;
			if (diffValueObj == null) {
				diffValue = "-";
			} else if (diffValueObj instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> diffValueMap = (Map<String, Object>) diffValueObj;
				diffValueMap.values().removeIf(Objects::isNull);
				diffValue = diffValueObj.toString();
			} else {
				diffValue = diffValueObj.toString();
			}
			TypeActivityLogDiff activityLogDiff = new TypeActivityLogDiff();
			activityLogDiff.setOp(diffOp);
			activityLogDiff.setPath(diffPath);
			activityLogDiff.setValue(diffValue);
			activityLogDiffs.add(activityLogDiff);
		}
		return activityLogDiffs;
	}

	private String getLexemeDetailsJson(Long lexemeId) throws Exception {

		WordLexeme lexeme = lexSearchDbService.getLexeme(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);

		final String[] excludeLexemeAttributeTypes = new String[] {
				FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(),
				FreeformType.NOTE.name(), FreeformType.OD_LEXEME_RECOMMENDATION.name()};

		List<String> tags = commonDataDbService.getLexemeTags(lexemeId);
		List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
		List<FreeForm> grammars = commonDataDbService.getLexemeGrammars(lexemeId);
		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples = commonDataDbService
				.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
		List<FreeForm> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
		List<NoteSourceTuple> lexemeNoteSourceTuples = commonDataDbService.getLexemeNoteSourceTuples(lexemeId);
		List<LexemeNote> lexemeNotes = conversionUtil.composeNotes(LexemeNote.class, lexemeId, lexemeNoteSourceTuples);
		List<NoteLangGroup> lexemeNoteLangGroups = conversionUtil.composeNoteLangGroups(lexemeNotes, null);
		List<FreeForm> odLexemeRecommendations = commonDataDbService.getOdLexemeRecommendations(lexemeId);
		List<Relation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
		List<SourceLink> lexemeSourceLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);
		List<CollocationTuple> primaryCollocTuples = lexSearchDbService.getPrimaryCollocationTuples(lexemeId);
		List<CollocationPosGroup> collocationPosGroups = conversionUtil.composeCollocPosGroups(primaryCollocTuples);
		List<CollocationTuple> secondaryCollocTuples = lexSearchDbService.getSecondaryCollocationTuples(lexemeId);
		List<Collocation> secondaryCollocations = conversionUtil.composeCollocations(secondaryCollocTuples);

		lexeme.setTags(tags);
		lexeme.setGovernments(governments);
		lexeme.setGrammars(grammars);
		lexeme.setUsages(usages);
		lexeme.setLexemeFreeforms(lexemeFreeforms);
		lexeme.setLexemeNoteLangGroups(lexemeNoteLangGroups);
		lexeme.setOdLexemeRecommendations(odLexemeRecommendations);
		lexeme.setLexemeRelations(lexemeRelations);
		lexeme.setSourceLinks(lexemeSourceLinks);
		lexeme.setCollocationPosGroups(collocationPosGroups);
		lexeme.setSecondaryCollocations(secondaryCollocations);

		ObjectMapper objectMapper = new ObjectMapper();
		String lexemeJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(lexeme);

		return lexemeJson;
	}

	private String getWordDetailsJson(Long wordId) throws Exception {

		Word word = lexSearchDbService.getWord(wordId);
		String wordLang = word.getLang();
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Relation> wordRelations = lexSearchDbService.getWordRelations(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
		List<Classifier> allWordRelationTypes = commonDataDbService.getWordRelationTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
		WordRelationDetails wordRelationDetails = conversionUtil.composeWordRelationDetails(wordRelations, wordLang, allWordRelationTypes);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);
		List<Relation> wordGroupMembers = lexSearchDbService.getWordGroupMembers(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
		List<WordGroup> wordGroups = conversionUtil.composeWordGroups(wordGroupMembers);
		List<FreeForm> odWordRecommendations = lexSearchDbService.getOdWordRecommendations(wordId);
		List<NoteSourceTuple> wordNoteSourceTuples = commonDataDbService.getWordNoteSourceTuples(wordId);
		List<WordNote> wordNotes = conversionUtil.composeNotes(WordNote.class, wordId, wordNoteSourceTuples);

		WordDetails wordDetails = new WordDetails();
		word.setNotes(wordNotes);
		wordDetails.setWord(word);
		wordDetails.setWordTypes(wordTypes);
		wordDetails.setWordEtymology(wordEtymology);
		wordDetails.setOdWordRecommendations(odWordRecommendations);

		wordRelationDetails.setWordGroups(wordGroups);
		wordDetails.setWordRelationDetails(wordRelationDetails);

		ObjectMapper objectMapper = new ObjectMapper();
		String wordDetailsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(wordDetails);

		return wordDetailsJson;
	}

	private String getMeaningDetailsJson(Long meaningId) throws Exception {

		final String[] excludeMeaningAttributeTypes = new String[] {
				FreeformType.LEARNER_COMMENT.name(), FreeformType.SEMANTIC_TYPE.name(), FreeformType.NOTE.name()};

		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId);
		meaningDomains = conversionUtil.removeOrderedClassifierDuplicates(meaningDomains);
		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, null, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<DefSourceAndNoteSourceTuple> definitionsDataTuples = commonDataDbService.getMeaningDefSourceAndNoteSourceTuples(meaningId);
		conversionUtil.composeMeaningDefinitions(definitions, definitionsDataTuples);
		List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);
		List<FreeForm> meaningLearnerComments = commonDataDbService.getMeaningLearnerComments(meaningId);
		List<ImageSourceTuple> meaningImageSourceTuples = commonDataDbService.getMeaningImageSourceTuples(meaningId);
		List<Image> meaningImages = conversionUtil.composeMeaningImages(meaningImageSourceTuples);
		List<NoteSourceTuple> meaningNoteSourceTuples = commonDataDbService.getMeaningNoteSourceTuples(meaningId);
		List<MeaningNote> meaningNotes = conversionUtil.composeNotes(MeaningNote.class, meaningId, meaningNoteSourceTuples);
		List<NoteLangGroup> meaningNoteLangGroups = conversionUtil.composeNoteLangGroups(meaningNotes, null);
		List<Classifier> meaningSemanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Relation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, null, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, null);

		Meaning meaning = new Meaning();
		meaning.setMeaningId(meaningId);
		meaning.setDomains(meaningDomains);
		meaning.setDefinitions(definitions);
		meaning.setFreeforms(meaningFreeforms);
		meaning.setLearnerComments(meaningLearnerComments);
		meaning.setImages(meaningImages);
		meaning.setNoteLangGroups(meaningNoteLangGroups);
		meaning.setSemanticTypes(meaningSemanticTypes);
		meaning.setRelations(meaningRelations);
		meaning.setDefinitionLangGroups(definitionLangGroups);

		ObjectMapper objectMapper = new ObjectMapper();
		String meaningJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(meaning);

		return meaningJson;
	}
}
