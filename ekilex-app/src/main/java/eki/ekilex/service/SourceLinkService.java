package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.ContentKey;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.ReferenceOwner;
import eki.common.constant.ReferenceType;
import eki.common.constant.SourceType;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.ActivityLogOwnerEntityDescr;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.api.FreeformOwner;

@Component
public class SourceLinkService extends AbstractSourceService implements GlobalConstant {

	@Transactional
	public SourceLink getSourceLink(Long sourceLinkId, ReferenceOwner referenceOwner) {

		SourceLink sourceLink = null;

		if (ReferenceOwner.FREEFORM.equals(referenceOwner)) {
			sourceLink = sourceLinkDbService.getFreeformSourceLink(sourceLinkId);
		} else if (ReferenceOwner.DEFINITION.equals(referenceOwner)) {
			sourceLink = sourceLinkDbService.getDefinitionSourceLink(sourceLinkId);
		} else if (ReferenceOwner.LEXEME.equals(referenceOwner)) {
			sourceLink = sourceLinkDbService.getLexemeSourceLink(sourceLinkId);
		}
		return sourceLink;
	}

	@Transactional
	public SourceLink getSourceLink(Long sourceLinkId, String sourceLinkContentKey) {

		SourceLink sourceLink = null;

		if (StringUtils.equals(ContentKey.FREEFORM_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getFreeformSourceLink(sourceLinkId);
		} else if (StringUtils.equals(ContentKey.DEFINITION_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getDefinitionSourceLink(sourceLinkId);
		} else if (StringUtils.equals(ContentKey.LEXEME_SOURCE_LINK, sourceLinkContentKey)) {
			sourceLink = sourceLinkDbService.getLexemeSourceLink(sourceLinkId);
		}
		return sourceLink;
	}

	@Transactional
	public Long createSourceLink(SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ReferenceOwner sourceLinkOwner = sourceLink.getOwner();
		Long ownerId = sourceLink.getOwnerId();
		Long sourceId = sourceLink.getSourceId();
		ReferenceType refType = sourceLink.getType();
		String value = sourceLink.getValue();
		String name = sourceLink.getName();
		if (ReferenceOwner.FREEFORM.equals(sourceLinkOwner)) {
			FreeformOwner freeformOwner = sourceLinkDbService.getFreeformOwner(ownerId);
			boolean isSupportedOwner = isSupportedSourceLink(freeformOwner);
			if (isSupportedOwner) {
				return createFreeformSourceLink(ownerId, sourceId, refType, value, name, roleDatasetCode, isManualEventOnUpdateEnabled);
			}
		} else if (ReferenceOwner.DEFINITION.equals(sourceLinkOwner)) {
			return createDefinitionSourceLink(ownerId, sourceId, refType, value, name, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ReferenceOwner.LEXEME.equals(sourceLinkOwner)) {
			return createLexemeSourceLink(ownerId, sourceId, refType, value, name, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
		return null;
	}

    @Transactional
    public void createSourceAndSourceLink(
			SourceType sourceType, List<SourceProperty> sourceProperties, Long sourceLinkOwnerId, String sourceLinkOwnerCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

        Long sourceId = createSource(sourceType, sourceProperties, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);

		String sourceLinkValue = sourceProperties.get(0).getValueText();
		ReferenceType sourceLinkRefType = ReferenceType.ANY;
        String sourceLinkName = null;

		if (ContentKey.DEFINITION_SOURCE_LINK.equals(sourceLinkOwnerCode)) {
            createDefinitionSourceLink(sourceLinkOwnerId, sourceId, sourceLinkRefType, sourceLinkValue, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
        } else if (ContentKey.LEXEME_SOURCE_LINK.equals(sourceLinkOwnerCode)) {
        	createLexemeSourceLink(sourceLinkOwnerId, sourceId, sourceLinkRefType, sourceLinkValue, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ContentKey.FREEFORM_SOURCE_LINK.equals(sourceLinkOwnerCode)) {
			createFreeformSourceLink(sourceLinkOwnerId, sourceId, sourceLinkRefType, sourceLinkValue, sourceLinkName, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
    }

	private boolean isSupportedSourceLink(FreeformOwner freeformOwner) {
		if (ActivityEntity.LEXEME.equals(freeformOwner.getEntity())
				&& FreeformType.NOTE.equals(freeformOwner.getType())) {
			return true;
		} else if (ActivityEntity.LEXEME.equals(freeformOwner.getEntity())
				&& FreeformType.USAGE.equals(freeformOwner.getType())) {
			return true;
		} else if (ActivityEntity.MEANING.equals(freeformOwner.getEntity())
				&& FreeformType.NOTE.equals(freeformOwner.getType())) {
			return true;
		} else if (ActivityEntity.MEANING.equals(freeformOwner.getEntity())
				&& FreeformType.IMAGE_FILE.equals(freeformOwner.getType())) {
			return true;
		} else if (ActivityEntity.DEFINITION.equals(freeformOwner.getEntity())
				&& FreeformType.NOTE.equals(freeformOwner.getType())) {
			return true;
		}
		return false;
	}

	@Transactional
	public void deleteSourceLink(ReferenceOwner sourceLinkOwner, Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (ReferenceOwner.FREEFORM.equals(sourceLinkOwner)) {
			deleteFreeformSourceLink(sourceLinkId, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ReferenceOwner.DEFINITION.equals(sourceLinkOwner)) {
			deleteDefinitionSourceLink(sourceLinkId, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else if (ReferenceOwner.LEXEME.equals(sourceLinkOwner)) {
			deleteLexemeSourceLink(sourceLinkId, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public Long createLexemeSourceLink(
			Long lexemeId, Long sourceId, ReferenceType refType, String sourceLinkValue, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createLexemeSourceLink(lexemeId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		return sourceLinkId;
	}

	@Transactional
	public void updateLexemeSourceLink(Long sourceLinkId, String sourceLinkValue, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateLexemeSourceLink(sourceLinkId, sourceLinkValue, sourceLinkName);
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
	public Long createFreeformSourceLink(
			Long freeformId, Long sourceId, ReferenceType refType, String sourceLinkValue, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformSourceLinkOwnerDescrByFreeform(freeformId);
		ActivityLogData activityLog = activityLogService
				.prepareActivityLog("createFreeformSourceLink", freeformOwnerDescr.getOwnerId(), freeformOwnerDescr.getOwnerName(), roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, freeformOwnerDescr.getEntityName());
		return sourceLinkId;
	}

	@Transactional
	public void updateFreeformSourceLink(Long sourceLinkId, String sourceLinkValue, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {
		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformSourceLinkOwnerDescrBySourceLink(sourceLinkId);
		ActivityLogData activityLog = activityLogService
				.prepareActivityLog("updateFreeformSourceLink", freeformOwnerDescr.getOwnerId(), freeformOwnerDescr.getOwnerName(), roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateFreeformSourceLink(sourceLinkId, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, freeformOwnerDescr.getEntityName());
	}

	@Transactional
	public void deleteFreeformSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {
		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformSourceLinkOwnerDescrBySourceLink(sourceLinkId);
		ActivityLogData activityLog = activityLogService
				.prepareActivityLog("deleteFreeformSourceLink", freeformOwnerDescr.getOwnerId(), freeformOwnerDescr.getOwnerName(), roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteFreeformSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, freeformOwnerDescr.getEntityName());
	}

	@Transactional
	public Long createDefinitionSourceLink(
			Long definitionId, Long sourceId, ReferenceType refType, String sourceLinkValue, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		return sourceLinkId;
	}

	@Transactional
	public void updateDefinitionSourceLink(Long sourceLinkId, String sourceLinkValue, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {
		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateDefinitionSourceLink(sourceLinkId, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	@Transactional
	public void deleteDefinitionSourceLink(Long sourceLinkId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {
		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.deleteDefinitionSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}
}
