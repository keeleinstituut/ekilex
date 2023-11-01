package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ActivityOwner;
import eki.common.constant.SourceType;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.service.db.SourceDbService;
import eki.ekilex.service.db.SourceLinkDbService;

public abstract class AbstractSourceService extends AbstractService {

	@Autowired
	protected SourceDbService sourceDbService;

	@Autowired
	protected SourceLinkDbService sourceLinkDbService;

	@Transactional
	public Long createSource(
			SourceType type, String name, String valuePrese, String comment, boolean isPublic, List<SourceProperty> sourceProperties, String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long sourceId = sourceDbService.createSource(type, name, value, valuePrese, comment, isPublic, sourceProperties);
		activityLogService.createActivityLog("createSource", sourceId, ActivityOwner.SOURCE, roleDatasetCode, isManualEventOnUpdateEnabled);
		return sourceId;
	}

	@Transactional
	@Deprecated
	public Long createSourceDeprecated(SourceType sourceType, List<SourceProperty> sourceProperties, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long sourceId = sourceDbService.createSourceDeprecated(sourceType, sourceProperties);
		activityLogService.createActivityLog("createSource", sourceId, ActivityOwner.SOURCE, roleDatasetCode, isManualEventOnUpdateEnabled);
		return sourceId;
	}

}
