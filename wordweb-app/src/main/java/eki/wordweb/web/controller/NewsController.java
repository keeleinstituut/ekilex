package eki.wordweb.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.NewsArticle;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class NewsController extends AbstractController {

	@GetMapping(NEWS_URI)
	public String news(Model model) {

		List<NewsArticle> newsArticles = commonDataService.getWordwebNewsArticles();
		if (CollectionUtils.isNotEmpty(newsArticles)) {
			model.addAttribute("newsArticles", newsArticles);
		}
		populateCommonModel(model);

		return NEWS_PAGE;
	}

	@PostMapping(NEWS_ACCEPT_URI)
	@ResponseBody
	public String newsAccept(HttpServletRequest request, HttpServletResponse response) {

		NewsArticle latestWordwebNewsArticle = commonDataService.getLatestWordwebNewsArticle();
		if (latestWordwebNewsArticle != null) {

			Long newsArticleId = latestWordwebNewsArticle.getNewsArticleId();
			deleteCookies(request, response, COOKIE_NAME_NEWS_ID);
			setCookie(response, COOKIE_NAME_NEWS_ID, String.valueOf(newsArticleId));
		}

		return NOTHING;
	}
}
