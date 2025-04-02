package eki.ekilex.service.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.FreeformConstant;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LastActivityType;
import eki.common.exception.IllegalParamException;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ActivityLog;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.ActivityLogOwnerEntityDescr;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Colloc;
import eki.ekilex.data.CollocPosGroup;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Freeform;
import eki.ekilex.data.Government;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.LexemeRelation;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.Media;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourcePropertyTuple;
import eki.ekilex.data.TypeActivityLogDiff;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordGroup;
import eki.ekilex.data.WordLexemeMeaningIds;
import eki.ekilex.data.WordOdRecommendation;
import eki.ekilex.data.WordRelation;
import eki.ekilex.service.db.ActivityLogDbService;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.LexDataDbService;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.SourceDbService;
import eki.ekilex.service.util.ConversionUtil;

@Component
public class ActivityLogService implements SystemConstant, GlobalConstant, FreeformConstant {

	private static final String ACTIVITY_LOG_DIFF_FIELD_NAME = "diff";

	private static final String EMPTY_CONTENT_JSON = "{}";

	private static final List<ActivityEntity> FIRST_DEPTH_FREEFORM_ENTITIES = Arrays.asList(
			ActivityEntity.FREEFORM,
			ActivityEntity.GOVERNMENT,
			ActivityEntity.GOVERNMENT_TYPE,
			ActivityEntity.GRAMMAR,
			ActivityEntity.LTB_ID,
			ActivityEntity.LTB_SOURCE,
			ActivityEntity.ADVICE_NOTE,
			ActivityEntity.UNCLASSIFIED,
			ActivityEntity.SOURCE_NAME,
			ActivityEntity.SOURCE_RT,
			ActivityEntity.SOURCE_CELEX,
			ActivityEntity.SOURCE_WWW,
			ActivityEntity.SOURCE_AUTHOR,
			ActivityEntity.SOURCE_ISBN,
			ActivityEntity.SOURCE_ISSN,
			ActivityEntity.SOURCE_PUBLISHER,
			ActivityEntity.SOURCE_PUBLICATION_YEAR,
			ActivityEntity.SOURCE_PUBLICATION_PLACE,
			ActivityEntity.SOURCE_PUBLICATION_NAME,
			ActivityEntity.SOURCE_FILE,
			ActivityEntity.SOURCE_EXPLANATION,
			ActivityEntity.SOURCE_ARTICLE_TITLE,
			ActivityEntity.SOURCE_ARTICLE_AUTHOR,
			ActivityEntity.EXTERNAL_SOURCE_ID,
			ActivityEntity.LEARNER_COMMENT,
			ActivityEntity.MEDIA_FILE,
			ActivityEntity.SEMANTIC_TYPE,
			ActivityEntity.SYSTEMATIC_POLYSEMY_PATTERN,
			ActivityEntity.ADVICE_NOTE);

	private static final List<ActivityEntity> SECOND_DEPTH_FREEFORM_ENTITIES = Arrays.asList(
			ActivityEntity.GOVERNMENT_PLACEMENT,
			ActivityEntity.GOVERNMENT_VARIANT,
			ActivityEntity.GOVERNMENT_OPTIONAL,
			ActivityEntity.SEMANTIC_TYPE_GROUP);

	private static final String FUNCT_NAME_DELETE_PARADIGM = "deleteParadigm";

	@Autowired
	protected UserContext userContext;

	@Autowired
	private ActivityLogDbService activityLogDbService;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private LexDataDbService lexDataDbService;

	@Autowired
	private SourceDbService sourceDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Transactional
	public List<ActivityLog> getWordActivityLog(Long wordId) {
		return activityLogDbService.getWordActivityLog(wordId);
	}

	@Transactional
	public List<ActivityLog> getMeaningActivityLog(Long meaningId) {
		return activityLogDbService.getMeaningActivityLog(meaningId);
	}

	@Transactional
	public List<ActivityLog> getSourceActivityLog(Long sourceId) {
		return activityLogDbService.getSourceActivityLog(sourceId);
	}

	public ActivityLogOwnerEntityDescr getFreeformSourceLinkOwnerDescr(Long sourceLinkId) throws Exception {
		Long freeformId = activityLogDbService.getFreeformSourceLinkOwnerId(sourceLinkId);
		return getFreeformOwnerDescr(freeformId);
	}

