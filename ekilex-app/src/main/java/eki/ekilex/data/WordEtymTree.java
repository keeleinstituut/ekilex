package eki.ekilex.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymTree extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long wordId;

	private String wordValue;

	private WordEtymNode root;

	private List<WordEtymNode> nodes;

	private List<WordEtymNodeLink> links;

	private int maxLevel;

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getWordValue() {
		return wordValue;
	}

	public void setWordValue(String wordValue) {
		this.wordValue = wordValue;
	}

	public WordEtymNode getRoot() {
		return root;
	}

	public void setRoot(WordEtymNode root) {
		this.root = root;
	}

	public List<WordEtymNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<WordEtymNode> nodes) {
		this.nodes = nodes;
	}

	public List<WordEtymNodeLink> getLinks() {
		return links;
	}

	public void setLinks(List<WordEtymNodeLink> links) {
		this.links = links;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

}
