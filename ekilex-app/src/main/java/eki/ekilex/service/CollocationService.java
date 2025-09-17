package eki.ekilex.service;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.service.core.ActivityLogService;
import eki.ekilex.service.db.CollocationDbService;

@Component
public class CollocationService {

	@Autowired
	private CollocationDbService collocationDbService;

	@Autowired
	private ActivityLogService activityLogService;

	@Transactional(rollbackFor = Exception.class)
	public void moveCollocMember(
			List<Long> collocLexemeIds,
			Long sourceCollocMemberLexemeId,
			Long targetCollocMemberLexemeId,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		if (CollectionUtils.isEmpty(collocLexemeIds)) {
			return;
		}
		if (sourceCollocMemberLexemeId.equals(targetCollocMemberLexemeId)) {
			return;
		}

		ActivityLogData sourceLexemeActivityLog = activityLogService.prepareActivityLog("moveCollocMember", sourceCollocMemberLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		ActivityLogData targetLexemeActivityLog = activityLogService.prepareActivityLog("moveCollocMember", targetCollocMemberLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		collocationDbService.moveCollocMember(collocLexemeIds, sourceCollocMemberLexemeId, targetCollocMemberLexemeId);
		activityLogService.createActivityLog(sourceLexemeActivityLog, sourceCollocMemberLexemeId, ActivityEntity.LEXEME);
		activityLogService.createActivityLog(targetLexemeActivityLog, targetCollocMemberLexemeId, ActivityEntity.LEXEME);
	}
}