	public Long getOwnerId(Long entityId, ActivityEntity entity) throws Exception {

		if (FIRST_DEPTH_FREEFORM_ENTITIES.contains(entity)) {
			ActivityLogOwnerEntityDescr freeformOwnerDescr = getFreeformOwnerDescr(entityId);
			return freeformOwnerDescr.getOwnerId();
		} else if (SECOND_DEPTH_FREEFORM_ENTITIES.contains(entity)) {
			Map<String, Object> freeformOwnerDataMap = activityLogDbService.getSecondDepthFreeformOwnerDataMap(entityId);
			ActivityLogOwnerEntityDescr freeformOwnerDescr = resolveOwnerDescr(freeformOwnerDataMap);
			return freeformOwnerDescr.getOwnerId();
		} else if (ActivityEntity.FREEFORM_SOURCE_LINK.equals(entity)) {
			ActivityLogOwnerEntityDescr freeformOwnerDescr = getFreeformSourceLinkOwnerDescr(entityId);
			return freeformOwnerDescr.getOwnerId();
		} else if (ActivityEntity.LEXEME_SOURCE_LINK.equals(entity)) {
			return activityLogDbService.getLexemeSourceLinkOwnerId(entityId);
		} else if (ActivityEntity.LEXEME_NOTE.equals(entity)) {
			return activityLogDbService.getLexemeNoteOwnerId(entityId);
		} else if (ActivityEntity.LEXEME_NOTE_SOURCE_LINK.equals(entity)) {
			return activityLogDbService.getLexemeNoteSourceLinkOwnerId(entityId);
		} else if (ActivityEntity.USAGE.equals(entity)) {
			return activityLogDbService.getUsageOwnerId(entityId);
		} else if (ActivityEntity.USAGE_SOURCE_LINK.equals(entity)) {
			return activityLogDbService.getUsageSourceLinkOwnerId(entityId);
		} else if (ActivityEntity.USAGE_TRANSLATION.equals(entity)) {
			return activityLogDbService.getUsageTranslationOwnerId(entityId);
		} else if (ActivityEntity.USAGE_DEFINITION.equals(entity)) {
			return activityLogDbService.getUsageDefinitionOwnerId(entityId);
		} else if (ActivityEntity.MEANING_IMAGE.equals(entity)) {
			return activityLogDbService.getMeaningImageOwnerId(entityId);
		} else if (ActivityEntity.MEANING_IMAGE_SOURCE_LINK.equals(entity)) {
			return activityLogDbService.getMeaningImageSourceLinkOwnerId(entityId);
		} else if (ActivityEntity.MEANING_NOTE.equals(entity)) {
			return activityLogDbService.getMeaningNoteOwnerId(entityId);
		} else if (ActivityEntity.MEANING_NOTE_SOURCE_LINK.equals(entity)) {
			return activityLogDbService.getMeaningNoteSourceLinkOwnerId(entityId);
		} else if (ActivityEntity.DEFINITION.equals(entity)) {
			return activityLogDbService.getDefinitionOwnerId(entityId);
		} else if (ActivityEntity.DEFINITION_SOURCE_LINK.equals(entity)) {
			return activityLogDbService.getDefinitionSourceLinkOwnerId(entityId);
		} else if (ActivityEntity.DEFINITION_NOTE.equals(entity)) {
			return activityLogDbService.getDefinitionNoteOwnerId(entityId);
		} else if (ActivityEntity.DEFINITION_NOTE_SOURCE_LINK.equals(entity)) {
			return activityLogDbService.getDefinitionNoteSourceLinkOwnerId(entityId);
		} else if (ActivityEntity.WORD_TYPE.equals(entity)) {
			return activityLogDbService.getWordTypeOwnerId(entityId);
		} else if (ActivityEntity.WORD_ETYMOLOGY.equals(entity)) {
			return activityLogDbService.getWordEtymologyOwnerId(entityId);
		} else if (ActivityEntity.WORD_OD_RECOMMENDATION.equals(entity)) {
			return activityLogDbService.getWordOdRecommendationOwnerId(entityId);
		} else if (ActivityEntity.WORD_RELATION.equals(entity)) {
			return activityLogDbService.getWordRelationOwnerId(entityId);
		} else if (ActivityEntity.LEXEME_RELATION.equals(entity)) {
			return activityLogDbService.getLexemeRelationOwnerId(entityId);
		} else if (ActivityEntity.MEANING_RELATION.equals(entity)) {
			return activityLogDbService.getMeaningRelationOwnerId(entityId);
		} else if (ActivityEntity.DOMAIN.equals(entity)) {
			return activityLogDbService.getMeaningDomainOwnerId(entityId);
		} else if (ActivityEntity.PARADIGM.equals(entity)) {
			return activityLogDbService.getParadigmOwnerId(entityId);
		} else {
			throw new IllegalParamException("Missing activity entity owner mapping for " + entity);
		}
	}

