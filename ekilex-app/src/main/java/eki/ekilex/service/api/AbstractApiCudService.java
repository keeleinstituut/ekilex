package eki.ekilex.service.api;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.ReferenceType;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.api.Definition;
import eki.ekilex.data.api.Freeform;
import eki.ekilex.data.api.SourceLink;
import eki.ekilex.service.AbstractCudService;
import eki.ekilex.service.db.SourceLinkDbService;

public abstract class AbstractApiCudService extends AbstractCudService implements GlobalConstant {

	protected static final ReferenceType DEFAULT_SOURCE_LINK_REF_TYPE = ReferenceType.ANY;

	@Autowired
	protected SourceLinkDbService sourceLinkDbService;

	protected void createOrUpdateDefinition(Definition definition, Long meaningId, String datasetCode, Complexity complexity, String roleDatasetCode) throws Exception {

		Long definitionId = definition.getDefinitionId();
		String definitionValue = StringUtils.trim(definition.getValue());
		String definitionLang = definition.getLang();
		String definitionTypeCode = definition.getDefinitionTypeCode();
		List<SourceLink> definitionSourceLinks = definition.getSourceLinks();

		if (StringUtils.isAnyBlank(definitionValue, definitionLang)) {
			return;
		}

		ActivityLogData activityLog;
		if (definitionId == null) {
			activityLog = activityLogService.prepareActivityLog("createDefinition", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			definitionId = cudDbService.createDefinition(meaningId, definitionValue, definitionValue, definitionLang, definitionTypeCode, complexity, PUBLICITY_PUBLIC);
			cudDbService.createDefinitionDataset(definitionId, datasetCode);
		} else {
			activityLog = activityLogService.prepareActivityLog("updateDefinition", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			cudDbService.updateDefinition(definitionId, definitionValue, definitionValue, definitionLang, definitionTypeCode, complexity, PUBLICITY_PUBLIC);
		}
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);

		if (CollectionUtils.isNotEmpty(definitionSourceLinks)) {

			for (SourceLink definitionSourceLink : definitionSourceLinks) {
				Long sourceLinkId = definitionSourceLink.getSourceLinkId();
				Long sourceId = definitionSourceLink.getSourceId();
				String sourceLinkName = definitionSourceLink.getSourceLinkName();
				if (sourceLinkId == null) {
					createDefinitionSourceLink(definitionId, sourceId, meaningId, sourceLinkName, roleDatasetCode);
				} else {
					updateDefinitionSourceLink(sourceLinkId, meaningId, sourceLinkName, roleDatasetCode);
				}
			}
		}
	}

	protected void createOrUpdateUsage(Freeform usage, Long lexemeId, boolean defaultPublicity, String roleDatasetCode) throws Exception {

		String userName = userContext.getUserName();
		Long usageId = usage.getId();
		String usageValue = usage.getValue();
		String usageLang = usage.getLang();
		boolean isUsagePublic = isPublic(usage.getPublicity(), defaultPublicity);
		List<SourceLink> usageSourceLinks = usage.getSourceLinks();

		FreeForm freeform = initFreeform(FreeformType.USAGE, usageValue, usageLang, isUsagePublic);

		ActivityLogData activityLog;
		if (usageId == null) {
			activityLog = activityLogService.prepareActivityLog("createUsage", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			usageId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
		} else {
			activityLog = activityLogService.prepareActivityLog("updateUsage", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			freeform.setId(usageId);
			cudDbService.updateFreeform(freeform, userName);
		}
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);

		if (CollectionUtils.isNotEmpty(usageSourceLinks)) {

			for (SourceLink usageSourceLink : usageSourceLinks) {
				Long sourceLinkId = usageSourceLink.getSourceLinkId();
				Long sourceId = usageSourceLink.getSourceId();
				String sourceLinkName = usageSourceLink.getSourceLinkName();
				ReferenceType refType = usageSourceLink.getType();
				if (refType == null) {
					refType = DEFAULT_SOURCE_LINK_REF_TYPE;
				}
				if (sourceLinkId == null) {
					createFreeformSourceLink(usageId, sourceId, sourceLinkName, refType, ActivityOwner.LEXEME, lexemeId, ActivityEntity.USAGE_SOURCE_LINK, roleDatasetCode);
				} else {
					updateFreeformSourceLink(sourceLinkId, sourceLinkName, ActivityOwner.LEXEME, lexemeId, ActivityEntity.USAGE_SOURCE_LINK, roleDatasetCode);
				}
			}
		}
	}

	protected FreeForm initFreeform(FreeformType freeformType, String value, String lang, boolean isPublic) {

		FreeForm freeform = new FreeForm();
		freeform.setType(freeformType);
		freeform.setValueText(value);
		freeform.setValuePrese(value);
		freeform.setLang(lang);
		freeform.setComplexity(Complexity.DETAIL);
		freeform.setPublic(isPublic);
		return freeform;
	}

	protected void createFreeformSourceLink(
			Long freeformId, Long sourceId, String sourceLinkName, ReferenceType refType, ActivityOwner owner, Long ownerId, ActivityEntity activityEntity,
			String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createFreeformSourceLink", ownerId, owner, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceId, refType, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, activityEntity);
	}

	protected void updateFreeformSourceLink(
			Long sourceLinkId, String sourceLinkName, ActivityOwner owner, Long ownerId, ActivityEntity activityEntity, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateFreeformSourceLink", ownerId, owner, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		sourceLinkDbService.updateFreeformSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, activityEntity);
	}

	private void createDefinitionSourceLink(Long definitionId, Long sourceId, Long meaningId, String sourceLinkName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceId, DEFAULT_SOURCE_LINK_REF_TYPE, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	private void updateDefinitionSourceLink(Long sourceLinkId, Long meaningId, String sourceLinkName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		sourceLinkDbService.updateDefinitionSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	protected boolean isPublic(Boolean itemPublicity, boolean defaultPublicity) {

		return itemPublicity == null ? defaultPublicity : itemPublicity;
	}
}
