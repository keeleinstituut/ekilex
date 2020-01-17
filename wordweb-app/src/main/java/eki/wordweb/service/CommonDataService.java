package eki.wordweb.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.service.db.CommonDataDbService;

@Component
public class CommonDataService {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Transactional
	public Map<String, String> getLangIso2Map() {
		return commonDataDbService.getLangIso2Map();
	}
}
