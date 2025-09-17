package eki.ekilex.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.PublishingConstant;
import eki.common.constant.WordRelationGroupType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Note;
import eki.ekilex.data.Publishing;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.PublishingDbService;
import eki.ekilex.service.db.SourceLinkDbService;
import eki.ekilex.service.db.TagDbService;
import eki.ekilex.service.util.ValueUtil;

public abstract class AbstractCudService extends AbstractService implements PublishingConstant {

	@Autowired
	protected ValueUtil valueUtil;

	@Autowired
	protected TextDecorationService textDecorationService;

	@Autowired
	protected CudDbService cudDbService;

	@Autowired
	protected SourceLinkDbService sourceLinkDbService;

	@Autowired
	protected LookupDbService lookupDbService;

	@Autowired
	protected TagDbService tagDbService;

	@Autowired
	private PublishingDbService publishingDbService;

	@Transactional(rollbackFor = Exception.class)
	public Long createDefinition(
			Long meaningId, String valuePrese, String languageCode, String datasetCode, String typeCode, boolean isPublic, EkiUser user,
			String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinition", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long definitionId = cudDbService.createDefinition(meaningId, value, valuePrese, languageCode, typeCode, isPublic);
		cudDbService.createDefinitionDataset(definitionId, datasetCode);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_DEFINITION, definitionId);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
		return definitionId;
	}

	@Transactional(rollbackFor = Exception.class)
	public WordLexemeMeaningIdTuple createLexeme(Long wordId, String datasetCode, Long meaningId, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		int currentLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(wordId, datasetCode);
		int lexemeLevel1 = currentLexemesMaxLevel1 + 1;
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexeme", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createLexemeWithCreateOrSelectMeaning(wordId, datasetCode, meaningId, lexemeLevel1, null, PUBLICITY_PUBLIC);
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		if (lexemeId == null) {
			return wordLexemeMeaningId;
		}
		tagDbService.createLexemeAutomaticTags(lexemeId);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_LEXEME, lexemeId);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);

		return wordLexemeMeaningId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void createLexemePos(Long lexemeId, String posCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemePos", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemePosId = cudDbService.createLexemePos(lexemeId, posCode);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createUsage(Long lexemeId, String valuePrese, String lang, boolean isPublic, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Usage usage = new Usage();
		usage.setValuePrese(valuePrese);
		usage.setLang(lang);
		usage.setPublic(isPublic);

		return createUsage(lexemeId, usage, user, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createUsage(Long lexemeId, Usage usage, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		setValueAndPrese(usage);
		applyCreateUpdate(usage);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsage", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long usageId = cudDbService.createUsage(lexemeId, usage);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_USAGE, usageId);
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
		return usageId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void createWordRelation(Long wordId, Long targetWordId, String relationTypeCode, String oppositeRelationTypeCode, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		WordRelationGroupType wordRelationGroupType = WordRelationGroupType.toRelationGroupType(relationTypeCode);
		if (wordRelationGroupType == null) {
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			Long relationId = cudDbService.createWordRelation(wordId, targetWordId, relationTypeCode, null);
			createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_WORD_RELATION, relationId);
			activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.WORD_RELATION);
			if (StringUtils.isNotEmpty(oppositeRelationTypeCode)) {
				boolean oppositeRelationExists = lookupDbService.wordRelationExists(targetWordId, wordId, oppositeRelationTypeCode);
				if (oppositeRelationExists) {
					return;
				}
				activityLog = activityLogService.prepareActivityLog("createWordRelation", targetWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
				Long oppositeRelationId = cudDbService.createWordRelation(targetWordId, wordId, oppositeRelationTypeCode, null);
				createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_WORD_RELATION, oppositeRelationId);
				activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.WORD_RELATION);
			}
		} else {
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			Long groupId = lookupDbService.getWordRelationGroupId(relationTypeCode, wordId);
			if (groupId == null) {
				groupId = cudDbService.createWordRelationGroup(relationTypeCode);
				cudDbService.createWordRelationGroupMember(groupId, wordId);
				cudDbService.createWordRelationGroupMember(groupId, targetWordId);
			} else if (!lookupDbService.isMemberOfWordRelationGroup(groupId, targetWordId)) {
				cudDbService.createWordRelationGroupMember(groupId, targetWordId);
			}
			activityLogService.createActivityLog(activityLog, targetWordId, ActivityEntity.WORD_RELATION_GROUP_MEMBER);
		}
	}

	protected Long createUsageSourceLink(Long lexemeId, Long usageId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createUsageSourceLink(usageId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
		return sourceLinkId;
	}

	protected void updateUsageSourceLink(Long lexemeId, Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateUsageSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
	}

	protected Long createLexemeNoteSourceLink(Long lexemeId, Long lexemeNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createLexemeNoteSourceLink(lexemeNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
		return sourceLinkId;
	}

	protected void updateLexemeNoteSourceLink(Long lexemeId, Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateLexemeNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
	}

	protected Long createMeaningNoteSourceLink(Long meaningId, Long meaningNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createMeaningNoteSourceLink(meaningNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
		return sourceLinkId;
	}

	protected void updateMeaningNoteSourceLink(Long meaningId, Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateMeaningNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
	}

	protected void createDefinitionSourceLink(Long meaningId, Long definitionId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	protected void updateDefinitionSourceLink(Long meaningId, Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateDefinitionSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	protected Note initNote(String valuePrese, String lang, boolean isPublic) {

		Note note = new Note();
		note.setValuePrese(valuePrese);
		note.setLang(lang);
		note.setPublic(isPublic);
		setValueAndPrese(note);

		return note;
	}

	protected void createPublishing(EkiUser user, String roleDatasetCode, String targetName, String entityName, Long entityId) {

		if (!StringUtils.equals(DATASET_EKI, roleDatasetCode)) {
			return;
		}

		String userName = user.getName();
		LocalDateTime now = LocalDateTime.now();

		Publishing publishing = new Publishing();
		publishing.setEventBy(userName);
		publishing.setEventOn(now);
		publishing.setTargetName(targetName);
		publishing.setEntityName(entityName);
		publishing.setEntityId(entityId);

		publishingDbService.createPublishing(publishing);
	}
}
