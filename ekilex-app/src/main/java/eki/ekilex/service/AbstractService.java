package eki.ekilex.service;

import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.data.ListData;
import eki.ekilex.service.db.LifecycleLogDbService;

public abstract class AbstractService {

	@Autowired
	private UserService userService;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

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
}
