drop view if exists view_ww_lexeme;
drop view if exists view_ww_meaning;
drop view if exists view_ww_form;
drop view if exists view_ww_word;
drop view if exists view_ww_classifier;
drop view if exists view_ww_dataset;
drop type if exists type_definition;
drop type if exists type_domain;
drop type if exists type_usage;
drop type if exists type_colloc_word;

drop table if exists freeform_ref_link;
drop table if exists definition_ref_link;
drop table if exists lex_colloc;
drop table if exists collocation_rel_group;--remove later
drop table if exists collocation_pos_group;--remove later
drop table if exists collocation_usage;-- remove later
drop table if exists lex_colloc_rel_group;
drop table if exists lex_colloc_pos_group;
drop table if exists lex_relation;
drop table if exists lexeme_freeform;
drop table if exists lexeme_register;
drop table if exists lexeme_pos;
drop table if exists lexeme_deriv;
drop table if exists lexeme;
drop table if exists collocation_freeform;
drop table if exists collocation;
drop table if exists definition_freeform;
drop table if exists definition_dataset;
drop table if exists definition;
drop table if exists meaning_relation;
drop table if exists meaning_freeform;
drop table if exists meaning_domain;
drop table if exists meaning;
drop table if exists form_relation;
drop table if exists form;
drop table if exists paradigm;
drop table if exists word_guid;
drop table if exists word_relation;
drop table if exists word;
drop table if exists source_freeform;
drop table if exists source;
drop table if exists freeform;
drop table if exists usage_author_type_label;-- remove later
drop table if exists usage_author_type;-- remove later
drop table if exists usage_type_label;
drop table if exists usage_type;
drop table if exists meaning_rel_type_label;
drop table if exists meaning_rel_type;
drop table if exists word_rel_type_label;
drop table if exists word_rel_type;
drop table if exists form_rel_type_label;
drop table if exists form_rel_type;
drop table if exists lex_rel_type_label;
drop table if exists lex_rel_type;
--drop table if exists meaning_type_label;
drop table if exists meaning_type;
--drop table if exists meaning_state_label;
drop table if exists meaning_state;-- remove later
drop table if exists process_state;
drop table if exists word_type_label;
drop table if exists word_type;
drop table if exists deriv_label;
drop table if exists deriv;
drop table if exists display_morph_label;
drop table if exists display_morph;
drop table if exists morph_label;
drop table if exists morph;
drop table if exists pos_label;
drop table if exists pos;
drop table if exists gender_label;
drop table if exists gender;
drop table if exists register_label;
drop table if exists register;
drop table if exists lexeme_type_label;-- remove later
drop table if exists lexeme_type;-- remove later
drop table if exists lexeme_frequency_label; -- removed for now
drop table if exists lexeme_frequency;
drop table if exists domain_label;
drop table if exists domain;
drop table if exists rection_type_label;--remove later
drop table if exists rection_type;--remove later
drop table if exists government_type_label;
drop table if exists government_type;
drop table if exists value_state_label;
drop table if exists value_state;
drop table if exists lang_label;
drop table if exists lang;
drop table if exists label_type;
drop table if exists dataset;
drop table if exists lifecycle_log;
drop table if exists eki_user;
drop table if exists person;
