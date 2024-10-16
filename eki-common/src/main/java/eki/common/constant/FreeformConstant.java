package eki.common.constant;

public interface FreeformConstant {

	String TECHNICAL_FF_TYPE_CODES_FILE_PATH = "txt/technical-freeform-type-codes.txt";

	String GOVERNMENT_CODE = "GOVERNMENT";

	String GRAMMAR_CODE = "GRAMMAR";

	String LEARNER_COMMENT_CODE = "LEARNER_COMMENT";

	String SEMANTIC_TYPE_CODE = "SEMANTIC_TYPE";

	String MEDIA_FILE_CODE = "MEDIA_FILE";

	String OD_WORD_RECOMMENDATION_CODE = "OD_WORD_RECOMMENDATION";

	String CONCEPT_ID_CODE = "CONCEPT_ID";

	String GENUS_CODE = "GENUS";

	String FAMILY_CODE = "FAMILY";

	String DESCRIBER_CODE = "DESCRIBER";

	String DESCRIBING_YEAR_CODE = "DESCRIBING_YEAR";

	String SOURCE_FILE_CODE = "SOURCE_FILE";

	String SOURCE_NAME_CODE = "SOURCE_NAME";

	// TODO to be removed soon
	String MEANING_IMAGE_CODE = "MEANING_IMAGE";

	// TODO to be renamed to SOURCE_NOTE soon
	String NOTE_CODE = "NOTE";

	// TODO to be removed soon
	String USAGE_CODE = "USAGE";

	String[] CLUELESS_SEARCH_LEXEME_FF_TYPE_CODES = new String[] {GOVERNMENT_CODE, GRAMMAR_CODE};

	String[] CLUELESS_SEARCH_MEANING_FF_TYPE_CODES = new String[] {CONCEPT_ID_CODE, LEARNER_COMMENT_CODE};

	String[] EXCLUDED_WORD_ATTRIBUTE_FF_TYPE_CODES = new String[] {OD_WORD_RECOMMENDATION_CODE};

	String[] EXCLUDED_LEXEME_ATTRIBUTE_FF_TYPE_CODES = new String[] {GOVERNMENT_CODE, GRAMMAR_CODE, USAGE_CODE, NOTE_CODE};

	String[] EXCLUDED_MEANING_ATTRIBUTE_FF_TYPE_CODES = new String[] {LEARNER_COMMENT_CODE, SEMANTIC_TYPE_CODE, NOTE_CODE, MEANING_IMAGE_CODE};

	String[] EXCLUDED_MEANING_ATTRIBUTE_FF_TYPE_CODES_MIN = new String[] {LEARNER_COMMENT_CODE, NOTE_CODE, MEANING_IMAGE_CODE};

	String[] MEANING_ATTRIBUTE_FF_TYPE_CODES = new String[] {CONCEPT_ID_CODE, GENUS_CODE, FAMILY_CODE, DESCRIBER_CODE, DESCRIBING_YEAR_CODE, SOURCE_FILE_CODE};
}
