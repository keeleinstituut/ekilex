package eki.common.constant;

//TODO update data structures
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
	String DATASET = "dataset";
	String TAG = "tag";

	// dynamic
	String SOURCE = "source";
	String SOURCE_FREEFORM = "source_freeform";
	String SOURCE_ACTIVITY_LOG = "source_activity_log";
	String WORD = "word";
	String WORD_GUID = "word_guid";
	String WORD_WORD_TYPE = "word_word_type";
	String WORD_ETYMOLOGY = "word_etymology";
	String WORD_ETYMOLOGY_RELATION = "word_etymology_relation";
	String WORD_ETYMOLOGY_SOURCE_LINK = "word_etymology_source_link";
	String WORD_GROUP_MEMBER = "word_group_member";
	String WORD_RELATION = "word_relation";
	String WORD_RELATION_PARAM = "word_relation_param";
	String WORD_GROUP = "word_group";
	String WORD_ACTIVITY_LOG = "word_activity_log";
	String WORD_FREEFORM = "word_freeform";
	String LEXEME = "lexeme";
	String LEXEME_POS = "lexeme_pos";
	String LEXEME_DERIV = "lexeme_deriv";
	String LEXEME_REGISTER = "lexeme_register";
	String LEXEME_REGION = "lexeme_region";
	String LEXEME_FREEFORM = "lexeme_freeform";
	String LEXEME_FREQUENCY = "lexeme_frequency";
	String LEXEME_RELATION = "lex_relation";
	String LEXEME_SOURCE_LINK = "lexeme_source_link";
	String LEXEME_ACTIVITY_LOG = "lexeme_activity_log";
	String LEXEME_TAG = "lexeme_tag";
	String LEX_COLLOC = "lex_colloc";
	String LEX_COLLOC_POS_GROUP = "lex_colloc_pos_group";
	String LEX_COLLOC_REL_GROUP = "lex_colloc_rel_group";
	String MEANING = "meaning";
	String MEANING_NR = "meaning_nr";
	String MEANING_DOMAIN = "meaning_domain";
	String MEANING_SEMANTIC_TYPE = "meaning_semantic_type";
	String MEANING_FREEFORM = "meaning_freeform";
	String MEANING_RELATION = "meaning_relation";
	String MEANING_ACTIVITY_LOG = "meaning_activity_log";
	String DEFINITION = "definition";
	String DEFINITION_FREEFORM = "definition_freeform";
	String DEFINITION_SOURCE_LINK = "definition_source_link";
	String DEFINITION_DATASET = "definition_dataset";
	String PARADIGM = "paradigm";
	String PARADIGM_FORM = "paradigm_form";
	String FORM = "form";
	String FORM_FREQUENCY = "form_frequency";
	String FREEFORM = "freeform";
	@Deprecated
	String FREEFORM_SOURCE_LINK = "freeform_source_link";
	String COLLOCATION = "collocation";
	String COLLOCATION_FREEFORM = "collocation_freeform";
	String ACTIVITY_LOG = "activity_log";
	String GAME_NONWORD = "game_nonword";
}
