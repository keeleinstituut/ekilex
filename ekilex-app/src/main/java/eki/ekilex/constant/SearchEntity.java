package eki.ekilex.constant;

public enum SearchEntity {
	WORD(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE}),
	FORM(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE}),
	DEFINITION(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE}),
	USAGE(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE}),
	CONCEPT_ID(new SearchKey[] {SearchKey.ID})
	;

	private SearchKey[] keys;

	SearchEntity(SearchKey[] keys) {
		this.keys = keys;
	}

	public SearchKey[] getKeys() {
		return keys;
	}
}
