package eki.ekilex.constant;

import java.util.List;

import static java.util.Arrays.asList;

public enum SearchEntity {

	HEADWORD(new SearchKey[] {
			SearchKey.VALUE, SearchKey.ID, SearchKey.LANGUAGE, SearchKey.WORD_TYPE, SearchKey.ASPECT, SearchKey.MORPHOPHONO_FORM, SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME, SearchKey.SECONDARY_MEANING_WORD, SearchKey.PUBLICITY, SearchKey.LEXEME_GRAMMAR, SearchKey.LEXEME_GOVERNMENT,
			SearchKey.LEXEME_POS, SearchKey.LEXEME_REGISTER, SearchKey.LEXEME_VALUE_STATE, SearchKey.LEXEME_PROFICIENCY_LEVEL, SearchKey.COMPLEXITY,
			SearchKey.WORD_NOTE, SearchKey.LEXEME_NOTE, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON, SearchKey.LAST_UPDATE_ON,
			SearchKey.FREQUENCY, SearchKey.RANK, SearchKey.WORD_RELATION}),
	WORD(new SearchKey[] {
			SearchKey.VALUE, SearchKey.ID, SearchKey.LANGUAGE, SearchKey.WORD_TYPE, SearchKey.ASPECT, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME,
			SearchKey.PUBLICITY, SearchKey.LEXEME_GRAMMAR, SearchKey.LEXEME_GOVERNMENT, SearchKey.LEXEME_POS,
			SearchKey.LEXEME_REGISTER, SearchKey.LEXEME_VALUE_STATE, SearchKey.COMPLEXITY,
			SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON}),
	TERM(new SearchKey[] {
			SearchKey.VALUE, SearchKey.ID, SearchKey.LANGUAGE, SearchKey.WORD_TYPE, SearchKey.LEXEME_REGISTER, SearchKey.LEXEME_VALUE_STATE, SearchKey.WORD_NOTE,
			SearchKey.LEXEME_NOTE, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME, SearchKey.PUBLICITY, SearchKey.WORD_RELATION, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON}),
	FORM(new SearchKey[] {SearchKey.VALUE, SearchKey.LANGUAGE, SearchKey.FREQUENCY, SearchKey.RANK}),
	DEFINITION(new SearchKey[] {
			SearchKey.VALUE_AND_EXISTS, SearchKey.LANGUAGE, SearchKey.PUBLICITY, SearchKey.COMPLEXITY, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME, SearchKey.DEFINITION_NOTE}),
	USAGE(new SearchKey[] {
			SearchKey.VALUE_AND_EXISTS, SearchKey.LANGUAGE, SearchKey.PUBLICITY, SearchKey.COMPLEXITY, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME}),
	MEANING(new SearchKey[] {SearchKey.DOMAIN, SearchKey.ID, SearchKey.MEANING_RELATION, SearchKey.SEMANTIC_TYPE, SearchKey.MEANING_NOTE}),
	CONCEPT(new SearchKey[] {
			SearchKey.DOMAIN, SearchKey.ID, SearchKey.ATTRIBUTE_NAME, SearchKey.ATTRIBUTE_VALUE, SearchKey.MEANING_RELATION, SearchKey.MEANING_NOTE,
			SearchKey.CREATED_OR_UPDATED_BY, SearchKey.UPDATED_ON, SearchKey.CREATED_ON, SearchKey.LAST_UPDATE_ON, SearchKey.MANUAL_UPDATE_ON}),
	NOTE(new SearchKey[] {SearchKey.VALUE_AND_EXISTS, SearchKey.SOURCE_REF, SearchKey.SOURCE_NAME}),
	TAG(new SearchKey[] {SearchKey.TAG_NAME, SearchKey.CREATED_OR_UPDATED_BY, SearchKey.CREATED_OR_UPDATED_ON}),
	OD_RECOMMENDATION(new SearchKey[] {SearchKey.VALUE_AND_EXISTS, SearchKey.UPDATED_ON}),
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
		return asList(HEADWORD, WORD, TAG, FORM, MEANING, DEFINITION, USAGE, NOTE, OD_RECOMMENDATION, CLUELESS);
	}

	public static List<SearchEntity> getTermEntities() {
		return asList(TERM, CONCEPT, TAG, DEFINITION, USAGE, NOTE, OD_RECOMMENDATION, CLUELESS);
	}

	public static List<SearchEntity> getSourceEntities() {
		return asList(SOURCE);
	}
}
