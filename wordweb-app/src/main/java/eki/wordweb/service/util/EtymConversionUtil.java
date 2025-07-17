package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.SourceLink;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordEtymLevel;
import eki.wordweb.data.WordEtymRelation;
import eki.wordweb.data.WordEtymTuple;

@Component
public class EtymConversionUtil {

	@Autowired
	private ClassifierUtil classifierUtil;

	public void composeWordEtymology(Word word, List<WordEtymTuple> wordEtymTuples, String displayLang) {

		if (CollectionUtils.isEmpty(wordEtymTuples)) {
			return;
		}
		wordEtymTuples.forEach(tuple -> {
			classifierUtil.applyClassifiers(tuple, displayLang);
		});

		composeWordEtymTree(word, wordEtymTuples);
	}

	private void composeWordEtymTree(Word word, List<WordEtymTuple> wordEtymTuples) {

		Map<Long, WordEtymTuple> wordEtymTupleMap = wordEtymTuples.stream()
				.collect(Collectors.groupingBy(WordEtymTuple::getWordEtymWordId))
				.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> entry.getKey(),
						entry -> entry.getValue().stream()
								.distinct()
								.collect(Collectors.toList())
								.get(0)));

		Long wordId = word.getWordId();
		WordEtymTuple headwordEtymTuple = wordEtymTupleMap.get(wordId);
		WordEtymLevel headwordEtymLevel = composeEtymTree(headwordEtymTuple, wordEtymTupleMap);
		word.setWordEtymologyTree(headwordEtymLevel);
	}

	private WordEtymLevel composeEtymTree(WordEtymTuple tuple, Map<Long, WordEtymTuple> wordEtymTupleMap) {

		WordEtymLevel wordEtymLevel = composeEtymLevel(tuple, tuple.isWordEtymIsQuestionable(), false, tuple.getWordEtymComment());
		composeEtymTree(wordEtymLevel, tuple.getWordEtymRelations(), wordEtymTupleMap);
		return wordEtymLevel;
	}

	private void composeEtymTree(
			WordEtymLevel wordEtymLevel, List<WordEtymRelation> wordEtymRelations,
			Map<Long, WordEtymTuple> wordEtymTupleMap) {

		if (CollectionUtils.isEmpty(wordEtymRelations)) {
			return;
		}
		wordEtymLevel.setTree(new ArrayList<>());

		for (WordEtymRelation relation : wordEtymRelations) {

			Long relatedWordId = relation.getRelatedWordId();
			Long etymLevelWordId = wordEtymLevel.getWordId();
			if (relatedWordId.equals(etymLevelWordId)) {
				continue;
			}
			WordEtymTuple relWordEtymTuple = wordEtymTupleMap.get(relatedWordId);
			if (relWordEtymTuple == null) {
				continue;
			}
			WordEtymLevel relWordEtymLevel = composeEtymLevel(relWordEtymTuple, relation.isQuestionable(), relation.isCompound(), relation.getComment());
			wordEtymLevel.getTree().add(relWordEtymLevel);
			composeEtymTree(relWordEtymLevel, relWordEtymTuple.getWordEtymRelations(), wordEtymTupleMap);
		}
	}

	private WordEtymLevel composeEtymLevel(WordEtymTuple tuple, boolean questionable, boolean compound, String comment) {

		WordEtymLevel wordEtymLevel = new WordEtymLevel();
		wordEtymLevel.setWordId(tuple.getWordEtymWordId());
		wordEtymLevel.setWordValue(tuple.getWordEtymWordValue());
		wordEtymLevel.setLang(tuple.getWordEtymWordLang());
		wordEtymLevel.setLanguage(tuple.getWordEtymWordLanguage());
		wordEtymLevel.setEtymologyTypeCode(tuple.getEtymologyTypeCode());
		wordEtymLevel.setEtymologyType(tuple.getEtymologyType());
		wordEtymLevel.setEtymYear(tuple.getEtymologyYear());
		wordEtymLevel.setQuestionable(questionable);
		wordEtymLevel.setCompound(compound);
		wordEtymLevel.setComment(comment);
		wordEtymLevel.setMeaningWordValues(tuple.getWordEtymMeaningWordValues());
		List<SourceLink> sourceLinks = tuple.getSourceLinks();
		if (CollectionUtils.isNotEmpty(sourceLinks)) {
			List<String> sourceLinkValues = sourceLinks.stream()
					.map(SourceLink::getSourceValue)
					.collect(Collectors.toList());
			wordEtymLevel.setSourceLinkValues(sourceLinkValues);
		}
		return wordEtymLevel;
	}
}
