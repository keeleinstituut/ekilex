package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.LexemeData;
import eki.ekilex.data.ProcessLog;
import eki.ekilex.service.db.ProcessDbService;

@Component
public class ProcessService {

	private static final String PROCESS_STATE_DELETED_MESSAGE = " - haldusolek kustutatud";

	@Autowired
	private ProcessDbService processDbService;

	@Autowired
	private UserService userService;

	@Transactional
	public List<ProcessLog> getLogForMeaning(Long meaningId) {
		return processDbService.getLogForMeaning(meaningId);
	}

	@Transactional
	public List<ProcessLog> getLogForWord(Long wordId) {
		return processDbService.getLogForWord(wordId);
	}

	@Transactional
	public List<ProcessLog> getLogForLexemeAndMeaning(Long lexemeId) {
		return processDbService.getLogForLexemeAndMeaning(lexemeId);
	}

	@Transactional
	public List<ProcessLog> getLogForLexemeAndWord(Long lexemeId) {
		return processDbService.getLogForLexemeAndWord(lexemeId);
	}

	@Transactional
	public void createLexemeProcessLog(Long lexemeId, String processStateCode) {

		String userName = userService.getAuthenticatedUser().getName();
		LexemeData lexemeData = processDbService.getLexemeData(lexemeId);
		String datasetCode = lexemeData.getDatasetCode();
		String recentProcessStateCode = lexemeData.getProcessStateCode();
		if (processStateCode == null) {
			recentProcessStateCode += PROCESS_STATE_DELETED_MESSAGE;
		}

		processDbService.createLexemeProcessLog(lexemeId, userName, datasetCode, recentProcessStateCode, processStateCode);
	}

	@Transactional
	public void createMeaningProcessLog(Long meaningId, String dataset, String value) {

		String userName = userService.getAuthenticatedUser().getName();
		processDbService.createMeaningProcessLog(meaningId, dataset, userName, value);
	}

	@Transactional
	public void createWordProcessLog(Long wordId, String dataset, String value) {

		String userName = userService.getAuthenticatedUser().getName();
		processDbService.createWordProcessLog(wordId, dataset, userName, value);
	}

	@Transactional
	public void updateLexemeProcessState(Long lexemeId, String processStateCode) {
		processDbService.updateLexemeProcessState(lexemeId, processStateCode);
	}
}
