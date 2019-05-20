package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.LexemeData;
import eki.ekilex.data.ProcessLog;
import eki.ekilex.service.db.ProcessLogDbService;

@Component
public class ProcessLogService {

	@Autowired
	private ProcessLogDbService processLogDbService;

	@Autowired
	private UserService userService;

	@Transactional
	public List<ProcessLog> getLogForMeaning(Long meaningId) {
		return processLogDbService.getLogForMeaning(meaningId);
	}

	@Transactional
	public List<ProcessLog> getLogForWord(Long wordId) {
		return processLogDbService.getLogForWord(wordId);
	}

	@Transactional
	public List<ProcessLog> getLogForLexemeAndMeaning(Long lexemeId) {
		return processLogDbService.getLogForLexemeAndMeaning(lexemeId);
	}

	@Transactional
	public List<ProcessLog> getLogForLexemeAndWord(Long lexemeId) {
		return processLogDbService.getLogForLexemeAndWord(lexemeId);
	}

	@Transactional
	public void createLexemeProcessLog(Long lexemeId, String processStateCode) {

		String userName = userService.getAuthenticatedUser().getName();
		LexemeData lexemeData = processLogDbService.getLexemeData(lexemeId);
		String datasetCode = lexemeData.getDatasetCode();
		String recentProcessStateCode = lexemeData.getProcessStateCode();

		processLogDbService.createLexemeProcessLog(lexemeId, userName, datasetCode, recentProcessStateCode, processStateCode);
	}

	@Transactional
	public void createMeaningProcessLog(Long meaningId, String dataset, String value) {

		String userName = userService.getAuthenticatedUser().getName();
		processLogDbService.createMeaningProcessLog(meaningId, dataset, userName, value);
	}

	@Transactional
	public void createWordProcessLog(Long wordId, String dataset, String value) {

		String userName = userService.getAuthenticatedUser().getName();
		processLogDbService.createWordProcessLog(wordId, dataset, userName, value);
	}
}
