package eki.wordweb.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.wordweb.data.NewWord;
import eki.wordweb.data.NewWordYear;
import eki.wordweb.data.NewsArticle;
import eki.wordweb.data.WordSuggestion;
import eki.wordweb.data.WordSuggestionPage;
import eki.wordweb.service.db.AncillaryDataDbService;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.service.util.WordConversionUtil;

@Component
public class AncillaryDataService {

	private static final int WORD_SUGGESTION_PAGE_SIZE = 20;

	private static final int WORD_SUGGESTION_MAX_PAGE_NUMBERS = 9;

	@Autowired
	private AncillaryDataDbService ancillaryDataDbService;

	@Autowired
	private WordConversionUtil wordConversionUtil;

	@Autowired
	private LanguageContext languageContext;

	@Transactional
	public List<NewWordYear> getNewWordYears() {

		List<NewWord> newWords = ancillaryDataDbService.getNewWords();
		wordConversionUtil.setWordTypeFlags(newWords);
		List<NewWordYear> newWordYears = newWords.stream()
				.collect(Collectors.groupingBy(NewWord::getRegYear))
				.entrySet().stream()
				.map(entry -> new NewWordYear(entry.getKey(), entry.getValue()))
				.sorted(Comparator.comparing(NewWordYear::getRegYear).reversed())
				.collect(Collectors.toList());
		return newWordYears;
	}

	@Transactional
	public NewsArticle getLatestWordwebNewsArticle() {

		String displayLang = languageContext.getIso3DisplayLang();
		NewsArticle newsArticle = ancillaryDataDbService.getLatestWordwebNewsArticle(displayLang);
		if (newsArticle == null) {
			return null;
		}
		String content = newsArticle.getContent();
		String contentCut = StringUtils.substringBefore(content, '.');
		newsArticle.setContentCut(contentCut);
		return newsArticle;
	}

	@Transactional
	public List<NewsArticle> getWordwebNewsArticles() {

		String displayLang = languageContext.getIso3DisplayLang();
		return ancillaryDataDbService.getWordwebNewsArticles(displayLang);
	}

	@Transactional
	public WordSuggestionPage getWordSuggestions(String pageNumStr) {

		int pageNum;
		try {
			pageNum = Integer.parseInt(pageNumStr);
		} catch (NumberFormatException e) {
			pageNum = 1;
		}

		int totalCount = ancillaryDataDbService.getWordSuggestionsCount();
		int totalPages = computeTotalPages(totalCount);
		int currentPage = Math.max(1, Math.min(pageNum, totalPages));
		int startPage = computeStartPage(currentPage, totalPages);
		int endPage = computeEndPage(startPage, totalPages);
		int offset = (currentPage - 1) * WORD_SUGGESTION_PAGE_SIZE;
		boolean showPaging = totalPages > 1;

		List<WordSuggestion> wordSuggestions = ancillaryDataDbService.getWordSuggestions(offset, WORD_SUGGESTION_PAGE_SIZE);

		WordSuggestionPage wordSuggestionPage = new WordSuggestionPage();
		wordSuggestionPage.setWordSuggestions(wordSuggestions);
		wordSuggestionPage.setCurrentPage(currentPage);
		wordSuggestionPage.setTotalPages(totalPages);
		wordSuggestionPage.setTotalCount(totalCount);
		wordSuggestionPage.setStartPage(startPage);
		wordSuggestionPage.setEndPage(endPage);
		wordSuggestionPage.setShowPaging(showPaging);

		return wordSuggestionPage;
	}

	private int computeTotalPages(int totalCount) {

		if (totalCount == 0) {
			return 1;
		}
		double totalPagesDecimal = (double) totalCount / WORD_SUGGESTION_PAGE_SIZE;
		return (int) Math.ceil(totalPagesDecimal);
	}

	private int computeStartPage(int currentPage, int totalPages) {

		if (totalPages <= WORD_SUGGESTION_MAX_PAGE_NUMBERS) {
			return 1;
		}
		return Math.max(1, Math.min(currentPage - WORD_SUGGESTION_MAX_PAGE_NUMBERS / 2, totalPages - WORD_SUGGESTION_MAX_PAGE_NUMBERS + 1));
	}

	private int computeEndPage(int startPage, int totalPages) {

		if (totalPages <= WORD_SUGGESTION_MAX_PAGE_NUMBERS) {
			return totalPages;
		}
		return Math.min(totalPages, startPage + WORD_SUGGESTION_MAX_PAGE_NUMBERS - 1);
	}
}
