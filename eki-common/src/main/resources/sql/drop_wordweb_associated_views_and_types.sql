-- wordweb associated views and types
drop view if exists view_ww_dataset_word_menu;
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
drop type if exists type_image_file; --TODO remove later
drop type if exists type_media_file;
drop type if exists type_usage;
drop type if exists type_source_link;
drop type if exists type_colloc_member;
drop type if exists type_word_etym_relation;
drop type if exists type_word_relation;
drop type if exists type_lexeme_relation;
drop type if exists type_meaning_relation;
