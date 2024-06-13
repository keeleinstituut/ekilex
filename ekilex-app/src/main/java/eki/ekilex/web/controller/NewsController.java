package eki.ekilex.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.NewsArticle;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.NewsService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class NewsController extends AbstractPrivatePageController {

	private static final String[] SUPPORTED_LANGUAGE_CODES = {LANGUAGE_CODE_EST, LANGUAGE_CODE_ENG, LANGUAGE_CODE_RUS};

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private NewsService newsService;

	@GetMapping(NEWS_URI)
	public String initPage(Model model) {

		List<NewsArticle> newsArticles = newsService.getNewsArticles();
		NewsArticle newNewsArticle = new NewsArticle();
		model.addAttribute("newsArticles", newsArticles);
		model.addAttribute("newNewsArticle", newNewsArticle);
		populateLanguagesModel(model);

		return NEWS_PAGE;
	}

	@PostMapping(value = NEWS_URI, params = "formOpName")
	public String formOperation(
			NewsArticle newsArticle,
			@RequestParam(value = "formOpName", required = true) String formOpName,
			Model model) {

		if (StringUtils.equals(formOpName, "save")) {

			newsService.saveNewsArticle(newsArticle);

			return REDIRECT_PREF + NEWS_URI;

		} else if (StringUtils.equals(formOpName, "delete")) {

			Long newsArticleId = newsArticle.getId();
			newsService.deleteNewsArticle(newsArticleId);

			return REDIRECT_PREF + NEWS_URI;
		}

		return REDIRECT_PREF + NEWS_URI;
	}

	private void populateLanguagesModel(Model model) {

		List<Classifier> allLanguages = commonDataService.getLanguages();
		List<Classifier> supportedLanguages = allLanguages.stream()
				.filter(classifier -> ArrayUtils.contains(SUPPORTED_LANGUAGE_CODES, classifier.getCode()))
				.collect(Collectors.toList());
		model.addAttribute("supportedLanguages", supportedLanguages);
	}
}
