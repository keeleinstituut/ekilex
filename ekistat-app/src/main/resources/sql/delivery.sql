-- upgrade from ver 1.3.* to 1.4.0

alter table ww_search rename column results_exist to result_exists;
alter table ww_search alter column search_word set not null;
alter table ww_search alter column search_mode set not null;
alter table ww_search alter column destin_langs set not null;
alter table ww_search alter column dataset_codes set not null;
alter table ww_search alter column search_uri set not null;
alter table ww_search alter column session_id set not null;
alter table ww_search alter column request_origin set not null;
alter table ww_exception alter column exception_name set not null;

create index ww_search_request_origin_idx on ww_search(request_origin);
create index ww_search_result_exists_idx on ww_search(result_exists);

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

insert into ww_search_default_count (
	search_word,
	result_exists,
	request_origin,
	search_date,
	search_count
)
select
	ws.search_word,
	ws.result_exists,
	ws.request_origin,
	date(ws.event_on),
	count(ws.id)
from
	ww_search ws
where
	ws.destin_langs = '{dlall}'
	and ws.dataset_codes = '{dsall}'
	and ws.search_mode = 'detail'
group by
	ws.search_word,
	ws.result_exists,
	ws.request_origin,
	date(ws.event_on)
;

insert into ww_search_filtered_count (
	search_word,
	search_mode,
	destin_langs,
	dataset_codes,
	result_exists,
	request_origin,
	search_date,
	search_count
)
select
	ws.search_word,
	ws.search_mode,
	ws.destin_langs,
	ws.dataset_codes,
	ws.result_exists,
	ws.request_origin,
	date(ws.event_on),
	count(ws.id)
from
	ww_search ws
where
	not (ws.destin_langs = '{dlall}'
		and ws.dataset_codes = '{dsall}'
		and ws.search_mode = 'detail')
group by
	ws.search_word,
	ws.search_mode,
	ws.destin_langs,
	ws.dataset_codes,
	ws.result_exists,
	ws.request_origin,
	date(ws.event_on)
;

analyse ww_search_default_count;
analyse ww_search_filtered_count;
