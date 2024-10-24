package eki.ekilex.service;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.ContentKey;
import eki.common.constant.ReferenceType;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceLink;

@Component
public class SourceLinkService extends AbstractSourceService implements ContentKey {

	@Transactional
	public SourceLink getSourceLink(String sourceLinkContentKey, Long sourceLinkId) {

		SourceLink sourceLink = null;

		if (StringUtils.equals(DEFINITION_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getDefinitionSourceLink(sourceLinkId);
		} else if (StringUtils.equals(DEFINITION_NOTE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getDefinitionNoteSourceLink(sourceLinkId);
		} else if (StringUtils.equals(LEXEME_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getLexemeSourceLink(sourceLinkId);
		} else if (StringUtils.equals(LEXEME_FREEFORM_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getFreeformSourceLink(sourceLinkId);
		} else if (StringUtils.equals(LEXEME_NOTE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getLexemeNoteSourceLink(sourceLinkId);
		} else if (StringUtils.equals(USAGE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getUsageSourceLink(sourceLinkId);
		} else if (StringUtils.equals(WORD_FREEFORM_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getFreeformSourceLink(sourceLinkId);
		} else if (StringUtils.equals(MEANING_IMAGE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getMeaningImageSourceLink(sourceLinkId);
		} else if (StringUtils.equals(MEANING_NOTE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getMeaningNoteSourceLink(sourceLinkId);
		} else if (StringUtils.equals(MEANING_FREEFORM_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getFreeformSourceLink(sourceLinkId);
		}
		if (sourceLink != null) {
			sourceLink.setContentKey(sourceLinkContentKey);
		}
		return sourceLink;
	}

	@Transactional(rollbackOn = Exception.class)
	public void createSourceAndSourceLink(
			Source source,
			Long sourceLinkOwnerId,
			String sourceLinkContentKey,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		Long sourceId = createSource(source, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);

		ReferenceType refType = ReferenceType.ANY;
		String sourceLinkName = null;

		if (DEFINITION_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createDefinitionSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (DEFINITION_NOTE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createDefinitionNoteSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (LEXEME_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createLexemeSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (LEXEME_FREEFORM_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createLexemeFreeformSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (LEXEME_NOTE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createLexemeNoteSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (USAGE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createUsageSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (WORD_FREEFORM_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createWordFreeformSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (MEANING_IMAGE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createMeaningImageSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (MEANING_NOTE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createMeaningNoteSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (MEANING_FREEFORM_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createMeaningFreeformSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createLexemeSourceLink(
			Long lexemeId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createLexemeSourceLink(lexemeId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createLexemeSourceLink(
			Long lexemeId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createLexemeSourceLink(lexemeId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeSourceLink(Long sourceLinkId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateLexemeSourceLink(sourceLinkId, name);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteLexemeSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createUsageSourceLink(Long usageId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createUsageSourceLink(usageId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createUsageSourceLink(Long usageId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createUsageSourceLink(usageId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateUsageSourceLink(Long sourceLinkId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateUsageSourceLink(sourceLinkId, name);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteUsageSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteUsageSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createLexemeFreeformSourceLink(Long freeformId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		Long lexemeId = activityLogService.getOwnerId(freeformId, ActivityEntity.FREEFORM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeFreeformSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeFreeformSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeFreeformSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateFreeformSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeFreeformSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeFreeformSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteFreeformSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createLexemeNoteSourceLink(Long lexemeNoteId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createLexemeNoteSourceLink(lexemeNoteId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createLexemeNoteSourceLink(Long lexemeNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createLexemeNoteSourceLink(lexemeNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeNoteSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateLexemeNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeNoteSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteLexemeNoteSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createDefinitionSourceLink(
			Long definitionId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createDefinitionSourceLink(definitionId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createDefinitionSourceLink(
			Long definitionId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateDefinitionSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateDefinitionSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteDefinitionSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteDefinitionSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createDefinitionNoteSourceLink(Long definitionNoteId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createDefinitionNoteSourceLink(definitionNoteId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createDefinitionNoteSourceLink(Long definitionNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createDefinitionNoteSourceLink(definitionNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateDefinitionNoteSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateDefinitionNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteDefinitionNoteSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteDefinitionSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createMeaningImageSourceLink(
			Long meaningImageId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createMeaningImageSourceLink(meaningImageId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createMeaningImageSourceLink(
			Long meaningImageId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningImageId, ActivityEntity.MEANING_IMAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningImageSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createMeaningImageSourceLink(meaningImageId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningImageSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningImageSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateMeaningImageSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningImageSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningImageSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteMeaningImageSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createMeaningFreeformSourceLink(Long freeformId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		Long meaningId = activityLogService.getOwnerId(freeformId, ActivityEntity.FREEFORM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningFreeformSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningFreeformSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningFreeformSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateFreeformSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningFreeformSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeFreeformSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteFreeformSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createMeaningNoteSourceLink(
			Long meaningNoteId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createMeaningNoteSourceLink(meaningNoteId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createMeaningNoteSourceLink(
			Long meaningNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createMeaningNoteSourceLink(meaningNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningNoteSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateMeaningNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningNoteSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteMeaningNoteSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createWordFreeformSourceLink(Long freeformId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		Long wordId = activityLogService.getOwnerId(freeformId, ActivityEntity.FREEFORM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordFreeformSourceLink", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordFreeformSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordFreeformSourceLink", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateFreeformSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteWordFreeformSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordFreeformSourceLink", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteFreeformSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
	}
}
