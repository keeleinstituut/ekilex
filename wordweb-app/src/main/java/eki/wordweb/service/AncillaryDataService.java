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
import eki.wordweb.service.db.AncillaryDataDbService;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.service.util.WordConversionUtil;

@Component
public class AncillaryDataService {

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

		String displayLang = languageContext.getDisplayLang();
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

		String displayLang = languageContext.getDisplayLang();
		return ancillaryDataDbService.getWordwebNewsArticles(displayLang);
	}

	@Transactional
	public List<WordSuggestion> getWordSuggestions() {
		return ancillaryDataDbService.getWordSuggestions();
	}
}
