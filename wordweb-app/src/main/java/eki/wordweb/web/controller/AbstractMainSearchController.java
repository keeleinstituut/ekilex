package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;

import eki.wordweb.data.NewsArticle;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.web.bean.SessionBean;

public abstract class AbstractMainSearchController extends AbstractSearchController {

	protected void setSearchCookies(HttpServletRequest request, HttpServletResponse response, SearchValidation searchValidation) {

		deleteCookies(request, response, COOKIE_NAME_DESTIN_LANGS, COOKIE_NAME_DATASETS);

		String destinLangsStr = StringUtils.join(searchValidation.getDestinLangs(), COOKIE_VALUES_SEPARATOR);
		String datasetCodesStr = StringUtils.join(searchValidation.getDatasetCodes(), COOKIE_VALUES_SEPARATOR);

		setCookie(response, COOKIE_NAME_DESTIN_LANGS, destinLangsStr);
		setCookie(response, COOKIE_NAME_DATASETS, datasetCodesStr);
	}

	protected void populateLangFilter(List<UiFilterElement> langFilter, SessionBean sessionBean, Model model) {

		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> selectedLangs = new ArrayList<>();
		if (CollectionUtils.isEmpty(destinLangs)) {
			destinLangs = new ArrayList<>();
			destinLangs.add(DESTIN_LANG_ALL);
		}
		sessionBean.setDestinLangs(destinLangs);
		for (UiFilterElement langFilterElement : langFilter) {
			String langCode = langFilterElement.getCode();
			boolean isSelected = destinLangs.contains(langCode);
			langFilterElement.setSelected(isSelected);
			if (isSelected) {
				selectedLangs.add(langFilterElement.getValue());
			}
		}

		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		String selectedLangsStr = StringUtils.join(selectedLangs, ", ");
		boolean isLangFiltered = !StringUtils.equals(destinLangsStr, DESTIN_LANG_ALL);

		model.addAttribute("langFilter", langFilter);
		model.addAttribute("destinLangsStr", destinLangsStr);
		model.addAttribute("selectedLangsStr", selectedLangsStr);
		model.addAttribute("isLangFiltered", isLangFiltered);
	}

	protected void populateUserPref(SessionBean sessionBean, Model model) {

		List<String> uiSections = sessionBean.getUiSections();
		if (uiSections == null) {
			uiSections = new ArrayList<>();
			sessionBean.setUiSections(uiSections);
		}
		model.addAttribute("uiSections", uiSections);
	}

	protected void populateLatestNewsModel(HttpServletRequest request, Model model) {

		NewsArticle latestWordwebNewsArticle = ancillaryDataService.getLatestWordwebNewsArticle();
		if (latestWordwebNewsArticle == null) {
			return;
		}
		Long latestNewsId = latestWordwebNewsArticle.getNewsArticleId();
		String latestNewsIdStr = String.valueOf(latestNewsId);
		Map<String, String> cookieMap = getWwCookieMap(request);
		String userNewsId = cookieMap.get(COOKIE_NAME_NEWS_ID);
		if (StringUtils.equals(latestNewsIdStr, userNewsId)) {
			return;
		}
		model.addAttribute("latestWordwebNewsArticle", latestWordwebNewsArticle);
	}
}
