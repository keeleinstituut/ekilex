package eki.wordweb.web.controller;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.FeedbackType;
import eki.common.data.AppResponse;
import eki.common.data.ExtendedFeedback;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.WordSuggestion;
import eki.wordweb.service.FeedbackService;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class WordSuggestionController extends AbstractController {

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LanguageContext languageContext;

	@GetMapping(WORD_SUGGESTION_URI)
	public String wordSuggestion(Model model) {

		List<WordSuggestion> wordSuggestions = ancillaryDataService.getWordSuggestions();
		model.addAttribute("wordSuggestions", wordSuggestions);
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
		wordSuggestion.setLastSearch(sessionBean.getSearchWord());
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
