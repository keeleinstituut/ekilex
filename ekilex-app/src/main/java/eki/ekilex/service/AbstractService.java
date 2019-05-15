package eki.ekilex.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.data.ListData;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.ProcessLogDbService;

public abstract class AbstractService {

	private static final String RECENT_STATE_MESSAGE = "Endine olek - ";

	@Autowired
	private UserService userService;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;


	@Autowired
	private ProcessLogDbService processLogDbService;

	protected void createLifecycleLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId) {
		createLifecycleLog(eventType, entity, property, entityId, null, null);
	}

	protected void createLifecycleLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId, String entry) {
		createLifecycleLog(eventType, entity, property, entityId, null, entry);
	}

	protected void createLifecycleLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId, String recent, String entry) {
		String userName = userService.getAuthenticatedUser().getName();
		lifecycleLogDbService.createLog(userName, eventType, entity, property, entityId, recent, entry);
	}

	protected void createLifecycleLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, ListData item) {
		String userName = userService.getAuthenticatedUser().getName();
		lifecycleLogDbService.createLog(userName, eventType, entity, property, item);
	}

	protected void createLexemeProcessLog(Long lexemeId, String processStateCode) {

		String userName = userService.getAuthenticatedUser().getName();
		Map<String, Object> entityData = processLogDbService.getLexemeData(lexemeId);
		String datasetCode = (String) entityData.get("dataset_code");
		String recentProcessStateCode = (String) entityData.get("process_state_code");
		String comment = RECENT_STATE_MESSAGE + recentProcessStateCode;

		processLogDbService.createLexemeProcessLog(lexemeId, userName, datasetCode, comment, processStateCode);
	}
}
