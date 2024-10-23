package eki.ekilex.service;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.service.db.DataRequestDbService;

@Component
public class DataRequestService {

	@Autowired
	private DataRequestDbService dataRequestDbService;

	@Transactional(rollbackOn = Exception.class)
	public void createDataRequest(Long userId, String requestKey, String content) {
		dataRequestDbService.createDataRequest(userId, requestKey, content);
	}

	@Transactional
	public String getDataRequestContent(String requestKey) {
		String requestContent = dataRequestDbService.getDataRequestContent(requestKey);
		if (StringUtils.isEmpty(requestContent)) {
			return "n/a";
		}
		dataRequestDbService.accessRequestContent(requestKey);
		return requestContent;
	}

}
