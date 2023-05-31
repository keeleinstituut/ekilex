package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.WordEtymPOC;
import eki.ekilex.data.WordEtymPOCRel;
import eki.ekilex.data.WordEtymPOCTuple;
import eki.ekilex.service.db.WordEtymDbService;

@Component
public class WordEtymService {

	@Autowired
	private WordEtymDbService wordEtymDbService;

	public WordEtymPOC getWordEtym(Long wordId) {

		List<WordEtymPOCTuple> wordEtymTuples = wordEtymDbService.getWordEtymTuples(wordId);

		Map<Long, WordEtymPOCTuple> wordEtymTupleMap = wordEtymTuples.stream().collect(Collectors.groupingBy(WordEtymPOCTuple::getWordEtymWordId)).entrySet()
				.stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().stream().distinct().collect(Collectors.toList()).get(0)));

		WordEtymPOCTuple headwordEtymTuple = wordEtymTupleMap.get(wordId);
		WordEtymPOC wordEtym = composeEtymTree(headwordEtymTuple, wordEtymTupleMap);

		return wordEtym;
	}

	private WordEtymPOC composeEtymTree(WordEtymPOCTuple tuple, Map<Long, WordEtymPOCTuple> wordEtymTupleMap) {

		WordEtymPOC wordEtymLevel = composeEtymLevel(tuple, tuple.isWordEtymIsQuestionable(), false, tuple.getWordEtymComment());
		composeEtymTree(wordEtymLevel, tuple.getWordEtymRelations(), wordEtymTupleMap);
		return wordEtymLevel;
	}

	private WordEtymPOC composeEtymLevel(WordEtymPOCTuple tuple, boolean questionable, boolean compound, String comment) {

		WordEtymPOC wordEtymLevel = new WordEtymPOC();
		wordEtymLevel.setWordId(tuple.getWordEtymWordId());
		wordEtymLevel.setWord(tuple.getWordEtymWord());
		wordEtymLevel.setLang(tuple.getWordEtymWordLang());
		wordEtymLevel.setEtymologyTypeCode(tuple.getEtymologyTypeCode());
		wordEtymLevel.setEtymYear(tuple.getEtymologyYear());
		wordEtymLevel.setQuestionable(questionable);
		wordEtymLevel.setCompound(compound);
		wordEtymLevel.setComment(comment);
		return wordEtymLevel;
	}

	private void composeEtymTree(WordEtymPOC wordEtymLevel, List<WordEtymPOCRel> wordEtymRelations, Map<Long, WordEtymPOCTuple> wordEtymTupleMap) {

		if (CollectionUtils.isEmpty(wordEtymRelations)) {
			return;
		}
		wordEtymLevel.setTree(new ArrayList<>());

		for (WordEtymPOCRel relation : wordEtymRelations) {
			Long relatedWordId = relation.getRelatedWordId();
			Long etymLevelWordId = wordEtymLevel.getWordId();
			if (relatedWordId.equals(etymLevelWordId)) {
				continue;
			}
			WordEtymPOCTuple relWordEtymTuple = wordEtymTupleMap.get(relatedWordId);
			WordEtymPOC relWordEtymLevel = composeEtymLevel(relWordEtymTuple, relation.isQuestionable(), relation.isCompound(), relation.getComment());
			wordEtymLevel.getTree().add(relWordEtymLevel);
			composeEtymTree(relWordEtymLevel, relWordEtymTuple.getWordEtymRelations(), wordEtymTupleMap);
		}
	}
}
