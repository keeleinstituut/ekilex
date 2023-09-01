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

	protected void createOrUpdateDefinition(Definition definition, Long meaningId, String datasetCode, Complexity complexity, String functName, String roleDatasetCode) throws Exception {

		Long definitionId = definition.getDefinitionId();
		String definitionValue = StringUtils.trim(definition.getValue());
		String definitionLang = definition.getLang();
		String definitionTypeCode = definition.getDefinitionTypeCode();
		List<SourceLink> definitionSourceLinks = definition.getSourceLinks();

		if (StringUtils.isAnyBlank(definitionValue, definitionLang)) {
			return;
		}

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		if (definitionId == null) {
			definitionId = cudDbService.createDefinition(meaningId, definitionValue, definitionValue, definitionLang, definitionTypeCode, complexity, PUBLICITY_PUBLIC);
			cudDbService.createDefinitionDataset(definitionId, datasetCode);
		} else {
			cudDbService.updateDefinition(definitionId, definitionValue, definitionValue, definitionLang, definitionTypeCode, complexity, PUBLICITY_PUBLIC);
		}
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);

		if (CollectionUtils.isNotEmpty(definitionSourceLinks)) {

			for (SourceLink definitionSourceLink : definitionSourceLinks) {
				Long sourceLinkId = definitionSourceLink.getSourceLinkId();
				Long sourceId = definitionSourceLink.getSourceId();
				String sourceLinkValue = definitionSourceLink.getValue();
				if (sourceLinkId == null) {
					createDefinitionSourceLink(definitionId, sourceId, meaningId, sourceLinkValue, functName, roleDatasetCode);
				} else {
					updateDefinitionSourceLink(sourceLinkId, meaningId, sourceLinkValue, functName, roleDatasetCode);
				}
			}
		}
	}

	protected void createOrUpdateUsage(Freeform usage, Long lexemeId, boolean defaultPublicity, String functName, String roleDatasetCode) throws Exception {

		String userName = userContext.getUserName();
		Long usageId = usage.getId();
		String usageValue = usage.getValue();
		String usageLang = usage.getLang();
		boolean isUsagePublic = isPublic(usage.getPublicity(), defaultPublicity);
		List<SourceLink> usageSourceLinks = usage.getSourceLinks();

		FreeForm freeform = initFreeform(FreeformType.USAGE, usageValue, usageLang, isUsagePublic);

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		if (usageId == null) {
			usageId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
		} else {
			freeform.setId(usageId);
			cudDbService.updateFreeform(freeform, userName);
		}
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);

		if (CollectionUtils.isNotEmpty(usageSourceLinks)) {

			for (SourceLink usageSourceLink : usageSourceLinks) {
				Long sourceLinkId = usageSourceLink.getSourceLinkId();
				Long sourceId = usageSourceLink.getSourceId();
				String sourceLinkValue = usageSourceLink.getValue();
				ReferenceType refType = usageSourceLink.getType();
				if (refType == null) {
					refType = DEFAULT_SOURCE_LINK_REF_TYPE;
				}
				if (sourceLinkId == null) {
					createFreeformSourceLink(usageId, sourceId, sourceLinkValue, refType, ActivityOwner.LEXEME, lexemeId, ActivityEntity.USAGE_SOURCE_LINK, functName, roleDatasetCode);
				} else {
					updateFreeformSourceLink(sourceLinkId, sourceLinkValue, ActivityOwner.LEXEME, lexemeId, ActivityEntity.USAGE_SOURCE_LINK, functName, roleDatasetCode);
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
			Long freeformId, Long sourceId, String sourceLinkValue, ReferenceType refType, ActivityOwner owner, Long ownerId,
			ActivityEntity activityEntity, String functName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, ownerId, owner, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		String sourceLinkName = null;
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, activityEntity);
	}

	protected void updateFreeformSourceLink(
			Long sourceLinkId, String sourceLinkValue, ActivityOwner owner, Long ownerId, ActivityEntity activityEntity, String functName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, ownerId, owner, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		String sourceLinkName = null;
		sourceLinkDbService.updateFreeformSourceLink(sourceLinkId, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, activityEntity);
	}

	private void createDefinitionSourceLink(Long definitionId, Long sourceId, Long meaningId, String sourceLinkValue, String functName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		String sourceLinkName = null;
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceId, DEFAULT_SOURCE_LINK_REF_TYPE, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	private void updateDefinitionSourceLink(Long sourceLinkId, Long meaningId, String sourceLinkValue, String functName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		String sourceLinkName = null;
		sourceLinkDbService.updateDefinitionSourceLink(sourceLinkId, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	protected boolean isPublic(Boolean itemPublicity, boolean defaultPublicity) {

		return itemPublicity == null ? defaultPublicity : itemPublicity;
	}
}
