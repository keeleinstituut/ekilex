package eki.ekilex.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceOwner;
import eki.common.constant.ReferenceType;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.ActivityLogOwnerEntityDescr;
import eki.ekilex.data.LogData;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.api.FreeformOwner;
import eki.ekilex.service.db.SourceLinkDbService;

@Component
public class SourceLinkService extends AbstractService {

	@Autowired
	private SourceLinkDbService sourceLinkDbService;

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
	public Long createSourceLink(SourceLink sourceLink) throws Exception {

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
				LifecycleEntity lifecycleEntity = freeformOwner.getEntity();
				return createFreeformSourceLink(ownerId, sourceId, refType, value, name, lifecycleEntity);
			}
		} else if (ReferenceOwner.DEFINITION.equals(sourceLinkOwner)) {
			return createDefinitionSourceLink(ownerId, sourceId, refType, value, name);
		} else if (ReferenceOwner.LEXEME.equals(sourceLinkOwner)) {
			return createLexemeSourceLink(ownerId, sourceId, refType, value, name);
		}
		return null;
	}

	private boolean isSupportedSourceLink(FreeformOwner freeformOwner) {
		if (LifecycleEntity.LEXEME.equals(freeformOwner.getEntity())
				&& FreeformType.NOTE.equals(freeformOwner.getType())) {
			return true;
		} else if (LifecycleEntity.LEXEME.equals(freeformOwner.getEntity())
				&& FreeformType.USAGE.equals(freeformOwner.getType())) {
			return true;
		} else if (LifecycleEntity.MEANING.equals(freeformOwner.getEntity())
				&& FreeformType.NOTE.equals(freeformOwner.getType())) {
			return true;
		} else if (LifecycleEntity.MEANING.equals(freeformOwner.getEntity())
				&& FreeformType.IMAGE_FILE.equals(freeformOwner.getType())) {
			return true;
		} else if (LifecycleEntity.DEFINITION.equals(freeformOwner.getEntity())
				&& FreeformType.NOTE.equals(freeformOwner.getType())) {
			return true;
		}
		return false;
	}

	@Transactional
	public void deleteSourceLink(ReferenceOwner sourceLinkOwner, Long sourceLinkId) throws Exception {

		if (ReferenceOwner.FREEFORM.equals(sourceLinkOwner)) {
			SourceLink sourceLink = sourceLinkDbService.getFreeformSourceLink(sourceLinkId);
			Long ownerId = sourceLink.getOwnerId();
			FreeformOwner freeformOwner = sourceLinkDbService.getFreeformOwner(ownerId);
			LifecycleEntity lifecycleEntity = freeformOwner.getEntity();
			deleteFreeformSourceLink(sourceLinkId, lifecycleEntity);
		} else if (ReferenceOwner.DEFINITION.equals(sourceLinkOwner)) {
			deleteDefinitionSourceLink(sourceLinkId);
		} else if (ReferenceOwner.LEXEME.equals(sourceLinkOwner)) {
			deleteLexemeSourceLink(sourceLinkId);
		}
	}

	@Transactional
	public Long createLexemeSourceLink(Long lexemeId, Long sourceId, ReferenceType refType, String sourceLinkValue, String sourceLinkName) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeSourceLink", lexemeId, LifecycleLogOwner.LEXEME);
		Long sourceLinkId = sourceLinkDbService.createLexemeSourceLink(lexemeId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.SOURCE_LINK, sourceLinkId, sourceLinkValue);
		createLifecycleLog(logData);
		return sourceLinkId;
	}

	@Transactional
	public void deleteLexemeSourceLink(Long sourceLinkId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.SOURCE_LINK, sourceLinkId, null);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeSourceLink", lexemeId, LifecycleLogOwner.LEXEME);
		sourceLinkDbService.deleteLexemeSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	@Transactional
	public Long createFreeformSourceLink(
			Long freeformId, Long sourceId, ReferenceType refType, String sourceLinkValue, String sourceLinkName, LifecycleEntity lifecycleEntity) throws Exception {
		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformSourceLinkOwnerDescrByFreeform(freeformId);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createFreeformSourceLink", freeformOwnerDescr.getOwnerId(), freeformOwnerDescr.getOwnerName());
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, freeformOwnerDescr.getEntityName());
		LogData logData = new LogData(LifecycleEventType.CREATE, lifecycleEntity, LifecycleProperty.FREEFORM_SOURCE_LINK, sourceLinkId, sourceLinkValue);
		createLifecycleLog(logData);
		return sourceLinkId;
	}

	//TODO not sure about this
	@Transactional
	public Long createFreeformSourceLink(
			Long freeformId, Long sourceId, ReferenceType refType, String sourceLinkValue, String sourceLinkName,
			LifecycleEntity lifecycleEntity, LifecycleProperty lifecycleProperty) throws Exception {
		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformSourceLinkOwnerDescrByFreeform(freeformId);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createFreeformSourceLink", freeformOwnerDescr.getOwnerId(), freeformOwnerDescr.getOwnerName());
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, freeformOwnerDescr.getEntityName());
		LogData logData = new LogData(LifecycleEventType.CREATE, lifecycleEntity, lifecycleProperty, sourceLinkId, sourceLinkValue);
		createLifecycleLog(logData);
		return sourceLinkId;
	}

	@Transactional
	public void deleteFreeformSourceLink(Long sourceLinkId, LifecycleEntity lifecycleEntity) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, lifecycleEntity, LifecycleProperty.FREEFORM_SOURCE_LINK, sourceLinkId, null);
		createLifecycleLog(logData);
		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformSourceLinkOwnerDescrBySourceLink(sourceLinkId);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteFreeformSourceLink", freeformOwnerDescr.getOwnerId(), freeformOwnerDescr.getOwnerName());
		sourceLinkDbService.deleteFreeformSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, freeformOwnerDescr.getEntityName());
	}

	@Transactional
	public Long createDefinitionSourceLink(Long definitionId, Long sourceId, ReferenceType refType, String sourceLinkValue, String sourceLinkName) throws Exception {
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionSourceLink", meaningId, LifecycleLogOwner.MEANING);
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.SOURCE_LINK, sourceLinkId, sourceLinkValue);
		createLifecycleLog(logData);
		return sourceLinkId;
	}

	@Transactional
	public void deleteDefinitionSourceLink(Long sourceLinkId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION, LifecycleProperty.SOURCE_LINK, sourceLinkId, null);
		createLifecycleLog(logData);
		Long meaningId = activityLogService.getOwnerId(sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionSourceLink", meaningId, LifecycleLogOwner.MEANING);
		sourceLinkDbService.deleteDefinitionSourceLink(sourceLinkId);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}
}
