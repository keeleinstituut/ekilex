package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.data.IntWrap;
import eki.ekilex.data.WordEtymNode;
import eki.ekilex.data.WordEtymNodeLink;
import eki.ekilex.data.WordEtymNodeTuple;
import eki.ekilex.data.WordEtymRel;
import eki.ekilex.data.WordEtymTree;
import eki.ekilex.service.db.WordEtymDbService;

@Component
public class WordEtymService {

	@Autowired
	private WordEtymDbService wordEtymDbService;

	@Transactional
	public WordEtymTree getWordEtymTree(Long wordId) {

		List<WordEtymNodeTuple> wordEtymTuples = wordEtymDbService.getWordEtymTuples(wordId);
		if (CollectionUtils.isEmpty(wordEtymTuples)) {
			return null;
		}

		Map<Long, WordEtymNodeTuple> wordEtymTupleMap = wordEtymTuples.stream()
				.collect(Collectors.toMap(WordEtymNodeTuple::getWordEtymWordId, row -> row));

		WordEtymTree wordEtymTree = composeEtymTree(wordId, wordEtymTupleMap);

		return wordEtymTree;
	}

	private WordEtymTree composeEtymTree(Long wordId, Map<Long, WordEtymNodeTuple> wordEtymTupleMap) {

		int topLevel = 1;
		WordEtymNodeTuple tuple = wordEtymTupleMap.get(wordId);
		WordEtymNode root = createEtymNode(tuple, topLevel);

		List<WordEtymNode> nodes = new ArrayList<>();
		List<Long> processedWordIds = new ArrayList<>();
		List<WordEtymNodeLink> links = new ArrayList<>();

		composeEtymNodes(root, nodes, tuple.getRelations(), wordEtymTupleMap, processedWordIds);
		composeEtymLinks(wordId, links, tuple.getRelations(), wordEtymTupleMap);

		WordEtymTree wordEtymTree = new WordEtymTree();
		wordEtymTree.setWordId(wordId);
		wordEtymTree.setWordValue(root.getWordValue());
		wordEtymTree.setRoot(root);
		wordEtymTree.setNodes(nodes);
		wordEtymTree.setLinks(links);
		applyMaxLevel(wordEtymTree);

		return wordEtymTree;
	}

	private void composeEtymNodes(
			WordEtymNode node,
			List<WordEtymNode> nodes,
			List<WordEtymRel> relations,
			Map<Long, WordEtymNodeTuple> wordEtymTupleMap,
			List<Long> processedWordIds) {

		int level = node.getLevel() + 1;
		Long nodeLevelWordId = node.getWordId();
		processedWordIds.add(nodeLevelWordId);
		nodes.add(node);

		if (CollectionUtils.isEmpty(relations)) {
			return;
		}

		for (WordEtymRel relation : relations) {

			Long relatedWordId = relation.getRelatedWordId();
			if (processedWordIds.contains(relatedWordId)) {
				continue;
			}
			WordEtymNodeTuple tuple = wordEtymTupleMap.get(relatedWordId);
			WordEtymNode child = createEtymNode(tuple, level);
			node.getChildren().add(child);
			composeEtymNodes(child, nodes, tuple.getRelations(), wordEtymTupleMap, processedWordIds);
		}
	}

	private void composeEtymLinks(
			Long wordId,
			List<WordEtymNodeLink> links,
			List<WordEtymRel> relations,
			Map<Long, WordEtymNodeTuple> wordEtymTupleMap) {

		if (CollectionUtils.isEmpty(relations)) {
			return;
		}

		for (WordEtymRel relation : relations) {

			WordEtymNodeLink link = createEtymLink(wordId, relation, wordEtymTupleMap);
			links.add(link);
			Long relatedWordId = relation.getRelatedWordId();
			WordEtymNodeTuple tuple = wordEtymTupleMap.get(relatedWordId);
			composeEtymLinks(relatedWordId, links, tuple.getRelations(), wordEtymTupleMap);
		}
	}

	private WordEtymNode createEtymNode(WordEtymNodeTuple tuple, int level) {

		WordEtymNode wordEtymLevel = new WordEtymNode();
		wordEtymLevel.setWordId(tuple.getWordEtymWordId());
		wordEtymLevel.setWordValue(tuple.getWordEtymWordValue());
		wordEtymLevel.setWordLang(tuple.getWordEtymWordLang());
		wordEtymLevel.setEtymologyTypeCode(tuple.getEtymologyTypeCode());
		wordEtymLevel.setEtymologyYear(tuple.getEtymologyYear());
		wordEtymLevel.setQuestionable(tuple.isQuestionable());
		wordEtymLevel.setCompound(false);
		wordEtymLevel.setCommentPrese(tuple.getCommentPrese());
		wordEtymLevel.setSourceLinks(tuple.getSourceLinks());
		wordEtymLevel.setMeaningWords(tuple.getMeaningWords());
		wordEtymLevel.setChildren(new ArrayList<>());
		wordEtymLevel.setLevel(level);

		return wordEtymLevel;
	}

	private WordEtymNodeLink createEtymLink(Long wordId, WordEtymRel relation, Map<Long, WordEtymNodeTuple> wordEtymTupleMap) {

		WordEtymNodeTuple sourceWordTuple = wordEtymTupleMap.get(wordId);
		WordEtymNodeTuple targetWordTuple = wordEtymTupleMap.get(relation.getRelatedWordId());

		WordEtymNodeLink link = new WordEtymNodeLink();
		link.setWordEtymRelId(relation.getWordEtymRelId());
		link.setSourceWordId(wordId);
		link.setSourceWordValue(sourceWordTuple.getWordEtymWordValue());
		link.setTargetWordId(relation.getRelatedWordId());
		link.setTargetWordValue(targetWordTuple.getWordEtymWordValue());
		link.setCommentPrese(relation.getCommentPrese());
		link.setQuestionable(relation.isQuestionable());
		link.setCompound(relation.isCompound());

		return link;
	}

	private void applyMaxLevel(WordEtymTree wordEtymTree) {

		WordEtymNode wordEtymRoot = wordEtymTree.getRoot();
		IntWrap totalMaxLevel = new IntWrap();
		collectMaxLevels(wordEtymRoot, totalMaxLevel);
		wordEtymTree.setMaxLevel(totalMaxLevel.getValue());
	}

	private void collectMaxLevels(WordEtymNode wordEtymNode, IntWrap totalMaxLevel) {

		int level = wordEtymNode.getLevel();
		totalMaxLevel.setMax(level);
		List<WordEtymNode> wordEtymChildren = wordEtymNode.getChildren();
		if (CollectionUtils.isEmpty(wordEtymChildren)) {
			return;
		}
		for (WordEtymNode wordEtymChild : wordEtymChildren) {
			collectMaxLevels(wordEtymChild, totalMaxLevel);
		}
	}
}
