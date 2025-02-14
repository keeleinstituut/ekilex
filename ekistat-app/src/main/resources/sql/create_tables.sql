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
  referrer_domain text,
  session_id text,
  request_origin text,
  event_on timestamp not null default statement_timestamp()
);
alter sequence ww_search_id_seq restart with 10000;

create table ww_exception
(
  id bigserial primary key,
  exception_name text,
  exception_message text,
  event_on timestamp not null default statement_timestamp()
);
alter sequence ww_exception_id_seq restart with 10000;

create index ww_search_search_word_idx on ww_search(search_word);
create index ww_search_search_mode_idx on ww_search(search_mode);
create index ww_search_event_on_idx on ww_search(event_on);
create index ww_search_request_origin_idx on ww_search(request_origin);
