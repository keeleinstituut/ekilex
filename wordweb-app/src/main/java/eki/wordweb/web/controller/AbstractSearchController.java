package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import eki.wordweb.data.SearchRequest;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.data.WordsData;
import eki.wordweb.web.bean.SessionBean;

public abstract class AbstractSearchController extends AbstractController {

	protected void populateLangFilter(List<UiFilterElement> langFilter, SessionBean sessionBean, Model model) {

		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> selectedLangs = new ArrayList<>();
		if (CollectionUtils.isEmpty(destinLangs)) {
			destinLangs = new ArrayList<>();
			destinLangs.add(DESTIN_LANG_ALL);
			sessionBean.setDestinLangs(destinLangs);
		}
		for (UiFilterElement langFilterElement : langFilter) {
			boolean isSelected = destinLangs.contains(langFilterElement.getCode());
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

	protected boolean isSearchForm(Model model) {
		Boolean isSearchForm = (Boolean) model.asMap().get(SEARCH_FORM);
		return BooleanUtils.toBoolean(isSearchForm);
	}

	protected void setSearchFormAttribute(RedirectAttributes redirectAttributes, boolean isSearchForm) {
		redirectAttributes.addFlashAttribute(SEARCH_FORM, isSearchForm);
	}

	protected Integer nullSafe(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		if (!StringUtils.isNumeric(value)) {
			return null;
		}
		return Integer.valueOf(value);
	}

	protected String cleanupMask(String searchWord) {

		if (StringUtils.isBlank(searchWord) ) {
			return searchWord;
		}
		searchWord = StringUtils.trim(searchWord);
		char wildcardChar = QUERY_MULTIPLE_CHARACTERS_SYM.charAt(0);
		char[] searchWordChars = searchWord.toCharArray();
		StringBuilder buf = new StringBuilder();
		char prevChar = ' ';
		for (char currChar : searchWordChars) {
			if ((currChar == wildcardChar) && (currChar == prevChar)) {
				continue;
			}
			buf.append(currChar);
			prevChar = currChar;
		}
		searchWord = buf.toString();
		return searchWord;
	}

	protected String decode(String value) {

		if (StringUtils.isBlank(value) ) {
			return value;
		}
		value = UriUtils.decode(value, UTF_8);
		value = StringUtils.replace(value, ENCODE_SYM_SLASH, "/");
		value = StringUtils.replace(value, ENCODE_SYM_BACKSLASH, "\\");
		value = StringUtils.replace(value, ENCODE_SYM_PERCENT, "%");
		return value;
	}

	protected SearchRequest populateSearchRequest(HttpServletRequest request, boolean isSearchForm, String searchMode, SearchValidation searchValidation, WordsData wordsData) {

		String sessionId = request.getSession().getId();
		String userAgent = request.getHeader("User-Agent");
		String referer = request.getHeader("referer");
		String serverDomain = request.getServerName();

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchValidation(searchValidation);
		searchRequest.setWordsData(wordsData);
		searchRequest.setSearchForm(isSearchForm);
		searchRequest.setSearchMode(searchMode);
		searchRequest.setSessionId(sessionId);
		searchRequest.setUserAgent(userAgent);
		searchRequest.setReferer(referer);
		searchRequest.setServerDomain(serverDomain);
		return searchRequest;
	}
}
