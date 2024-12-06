package eki.ekilex.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ActivityOwner;
import eki.ekilex.data.Source;
import eki.ekilex.service.db.SourceDbService;
import eki.ekilex.service.db.SourceLinkDbService;

public abstract class AbstractSourceService extends AbstractService {

	@Autowired
	protected SourceDbService sourceDbService;

	@Autowired
	protected SourceLinkDbService sourceLinkDbService;

	@Transactional(rollbackOn = Exception.class)
	public Long createSource(
			Source source,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		String valuePrese = source.getValuePrese();
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		source.setValue(value);
		source.setDatasetCode(roleDatasetCode);

		Long sourceId = sourceDbService.createSource(source);

		activityLogService.createActivityLog("createSource", sourceId, ActivityOwner.SOURCE, roleDatasetCode, isManualEventOnUpdateEnabled);

		return sourceId;
	}
}
