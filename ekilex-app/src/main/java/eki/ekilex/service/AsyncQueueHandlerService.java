package eki.ekilex.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import eki.common.util.CodeGenerator;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.QueueContent;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.TermSearchResultQueueContent;

@Component
public class AsyncQueueHandlerService implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(AsyncQueueHandlerService.class);

	@Value("${ekilex.app.url:}")
	private String ekilexAppUrl;

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private DataRequestService dataRequestService;

	@Autowired
	private EmailService emailService;

	@Async
	public void handleTermSearchResultSerialisation(EkiUser user, QueueContent content) throws Exception {

		String requestKey = CodeGenerator.generateUniqueId();
		logger.info("Handling asynchronous term search result serialisation \"{}\" for \"{}\"", requestKey, user.getName());

		TermSearchResultQueueContent termSearchResultQueueContent = (TermSearchResultQueueContent) content;
		EkiUserProfile userProfile = termSearchResultQueueContent.getUserProfile();
		List<ClassifierSelect> languagesOrder = termSearchResultQueueContent.getLanguagesOrder();
		String searchUri = termSearchResultQueueContent.getSearchUri();
		SearchUriData searchUriData = termSearchResultQueueContent.getSearchUriData();
		String searchMode = searchUriData.getSearchMode();
		List<String> selectedDatasets = searchUriData.getSelectedDatasets();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		String resultLang = searchUriData.getResultLang();
		Long userId = user.getId();

		byte[] bytes = null;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			bytes = termSearchService.serialiseTermSearchResult(detailSearchFilter, selectedDatasets, languagesOrder, resultLang, userProfile, user);
		} else {
			bytes = termSearchService.serialiseTermSearchResult(simpleSearchFilter, selectedDatasets, languagesOrder, resultLang, userProfile, user);
		}
		String json = new String(bytes, StandardCharsets.UTF_8);
		String termSearchUrl = ekilexAppUrl + TERM_SEARCH_URI + searchUri;
		String termSearchResultUrl = ekilexAppUrl + TERM_SEARCH_RESULT_ACCESS_URI + "/" + requestKey;
		dataRequestService.createDataRequest(userId, requestKey, json);
		emailService.sendTermSearchResult(user, termSearchUrl, termSearchResultUrl);

		logger.info("Term search result serialisation \"{}\" complete", requestKey);
	}
}
