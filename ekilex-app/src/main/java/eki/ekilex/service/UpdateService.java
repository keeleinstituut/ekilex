package eki.ekilex.service;

import eki.ekilex.data.OrderingData;
import eki.ekilex.service.db.UpdateDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UpdateService {

	@Autowired
	private UpdateDbService updateDbService;

	@Transactional
	public void updateUsageValue(Long id, String value) {
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateUsageTranslationValue(Long id, String value) {
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long id, String value) {
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateDefinitionValue(Long id, String value) {
		updateDbService.updateDefinitionValue(id, value);
	}

	@Transactional
	public void updateDefinitionOrdering(List<OrderingData> items) {
		updateDbService.updateDefinitionOrderby(items);
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<OrderingData> items) {
		updateDbService.updateLexemeRelationOrderby(items);
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<OrderingData> items) {
		updateDbService.updateMeaningRelationOrderby(items);
	}
}