	public ActivityLogOwnerEntityDescr getFreeformOwnerDescr(Long freeformId) throws Exception {
		Map<String, Object> freeformOwnerDataMap = activityLogDbService.getFirstDepthFreeformOwnerDataMap(freeformId);
		return resolveOwnerDescr(freeformOwnerDataMap);
	}

	private ActivityLogOwnerEntityDescr resolveOwnerDescr(Map<String, Object> freeformOwnerDataMap) throws Exception {
		if (MapUtils.isEmpty(freeformOwnerDataMap)) {
			throw new IllegalParamException("Unable to locate freeform");
		}
		String ffTypeCode = (String) freeformOwnerDataMap.get("freeform_type_code");
		ActivityEntity activityEntity;
		Long id;
		id = (Long) freeformOwnerDataMap.get("lexeme_id");
		if (id != null) {
			return new ActivityLogOwnerEntityDescr(ActivityOwner.LEXEME, id, ActivityEntity.FREEFORM);
		}
		id = (Long) freeformOwnerDataMap.get("word_id");
		if (id != null) {
			return new ActivityLogOwnerEntityDescr(ActivityOwner.WORD, id, ActivityEntity.FREEFORM);
		}
		id = (Long) freeformOwnerDataMap.get("meaning_id");
		if (id != null) {
			return new ActivityLogOwnerEntityDescr(ActivityOwner.MEANING, id, ActivityEntity.FREEFORM);
		}
		id = (Long) freeformOwnerDataMap.get("d_meaning_id");
		if (id != null) {
			return new ActivityLogOwnerEntityDescr(ActivityOwner.MEANING, id, ActivityEntity.FREEFORM);
		}
		id = (Long) freeformOwnerDataMap.get("source_id");
		if (id != null) {
			if (StringUtils.equals(NOTE_CODE, ffTypeCode)) {
				activityEntity = ActivityEntity.SOURCE_NOTE;
			} else {
				try {
					activityEntity = ActivityEntity.valueOf(ffTypeCode);
				} catch (Exception e) {
					throw new IllegalParamException("Missing activity entity owner mapping for source freeform " + ffTypeCode);
				}
			}
			return new ActivityLogOwnerEntityDescr(ActivityOwner.SOURCE, id, activityEntity);
		}
		throw new IllegalParamException("Unable to locate owner of the freeform");
	}

	public ActivityLogData prepareActivityLog(String functName, Long ownerId, ActivityOwner ownerName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLogData = initCore(functName, ownerId, ownerName, roleDatasetCode, isManualEventOnUpdateEnabled);

		WordLexemeMeaningIds prevWlmIds;
		String prevData;

		if (ActivityOwner.LEXEME.equals(ownerName)) {
			Long lexemeId = Long.valueOf(ownerId);
			prevData = getLexemeDetailsJson(lexemeId);
			prevWlmIds = activityLogDbService.getWordMeaningIds(lexemeId);
			activityLogData.setPrevData(prevData);
			activityLogData.setPrevWlmIds(prevWlmIds);
		} else if (ActivityOwner.WORD.equals(ownerName)) {
			Long wordId = Long.valueOf(ownerId);
			prevData = getWordDetailsJson(wordId, functName);
			prevWlmIds = activityLogDbService.getLexemeMeaningIds(wordId);
			activityLogData.setPrevData(prevData);
			activityLogData.setPrevWlmIds(prevWlmIds);
		} else if (ActivityOwner.MEANING.equals(ownerName)) {
			Long meaningId = Long.valueOf(ownerId);
			prevData = getMeaningDetailsJson(meaningId);
			prevWlmIds = activityLogDbService.getLexemeWordIds(meaningId);
			activityLogData.setPrevData(prevData);
			activityLogData.setPrevWlmIds(prevWlmIds);
		} else if (ActivityOwner.SOURCE.equals(ownerName)) {
			Long sourceId = Long.valueOf(ownerId);
			prevData = getSourceJson(sourceId);
			activityLogData.setPrevData(prevData);
		}
		return activityLogData;
	}

