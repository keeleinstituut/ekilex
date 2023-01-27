package eki.ekilex.data;

import java.util.List;

public class SynonymLangGroup extends LangGroup {

	private static final long serialVersionUID = 1L;

	private List<Synonym> synonyms;

	private List<InexactSynonym> inexactSynonyms;

	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}

	public List<InexactSynonym> getInexactSynonyms() {
		return inexactSynonyms;
	}

	public void setInexactSynonyms(List<InexactSynonym> inexactSynonyms) {
		this.inexactSynonyms = inexactSynonyms;
	}
}
