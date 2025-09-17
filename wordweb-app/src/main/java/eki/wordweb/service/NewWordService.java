package eki.wordweb.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.wordweb.data.NewWord;
import eki.wordweb.data.NewWordYear;
import eki.wordweb.service.db.CommonDataDbService;
import eki.wordweb.service.util.WordConversionUtil;

@Component
public class NewWordService {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private WordConversionUtil wordConversionUtil;

	@Transactional
	public List<NewWordYear> getNewWordYears() {

		List<NewWord> newWords = commonDataDbService.getNewWords();
		wordConversionUtil.setWordTypeFlags(newWords);
		List<NewWordYear> newWordYears = newWords.stream()
				.collect(Collectors.groupingBy(NewWord::getRegYear))
				.entrySet().stream()
				.map(entry -> new NewWordYear(entry.getKey(), entry.getValue()))
				.sorted(Comparator.comparing(NewWordYear::getRegYear).reversed())
				.collect(Collectors.toList());
		return newWordYears;
	}
}
