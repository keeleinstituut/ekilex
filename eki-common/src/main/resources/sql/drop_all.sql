-- wordweb associated views and types
drop view if exists view_ww_word_search;
drop view if exists view_ww_word;
drop view if exists view_ww_form;
drop view if exists view_ww_meaning;
drop view if exists view_ww_lexeme;
drop view if exists view_ww_collocation;
drop view if exists view_ww_classifier;
drop view if exists view_ww_dataset;
drop view if exists view_ww_word_etymology;
drop view if exists view_ww_word_relation;
drop view if exists view_ww_lexeme_relation;
drop view if exists view_ww_meaning_relation;
drop view if exists view_ww_lexeme_source_link;
drop view if exists view_ww_lexeme_freeform_source_link;
drop view if exists view_ww_meaning_freeform_source_link;
drop view if exists view_ww_word_etym_source_link;
drop view if exists view_ww_definition_source_link;
drop view if exists view_ww_lexical_decision_data;
drop view if exists view_ww_similarity_judgement_data;
drop type if exists type_meaning_word;
drop type if exists type_freeform;
drop type if exists type_lang_complexity;
drop type if exists type_definition;
drop type if exists type_domain;
drop type if exists type_image_file;
drop type if exists type_usage;
drop type if exists type_source_link;
drop type if exists type_colloc_member;
drop type if exists type_word_etym_relation;
drop type if exists type_word_relation;
drop type if exists type_lexeme_relation;
drop type if exists type_meaning_relation;

-- ekilex associated tables and types
drop type if exists type_term_meaning_word;
drop type if exists type_word_rel_param;
drop type if exists type_classifier;
drop table if exists temp_ds_import_pk_map;
drop table if exists temp_ds_import_queue;
drop table if exists feedback_log_comment;
drop table if exists feedback_log;
drop table if exists game_nonword;
drop table if exists freeform_source_link;
drop table if exists definition_source_link;
drop table if exists lexeme_source_link;
drop table if exists word_etymology_source_link;
drop table if exists lex_colloc;
drop table if exists lex_colloc_rel_group;
drop table if exists lex_colloc_pos_group;
drop table if exists lex_relation;
drop table if exists lexeme_frequency;
drop table if exists lexeme_lifecycle_log;
drop table if exists lexeme_process_log;
drop table if exists lexeme_freeform;
drop table if exists lexeme_register;
drop table if exists lexeme_pos;
drop table if exists lexeme_deriv;
drop table if exists lexeme_region;
drop table if exists layer_state; -- remove later
drop table if exists lexeme_tag;
drop table if exists lexeme;
drop table if exists collocation_freeform;
drop table if exists collocation;
drop table if exists definition_freeform;
drop table if exists definition_dataset;
drop table if exists definition;
drop table if exists meaning_lifecycle_log;
drop table if exists meaning_process_log; -- remove later
drop table if exists meaning_relation;
drop table if exists meaning_freeform;
drop table if exists meaning_domain;
drop table if exists meaning_semantic_type;
drop table if exists meaning_nr;
drop table if exists meaning;
drop table if exists form_frequency;
drop table if exists form;
drop table if exists paradigm;
drop table if exists word_lifecycle_log;
drop table if exists word_process_log; -- remove later
drop table if exists word_freeform;
drop table if exists word_etymology_relation;
drop table if exists word_etymology;
drop table if exists word_guid;
drop table if exists word_group_member;
drop table if exists word_group;
drop table if exists word_relation_param;
drop table if exists word_relation;
drop table if exists word_word_type;
drop table if exists word;
drop table if exists process_log_source_link; -- remove later
drop table if exists process_log; -- remove later
drop table if exists source_lifecycle_log;
drop table if exists source_freeform;
drop table if exists source;
drop table if exists freeform;
drop table if exists eki_user_profile;
drop table if exists dataset_permission;
drop table if exists dataset;
drop table if exists etymology_type;
drop table if exists usage_type_label;
drop table if exists usage_type;
drop table if exists meaning_rel_mapping;
drop table if exists meaning_rel_type_label;
drop table if exists meaning_rel_type;
drop table if exists word_rel_mapping;
drop table if exists word_rel_type_label;
drop table if exists word_rel_type;
drop table if exists lex_rel_mapping;
drop table if exists lex_rel_type_label;
drop table if exists lex_rel_type;
drop table if exists process_state; -- remove later
drop table if exists region;
drop table if exists word_type_label;
drop table if exists word_type;
drop table if exists aspect_label;
drop table if exists aspect;
drop table if exists deriv_label;
drop table if exists deriv;
drop table if exists display_morph_label;
drop table if exists display_morph;
drop table if exists morph_label;
drop table if exists morph;
drop table if exists pos_label;
drop table if exists pos;
drop table if exists pos_group_label;
drop table if exists pos_group;
drop table if exists gender_label;
drop table if exists gender;
drop table if exists register_label;
drop table if exists register;
drop table if exists semantic_type_label;
drop table if exists semantic_type;
drop table if exists frequency_group;
drop table if exists domain_label;
drop table if exists domain;
drop table if exists definition_type_label;
drop table if exists definition_type;
drop table if exists government_type_label;
drop table if exists government_type;
drop table if exists value_state_label;
drop table if exists value_state;
drop table if exists language_label;
drop table if exists language;
drop table if exists label_type;
drop table if exists lifecycle_log;
drop table if exists eki_user_application;
drop table if exists eki_user;
drop table if exists tag;
drop table if exists lexeme_activity_log;
drop table if exists word_activity_log;
drop table if exists meaning_activity_log;
drop table if exists activity_log;
drop type if exists type_activity_log_diff;
