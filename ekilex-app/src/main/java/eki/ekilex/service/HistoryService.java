package eki.ekilex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.ekilex.data.ActivityLogHistory;
import eki.ekilex.service.db.HistoryDbService;

@Component
public class HistoryService {

	@Autowired
	private HistoryDbService historyDbService;

	@Transactional
	public List<ActivityLogHistory> getWordsHistory(int offset, int limit) {
		return historyDbService.getWordsHistory(offset, limit);
	}

	@Transactional
	public List<ActivityLogHistory> getMeaningsHistory(int offset, int limit) {
		return historyDbService.getMeaningsHistory(offset, limit);
	}
}