	public void createActivityLog(String functName, Long ownerId, ActivityOwner ownerName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long entityId = Long.valueOf(ownerId);
		ActivityEntity entityName = ActivityEntity.valueOf(ownerName.name());
		ActivityLogData activityLogData;
		if (StringUtils.startsWith(functName, "delete")) {
			activityLogData = prepareActivityLog(functName, ownerId, ownerName, roleDatasetCode, isManualEventOnUpdateEnabled);
			activityLogData.setEntityId(entityId);
			activityLogData.setEntityName(entityName);
			activityLogData.setCurrData(EMPTY_CONTENT_JSON);
			activityLogData.setCurrWlmIds(new WordLexemeMeaningIds());
			if (ActivityOwner.LEXEME.equals(ownerName)) {
				handleWlmActivityLog(activityLogData);
			} else if (ActivityOwner.WORD.equals(ownerName)) {
				handleWlmActivityLog(activityLogData);
			} else if (ActivityOwner.MEANING.equals(ownerName)) {
				handleWlmActivityLog(activityLogData);
			} else if (ActivityOwner.SOURCE.equals(ownerName)) {
				handleSourceActivityLog(activityLogData);
			}
		} else {
			activityLogData = initCore(functName, ownerId, ownerName, roleDatasetCode, isManualEventOnUpdateEnabled);
			activityLogData.setPrevData(EMPTY_CONTENT_JSON);
			activityLogData.setPrevWlmIds(new WordLexemeMeaningIds());
			createActivityLog(activityLogData, entityId, entityName);
		}
	}

	public void createActivityLogUnknownEntity(ActivityLogData activityLogData, ActivityEntity entityName) throws Exception {
		createActivityLog(activityLogData, -1L, entityName);
	}

	public void createActivityLog(ActivityLogData activityLogData, Long entityId, ActivityEntity entityName) throws Exception {

		activityLogData.setEntityId(entityId);
		activityLogData.setEntityName(entityName);
		String functName = activityLogData.getFunctName();
		ActivityOwner ownerName = activityLogData.getOwnerName();
		Long ownerId = activityLogData.getOwnerId();
		WordLexemeMeaningIds currWlmIds = null;
		String currData = null;

		if (ActivityOwner.LEXEME.equals(ownerName)) {
			Long lexemeId = Long.valueOf(ownerId);
			currData = getLexemeDetailsJson(lexemeId);
			currWlmIds = activityLogDbService.getWordMeaningIds(lexemeId);
			activityLogData.setCurrData(currData);
			activityLogData.setCurrWlmIds(currWlmIds);
			handleWlmActivityLog(activityLogData);
		} else if (ActivityOwner.WORD.equals(ownerName)) {
			Long wordId = Long.valueOf(ownerId);
			currData = getWordDetailsJson(wordId, functName);
			currWlmIds = activityLogDbService.getLexemeMeaningIds(wordId);
			activityLogData.setCurrData(currData);
			activityLogData.setCurrWlmIds(currWlmIds);
			handleWlmActivityLog(activityLogData);
		} else if (ActivityOwner.MEANING.equals(ownerName)) {
			Long meaningId = Long.valueOf(ownerId);
			currData = getMeaningDetailsJson(meaningId);
			currWlmIds = activityLogDbService.getLexemeWordIds(meaningId);
			activityLogData.setCurrData(currData);
			activityLogData.setCurrWlmIds(currWlmIds);
			handleWlmActivityLog(activityLogData);
		} else if (ActivityOwner.SOURCE.equals(ownerName)) {
			Long sourceId = Long.valueOf(ownerId);
			currData = getSourceJson(sourceId);
			activityLogData.setCurrData(currData);
			handleSourceActivityLog(activityLogData);
		}
	}

