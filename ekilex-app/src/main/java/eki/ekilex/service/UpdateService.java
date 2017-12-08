package eki.ekilex.service;

import eki.ekilex.service.db.UpdateDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
}
