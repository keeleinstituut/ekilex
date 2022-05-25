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
	public Long createSource(SourceType sourceType, List<SourceProperty> sourceProperties, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long sourceId = sourceDbService.createSource(sourceType, sourceProperties);
		activityLogService.createActivityLog("createSource", sourceId, ActivityOwner.SOURCE, isManualEventOnUpdateEnabled);
		return sourceId;
	}

}
