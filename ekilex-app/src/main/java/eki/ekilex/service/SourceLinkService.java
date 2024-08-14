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
public class SourceLinkService extends AbstractSourceService {

	@Transactional
	public SourceLink getSourceLink(String sourceLinkContentKey, Long sourceLinkId) {

		SourceLink sourceLink = null;

		if (StringUtils.equals(ContentKey.DEFINITION_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getDefinitionSourceLink(sourceLinkId);
		} else if (StringUtils.equals(ContentKey.DEFINITION_NOTE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getDefinitionNoteSourceLink(sourceLinkId);
		} else if (StringUtils.equals(ContentKey.LEXEME_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getLexemeSourceLink(sourceLinkId);
		} else if (StringUtils.equals(ContentKey.LEXEME_NOTE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getLexemeNoteSourceLink(sourceLinkId);
		} else if (StringUtils.equals(ContentKey.USAGE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getUsageSourceLink(sourceLinkId);
		} else if (StringUtils.equals(ContentKey.MEANING_IMAGE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getMeaningImageSourceLink(sourceLinkId);
		} else if (StringUtils.equals(ContentKey.MEANING_NOTE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getMeaningNoteSourceLink(sourceLinkId);
		}
		if (sourceLink != null) {
			sourceLink.setContentKey(sourceLinkContentKey);
		}
		return sourceLink;
	}

	@Transactional
	public void createSourceAndSourceLink(
			Source source,
			Long sourceLinkOwnerId,
			String sourceLinkContentKey,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		Long sourceId = createSource(source, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);

		ReferenceType refType = ReferenceType.ANY;
		String sourceLinkName = null;

		if (ContentKey.DEFINITION_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createDefinitionSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ContentKey.DEFINITION_NOTE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createDefinitionNoteSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ContentKey.LEXEME_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createLexemeSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ContentKey.LEXEME_NOTE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createLexemeNoteSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ContentKey.USAGE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createUsageSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ContentKey.MEANING_IMAGE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createMeaningImageSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ContentKey.MEANING_NOTE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createMeaningNoteSourceLink(sourceLinkOwnerId, sourceId, refType, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public Long createLexemeSourceLink(
			Long lexemeId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createLexemeSourceLink(lexemeId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public Long createLexemeSourceLink(
			Long lexemeId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createLexemeSourceLink(lexemeId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		return sourceLinkId;
	}

	@Transactional
	public void updateLexemeSourceLink(Long sourceLinkId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateLexemeSourceLink(sourceLinkId, name);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	@Transactional
	public void deleteLexemeSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteLexemeSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	@Transactional
	public Long createUsageSourceLink(Long usageId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createUsageSourceLink(usageId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public Long createUsageSourceLink(Long usageId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createUsageSourceLink(usageId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional
	public void updateUsageSourceLink(Long sourceLinkId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateUsageSourceLink(sourceLinkId, name);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
	}

	@Transactional
	public void deleteUsageSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteUsageSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
	}

	@Transactional
	public Long createLexemeNoteSourceLink(Long lexemeNoteId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createLexemeNoteSourceLink(lexemeNoteId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public Long createLexemeNoteSourceLink(Long lexemeNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createLexemeNoteSourceLink(lexemeNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional
	public void updateLexemeNoteSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateLexemeNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
	}

	@Transactional
	public void deleteLexemeNoteSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteLexemeNoteSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
	}

	@Transactional
	public Long createDefinitionSourceLink(
			Long definitionId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createDefinitionSourceLink(definitionId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public Long createDefinitionSourceLink(
			Long definitionId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional
	public void updateDefinitionSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateDefinitionSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	@Transactional
	public void deleteDefinitionSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteDefinitionSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	@Transactional
	public Long createDefinitionNoteSourceLink(Long definitionNoteId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createDefinitionNoteSourceLink(definitionNoteId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public Long createDefinitionNoteSourceLink(Long definitionNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createDefinitionNoteSourceLink(definitionNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional
	public void updateDefinitionNoteSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateDefinitionNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
	}

	@Transactional
	public void deleteDefinitionNoteSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteDefinitionSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
	}

	@Transactional
	public Long createMeaningImageSourceLink(
			Long meaningImageId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createMeaningImageSourceLink(meaningImageId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public Long createMeaningImageSourceLink(
			Long meaningImageId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningImageId, ActivityEntity.MEANING_IMAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningImageSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createMeaningImageSourceLink(meaningImageId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional
	public void updateMeaningImageSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningImageSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateMeaningImageSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
	}

	@Transactional
	public void deleteMeaningImageSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningImageSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteMeaningImageSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
	}

	@Transactional
	public Long createMeaningNoteSourceLink(
			Long meaningNoteId, Long sourceId, ReferenceType type, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createMeaningNoteSourceLink(meaningNoteId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public Long createMeaningNoteSourceLink(
			Long meaningNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createMeaningNoteSourceLink(meaningNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional
	public void updateMeaningNoteSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateMeaningNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
	}

	@Transactional
	public void deleteMeaningNoteSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteMeaningNoteSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
	}
}
