package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.LayerName;
import eki.ekilex.data.LexemeData;
import eki.ekilex.data.ProcessLog;
import eki.ekilex.service.db.ProcessDbService;

@Component
public class ProcessService implements GlobalConstant {

	private static final String PROCESS_STATE_DELETED_MESSAGE = " - haldusolek kustutatud";

	@Autowired
	private ProcessDbService processDbService;

	@Transactional
	public List<ProcessLog> getLogForLexeme(Long lexemeId) {
		return processDbService.getLogForLexeme(lexemeId);
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
		// TODO change to lifecycle log?
		processDbService.createLexemeProcessLog(lexemeId, userName, recentProcessStateCode, null, processStateCode, datasetCode);
	}

	@Transactional
	public void updateLayerProcessStateComplete(Long wordId, String userName, String datasetCode, LayerName layerName) {

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
	public void updateSynProcessState(Long lexemeId, String userName, String processStateCode, LayerName layerName) {

		LexemeData lexemeData = processDbService.getLexemeData(lexemeId, layerName);
		String datasetCode = lexemeData.getDatasetCode();
		String recentProcessStateCode = lexemeData.getLayerProcessStateCode();
		if (!StringUtils.equals(recentProcessStateCode, processStateCode)) {
			processDbService.createOrUpdateLayerProcessState(lexemeId, layerName, processStateCode);
			processDbService.createLexemeProcessLog(lexemeId, userName, recentProcessStateCode, null, processStateCode, datasetCode, layerName);
		}
	}
}
