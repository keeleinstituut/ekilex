create table ww_search
(
  id bigserial primary key,
  search_word text,
  homonym_nr integer,
  search_mode text,
  destin_langs varchar(10) array,
  dataset_codes varchar(10) array,
  search_uri text,
  result_count integer,
  results_exist boolean,
  single_result boolean,
  user_agent text,
  event_on timestamp not null default statement_timestamp()
);
alter sequence ww_search_id_seq restart with 10000;