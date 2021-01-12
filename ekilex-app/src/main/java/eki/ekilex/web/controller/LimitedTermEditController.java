package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.service.CudService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LimitedTermEditController extends AbstractMutableDataPageController {

	@Autowired
	private CudService cudService;

	@Autowired
	private SearchHelper searchHelper;

	@PostMapping(LIM_TERM_CREATE_WORD_URI)
	public String createWord(WordLexemeMeaningDetails wordDetails, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		valueUtil.trimAndCleanAndRemoveHtml(wordDetails);

		String wordValue = wordDetails.getWordValue();
		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {
			String language = wordDetails.getLanguage();
			List<String> userPrefDatasetCodes = getUserPreferredDatasetCodes();

			sessionBean.setRecentLanguage(language);
			searchUri = searchHelper.composeSearchUri(userPrefDatasetCodes, wordValue);
			cudService.createWord(wordDetails);
		}
		return "redirect:" + LIM_TERM_SEARCH_URI + searchUri;
	}

}
