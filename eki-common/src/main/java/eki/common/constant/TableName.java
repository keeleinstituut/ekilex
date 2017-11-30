package eki.common.constant;

public interface TableName {

	public static final String EKI_USER = "eki_user";

	// classifiers
	public static final String LANG = "lang";
	public static final String DOMAIN = "domain";
	public static final String DOMAIN_LABEL = "domain_label";
	public static final String REGISTER = "register";
	public static final String POS = "pos";
	public static final String POS_LABEL = "pos_label";
	public static final String DERIV = "deriv";
	public static final String MORPH = "morph";
	public static final String MORPH_LABEL = "morph_label";
	public static final String LEXEME_TYPE = "lexeme_type";
	public static final String MEANING_STATE = "meaning_state";
	public static final String MEANING_TYPE = "meaning_type";
	public static final String LABEL_TYPE = "label_type";

	// dynamic
	public static final String SOURCE = "source";
	public static final String WORD = "word";
	public static final String DECLINATION = "declination";
	public static final String LEXEME = "lexeme";
	public static final String MEANING = "meaning";
	public static final String DEFINITION = "definition";
	public static final String PARADIGM = "paradigm";
	public static final String FORM = "form";
	public static final String FREEFORM = "freeform";
	public static final String LEXEME_FREEFORM = "lexeme_freeform";
	public static final String MEANING_FREEFORM = "meaning_freeform";
	public static final String DEFINITION_FREEFORM = "definition_freeform";
	public static final String SOURCE_FREEFORM = "source_freeform";
	public static final String DATASET = "dataset";
	public static final String VIEW = "vievv";
	public static final String WORD_GUID = "word_guid";
	public static final String LEXEME_RELATION = "lex_relation";
	public static final String FORM_RELATION = "form_relation";
	public static final String LIFECYCLE_LOG = "lifecycle_log";
	public static final String WORD_RELATION = "word_relation";

	// classifier binds
	public static final String MEANING_DOMAIN = "meaning_domain";
	public static final String LEXEME_POS = "lexeme_pos";
	public static final String LEXEME_DERIV = "lexeme_deriv";

	// dataset binds
	public static final String MEANING_DATASET = "meaning_dataset";
	public static final String LEXEME_DATASET = "lexeme_dataset";
	public static final String DEFINITION_DATASET = "definition_dataset";
	public static final String LEX_RELATION_DATASET = "lex_relation_dataset";
}
