package eki.ekilex.constant;

import java.util.List;

import static java.util.Arrays.asList;

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

	public static List<SearchEntity> lexEntities() {
		return asList(WORD, FORM, DEFINITION, USAGE);
	}

	public static List<SearchEntity> termEntities() {
		return asList(WORD, FORM, DEFINITION, USAGE, CONCEPT_ID);
	}
}
