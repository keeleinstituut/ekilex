package eki.ekilex.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.ContentKey;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.ActivityLogOwnerEntityDescr;
import eki.ekilex.data.ListData;
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
		} else if (StringUtils.equals(LEXEME_NOTE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getLexemeNoteSourceLink(sourceLinkId);
		} else if (StringUtils.equals(USAGE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getUsageSourceLink(sourceLinkId);
		} else if (StringUtils.equals(MEANING_IMAGE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getMeaningImageSourceLink(sourceLinkId);
		} else if (StringUtils.equals(MEANING_NOTE_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getMeaningNoteSourceLink(sourceLinkId);
		} else if (StringUtils.equals(FREEFORM_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getFreeformSourceLink(sourceLinkId);
		}
		if (sourceLink != null) {
			sourceLink.setContentKey(sourceLinkContentKey);
		}
		return sourceLink;
	}

	@Transactional(rollbackFor = Exception.class)
	public void createSourceAndSourceLink(
			Source source,
			Long sourceLinkOwnerId,
			String sourceLinkContentKey,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		Long sourceId = createSource(source, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);

		String sourceLinkName = null;

		if (DEFINITION_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createDefinitionSourceLink(sourceLinkOwnerId, sourceId, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (DEFINITION_NOTE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createDefinitionNoteSourceLink(sourceLinkOwnerId, sourceId, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (LEXEME_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createLexemeSourceLink(sourceLinkOwnerId, sourceId, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (LEXEME_NOTE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createLexemeNoteSourceLink(sourceLinkOwnerId, sourceId, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (USAGE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createUsageSourceLink(sourceLinkOwnerId, sourceId, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (MEANING_IMAGE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createMeaningImageSourceLink(sourceLinkOwnerId, sourceId, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (MEANING_NOTE_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createMeaningNoteSourceLink(sourceLinkOwnerId, sourceId, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (FREEFORM_SOURCE_LINK.equals(sourceLinkContentKey)) {
			createFreeformSourceLink(sourceLinkOwnerId, sourceId, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createLexemeSourceLink(
			Long lexemeId, Long sourceId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createLexemeSourceLink(lexemeId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createLexemeSourceLink(
			Long lexemeId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createLexemeSourceLink(lexemeId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		return sourceLinkId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateLexemeSourceLink(Long sourceLinkId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateLexemeSourceLink(sourceLinkId, name);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateLexemeSourceLinkOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long sourceLinkId = item.getId();
			Long lexemeId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeSourceLinkOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceLinkDbService.updateLexemeSourceLinkOrderby(item);
			activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteLexemeSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteLexemeSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createUsageSourceLink(Long usageId, Long sourceId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createUsageSourceLink(usageId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createUsageSourceLink(Long usageId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createUsageSourceLink(usageId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateUsageSourceLink(Long sourceLinkId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateUsageSourceLink(sourceLinkId, name);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateUsageSourceLinkOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long sourceLinkId = item.getId();
			Long lexemeId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageSourceLinkOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceLinkDbService.updateUsageSourceLinkOrderby(item);
			activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteUsageSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteUsageSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createLexemeNoteSourceLink(Long lexemeNoteId, Long sourceId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createLexemeNoteSourceLink(lexemeNoteId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createLexemeNoteSourceLink(Long lexemeNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createLexemeNoteSourceLink(lexemeNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateLexemeNoteSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateLexemeNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateLexemeNoteSourceLinkOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long sourceLinkId = item.getId();
			Long lexemeId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteSourceLinkOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceLinkDbService.updateLexemeNoteSourceLinkOrderby(item);
			activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteLexemeNoteSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteLexemeNoteSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createDefinitionSourceLink(
			Long definitionId, Long sourceId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createDefinitionSourceLink(definitionId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createDefinitionSourceLink(
			Long definitionId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateDefinitionSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateDefinitionSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateDefinitionSourceLinkOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long sourceLinkId = item.getId();
			Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionSourceLinkOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceLinkDbService.updateDefinitionSourceLinkOrderby(item);
			activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteDefinitionSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteDefinitionSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createDefinitionNoteSourceLink(Long definitionNoteId, Long sourceId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createDefinitionNoteSourceLink(definitionNoteId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createDefinitionNoteSourceLink(Long definitionNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createDefinitionNoteSourceLink(definitionNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateDefinitionNoteSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateDefinitionNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateDefinitionNoteSourceLinkOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long sourceLinkId = item.getId();
			Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNoteSourceLinkOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceLinkDbService.updateDefinitionNoteSourceLinkOrderby(item);
			activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteDefinitionNoteSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteDefinitionNoteSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createMeaningImageSourceLink(
			Long meaningImageId, Long sourceId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createMeaningImageSourceLink(meaningImageId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createMeaningImageSourceLink(
			Long meaningImageId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(meaningImageId, ActivityEntity.MEANING_IMAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningImageSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createMeaningImageSourceLink(meaningImageId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateMeaningImageSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningImageSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateMeaningImageSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateMeaningImageSourceLinkOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long sourceLinkId = item.getId();
			Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningImageSourceLinkOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceLinkDbService.updateMeaningImageSourceLinkOrderby(item);
			activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteMeaningImageSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningImageSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteMeaningImageSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createMeaningNoteSourceLink(
			Long meaningNoteId, Long sourceId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createMeaningNoteSourceLink(meaningNoteId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createMeaningNoteSourceLink(
			Long meaningNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createMeaningNoteSourceLink(meaningNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateMeaningNoteSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateMeaningNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_IMAGE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateMeaningNoteSourceLinkOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long sourceLinkId = item.getId();
			Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteSourceLinkOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceLinkDbService.updateMeaningNoteSourceLinkOrderby(item);
			activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteMeaningNoteSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getActivityOwnerId(sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteMeaningNoteSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createFreeformSourceLink(Long freeformId, Long sourceId, String name, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SourceLink sourceLink = new SourceLink();
		sourceLink.setName(name);
		sourceLink.setSourceId(sourceId);

		return createFreeformSourceLink(freeformId, sourceLink, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createFreeformSourceLink(Long freeformId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformOwnerDescr(freeformId);
		Long ownerId = freeformOwnerDescr.getOwnerId();
		ActivityOwner owner = freeformOwnerDescr.getOwnerName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createFreeformSourceLink", ownerId, owner, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);

		return sourceLinkId;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateFreeformSourceLink(Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformSourceLinkOwnerDescr(sourceLinkId);
		Long ownerId = freeformOwnerDescr.getOwnerId();
		ActivityOwner owner = freeformOwnerDescr.getOwnerName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateFreeformSourceLink", ownerId, owner, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateFreeformSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateFreeformSourceLinkOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long sourceLinkId = item.getId();
			ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformSourceLinkOwnerDescr(sourceLinkId);
			Long ownerId = freeformOwnerDescr.getOwnerId();
			ActivityOwner owner = freeformOwnerDescr.getOwnerName();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateFreeformSourceLinkOrdering", ownerId, owner, roleDatasetCode, isManualEventOnUpdateEnabled);
			sourceLinkDbService.updateFreeformSourceLinkOrderby(item);
			activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteFreeformSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformSourceLinkOwnerDescr(sourceLinkId);
		Long ownerId = freeformOwnerDescr.getOwnerId();
		ActivityOwner owner = freeformOwnerDescr.getOwnerName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteFreeformSourceLink", ownerId, owner, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteFreeformSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.FREEFORM_SOURCE_LINK);
	}

}
