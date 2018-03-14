package eki.wordweb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.Word;
import eki.wordweb.service.db.LexSearchDbService;

@Component
public class LexSearchService {

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Transactional
	public List<Word> findWords(String searchFilter) {

		List<Word> words = lexSearchDbService.findWords(searchFilter).into(Word.class);
		return words;
	}
}