	public void joinApproveMeaning(Long targetMeaningId, Long sourceMeaningId) {

		final Timestamp targetMeaningLastActivityEventOn = activityLogDbService.getMeaningLastActivityLog(targetMeaningId, LastActivityType.APPROVE);
		final Timestamp sourceMeaningLastActivityEventOn = activityLogDbService.getMeaningLastActivityLog(sourceMeaningId, LastActivityType.APPROVE);
		if (targetMeaningLastActivityEventOn == null) {
			return;
		}
		if (sourceMeaningLastActivityEventOn == null) {
			activityLogDbService.deleteMeaningLastActivityLog(targetMeaningId, LastActivityType.APPROVE);
			return;
		}
		if (targetMeaningLastActivityEventOn.before(sourceMeaningLastActivityEventOn)) {
			return;
		}
		if (targetMeaningLastActivityEventOn.after(sourceMeaningLastActivityEventOn)) {
			activityLogDbService.deleteMeaningLastActivityLog(targetMeaningId, LastActivityType.APPROVE);
			activityLogDbService.moveMeaningLastActivityLog(targetMeaningId, sourceMeaningId, LastActivityType.APPROVE);
		}
	}

	private ActivityLogData initCore(String functName, Long ownerId, ActivityOwner ownerName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) {

		String userName = userContext.getUserName();
		ActivityLogData activityLogData = new ActivityLogData();
		activityLogData.setEventBy(userName);
		activityLogData.setDatasetCode(roleDatasetCode);
		activityLogData.setFunctName(functName);
		activityLogData.setOwnerId(ownerId);
		activityLogData.setOwnerName(ownerName);
		activityLogData.setManualEventOnUpdateEnabled(isManualEventOnUpdateEnabled);

		return activityLogData;
	}

	private void handleWlmActivityLog(ActivityLogData activityLogData) throws Exception {

		boolean isManualEventOnUpdateEnabled = activityLogData.isManualEventOnUpdateEnabled();
		calcDiffs(activityLogData);

		WordLexemeMeaningIds prevWlmIds = activityLogData.getPrevWlmIds();
		WordLexemeMeaningIds currWlmIds = activityLogData.getCurrWlmIds();

		Long[] lexemeIds = collect(prevWlmIds.getLexemeIds(), currWlmIds.getLexemeIds());
		Long[] wordIds = collect(prevWlmIds.getWordIds(), currWlmIds.getWordIds());
		Long[] meaningIds = collect(prevWlmIds.getMeaningIds(), currWlmIds.getMeaningIds());

		Long activityLogId = activityLogDbService.create(activityLogData);
		Timestamp eventOn = activityLogDbService.getActivityLogEventOn(activityLogId);
		activityLogDbService.createLexemesActivityLogs(activityLogId, lexemeIds);
		activityLogDbService.createWordsActivityLogs(activityLogId, wordIds);
		activityLogDbService.createMeaningsActivityLogs(activityLogId, meaningIds);

		for (Long wordId : wordIds) {
			activityLogDbService.createOrUpdateWordLastActivityLog(wordId);
			if (isManualEventOnUpdateEnabled) {
				activityLogDbService.updateWordManualEventOn(wordId, eventOn);
			}
		}
		// FIXME temp solution for better syn view ordering performance
		if (activityLogData.getEntityName().equals(ActivityEntity.WORD_RELATION)) {
			return;
		}
		for (Long meaningId : meaningIds) {
			activityLogDbService.createOrUpdateMeaningLastActivityLog(meaningId, LastActivityType.EDIT);
		}

		if (isManualEventOnUpdateEnabled) {
			List<Long> roleDatasetMeaningIds = filterRoleDatasetMeaningIds(meaningIds);
			for (Long roleDatasetMeaningId : roleDatasetMeaningIds) {
				activityLogDbService.updateMeaningManualEventOn(roleDatasetMeaningId, eventOn);
			}
		}

		handleApproveMeaningEvent(activityLogData);
	}

