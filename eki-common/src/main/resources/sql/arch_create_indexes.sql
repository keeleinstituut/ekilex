
create index activity_log_bulk_activity_log_id_idx on activity_log_bulk(activity_log_id);
create index activity_log_bulk_owner_id_idx on activity_log_bulk(owner_id);
create index activity_log_bulk_owner_name_idx on activity_log_bulk(owner_name);
create index activity_log_bulk_entity_id_idx on activity_log_bulk(entity_id);
create index activity_log_bulk_entity_name_idx on activity_log_bulk(entity_name);
create index activity_log_bulk_curr_data_word_id_idx on activity_log_bulk(cast(curr_data ->> 'wordId' as bigint));
create index activity_log_bulk_curr_data_meaning_id_idx on activity_log_bulk(cast(curr_data ->> 'meaningId' as bigint));
create index activity_log_bulk_curr_data_lexeme_id_idx on activity_log_bulk(cast(curr_data ->> 'lexemeId' as bigint));
