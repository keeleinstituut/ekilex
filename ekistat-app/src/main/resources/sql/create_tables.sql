drop table if exists ww_exception;
drop table if exists ww_search;
drop table if exists ww_search_default_count;
drop table if exists ww_search_filtered_count;

create table ww_search
(
  id bigserial primary key,
  search_word text not null,
  homonym_nr integer,
  search_mode text not null,
  destin_langs varchar(10) array not null,
  dataset_codes varchar(10) array not null,
  search_uri text not null,
  result_count integer,
  result_exists boolean,
  single_result boolean,
  request_origin text not null,
  user_agent text,
  referrer_domain text,
  server_domain text,
  session_id text not null,
  event_on timestamp not null default statement_timestamp()
);
alter sequence ww_search_id_seq restart with 10000;

create index ww_search_search_word_idx on ww_search(search_word);
create index ww_search_search_mode_idx on ww_search(search_mode);
create index ww_search_request_origin_idx on ww_search(request_origin);
create index ww_search_result_exists_idx on ww_search(result_exists);
create index ww_search_referrer_domain_idx on ww_search(referrer_domain);
create index ww_search_server_domain_idx on ww_search(server_domain);
create index ww_search_event_on_idx on ww_search(event_on);

create table ww_exception
(
  id bigserial primary key,
  exception_name text not null,
  exception_message text,
  remote_host text,
  event_on timestamp not null default statement_timestamp()
);
alter sequence ww_exception_id_seq restart with 10000;

create index ww_exception_remote_host_idx on ww_exception(remote_host);

create table ww_search_default_count (
	id bigserial primary key,
	search_word text not null,
	result_exists boolean not null,
	request_origin text not null,
	search_date date not null default current_date,
	search_count integer not null default 0,
	unique (search_word, result_exists, request_origin, search_date)
);
alter sequence ww_search_default_count_id_seq restart with 10000;

create index ww_search_default_count_search_word_idx on ww_search_default_count(search_word);
create index ww_search_default_count_result_exists_idx on ww_search_default_count(result_exists);
create index ww_search_default_count_request_origin_idx on ww_search_default_count(request_origin);
create index ww_search_default_count_search_date_idx on ww_search_default_count(search_date);
create index ww_search_default_count_search_count_idx on ww_search_default_count(search_count);
create index ww_search_default_count_search_count_desc_idx on ww_search_default_count(search_count desc);

create table ww_search_filtered_count (
	id bigserial primary key,
	search_word text not null,
	search_mode text not null,
	destin_langs varchar(10) array not null,
	dataset_codes varchar(10) array not null,
	result_exists boolean not null,
	request_origin text not null,
	search_date date not null default current_date,
	search_count integer not null default 0,
	unique (search_word, search_mode, destin_langs, dataset_codes, result_exists, request_origin, search_date)
);
alter sequence ww_search_filtered_count_id_seq restart with 10000;

create index ww_search_filtered_count_search_word_idx on ww_search_filtered_count(search_word);
create index ww_search_filtered_count_search_mode_idx on ww_search_filtered_count(search_mode);
create index ww_search_filtered_count_destin_langs_idx on ww_search_filtered_count(destin_langs);
create index ww_search_filtered_count_dataset_codes_idx on ww_search_filtered_count(dataset_codes);
create index ww_search_filtered_count_result_exists_idx on ww_search_filtered_count(result_exists);
create index ww_search_filtered_count_request_origin_idx on ww_search_filtered_count(request_origin);
create index ww_search_filtered_count_search_date_idx on ww_search_filtered_count(search_date);
create index ww_search_filtered_count_search_count_idx on ww_search_filtered_count(search_count);
create index ww_search_filtered_count_search_count_desc_idx on ww_search_filtered_count(search_count desc);