	private Long[] collect(Long[] ids1, Long[] ids2) {
		if ((ids1 == null) && (ids2 == null)) {
			return ArrayUtils.EMPTY_LONG_OBJECT_ARRAY;
		}
		if (ids1 == null) {
			return ids2;
		}
		if (ids2 == null) {
			return ids1;
		}
		Long[] ids = ArrayUtils.addAll(ids1, ids2);
		ids = Arrays.stream(ids).distinct().toArray(Long[]::new);
		return ids;
	}

	private void handleApproveMeaningEvent(ActivityLogData activityLogData) {
		final ActivityOwner ownerName = activityLogData.getOwnerName();
		final Long ownerId = activityLogData.getOwnerId();
		final String functName = activityLogData.getFunctName();
		if (ActivityOwner.MEANING.equals(ownerName)) {
			Long meaningId = Long.valueOf(ownerId);
			if (StringUtils.equals(functName, FUNCT_NAME_APPROVE_MEANING)) {
				activityLogDbService.createOrUpdateMeaningLastActivityLog(meaningId, LastActivityType.APPROVE);
			}
		}
	}

	private void handleSourceActivityLog(ActivityLogData activityLogData) throws Exception {

		calcDiffs(activityLogData);

		Long activityLogId = activityLogDbService.create(activityLogData);
		activityLogDbService.createSourceActivityLog(activityLogId, activityLogData.getOwnerId());

		activityLogData.setId(activityLogId);
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

	private List<Long> filterRoleDatasetMeaningIds(Long[] meaningIds) {

		List<Long> roleDatasetMeaningIds = new ArrayList<>();
		EkiUser user = userContext.getUser();

		DatasetPermission userRole = user.getRecentRole();
		String roleDatasetCode = userRole.getDatasetCode();
		for (Long meaningId : meaningIds) {
			String meaningDatasetCode = lookupDbService.getMeaningFirstDatasetCode(meaningId);
			if (StringUtils.equals(meaningDatasetCode, roleDatasetCode)) {
				roleDatasetMeaningIds.add(meaningId);
			}
		}

		return roleDatasetMeaningIds;
	}

	private String getLexemeDetailsJson(Long lexemeId) throws Exception {

		Lexeme lexeme = lexSearchDbService.getLexeme(lexemeId, CLASSIF_LABEL_LANG_EST);
		if (lexeme == null) {
			return EMPTY_CONTENT_JSON;
		}
		Long wordId = lexeme.getWordId();
		Word word = lexSearchDbService.getWord(wordId);
		List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId);
		List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
		List<Freeform> grammars = commonDataDbService.getLexemeGrammars(lexemeId);
		List<Freeform> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, EXCLUDED_LEXEME_ATTRIBUTE_FF_TYPE_CODES, CLASSIF_LABEL_LANG_EST);
		List<LexemeNote> lexemeNotes = lexeme.getNotes();
		List<NoteLangGroup> lexemeNoteLangGroups = conversionUtil.composeNoteLangGroups(lexemeNotes, null);
		List<LexemeRelation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, CLASSIF_LABEL_LANG_EST);
		List<CollocPosGroup> primaryCollocations = lexDataDbService.getPrimaryCollocations(lexemeId, CLASSIF_LABEL_LANG_EST);
		List<Colloc> secondaryCollocations = lexDataDbService.getSecondaryCollocations(lexemeId);
		boolean isCollocationsExist = lexDataDbService.isCollocationsExist(lexemeId);

		lexeme.setLexemeWord(word);
		lexeme.setMeaningWords(meaningWords);
		lexeme.setGovernments(governments);
		lexeme.setGrammars(grammars);
		lexeme.setFreeforms(lexemeFreeforms);
		lexeme.setNoteLangGroups(lexemeNoteLangGroups);
		lexeme.setLexemeRelations(lexemeRelations);
		lexeme.setPrimaryCollocations(primaryCollocations);
		lexeme.setSecondaryCollocations(secondaryCollocations);
		lexeme.setCollocationsExist(isCollocationsExist);

		ObjectMapper objectMapper = new ObjectMapper();
		String lexemeJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(lexeme);

		return lexemeJson;
	}

	private String getWordDetailsJson(Long wordId, String functName) throws Exception {

		Word word = lexSearchDbService.getWord(wordId);
		if (word == null) {
			return EMPTY_CONTENT_JSON;
		}
		List<Freeform> wordFreeforms = commonDataDbService.getWordFreeforms(wordId, EXCLUDED_WORD_ATTRIBUTE_FF_TYPE_CODES, CLASSIF_LABEL_LANG_EST);
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, CLASSIF_LABEL_LANG_EST);
		List<WordRelation> wordRelations = lexDataDbService.getWordRelations(wordId, CLASSIF_LABEL_LANG_EST);
		List<WordRelation> wordGroupMembers = lexDataDbService.getWordGroupMembers(wordId, CLASSIF_LABEL_LANG_EST);
		List<WordGroup> wordGroups = conversionUtil.composeWordGroups(wordGroupMembers, null);
		List<WordEtymTuple> wordEtymTuples = lexDataDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);
		WordOdRecommendation wordOdRecommendation = commonDataDbService.getWordOdRecommendation(wordId);
		List<Paradigm> paradigms = null;
		if (StringUtils.equals(FUNCT_NAME_DELETE_PARADIGM, functName)) {
			List<ParadigmFormTuple> paradigmFormTuples = lexSearchDbService.getParadigmFormTuples(wordId, CLASSIF_LABEL_LANG_EST);
			paradigms = conversionUtil.composeParadigms(paradigmFormTuples);
		}

		word.setFreeforms(wordFreeforms);
		word.setWordTypes(wordTypes);
		word.setRelations(wordRelations);
		word.setGroups(wordGroups);
		word.setEtymology(wordEtymology);
		word.setWordOdRecommendation(wordOdRecommendation);
		word.setParadigms(paradigms);

		ObjectMapper objectMapper = new ObjectMapper();
		String wordDetailsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(word);

		return wordDetailsJson;
	}

	private String getMeaningDetailsJson(Long meaningId) throws Exception {

		Meaning meaning = commonDataDbService.getMeaning(meaningId);
		if (meaning == null) {
			return EMPTY_CONTENT_JSON;
		}

		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST);
		List<String> meaningTags = commonDataDbService.getMeaningTags(meaningId);
		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, CLASSIF_LABEL_LANG_EST);
		List<Freeform> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, EXCLUDED_MEANING_ATTRIBUTE_FF_TYPE_CODES, CLASSIF_LABEL_LANG_EST);
		List<Freeform> meaningLearnerComments = commonDataDbService.getMeaningLearnerComments(meaningId);
		List<Media> meaningImages = commonDataDbService.getMeaningImagesAsMedia(meaningId);
		List<Media> meaningMedias = commonDataDbService.getMeaningMediaFiles(meaningId);
		List<MeaningNote> meaningNotes = commonDataDbService.getMeaningNotes(meaningId);
		List<NoteLangGroup> meaningNoteLangGroups = conversionUtil.composeNoteLangGroups(meaningNotes, null);
		List<Classifier> meaningSemanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, CLASSIF_LABEL_LANG_EST);
		List<MeaningRelation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, null, CLASSIF_LABEL_LANG_EST);
		List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, null);

		meaning.setMeaningId(meaningId);
		meaning.setDomains(meaningDomains);
		meaning.setTags(meaningTags);
		meaning.setDefinitions(definitions);
		meaning.setFreeforms(meaningFreeforms);
		meaning.setLearnerComments(meaningLearnerComments);
		meaning.setImages(meaningImages);
		meaning.setMedias(meaningMedias);
		meaning.setNoteLangGroups(meaningNoteLangGroups);
		meaning.setSemanticTypes(meaningSemanticTypes);
		meaning.setRelations(meaningRelations);
		meaning.setDefinitionLangGroups(definitionLangGroups);

		ObjectMapper objectMapper = new ObjectMapper();
		String meaningJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(meaning);

		return meaningJson;
	}

	private String getSourceJson(Long sourceId) throws Exception {

		Source source = sourceDbService.getSource(sourceId);
		if (source == null) {
			return EMPTY_CONTENT_JSON;
		}

		List<SourcePropertyTuple> sourcePropertyTuples = sourceDbService.getSourcePropertyTuples(sourceId);
		if (CollectionUtils.isNotEmpty(sourcePropertyTuples)) {
			conversionUtil.composeSource(source, sourcePropertyTuples);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		String sourceJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(source);

		return sourceJson;
	}

}
