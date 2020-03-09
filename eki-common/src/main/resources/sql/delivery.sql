-- upgrade from ver 1.14.0 to 1.15.0
alter table eki_user_profile add column preferred_layer_name varchar(100) null;
alter table eki_user_profile rename column preferred_biling_candidate_langs to preferred_syn_candidate_langs;
alter table eki_user_profile rename column preferred_biling_lex_meaning_word_langs to preferred_syn_lex_meaning_word_langs;