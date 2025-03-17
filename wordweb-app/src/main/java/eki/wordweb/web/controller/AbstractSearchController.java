package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import eki.wordweb.data.NewsArticle;
import eki.wordweb.data.SearchValidation;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.web.bean.SessionBean;
import eki.wordweb.web.util.StatDataUtil;

public abstract class AbstractSearchController extends AbstractController {

	@Autowired
	protected StatDataUtil statDataUtil;

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

		NewsArticle latestWordwebNewsArticle = commonDataService.getLatestWordwebNewsArticle();
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

	protected String cleanupMain(String value) {

		value = StringUtils.trim(value);
		if (StringUtils.isBlank(value)) {
			return value;
		}
		while (StringUtils.endsWith(value, "-")) {
			value = StringUtils.removeEnd(value, "-");
		}
		while (StringUtils.startsWith(value, "-")) {
			value = StringUtils.removeStart(value, "-");
		}
		if (StringUtils.length(value) > SEARCH_WORD_MAX_LENGTH) {
			value = StringUtils.left(value, SEARCH_WORD_MAX_LENGTH);
		}
		return value;
	}

	protected String cleanupBasic(String value) {

		value = StringUtils.trim(value);
		if (StringUtils.isBlank(value)) {
			return value;
		}
		if (StringUtils.length(value) > SEARCH_WORD_MAX_LENGTH) {
			value = StringUtils.left(value, SEARCH_WORD_MAX_LENGTH);
		}
		return value;
	}

	protected String cleanupMask(String searchWord) {

		if (StringUtils.isBlank(searchWord)) {
			return searchWord;
		}
		searchWord = StringUtils.trim(searchWord);
		char wildcardChar = SEARCH_MASK_CHARS.charAt(0);
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

	protected boolean isValidMaskedSearch(String searchWord, String cleanMaskSearchWord) {

		if (StringUtils.isBlank(cleanMaskSearchWord)) {
			return false;
		}
		char searchMaskCharsChar = SEARCH_MASK_CHARS.charAt(0);
		char searchMaskCharChar = SEARCH_MASK_CHAR.charAt(0);
		String testSearchWord = cleanMaskSearchWord;
		testSearchWord = StringUtils.remove(testSearchWord, searchMaskCharsChar);
		testSearchWord = StringUtils.remove(testSearchWord, searchMaskCharChar);
		if (StringUtils.isBlank(testSearchWord)) {
			return false;
		}
		if (StringUtils.containsOnly(cleanMaskSearchWord, searchMaskCharsChar, searchMaskCharChar)) {
			return false;
		}
		return true;
	}

	protected String decode(String value) {

		if (StringUtils.isBlank(value)) {
			return value;
		}
		value = UriUtils.decode(value, UTF_8);
		value = StringUtils.replace(value, ENCODE_SYM_SLASH, "/");
		value = StringUtils.replace(value, ENCODE_SYM_BACKSLASH, "\\");
		value = StringUtils.replace(value, ENCODE_SYM_PERCENT, "%");
		return value;
	}

}
