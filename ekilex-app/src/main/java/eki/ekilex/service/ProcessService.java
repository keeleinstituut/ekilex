package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.DbConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.LexemeData;
import eki.ekilex.data.ProcessLog;
import eki.ekilex.service.db.ProcessDbService;

@Component
public class ProcessService implements DbConstant {

	private static final String PROCESS_STATE_DELETED_MESSAGE = " - haldusolek kustutatud";

	@Autowired
	private ProcessDbService processDbService;

	@Autowired
	private TextDecorationService textDecorationService;

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
	public void createMeaningProcessLog(Long meaningId, String userName, String commentPrese, String datasetCode) {

		String comment = textDecorationService.cleanEkiElementMarkup(commentPrese);
		processDbService.createMeaningProcessLog(meaningId, userName, comment, commentPrese, datasetCode);
	}

	@Transactional
	public void createWordProcessLog(Long wordId, String userName, String commentPrese, String datasetCode) {

		String comment = textDecorationService.cleanEkiElementMarkup(commentPrese);
		processDbService.createWordProcessLog(wordId, userName, comment, commentPrese, datasetCode);
	}

	@Transactional
	public void updateLexemeProcessState(Long lexemeId, String userName, String processStateCode) {

		LexemeData lexemeData = processDbService.getLexemeData(lexemeId);
		String datasetCode = lexemeData.getDatasetCode();
		String recentProcessStateCode = lexemeData.getProcessStateCode();
		if (processStateCode == null) {
			recentProcessStateCode += PROCESS_STATE_DELETED_MESSAGE;
		}

		processDbService.updateLexemeProcessState(lexemeId, processStateCode);
		processDbService.createLexemeProcessLog(lexemeId, userName, recentProcessStateCode, null, processStateCode, datasetCode);
	}

	@Transactional
	public void updateLayerProcessStateComplete(Long wordId, String userName, String datasetCode, String layerName) {

		final String newProcessStateCode = PROCESS_STATE_COMPLETE;
		List<LexemeData> lexemeDatas = processDbService.getLexemeDatas(wordId, datasetCode, layerName);
		for (LexemeData lexemeData : lexemeDatas) {
			Long lexemeId = lexemeData.getId();
			String recentProcessStateCode = lexemeData.getLayerProcessStateCode();
			if (!StringUtils.equals(recentProcessStateCode, newProcessStateCode)) {
				processDbService.createOrUpdateLayerProcessState(lexemeId, layerName, newProcessStateCode);
				processDbService.createLexemeProcessLog(lexemeId, userName, recentProcessStateCode, null, newProcessStateCode, datasetCode, layerName);
			}
		}
	}

	@Transactional
	public void updateSynProcessState(Long lexemeId, String userName, String processStateCode, String layerName) {

		LexemeData lexemeData = processDbService.getLexemeData(lexemeId, layerName);
		String datasetCode = lexemeData.getDatasetCode();
		String recentProcessStateCode = lexemeData.getLayerProcessStateCode();
		if (!StringUtils.equals(recentProcessStateCode, processStateCode)) {
			processDbService.createOrUpdateLayerProcessState(lexemeId, layerName, processStateCode);
			processDbService.createLexemeProcessLog(lexemeId, userName, recentProcessStateCode, null, processStateCode, datasetCode, layerName);
		}
	}
}
