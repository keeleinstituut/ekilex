package eki.ekilex.constant;

import java.util.List;

import static java.util.Arrays.asList;

public enum SearchEntity {

	HEADWORD(
			SearchKey.VALUE,
			SearchKey.ID,
			SearchKey.COMMA_SEPARATED_IDS,
			SearchKey.LANGUAGE_DIRECT,
			SearchKey.WORD_TYPE,
			SearchKey.DISPLAY_MORPH,
			SearchKey.ASPECT,
			SearchKey.VOCAL_FORM,
			SearchKey.MORPHOPHONO_FORM,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE,
			SearchKey.SECONDARY_MEANING_WORD,
			SearchKey.PUBLICITY,
			SearchKey.LEXEME_GRAMMAR,
			SearchKey.LEXEME_GOVERNMENT,
			SearchKey.LEXEME_POS,
			SearchKey.LEXEME_REGISTER,
			SearchKey.LEXEME_DERIV,
			SearchKey.LEXEME_VALUE_STATE,
			SearchKey.LEXEME_PROFICIENCY_LEVEL,
			SearchKey.LEXEME_RELATION,
			SearchKey.COMPLEXITY,
			SearchKey.WORD_FORUM,
			SearchKey.LEXEME_NOTE,
			SearchKey.CREATED_OR_UPDATED_BY,
			SearchKey.UPDATED_ON,
			SearchKey.CREATED_ON,
			SearchKey.LAST_UPDATE_ON,
			SearchKey.FREQUENCY,
			SearchKey.RANK,
			SearchKey.WORD_RELATION),
	WORD(
			SearchKey.VALUE,
			SearchKey.ID,
			SearchKey.LANGUAGE_INDIRECT,
			SearchKey.WORD_TYPE,
			SearchKey.ASPECT,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE,
			SearchKey.PUBLICITY,
			SearchKey.LEXEME_GRAMMAR,
			SearchKey.LEXEME_GOVERNMENT,
			SearchKey.LEXEME_POS,
			SearchKey.LEXEME_REGISTER,
			SearchKey.LEXEME_VALUE_STATE,
			SearchKey.COMPLEXITY,
			SearchKey.CREATED_OR_UPDATED_BY,
			SearchKey.UPDATED_ON,
			SearchKey.CREATED_ON),
	TERM(
			SearchKey.VALUE,
			SearchKey.ID,
			SearchKey.COMMA_SEPARATED_IDS,
			SearchKey.LANGUAGE_INDIRECT,
			SearchKey.WORD_TYPE,
			SearchKey.LEXEME_REGISTER,
			SearchKey.LEXEME_VALUE_STATE,
			SearchKey.WORD_FORUM,
			SearchKey.LEXEME_NOTE,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE,
			SearchKey.PUBLICITY,
			SearchKey.WORD_RELATION,
			SearchKey.CREATED_OR_UPDATED_BY,
			SearchKey.LAST_UPDATE_ON,
			SearchKey.UPDATED_ON),
	FORM(
			SearchKey.VALUE,
			SearchKey.LANGUAGE_INDIRECT,
			SearchKey.FREQUENCY,
			SearchKey.RANK),
	DEFINITION(
			SearchKey.VALUE_AND_EXISTS,
			SearchKey.LANGUAGE_INDIRECT,
			SearchKey.PUBLICITY,
			SearchKey.COMPLEXITY,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE,
			SearchKey.DEFINITION_NOTE),
	USAGE(
			SearchKey.VALUE_AND_EXISTS,
			SearchKey.LANGUAGE_INDIRECT,
			SearchKey.PUBLICITY,
			SearchKey.COMPLEXITY,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE),
	MEANING(
			SearchKey.DOMAIN,
			SearchKey.ID,
			SearchKey.COMMA_SEPARATED_IDS,
			SearchKey.MEANING_RELATION,
			SearchKey.SEMANTIC_TYPE,
			SearchKey.MEANING_NOTE,
			SearchKey.MEANING_FORUM),
	CONCEPT(
			SearchKey.DOMAIN,
			SearchKey.ID,
			SearchKey.COMMA_SEPARATED_IDS,
			SearchKey.ATTRIBUTE_NAME,
			SearchKey.ATTRIBUTE_VALUE,
			SearchKey.MEANING_RELATION,
			SearchKey.MEANING_NOTE,
			SearchKey.MEANING_FORUM,
			SearchKey.CREATED_OR_UPDATED_BY,
			SearchKey.UPDATED_ON,
			SearchKey.CREATED_ON,
			SearchKey.LAST_UPDATE_ON,
			SearchKey.MANUAL_UPDATE_ON),
	NOTE(
			SearchKey.VALUE_AND_EXISTS,
			SearchKey.PUBLICITY,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE),
	TAG(
			SearchKey.TAG_NAME,
			SearchKey.CREATED_OR_UPDATED_BY,
			SearchKey.CREATED_OR_UPDATED_ON),
	OD_RECOMMENDATION(
			SearchKey.VALUE_AND_EXISTS,
			SearchKey.UPDATED_ON),
	CLUELESS(
			SearchKey.VALUE,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_ID),
	SOURCE(
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE,
			SearchKey.DATASET_USAGE,
			SearchKey.CREATED_BY,
			SearchKey.CREATED_ON,
			SearchKey.UPDATED_BY,
			SearchKey.UPDATED_ON)
	;

	private SearchKey[] keys;

	SearchEntity(SearchKey... keys) {
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
