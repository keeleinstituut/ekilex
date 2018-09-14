package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.LifecycleLog;
import eki.ekilex.service.db.LifecycleLogDbService;

@Component
public class LifecycleLogService {

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	@Transactional
	public List<LifecycleLog> getLogForWord(Long wordId) {
		return lifecycleLogDbService.getLogForWord(wordId);
	}
}
