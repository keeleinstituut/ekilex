package eki.ekilex.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.data.LogData;
import eki.ekilex.service.db.ProcessDbService;

@Component
public class ProcessService extends AbstractService implements GlobalConstant {

	// TODO move to some other service? - yogesh

	@Autowired
	private ProcessDbService processDbService;

	@Transactional
	public void updateLexemeProcessState(Long lexemeId, String processStateCode) {

		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.PROCESS_STATE, lexemeId, processStateCode);
		createLifecycleLog(logData);
		processDbService.updateLexemeProcessState(lexemeId, processStateCode);
	}

}
