package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.Word;
import eki.wordweb.data.WordEtymLevel;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.type.TypeSourceLink;
import eki.wordweb.data.type.TypeWordEtymRelation;

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

		List<TypeSourceLink> wordEtymSourceLinks = word.getWordEtymSourceLinks();

		Map<Long, List<TypeSourceLink>> wordEtymSourceLinkMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(wordEtymSourceLinks)) {
			wordEtymSourceLinkMap = wordEtymSourceLinks.stream().collect(Collectors.groupingBy(TypeSourceLink::getOwnerId));
		}

		composeWordEtymTree(word, wordEtymTuples, wordEtymSourceLinkMap);
	}

	private void composeWordEtymTree(Word word, List<WordEtymTuple> wordEtymTuples, Map<Long, List<TypeSourceLink>> wordEtymSourceLinkMap) {

		Map<Long, WordEtymTuple> wordEtymTupleMap = wordEtymTuples.stream().collect(Collectors.groupingBy(WordEtymTuple::getWordEtymWordId))
				.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().stream().distinct().collect(Collectors.toList()).get(0)));

		WordEtymTuple headwordEtymTuple = wordEtymTupleMap.get(word.getWordId());
		WordEtymLevel headwordEtymLevel = composeEtymTree(headwordEtymTuple, wordEtymTupleMap, wordEtymSourceLinkMap);
		word.setWordEtymologyTree(headwordEtymLevel);
	}

	private WordEtymLevel composeEtymTree(WordEtymTuple tuple, Map<Long, WordEtymTuple> wordEtymTupleMap, Map<Long, List<TypeSourceLink>> wordEtymSourceLinkMap) {

		WordEtymLevel wordEtymLevel = composeEtymLevel(tuple, tuple.isWordEtymIsQuestionable(), false, tuple.getWordEtymComment(), wordEtymSourceLinkMap);
		composeEtymTree(wordEtymLevel, tuple.getWordEtymRelations(), wordEtymTupleMap, wordEtymSourceLinkMap);
		return wordEtymLevel;
	}

	private void composeEtymTree(
			WordEtymLevel wordEtymLevel, List<TypeWordEtymRelation> wordEtymRelations,
			Map<Long, WordEtymTuple> wordEtymTupleMap, Map<Long, List<TypeSourceLink>> wordEtymSourceLinkMap) {

		if (CollectionUtils.isEmpty(wordEtymRelations)) {
			return;
		}
		wordEtymLevel.setTree(new ArrayList<>());

		for (TypeWordEtymRelation relation : wordEtymRelations) {
			Long relatedWordId = relation.getRelatedWordId();
			Long etymLevelWordId = wordEtymLevel.getWordId();
			if (relatedWordId.equals(etymLevelWordId)) {
				continue;
			}
			WordEtymTuple relWordEtymTuple = wordEtymTupleMap.get(relatedWordId);
			WordEtymLevel relWordEtymLevel = composeEtymLevel(relWordEtymTuple, relation.isQuestionable(), relation.isCompound(), relation.getComment(), wordEtymSourceLinkMap);
			wordEtymLevel.getTree().add(relWordEtymLevel);
			composeEtymTree(relWordEtymLevel, relWordEtymTuple.getWordEtymRelations(), wordEtymTupleMap, wordEtymSourceLinkMap);
		}
	}

	private WordEtymLevel composeEtymLevel(WordEtymTuple tuple, boolean questionable, boolean compound, String comment, Map<Long, List<TypeSourceLink>> wordEtymSourceLinkMap) {
		WordEtymLevel wordEtymLevel = new WordEtymLevel();
		wordEtymLevel.setWordId(tuple.getWordEtymWordId());
		wordEtymLevel.setWord(tuple.getWordEtymWord());
		wordEtymLevel.setLang(tuple.getWordEtymWordLang());
		wordEtymLevel.setLanguage(tuple.getWordEtymWordLanguage());
		wordEtymLevel.setMeaningWords(tuple.getWordEtymWordMeaningWords());
		wordEtymLevel.setEtymologyTypeCode(tuple.getEtymologyTypeCode());
		wordEtymLevel.setEtymologyType(tuple.getEtymologyType());
		wordEtymLevel.setEtymYear(tuple.getEtymologyYear());
		wordEtymLevel.setQuestionable(questionable);
		wordEtymLevel.setCompound(compound);
		wordEtymLevel.setComment(comment);
		List<TypeSourceLink> sourceLinks = wordEtymSourceLinkMap.get(tuple.getWordEtymId());
		if (CollectionUtils.isNotEmpty(sourceLinks)) {
			List<String> sourceLinkValues = sourceLinks.stream().map(TypeSourceLink::getValue).collect(Collectors.toList());
			wordEtymLevel.setSourceLinkValues(sourceLinkValues);
		}
		return wordEtymLevel;
	}
}
