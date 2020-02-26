package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;

import eki.common.constant.DbConstant;
import eki.common.constant.LayerName;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SearchUriData;
import eki.ekilex.data.WordsResult;
import eki.ekilex.data.WordsSynResult;
import eki.ekilex.service.SynSearchService;
import eki.ekilex.web.bean.SessionBean;

public class AbstractSynSearchController extends AbstractSearchController implements SystemConstant, DbConstant {

	@Autowired
	protected SynSearchService synSearchService;

	protected void initPage(String searchPage, Model model) {

		initSearchForms(searchPage, model);
		resetUserRole(model);

		WordsResult wordsResult = new WordsResult();
		model.addAttribute("wordsResult", wordsResult);
	}

	protected void initSearch(Model model, String searchPage, String searchUri, LayerName layerName) throws Exception {

		initSearchForms(searchPage, model);
		resetUserRole(model);

		SearchUriData searchUriData = searchHelper.parseSearchUri(searchPage, searchUri);

		if (!searchUriData.isValid()) {
			initSearchForms(searchPage, model);
			model.addAttribute("wordsResult", new WordsResult());
			model.addAttribute("noResults", true);
			return;
		}

		SessionBean sessionBean = getSessionBean(model);
		String roleDatasetCode = getDatasetCodeFromRole(sessionBean);
		List<String> roleDatasets = new ArrayList<>(Arrays.asList(roleDatasetCode));

		String searchMode = searchUriData.getSearchMode();
		String simpleSearchFilter = searchUriData.getSimpleSearchFilter();
		SearchFilter detailSearchFilter = searchUriData.getDetailSearchFilter();
		boolean fetchAll = false;

		WordsSynResult wordsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			wordsResult = synSearchService.getWords(detailSearchFilter, roleDatasets, layerName, fetchAll, DEFAULT_OFFSET);
		} else {
			wordsResult = synSearchService.getWords(simpleSearchFilter, roleDatasets, layerName, fetchAll, DEFAULT_OFFSET);
		}
		boolean noResults = wordsResult.getTotalCount() == 0;
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("simpleSearchFilter", simpleSearchFilter);
		model.addAttribute("detailSearchFilter", detailSearchFilter);
		model.addAttribute("wordsResult", wordsResult);
		model.addAttribute("noResults", noResults);
	}

	protected String getDatasetCodeFromRole(SessionBean sessionBean) {
		DatasetPermission role = sessionBean.getUserRole();
		if (role == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		return role.getDatasetCode();
	}
}
