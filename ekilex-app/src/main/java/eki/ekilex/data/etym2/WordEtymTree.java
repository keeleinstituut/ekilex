package eki.ekilex.data.etym2;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class WordEtymTree extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private WordEtymWord headword;

	private List<WordEtymLevel> levels;

	public WordEtymWord getHeadword() {
		return headword;
	}

	public void setHeadword(WordEtymWord headword) {
		this.headword = headword;
	}

	public List<WordEtymLevel> getLevels() {
		return levels;
	}

	public void setLevels(List<WordEtymLevel> levels) {
		this.levels = levels;
	}
}
