package eki.ekilex.data;

import java.util.List;

public class SynonymLangGroup extends LangGroup {

	private static final long serialVersionUID = 1L;

	private List<Synonym> synonyms;

	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}
}
