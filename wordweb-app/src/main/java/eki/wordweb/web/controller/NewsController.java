package eki.wordweb.web.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
	public String news(HttpServletRequest request, HttpServletResponse response, Model model) {

		List<NewsArticle> newsArticles = commonDataService.getWordwebNewsArticles();
		if (CollectionUtils.isNotEmpty(newsArticles)) {
			NewsArticle latestWordwebNewsArticle = newsArticles.get(0);
			handleNewsCookie(latestWordwebNewsArticle, request, response);
			model.addAttribute("newsArticles", newsArticles);
		}
		populateCommonModel(model);

		return NEWS_PAGE;
	}

	@PostMapping(NEWS_ACCEPT_URI)
	@ResponseBody
	public String newsAccept(HttpServletRequest request, HttpServletResponse response) {

		NewsArticle latestWordwebNewsArticle = commonDataService.getLatestWordwebNewsArticle();
		handleNewsCookie(latestWordwebNewsArticle, request, response);

		return NOTHING;
	}

	private void handleNewsCookie(NewsArticle newsArticle, HttpServletRequest request, HttpServletResponse response) {

		if (newsArticle == null) {
			return;
		}
		Long newsArticleId = newsArticle.getNewsArticleId();
		Cookie[] cookies = request.getCookies();
		boolean isNewsCookieAlreadySet = false;
		if (ArrayUtils.isNotEmpty(cookies)) {
			isNewsCookieAlreadySet = Arrays.stream(cookies)
					.anyMatch(cookie -> {
						String cookieName = cookie.getName();
						String cookieValue = cookie.getValue();
						return StringUtils.equals(COOKIE_NAME_NEWS_ID, cookieName)
								&& StringUtils.equals(cookieValue, newsArticleId.toString());
					});
		}
		if (isNewsCookieAlreadySet) {
			return;
		}
		deleteCookies(request, response, COOKIE_NAME_NEWS_ID);
		setCookie(response, COOKIE_NAME_NEWS_ID, String.valueOf(newsArticleId));
	}
}
