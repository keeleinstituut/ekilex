package eki.wordweb.web.controller;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.FeedbackType;
import eki.common.data.AppResponse;
import eki.common.data.ExtendedFeedback;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.WordSuggestionPage;
import eki.wordweb.service.FeedbackService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class WordSuggestionController extends AbstractController {

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping({
			WORD_SUGGESTION_URI,
			WORD_SUGGESTION_URI + "/{pageNum}"
	})
	public String wordSuggestion(
			@PathVariable(value = "pageNum", required = false) String pageNumStr,
			Model model) {

		Integer pageNum = nullSafe(pageNumStr);
		if (pageNum == null) {
			pageNum = 1;
		}
		WordSuggestionPage wordSuggestionPage = ancillaryDataService.getWordSuggestions(pageNum);
		model.addAttribute("wordSuggestionsPage", wordSuggestionPage);
		populateCommonModel(model);

		return WORD_SUGGESTION_PAGE;
	}

	@PostMapping(WORD_SUGGESTION_URI)
	@ResponseBody
	public AppResponse wordSuggestion(
			ExtendedFeedback wordSuggestion,
			@ModelAttribute(SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		wordSuggestion.setFeedbackType(FeedbackType.WORD_SUGGESTION);
		wordSuggestion.setLastSearch(sessionBean.getSearchWordValue());
		AppResponse response = feedbackService.feedback(wordSuggestion);
		String messageKey = response.getMessageKey();
		if (StringUtils.isNotBlank(messageKey)) {
			Locale displayLocale = languageContext.getDisplayLocale();
			String messageValue = messageSource.getMessage(messageKey, new Object[0], displayLocale);
			response.setMessageValue(messageValue);
		}
		return response;
	}
}
