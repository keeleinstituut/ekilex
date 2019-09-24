-- always run this after full import
insert into eki_user_profile (user_id) (select eki_user.id from eki_user);

-- 02.09.2019
alter table lexeme add column type varchar(50);

-- this can be executed after the type introducing implementation is finished
update lexeme set type = 'PRIMARY' where type is null;

-- TODO change lexeme.type not nullable

-- 24.09.2019 wrapper type for term search results projection - modified!
drop type if exists type_term_meaning_word;
create type type_term_meaning_word as (word_id bigint, word text, homonym_nr integer, lang char(3), word_type_codes varchar(100) array, dataset_codes varchar(10) array, matching_word boolean);

-- 24.09.2019 only required when using dataset importer
drop table if exists temp_ds_import_pk_map cascade;
create table temp_ds_import_pk_map
(
  id bigserial primary key,
  import_code varchar(100) not null,
  created_on timestamp not null default statement_timestamp(),
  table_name text not null,
  source_pk bigint not null,
  target_pk bigint not null
);

create index temp_ds_import_pk_map_import_code_idx on temp_ds_import_pk_map(import_code);
create index temp_ds_import_pk_map_table_name_idx on temp_ds_import_pk_map(table_name);
create index temp_ds_import_pk_map_source_pk_idx on temp_ds_import_pk_map(source_pk);
create index temp_ds_import_pk_map_target_pk_idx on temp_ds_import_pk_map(target_pk);

drop table if exists temp_ds_import_queue cascade;
create table temp_ds_import_queue
(
  id bigserial primary key,
  import_code varchar(100) not null,
  created_on timestamp not null default statement_timestamp(),
  table_name text not null,
  content text not null  
);

create index temp_ds_import_queue_import_code_idx on temp_ds_import_queue(import_code);
create index temp_ds_import_queue_table_name_idx on temp_ds_import_queue(table_name);
