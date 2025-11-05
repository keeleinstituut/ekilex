package eki.ekilex.constant;

import static java.util.Arrays.asList;

import java.util.List;

public enum SearchEntity {

	HEADWORD(
			SearchKey.VALUE,
			SearchKey.ID,
			SearchKey.COMMA_SEPARATED_IDS,
			SearchKey.LANGUAGE_DIRECT,
			SearchKey.WORD_STATUS,
			SearchKey.WORD_TYPE,
			SearchKey.DISPLAY_MORPH,
			SearchKey.ASPECT,
			SearchKey.VOCAL_FORM,
			SearchKey.MORPHOPHONO_FORM,
			SearchKey.REG_YEAR,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE,
			SearchKey.SECONDARY_MEANING_WORD,
			SearchKey.PUBLICITY,
			SearchKey.PUBLISHING_TARGET,
			SearchKey.LEXEME_GRAMMAR,
			SearchKey.LEXEME_GOVERNMENT,
			SearchKey.LEXEME_POS,
			SearchKey.LEXEME_REGISTER,
			SearchKey.LEXEME_DERIV,
			SearchKey.LEXEME_VALUE_STATE,
			SearchKey.LEXEME_PROFICIENCY_LEVEL,
			SearchKey.LEXEME_RELATION,
			SearchKey.LEXEME_NOTE,
			SearchKey.LEXEME_ATTRIBUTE_NAME,
			SearchKey.WORD_ATTRIBUTE_NAME,
			SearchKey.ATTRIBUTE_VALUE,
			SearchKey.WORD_RELATION,
			SearchKey.WORD_FORUM,
			SearchKey.CREATED_OR_UPDATED_BY,
			SearchKey.UPDATED_ON,
			SearchKey.CREATED_ON,
			SearchKey.LAST_UPDATE_ON,
			SearchKey.FREQUENCY,
			SearchKey.RANK),
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
			SearchKey.PUBLISHING_TARGET,
			SearchKey.LEXEME_GRAMMAR,
			SearchKey.LEXEME_GOVERNMENT,
			SearchKey.LEXEME_POS,
			SearchKey.LEXEME_REGISTER,
			SearchKey.LEXEME_VALUE_STATE,
			SearchKey.CREATED_OR_UPDATED_BY,
			SearchKey.UPDATED_ON,
			SearchKey.CREATED_ON),
	TERM(
			SearchKey.VALUE,
			SearchKey.ID,
			SearchKey.COMMA_SEPARATED_IDS,
			SearchKey.LANGUAGE_INDIRECT,
			SearchKey.WORD_STATUS,
			SearchKey.WORD_TYPE,
			SearchKey.WORD_FORUM,
			SearchKey.LEXEME_REGISTER,
			SearchKey.LEXEME_VALUE_STATE,
			SearchKey.LEXEME_NOTE,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE,
			SearchKey.PUBLICITY,
			SearchKey.TERM_ATTRIBUTE_NAME,
			SearchKey.LEXEME_ATTRIBUTE_NAME,
			SearchKey.ATTRIBUTE_VALUE,
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
			SearchKey.PUBLISHING_TARGET,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE,
			SearchKey.DEFINITION_NOTE),
	USAGE(
			SearchKey.VALUE_AND_EXISTS,
			SearchKey.LANGUAGE_INDIRECT,
			SearchKey.PUBLICITY,
			SearchKey.PUBLISHING_TARGET,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE),
	MEANING(
			SearchKey.DOMAIN,
			SearchKey.ID,
			SearchKey.COMMA_SEPARATED_IDS,
			SearchKey.MEANING_ATTRIBUTE_NAME,
			SearchKey.ATTRIBUTE_VALUE,
			SearchKey.MEANING_RELATION,
			SearchKey.SEMANTIC_TYPE,
			SearchKey.MEANING_NOTE,
			SearchKey.MEANING_FORUM),
	CONCEPT(
			SearchKey.DOMAIN,
			SearchKey.ID,
			SearchKey.COMMA_SEPARATED_IDS,
			SearchKey.CONCEPT_ATTRIBUTE_NAME,
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
	EKI_RECOMMENDATION(
			SearchKey.VALUE_AND_EXISTS,
			SearchKey.UPDATED_ON),
	PUBLISHING(
			SearchKey.PUBLISHING_LEXEME,
			SearchKey.PUBLISHING_GRAMMAR,
			SearchKey.PUBLISHING_GOVERNMENT,
			SearchKey.PUBLISHING_MEANING_MEDIA),
	CLUELESS(
			SearchKey.VALUE,
			SearchKey.SOURCE_REF,
			SearchKey.SOURCE_ID),
	SOURCE(
			SearchKey.SOURCE_NAME,
			SearchKey.SOURCE_VALUE,
			SearchKey.SOURCE_ID,
			SearchKey.SOURCE_COMMENT,
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
		return asList(HEADWORD, WORD, TAG, FORM, MEANING, DEFINITION, USAGE, NOTE, EKI_RECOMMENDATION, PUBLISHING, CLUELESS);
	}

	public static List<SearchEntity> getTermEntities() {
		return asList(TERM, CONCEPT, TAG, DEFINITION, USAGE, NOTE, EKI_RECOMMENDATION, CLUELESS);
	}

	public static List<SearchEntity> getSourceEntities() {
		return asList(SOURCE);
	}
}
