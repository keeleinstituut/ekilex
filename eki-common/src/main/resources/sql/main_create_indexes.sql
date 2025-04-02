create index eki_user_email_idx on eki_user(email);
create index eki_user_api_key_idx on eki_user(api_key);
create index eki_user_profile_user_id_idx on eki_user_profile(user_id);
create index eki_user_profile_recent_dataset_permission_id_idx on eki_user_profile(recent_dataset_permission_id);
create index dataset_code_idx on dataset(code);
create index dataset_type_idx on dataset(type);
create index dataset_freeform_type_dataset_code_idx on dataset_freeform_type(dataset_code);
create index dataset_freeform_type_freeform_owner_idx on dataset_freeform_type(freeform_owner);
create index dataset_perm_user_id_idx on dataset_permission(user_id);
create index dataset_perm_dataset_code_idx on dataset_permission(dataset_code);
create index dataset_perm_dataset_full_cmplx_idx on dataset_permission(user_id, auth_operation, auth_item, dataset_code, auth_lang);
create index form_value_idx on form(value);
create index form_value_lower_idx on form(lower(value));
create index form_value_lower_prefix_idx on form (lower(value) text_pattern_ops);
create index form_morph_code_idx on form(morph_code);
create index paradigm_word_id_idx on paradigm(word_id);
create index paradigm_form_paradigm_id_idx on paradigm_form(paradigm_id);
create index paradigm_form_form_id_idx on paradigm_form(form_id);
create index paradigm_form_display_form_idx on paradigm_form(display_form);
create index paradigm_form_display_level_idx on paradigm_form(display_level);
create index word_homonym_nr_idx on word(homonym_nr);
create index word_lang_idx on word(lang);
create index word_value_idx on word(value);
create index word_value_lower_idx on word(lower(value));
create index word_value_lower_prefix_idx on word(lower(value) text_pattern_ops);
create index word_value_as_word_idx on word(value_as_word);
create index word_value_as_word_lower_idx on word(lower(value_as_word));
create index word_value_as_word_lower_prefix_idx on word(lower(value_as_word) text_pattern_ops);
create index word_manual_event_on_idx on word(manual_event_on);
create index word_morphophono_form_idx on word(morphophono_form);
create index word_morphophono_form_lower_idx on word(lower(morphophono_form));
create index word_is_public_idx on word(is_public);
create index word_etym_word_id_idx on word_etymology(word_id);
create index word_etym_etym_type_code_idx on word_etymology(etymology_type_code);
create index word_etym_rel_word_etym_id_idx on word_etymology_relation(word_etym_id);
create index word_etym_rel_rel_word_id_idx on word_etymology_relation(related_word_id);
create index word_od_recommendation_word_id_idx on word_od_recommendation(word_id);
create index word_od_recommendation_value_idx on word_od_recommendation(value);
create index word_od_recommendation_value_lower_idx on word_od_recommendation(lower(value));
create index word_guid_word_id_idx on word_guid(word_id);
create index word_guid_dataset_code_idx on word_guid(dataset_code);
create index word_guid_guid_idx on word_guid(guid);
create index word_tag_word_id_idx on word_tag(word_id);
create index word_tag_tag_name_idx on word_tag(tag_name);
create index word_tag_tag_name_lower_idx on word_tag(lower(tag_name));
create index word_group_member_group_id_idx on word_group_member(word_group_id);
create index word_group_member_word_id_idx on word_group_member(word_id);
create index word_word_type_word_id_idx on word_word_type(word_id);
create index word_word_type_idx on word_word_type(word_type_code);
create index meaning_manual_event_on_idx on meaning(manual_event_on);
create index meaning_nr_meaning_id_idx on meaning_nr(meaning_id);
create index meaning_nr_dataset_code_idx on meaning_nr(dataset_code);
create index meaning_nr_mnr_idx on meaning_nr(mnr);
create index lexeme_word_id_idx on lexeme(word_id);
create index lexeme_meaning_id_idx on lexeme(meaning_id);
create index lexeme_dataset_code_idx on lexeme(dataset_code);
create index lexeme_value_state_code_idx on lexeme(value_state_code);
create index lexeme_proficiency_level_code_idx on lexeme(proficiency_level_code);
create index lexeme_is_word_idx on lexeme(is_word);
create index lexeme_is_collocation_idx on lexeme(is_collocation);
create index lexeme_is_public_idx on lexeme(is_public);
create index lexeme_complexity_idx on lexeme(complexity);
create index lexeme_tag_lexeme_id_idx on lexeme_tag(lexeme_id);
create index lexeme_tag_tag_name_idx on lexeme_tag(tag_name);
create index lexeme_tag_tag_name_lower_idx on lexeme_tag(lower(tag_name));
create index definition_meaning_id_idx on definition(meaning_id);
create index definition_lang_idx on definition(lang);
create index definition_complexity_idx on definition(complexity);
create index definition_is_public_idx on definition(is_public);
create index meaning_relation_meaning1_id_idx on meaning_relation(meaning1_id);
create index meaning_relation_meaning2_id_idx on meaning_relation(meaning2_id);
create index meaning_rel_mapping_code1_idx on meaning_rel_mapping(code1);
create index meaning_rel_mapping_code2_idx on meaning_rel_mapping(code2);
create index meaning_tag_meaning_id_idx on meaning_tag(meaning_id);
create index meaning_tag_tag_name_idx on meaning_tag(tag_name);
create index meaning_tag_tag_name_lower_idx on meaning_tag(lower(tag_name));
create index lex_relation_lexeme1_id_idx on lex_relation(lexeme1_id);
create index lex_relation_lexeme2_id_idx on lex_relation(lexeme2_id);
create index lex_rel_mapping_code1_idx on lex_rel_mapping(code1);
create index lex_rel_mapping_code2_idx on lex_rel_mapping(code2);
create index word_relation_word1_id_idx on word_relation(word1_id);
create index word_relation_word2_id_idx on word_relation(word2_id);
create index word_relation_word_rel_type_code_idx on word_relation(word_rel_type_code);
create index word_relation_param_word_relation_id_idx on word_relation_param(word_relation_id);
create index word_rel_mapping_code1_idx on word_rel_mapping(code1);
create index word_rel_mapping_code2_idx on word_rel_mapping(code2);
create index freeform_parent_id_idx on freeform(parent_id);
create index freeform_value_idx on freeform(value);
create index freeform_value_lower_idx on freeform(lower(value));
create index freeform_type_code_idx on freeform(freeform_type_code);
create index freeform_lang_idx on freeform(lang);
create index freeform_complexity_idx on freeform(complexity);
create index freeform_is_public_idx on freeform(is_public);
create index source_dataset_code_idx on source(dataset_code);
create index source_type_idx on source(type);
create index source_name_idx on source(name);
create index source_name_lower_idx on source(lower(name));
create index source_name_lower_prefix_idx on source(lower(name) text_pattern_ops);
create index source_value_idx on source(value);
create index source_value_lower_idx on source(lower(value));
create index source_value_lower_prefix_idx on source(lower(value) text_pattern_ops);
create index source_comment_idx on source(comment);
create index source_comment_lower_idx on source(lower(comment));
create index source_comment_lower_prefix_idx on source(lower(comment) text_pattern_ops);
create index source_freeform_source_id_idx on source_freeform(source_id);
create index source_freeform_freeform_id_idx on source_freeform(freeform_id);
create index meaning_freeform_meaning_id_idx on meaning_freeform(meaning_id);
create index meaning_freeform_freeform_id_idx on meaning_freeform(freeform_id);
create index lexeme_freeform_lexeme_id_idx on lexeme_freeform(lexeme_id);
create index lexeme_freeform_freeform_id_idx on lexeme_freeform(freeform_id);
create index word_freeform_word_id_idx on word_freeform(word_id);
create index word_freeform_freeform_id_idx on word_freeform(freeform_id);
create index definition_freeform_definition_id_idx on definition_freeform(definition_id);
create index definition_freeform_freeform_id_idx on definition_freeform(freeform_id);
create index freeform_source_link_freeform_id_idx on freeform_source_link(freeform_id);
create index freeform_source_link_source_id_idx on freeform_source_link(source_id);
create index freeform_source_link_name_idx on freeform_source_link(name);
create index freeform_source_link_name_lower_idx on freeform_source_link(lower(name));
create index freeform_source_link_value_idx on freeform_source_link(value);
create index freeform_source_link_value_lower_idx on freeform_source_link(lower(value));
create index definition_source_link_definition_id_idx on definition_source_link(definition_id);
create index definition_source_link_source_id_idx on definition_source_link(source_id);
create index definition_source_link_name_idx on definition_source_link(name);
create index definition_source_link_name_lower_idx on definition_source_link(lower(name));
create index definition_source_link_value_idx on definition_source_link(value);
create index definition_source_link_value_lower_idx on definition_source_link(lower(value));
create index lexeme_source_link_lexeme_id_idx on lexeme_source_link(lexeme_id);
create index lexeme_source_link_source_id_idx on lexeme_source_link(source_id);
create index lexeme_source_link_name_idx on lexeme_source_link(name);
create index lexeme_source_link_name_lower_idx on lexeme_source_link(lower(name));
create index lexeme_source_link_value_idx on lexeme_source_link(value);
create index lexeme_source_link_value_lower_idx on lexeme_source_link(lower(value));
create index word_etym_source_link_word_etym_id_idx on word_etymology_source_link(word_etym_id);
create index word_etym_source_link_source_id_idx on word_etymology_source_link(source_id);
create index lex_colloc_pos_group_lexeme_id_idx on lex_colloc_pos_group(lexeme_id);
create index lex_colloc_rel_group_pos_group_id_idx on lex_colloc_rel_group(pos_group_id);
create index lex_colloc_lexeme_id_idx on lex_colloc(lexeme_id);
create index lex_colloc_rel_group_id_idx on lex_colloc(rel_group_id);
create index lex_colloc_collocation_id_idx on lex_colloc(collocation_id);
create index collocation_value_idx on collocation(value);
create index collocation_member_colloc_lexeme_id_idx on collocation_member(colloc_lexeme_id);
create index collocation_member_member_lexeme_id_idx on collocation_member(member_lexeme_id);
create index collocation_member_member_form_id_idx on collocation_member(member_form_id);
create index collocation_member_pos_group_code_idx on collocation_member(pos_group_code);
create index collocation_member_rel_group_code_idx on collocation_member(rel_group_code);
create index lexeme_register_lexeme_id_idx on lexeme_register(lexeme_id);
create index lexeme_pos_lexeme_id_idx on lexeme_pos(lexeme_id);
create index lexeme_pos_pos_code_idx on lexeme_pos(pos_code);
create index lexeme_deriv_lexeme_id_idx on lexeme_deriv(lexeme_id);
create index lexeme_region_lexeme_id_idx on lexeme_region(lexeme_id);
create index meaning_domain_meaning_id_idx on meaning_domain(meaning_id);
create index meaning_domain_code_origin_idx on meaning_domain(domain_code, domain_origin);
create index meaning_domain_origin_idx on meaning_domain(domain_origin);
create index meaning_domain_code_idx on meaning_domain(domain_code);
create index meaning_semantic_type_meaning_id_idx on meaning_semantic_type(meaning_id);
create index usage_original_freeform_id_idx on usage(original_freeform_id);
create index usage_lexeme_id_idx on usage(lexeme_id);
create index usage_lang_idx on usage(lang);
create index usage_complexity_idx on usage(complexity);
create index usage_is_public_idx on usage(is_public);
create index usage_source_link_usage_id_idx on usage_source_link(usage_id);
create index usage_source_link_source_id_idx on usage_source_link(source_id);
create index usage_source_link_name_idx on usage_source_link(name);
create index usage_source_link_name_lower_idx on usage_source_link(lower(name));
create index usage_translation_original_freeform_id_idx on usage_translation(original_freeform_id);
create index usage_translation_usage_id_idx on usage_translation(usage_id);
create index usage_translation_lang_idx on usage_translation(lang);
create index usage_definition_original_freeform_id_idx on usage_definition(original_freeform_id);
create index usage_definition_usage_id_idx on usage_definition(usage_id);
create index usage_definition_lang_idx on usage_definition(lang);
create index lexeme_note_original_freeform_id_idx on lexeme_note(original_freeform_id);
create index lexeme_note_lexeme_id_idx on lexeme_note(lexeme_id);
create index lexeme_note_lang_idx on lexeme_note(lang);
create index lexeme_note_complexity_idx on lexeme_note(complexity);
create index lexeme_note_is_public_idx on lexeme_note(is_public);
create index lexeme_note_source_link_lexeme_note_id_idx on lexeme_note_source_link(lexeme_note_id);
create index lexeme_note_source_link_source_id_idx on lexeme_note_source_link(source_id);
create index lexeme_note_source_link_name_idx on lexeme_note_source_link(name);
create index lexeme_note_source_link_name_lower_idx on lexeme_note_source_link(lower(name));
create index meaning_note_original_freeform_id_idx on meaning_note(original_freeform_id);
create index meaning_note_meaning_id_idx on meaning_note(meaning_id);
create index meaning_note_lang_idx on meaning_note(lang);
create index meaning_note_complexity_idx on meaning_note(complexity);
create index meaning_note_is_public_idx on meaning_note(is_public);
create index meaning_note_source_link_meaning_note_id_idx on meaning_note_source_link(meaning_note_id);
create index meaning_note_source_link_source_id_idx on meaning_note_source_link(source_id);
create index meaning_note_source_link_name_idx on meaning_note_source_link(name);
create index meaning_note_source_link_name_lower_idx on meaning_note_source_link(lower(name));
create index meaning_image_original_freeform_id_idx on meaning_image(original_freeform_id);
create index meaning_image_meaning_id_idx on meaning_image(meaning_id);
create index meaning_image_lang_idx on meaning_image(title);
create index meaning_image_complexity_idx on meaning_image(complexity);
create index meaning_image_is_public_idx on meaning_image(is_public);
create index meaning_image_source_link_meaning_image_id_idx on meaning_image_source_link(meaning_image_id);
create index meaning_image_source_link_source_id_idx on meaning_image_source_link(source_id);
create index meaning_image_source_link_name_idx on meaning_image_source_link(name);
create index meaning_image_source_link_name_lower_idx on meaning_image_source_link(lower(name));
create index definition_note_original_freeform_id_idx on definition_note(original_freeform_id);
create index definition_note_definition_id_idx on definition_note(definition_id);
create index definition_note_lang_idx on definition_note(lang);
create index definition_note_complexity_idx on definition_note(complexity);
create index definition_note_is_public_idx on definition_note(is_public);
create index definition_note_source_link_definition_note_id_idx on definition_note_source_link(definition_note_id);
create index definition_note_source_link_source_id_idx on definition_note_source_link(source_id);
create index definition_note_source_link_name_idx on definition_note_source_link(name);
create index definition_note_source_link_name_lower_idx on definition_note_source_link(lower(name));
create index form_freq_corp_id_idx on form_freq(freq_corp_id);
create index form_freq_form_id_idx on form_freq(form_id);
create index form_freq_value_id_idx on form_freq(value);
create index form_freq_rank_id_idx on form_freq(rank);
create index morph_freq_corp_id_idx on morph_freq(freq_corp_id);
create index morph_freq_morph_code_idx on morph_freq(morph_code);
create index morph_freq_value_id_idx on morph_freq(value);
create index morph_freq_rank_id_idx on morph_freq(rank);
create index word_freq_corp_id_idx on word_freq(freq_corp_id);
create index word_freq_word_id_idx on word_freq(word_id);
create index word_freq_value_id_idx on word_freq(value);
create index word_freq_rank_id_idx on word_freq(rank);
create index data_request_user_id_idx on data_request(user_id);
create index news_article_type_idx on news_article(type);
create index news_article_lang_idx on news_article(lang);
create index lexeme_activity_log_lexeme_id_idx on lexeme_activity_log(lexeme_id);
create index lexeme_activity_log_log_id_idx on lexeme_activity_log(activity_log_id);
create index word_activity_log_word_id_idx on word_activity_log(word_id);
create index word_activity_log_log_id_idx on word_activity_log(activity_log_id);
create index word_last_activity_log_word_id_idx on word_last_activity_log(word_id);
create index word_last_activity_log_log_id_idx on word_last_activity_log(activity_log_id);
create index meaning_activity_log_meaning_id_idx on meaning_activity_log(meaning_id);
create index meaning_activity_log_log_id_idx on meaning_activity_log(activity_log_id);
create index meaning_last_activity_log_meaning_id_idx on meaning_last_activity_log(meaning_id);
create index meaning_last_activity_log_log_id_idx on meaning_last_activity_log(activity_log_id);
create index source_activity_log_source_id_idx on source_activity_log(source_id);
create index source_activity_log_log_id_idx on source_activity_log(activity_log_id);
create index activity_log_event_on_idx on activity_log(event_on);
create index activity_log_event_on_desc_idx on activity_log(event_on desc);
create index activity_log_event_on_ms_idx on activity_log((date_part('epoch', event_on) * 1000));
create index activity_log_event_on_desc_ms_idx on activity_log((date_part('epoch', event_on) * 1000) desc);
create index activity_log_event_by_idx on activity_log(event_by);
create index activity_log_event_by_lower_idx on activity_log(lower(event_by));
create index activity_log_owner_idx on activity_log(owner_name, owner_id);
create index activity_log_owner_name_idx on activity_log(owner_name);
create index activity_log_dataset_code_idx on activity_log(dataset_code);
create index activity_funct_name_idx on activity_log(funct_name);
create index activity_entity_name_idx on activity_log(entity_name);
create index activity_entity_name_owner_name_event_on_idx on activity_log(entity_name, owner_name, (date_part('epoch', event_on) * 1000));
create index activity_entity_id_idx on activity_log(entity_id);
create index feedback_log_comment_log_id_idx on feedback_log_comment(feedback_log_id);
create index api_request_count_auth_name_idx on api_request_count(auth_name);
create index api_request_count_generic_path_idx on api_request_count(generic_path);
create index api_request_count_count_idx on api_request_count(count);
create index api_error_count_auth_name_idx on api_error_count(auth_name);
create index api_error_count_generic_path_idx on api_error_count(generic_path);
create index api_error_count_message_idx on api_error_count(message);
create index api_error_count_count_idx on api_error_count(count);
create index temp_ds_import_pk_map_import_code_idx on temp_ds_import_pk_map(import_code);
create index temp_ds_import_pk_map_table_name_idx on temp_ds_import_pk_map(table_name);
create index temp_ds_import_pk_map_source_pk_idx on temp_ds_import_pk_map(source_pk);
create index temp_ds_import_pk_map_target_pk_idx on temp_ds_import_pk_map(target_pk);
create index temp_ds_import_queue_import_code_idx on temp_ds_import_queue(import_code);
create index temp_ds_import_queue_table_name_idx on temp_ds_import_queue(table_name);
create index domain_code_origin_idx on domain(code, origin);
create index domain_parent_code_origin_idx on domain(parent_code, parent_origin);
create index domain_label_code_origin_idx on domain_label(code, origin);
create index definition_fts_idx on definition using gin(to_tsvector('simple', value));
create index freeform_fts_idx on freeform using gin(to_tsvector('simple', value));
create index form_fts_idx on form using gin(to_tsvector('simple', value));
create index usage_fts_idx on usage using gin(to_tsvector('simple', value));
create index lexeme_note_fts_idx on lexeme_note using gin(to_tsvector('simple', value));
create index meaning_note_fts_idx on meaning_note using gin(to_tsvector('simple', value));
create index definition_note_fts_idx on definition_note using gin(to_tsvector('simple', value));


