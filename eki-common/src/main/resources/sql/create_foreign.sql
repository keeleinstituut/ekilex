-- run this only first time
-- create server activity_log_srv foreign data wrapper postgres_fdw options (host 'localhost', dbname 'ekilog');
-- create user mapping for ekilex server activity_log_srv options (user 'ekilog', password 'ek1l0g');
-- grant usage on foreign server activity_log_srv to ekilex;

-- drop

drop foreign table if exists source_activity_log_fdw cascade;
drop foreign table if exists source_activity_log_id_fdw cascade;
drop function if exists source_activity_log_id_func;
drop foreign table if exists meaning_last_activity_log_fdw cascade;
drop foreign table if exists meaning_last_activity_log_id_fdw cascade;
drop function if exists meaning_last_activity_log_id_func;
drop foreign table if exists meaning_activity_log_fdw cascade;
drop foreign table if exists meaning_activity_log_id_fdw cascade;
drop function if exists meaning_activity_log_id_func;
drop foreign table if exists word_last_activity_log_fdw cascade;
drop foreign table if exists word_last_activity_log_id_fdw cascade;
drop function if exists word_last_activity_log_id_func;
drop foreign table if exists word_activity_log_fdw cascade;
drop foreign table if exists word_activity_log_id_fdw cascade;
drop function if exists word_activity_log_id_func;
drop foreign table if exists lexeme_activity_log_fdw cascade;
drop foreign table if exists lexeme_activity_log_id_fdw cascade;
drop function if exists lexeme_activity_log_id_func;
drop foreign table if exists activity_log_fdw cascade;
drop foreign table if exists activity_log_id_fdw cascade;
drop function if exists activity_log_id_func;
drop type if exists type_activity_log_diff;

-- create

create type type_activity_log_diff as (
  op varchar(100), 
  path text, 
  value text
);

create foreign table activity_log_id_fdw (
	id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'activity_log_id_seq_view');

create function activity_log_id_func() returns bigint as
	'select id from activity_log_id_fdw;'
	language sql;

create foreign table activity_log_fdw (
  id bigint not null default activity_log_id_func(),
  event_by text not null, 
  event_on timestamp not null default statement_timestamp(), 
  dataset_code varchar(10) null, 
  funct_name text not null, 
  owner_id bigint not null, 
  owner_name text not null, 
  entity_id bigint not null, 
  entity_name text not null, 
  prev_data jsonb not null, 
  curr_data jsonb not null, 
  prev_diffs type_activity_log_diff array not null, 
  curr_diffs type_activity_log_diff array not null
) server activity_log_srv options (schema_name 'public', table_name 'activity_log');

create foreign table lexeme_activity_log_id_fdw (
	id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'lexeme_activity_log_id_seq_view');

create function lexeme_activity_log_id_func() returns bigint as
	'select id from lexeme_activity_log_id_fdw;'
	language sql;

create foreign table lexeme_activity_log_fdw (
  id bigint not null default lexeme_activity_log_id_func(), 
  lexeme_id bigint not null, 
  activity_log_id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'lexeme_activity_log');

create foreign table word_activity_log_id_fdw (
	id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'word_activity_log_id_seq_view');

create function word_activity_log_id_func() returns bigint as
	'select id from word_activity_log_id_fdw;'
	language sql;

create foreign table word_activity_log_fdw (
  id bigint not null default word_activity_log_id_func(), 
  word_id bigint not null, 
  activity_log_id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'word_activity_log');

create foreign table word_last_activity_log_id_fdw (
	id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'word_last_activity_log_id_seq_view');

create function word_last_activity_log_id_func() returns bigint as
	'select id from word_last_activity_log_id_fdw;'
	language sql;

create foreign table word_last_activity_log_fdw (
  id bigint not null default word_last_activity_log_id_func(), 
  word_id bigint not null, 
  activity_log_id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'word_last_activity_log');

create foreign table meaning_activity_log_id_fdw (
	id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'meaning_activity_log_id_seq_view');

create function meaning_activity_log_id_func() returns bigint as
	'select id from meaning_activity_log_id_fdw;'
	language sql;

create foreign table meaning_activity_log_fdw (
  id bigint not null default meaning_activity_log_id_func(), 
  meaning_id bigint not null, 
  activity_log_id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'meaning_activity_log');

create foreign table meaning_last_activity_log_id_fdw (
	id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'meaning_last_activity_log_id_seq_view');

create function meaning_last_activity_log_id_func() returns bigint as
	'select id from meaning_last_activity_log_id_fdw;'
	language sql;

create foreign table meaning_last_activity_log_fdw (
  id bigint not null default meaning_last_activity_log_id_func(), 
  meaning_id bigint not null, 
  activity_log_id bigint not null,
  type varchar(100) not null
) server activity_log_srv options (schema_name 'public', table_name 'meaning_last_activity_log');

create foreign table source_activity_log_id_fdw (
	id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'source_activity_log_id_seq_view');

create function source_activity_log_id_func() returns bigint as
	'select id from source_activity_log_id_fdw;'
	language sql;

create foreign table source_activity_log_fdw (
	id bigint not null default source_activity_log_id_func(),
	source_id bigint not null,
	activity_log_id bigint not null
) server activity_log_srv options (schema_name 'public', table_name 'source_activity_log');


