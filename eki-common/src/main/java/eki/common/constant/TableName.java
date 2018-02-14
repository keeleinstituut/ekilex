package eki.common.constant;

public interface TableName {

	String EKI_USER = "eki_user";
	String PERSON = "person";

	// classifiers
	String LANG = "lang";
	String DOMAIN = "domain";
	String DOMAIN_LABEL = "domain_label";
	String REGISTER = "register";
	String POS = "pos";
	String POS_LABEL = "pos_label";
	String DERIV = "deriv";
	String MORPH = "morph";
	String MORPH_LABEL = "morph_label";
	String LEXEME_TYPE = "lexeme_type";
	String MEANING_STATE = "meaning_state";
	String MEANING_TYPE = "meaning_type";
	String LABEL_TYPE = "label_type";

	// dynamic
	String SOURCE = "source";
	String WORD = "word";
	String DECLINATION = "declination";
	String LEXEME = "lexeme";
	String MEANING = "meaning";
	String DEFINITION = "definition";
	String PARADIGM = "paradigm";
	String FORM = "form";
	String FREEFORM = "freeform";
	String LEXEME_FREEFORM = "lexeme_freeform";
	String MEANING_FREEFORM = "meaning_freeform";
	String DEFINITION_FREEFORM = "definition_freeform";
	String SOURCE_FREEFORM = "source_freeform";
	String FREEFORM_REF_LINK = "freeform_ref_link";
	String DEFINITION_REF_LINK = "definition_ref_link";
	String DATASET = "dataset";
	String VIEW = "vievv";
	String WORD_GUID = "word_guid";
	String LEXEME_RELATION = "lex_relation";
	String FORM_RELATION = "form_relation";
	String LIFECYCLE_LOG = "lifecycle_log";
	String WORD_RELATION = "word_relation";
	String MEANING_RELATION = "meaning_relation";
	String COLLOCATION = "collocation";
	String COLLOCATION_USAGE = "collocation_usage";

	// classifier binds
	String MEANING_DOMAIN = "meaning_domain";
	String LEXEME_POS = "lexeme_pos";
	String LEXEME_DERIV = "lexeme_deriv";
	String LEXEME_REGISTER = "lexeme_register";

	// dataset binds
	String DEFINITION_DATASET = "definition_dataset";
	
}
