package eki.wordweb.web.util;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.RequestOrigin;
import eki.common.data.SearchStat;
import eki.wordweb.data.AbstractSearchResult;
import eki.wordweb.data.SearchValidation;

@Component
public class StatDataUtil {

	public SearchStat composeSearchStat(
			HttpServletRequest request,
			boolean isSearchForm,
			String searchMode,
			SearchValidation searchValidation,
			AbstractSearchResult searchResult) throws Exception {

		String sessionId = request.getSession().getId();
		String userAgent = request.getHeader("User-Agent");
		String referer = request.getHeader("referer");
		String serverDomain = request.getServerName();

		String searchWord = searchValidation.getSearchWord();
		Integer homonymNr = searchValidation.getHomonymNr();
		List<String> destinLangs = searchValidation.getDestinLangs();
		List<String> datasetCodes = searchValidation.getDatasetCodes();
		String searchUri = searchValidation.getSearchUri();

		int resultCount = searchResult.getResultCount();
		boolean resultsExist = searchResult.isResultsExist();
		boolean isSingleResult = searchResult.isSingleResult();

		String referrerDomain = null;
		if (referer != null) {
			referrerDomain = new URI(referer).getHost();
		}

		RequestOrigin requestOrigin;
		if (isSearchForm) {
			requestOrigin = RequestOrigin.SEARCH;
		} else if (StringUtils.equals(serverDomain, referrerDomain)) {
			requestOrigin = RequestOrigin.INSIDE_NAVIGATION;
		} else {
			requestOrigin = RequestOrigin.OUTSIDE_NAVIGATION;
		}

		SearchStat searchStat = new SearchStat();
		searchStat.setSearchWord(searchWord);
		searchStat.setHomonymNr(homonymNr);
		searchStat.setSearchMode(searchMode);
		searchStat.setDestinLangs(destinLangs);
		searchStat.setDatasetCodes(datasetCodes);
		searchStat.setSearchUri(searchUri);
		searchStat.setResultCount(resultCount);
		searchStat.setResultsExist(resultsExist);
		searchStat.setSingleResult(isSingleResult);
		searchStat.setSessionId(sessionId);
		searchStat.setUserAgent(userAgent);
		searchStat.setReferrerDomain(referrerDomain);
		searchStat.setRequestOrigin(requestOrigin);

		return searchStat;
	}
}
