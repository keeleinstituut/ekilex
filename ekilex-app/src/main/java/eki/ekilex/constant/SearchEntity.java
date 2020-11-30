package eki.ekilex.constant;

import java.util.List;

import static java.util.Arrays.asList;

public enum SearchEntity {

	HEADWORD(new SearchKey[] {
			SearchKey.VALUE, SearchKey.ID, SearchKey.LANGUAGE, SearchKey.WORD_TYPE, SearchKey.ASPECT, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME,
			SearchKey.SECONDARY_MEANING_WORD, SearchKey.PUBLICITY, SearchKey.LEXEME_GRAMMAR, SearchKey.LEXEME_GOVERNMENT, SearchKey.LEXEME_POS,
			SearchKey.LEXEME_REGISTER, SearchKey.COMPLEXITY, SearchKey.OD_RECOMMENDATION,
			SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON, SearchKey.FREQUENCY, SearchKey.RANK}),
	WORD(new SearchKey[] {
			SearchKey.VALUE, SearchKey.ID, SearchKey.LANGUAGE, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME,
			SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON}),
	TERM(new SearchKey[] {
			SearchKey.VALUE, SearchKey.ID, SearchKey.LANGUAGE, SearchKey.WORD_TYPE, SearchKey.LEXEME_REGISTER, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME,
			SearchKey.PUBLICITY, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON}),
	FORM(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE, SearchKey.FREQUENCY, SearchKey.RANK}),
	DEFINITION(new SearchKey[] {SearchKey.VALUE_AND_EXISTS, SearchKey.LANGUAGE, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME}),
	USAGE(new SearchKey[] {SearchKey.VALUE_AND_EXISTS, SearchKey.LANGUAGE, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME}),
	MEANING(new SearchKey[] {SearchKey.DOMAIN, SearchKey.ID, SearchKey.RELATION_TYPE, SearchKey.SEMANTIC_TYPE}),
	CONCEPT(new SearchKey[] {
			SearchKey.DOMAIN, SearchKey.ID, SearchKey.CONCEPT_ID, SearchKey.ATTRIBUTE,
			SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON}),
	NOTE(new SearchKey[] {SearchKey.VALUE_AND_EXISTS, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME}),
	TAG(new SearchKey[] {SearchKey.TAG_NAME, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.CREATED_OR_UPDATED_ON}),
	CLUELESS(new SearchKey[] {SearchKey.VALUE, SearchKey.SOURCE_REF, SearchKey.SOURCE_ID}),
	SOURCE(new SearchKey[] {SearchKey.VALUE, SearchKey.DATASET_USAGE, SearchKey.CREATED_BY, SearchKey.CREATED_ON, SearchKey.UPDATED_BY, SearchKey.UPDATED_ON})
	;

	private SearchKey[] keys;

	SearchEntity(SearchKey[] keys) {
		this.keys = keys;
	}

	public SearchKey[] getKeys() {
		return keys;
	}

	public static List<SearchEntity> getLexEntities() {
		return asList(HEADWORD, WORD, TAG, FORM, MEANING, DEFINITION, USAGE, NOTE, CLUELESS);
	}

	public static List<SearchEntity> getTermEntities() {
		return asList(TERM, CONCEPT, TAG, DEFINITION, USAGE, NOTE, CLUELESS);
	}

	public static List<SearchEntity> getSourceEntities() {
		return asList(SOURCE);
	}
}
