package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.WordsResult;
import eki.ekilex.service.FullSynSearchService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SynSearchController extends AbstractPrivateSearchController {

	@Autowired
	private FullSynSearchService fullSynSearchService;

	@PostMapping(SYN_PAGING_URI)
	public String paging(
			@RequestParam("offset") int offset,
			@RequestParam("searchUri") String searchUri,
			@RequestParam("direction") String direction,
			@RequestParam(name = "pageNum", required = false) Integer pageNum,
			Model model) throws Exception {

		SearchUriData searchUriData = searchHelper.parseSearchUri(PART_SYN_SEARCH_PAGE, searchUri);

		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean noLimit = false;

		UserContextData userContextData = getUserContextData();
		EkiUser user = userContextData.getUser();
		List<String> tagNames = userContextData.getTagNames();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();
		if (StringUtils.isEmpty(userRoleDatasetCode)) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		List<String> datasetCodes = new ArrayList<>(Arrays.asList(userRoleDatasetCode));

		if (StringUtils.equals("next", direction)) {
			offset += DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("previous", direction)) {
			offset -= DEFAULT_MAX_RESULTS_LIMIT;
		} else if (StringUtils.equals("page", direction)) {
			offset = (pageNum - 1) * DEFAULT_MAX_RESULTS_LIMIT;
		}

		WordsResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			wordsResult = fullSynSearchService.getWords(detailSearchFilter, datasetCodes, tagNames, user, offset, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		} else {
			wordsResult = fullSynSearchService.getWords(simpleSearchFilter, datasetCodes, tagNames, user, offset, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		}

		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("searchUri", searchUri); // no way of knowing which syn page context

		return COMMON_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "search_result";
	}
}
