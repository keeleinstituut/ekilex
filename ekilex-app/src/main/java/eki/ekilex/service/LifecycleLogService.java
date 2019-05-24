package eki.ekilex.service;

import java.util.List;
import java.util.stream.Collectors;

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
		List<LifecycleLog> logForWord = lifecycleLogDbService.getLogForWord(wordId);
		List<LifecycleLog> distinctLogForWord = logForWord.stream().distinct().collect(Collectors.toList());
		return distinctLogForWord;
	}

	@Transactional
	public List<LifecycleLog> getLogForMeaning(Long meaningId) {
		List<LifecycleLog> logForMeaning = lifecycleLogDbService.getLogForMeaning(meaningId);
		List<LifecycleLog> distinctLogForMeaning = logForMeaning.stream().distinct().collect(Collectors.toList());
		return distinctLogForMeaning;
	}

	@Transactional
	public List<LifecycleLog> getLogForSource(Long sourceId) {
		return lifecycleLogDbService.getLogForSource(sourceId);
	}
}
