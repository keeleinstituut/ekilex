create index ww_search_search_word_idx on ww_search(search_word);
create index ww_search_search_mode_idx on ww_search(search_mode);
create index ww_search_event_on_idx on ww_search(event_on);

drop index if exists ww_search_destin_langs_idx;
drop index if exists ww_search_dataset_codes_idx;