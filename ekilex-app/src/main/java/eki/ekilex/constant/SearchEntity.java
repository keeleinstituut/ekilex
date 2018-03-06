package eki.ekilex.constant;

import java.util.List;

import static java.util.Arrays.asList;

public enum SearchEntity {
	WORD(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE, SearchKey.SOURCE_CODE, SearchKey.SOURCE_NAME}),
	FORM(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE}),
	DEFINITION(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE, SearchKey.SOURCE_CODE, SearchKey.SOURCE_NAME}),
	USAGE(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE}),
	MEANING(new SearchKey[] {SearchKey.DOMAIN}),
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
		return asList(WORD, FORM, DEFINITION, USAGE, MEANING);
	}

	public static List<SearchEntity> termEntities() {
		return asList(WORD, FORM, DEFINITION, USAGE, CONCEPT_ID);
	}
}
