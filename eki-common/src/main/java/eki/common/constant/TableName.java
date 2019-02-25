package eki.common.constant;

public interface TableName {

	String EKI_USER = "eki_user";

	// classifiers
	String LANGUAGE = "language";
	String DOMAIN = "domain";
	String DOMAIN_LABEL = "domain_label";
	String REGISTER = "register";
	String POS = "pos";
	String POS_LABEL = "pos_label";
	String DERIV = "deriv";
	String MORPH = "morph";
	String MORPH_LABEL = "morph_label";
	String LABEL_TYPE = "label_type";
	String GENDER = "gender";

	// dynamic
	String SOURCE = "source";
	String WORD = "word";
	String LEXEME = "lexeme";
	String MEANING = "meaning";
	String MEANING_NR = "meaning_nr";
	String DEFINITION = "definition";
	String PARADIGM = "paradigm";
	String FORM = "form";
	String FREEFORM = "freeform";
	String LEXEME_FREEFORM = "lexeme_freeform";
	String MEANING_FREEFORM = "meaning_freeform";
	String DEFINITION_FREEFORM = "definition_freeform";
	String SOURCE_FREEFORM = "source_freeform";
	String FREEFORM_SOURCE_LINK = "freeform_source_link";
	String DEFINITION_SOURCE_LINK = "definition_source_link";
	String LEXEME_SOURCE_LINK = "lexeme_source_link";
	String WORD_SOURCE_LINK = "word_source_link";
	String DATASET = "dataset";
	String WORD_ETYMOLOGY = "word_etymology";
	String WORD_GUID = "word_guid";
	String WORD_WORD_TYPE = "word_word_type";
	String LEXEME_RELATION = "lex_relation";
	String WORD_RELATION = "word_relation";
	String MEANING_RELATION = "meaning_relation";
	String LEX_COLLOC = "lex_colloc";
	String LEX_COLLOC_POS_GROUP = "lex_colloc_pos_group";
	String LEX_COLLOC_REL_GROUP = "lex_colloc_rel_group";
	String COLLOCATION = "collocation";
	String COLLOCATION_FREEFORM = "collocation_freeform";
	String WORD_RELATION_GROUP = "word_group";
	String WORD_RELATION_GROUP_MEMBER = "word_group_member";
	String LIFECYCLE_LOG = "lifecycle_log";
	String WORD_LIFECYCLE_LOG = "word_lifecycle_log";
	String LEXEME_LIFECYCLE_LOG = "lexeme_lifecycle_log";
	String MEANING_LIFECYCLE_LOG = "meaning_lifecycle_log";
	String GAME_NONWORD = "game_nonword";

	// classifier binds
	String MEANING_DOMAIN = "meaning_domain";
	String LEXEME_POS = "lexeme_pos";
	String LEXEME_DERIV = "lexeme_deriv";
	String LEXEME_REGISTER = "lexeme_register";

	// dataset binds
	String DEFINITION_DATASET = "definition_dataset";

}
