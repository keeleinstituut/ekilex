package eki.ekilex.service.api;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;
import eki.ekilex.data.api.Definition;
import eki.ekilex.service.AbstractCudService;
import eki.ekilex.service.db.SourceLinkDbService;

public abstract class AbstractApiCudService extends AbstractCudService {

	@Autowired
	protected SourceLinkDbService sourceLinkDbService;

	protected void createOrUpdateDefinition(Definition definition, Long meaningId, String datasetCode, Complexity defaultComplexity, Boolean defaultPublicity, String roleDatasetCode) throws Exception {

		Long definitionId = definition.getId();
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
			definitionId = cudDbService.createDefinition(meaningId, definitionValue, definitionValue, definitionLang, definitionTypeCode, defaultComplexity, defaultPublicity);
			cudDbService.createDefinitionDataset(definitionId, datasetCode);
		} else {
			activityLog = activityLogService.prepareActivityLog("updateDefinition", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			cudDbService.updateDefinition(definitionId, definitionValue, definitionValue, definitionLang, definitionTypeCode, null, null);
		}
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);

		if (CollectionUtils.isNotEmpty(definitionSourceLinks)) {

			for (SourceLink definitionSourceLink : definitionSourceLinks) {
				Long sourceLinkId = definitionSourceLink.getId();
				String sourceLinkName = definitionSourceLink.getName();
				if (sourceLinkId == null) {
					createDefinitionSourceLink(meaningId, definitionId, definitionSourceLink, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				} else {
					updateDefinitionSourceLink(meaningId, sourceLinkId, sourceLinkName, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				}
			}
		}
	}

	protected void createOrUpdateUsage(Long lexemeId, Usage usage, Complexity defaultComplexity, Boolean defaultPublicity, String roleDatasetCode) throws Exception {

		Long usageId = usage.getId();
		List<SourceLink> usageSourceLinks = usage.getSourceLinks();
		ActivityLogData activityLog;

		if (usageId == null) {
			applyCreateUpdate(usage);
			activityLog = activityLogService.prepareActivityLog("createUsage", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			usageId = cudDbService.createUsage(lexemeId, usage);
		} else {
			applyUpdate(usage);
			activityLog = activityLogService.prepareActivityLog("updateUsage", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			cudDbService.updateUsage(usage);
		}
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);

		if (CollectionUtils.isNotEmpty(usageSourceLinks)) {

			for (SourceLink usageSourceLink : usageSourceLinks) {

				Long sourceLinkId = usageSourceLink.getId();
				String sourceLinkName = usageSourceLink.getName();
				if (sourceLinkId == null) {
					createUsageSourceLink(lexemeId, usageId, usageSourceLink, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				} else {
					updateUsageSourceLink(lexemeId, sourceLinkId, sourceLinkName, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				}
			}
		}
	}

}
