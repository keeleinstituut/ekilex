package eki.ekilex.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.service.db.CommonDataDbService;

@Component
public class CommonDataService {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Transactional
	public Map<String, String> getDatasetNameMap() {
		return commonDataDbService.getDatasetNameMap();
	}
}
