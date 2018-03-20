package eki.wordweb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Word;
import eki.wordweb.service.db.LexSearchDbService;
import eki.wordweb.service.util.ConversionUtil;

@Component
public class LexSearchService {

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Transactional
	public List<Word> findWords(String searchFilter) {

		List<Word> words = lexSearchDbService.findWords(searchFilter).into(Word.class);
		return words;
	}

	@Transactional
	public List<Lexeme> findLexemes(Long wordId, String displayLang) {

		List<LexemeMeaningTuple> lexemeMeaningTuples = lexSearchDbService.findLexemeMeaningTuples(wordId).into(LexemeMeaningTuple.class);
		List<Lexeme> lexemes = conversionUtil.composeLexemes(lexemeMeaningTuples, displayLang);
		return lexemes;
	}

}
