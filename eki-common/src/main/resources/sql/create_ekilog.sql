-- drop

drop table if exists lexeme_activity_log;
drop table if exists word_activity_log;
drop table if exists word_last_activity_log;
drop table if exists meaning_activity_log;
drop table if exists meaning_last_activity_log;
drop table if exists source_activity_log;
drop table if exists activity_log;
drop view if exists source_activity_log_id_seq_view;
drop view if exists meaning_last_activity_log_id_seq_view;
drop view if exists meaning_activity_log_id_seq_view;
drop view if exists word_last_activity_log_id_seq_view;
drop view if exists word_activity_log_id_seq_view;
drop view if exists lexeme_activity_log_id_seq_view;
drop view if exists activity_log_id_seq_view;
drop sequence if exists source_activity_log_id_seq;
drop sequence if exists meaning_last_activity_log_id_seq;
drop sequence if exists meaning_activity_log_id_seq;
drop sequence if exists word_last_activity_log_id_seq;
drop sequence if exists word_activity_log_id_seq;
drop sequence if exists lexeme_activity_log_id_seq;
drop sequence if exists activity_log_id_seq;
drop type if exists type_activity_log_diff;

-- create

create type type_activity_log_diff as (
  op varchar(100), 
  path text, 
  value text
);

create sequence activity_log_id_seq start 1;

create view activity_log_id_seq_view as select nextval('activity_log_id_seq') as id;

create table activity_log (
  id bigint not null default nextval('activity_log_id_seq'),
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
  curr_diffs type_activity_log_diff array not null,
  primary key(id)
);

create sequence lexeme_activity_log_id_seq start 1;

create view lexeme_activity_log_id_seq_view as select nextval('lexeme_activity_log_id_seq') as id;

create table lexeme_activity_log (
  id bigint not null default nextval('lexeme_activity_log_id_seq'), 
  lexeme_id bigint not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null,
  primary key(id),
  unique(lexeme_id, activity_log_id)
);

create sequence word_activity_log_id_seq start 1;

create view word_activity_log_id_seq_view as select nextval('word_activity_log_id_seq') as id;

create table word_activity_log (
  id bigint not null default nextval('word_activity_log_id_seq'), 
  word_id bigint not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null,
  primary key(id),
  unique(word_id, activity_log_id)
);

create sequence word_last_activity_log_id_seq start 1;

create view word_last_activity_log_id_seq_view as select nextval('word_last_activity_log_id_seq') as id;

create table word_last_activity_log (
  id bigint not null default nextval('word_last_activity_log_id_seq'), 
  word_id bigint not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null,
  primary key(id),
  unique(word_id)
);

create sequence meaning_activity_log_id_seq start 1;

create view meaning_activity_log_id_seq_view as select nextval('meaning_activity_log_id_seq') as id;

create table meaning_activity_log (
  id bigint not null default nextval('meaning_activity_log_id_seq'), 
  meaning_id bigint not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null,
  primary key(id),
  unique(meaning_id, activity_log_id)
);

create sequence meaning_last_activity_log_id_seq start 1;

create view meaning_last_activity_log_id_seq_view as select nextval('meaning_last_activity_log_id_seq') as id;

create table meaning_last_activity_log (
  id bigint not null default nextval('meaning_last_activity_log_id_seq'), 
  meaning_id bigint not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null, 
  type varchar(100) not null,
  primary key(id),
  unique(meaning_id, type)
);

create sequence source_activity_log_id_seq start 1;

create view source_activity_log_id_seq_view as select nextval('source_activity_log_id_seq') as id;

create table source_activity_log (
  id bigint not null default nextval('source_activity_log_id_seq'), 
  source_id bigint not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null,
  primary key(id),
  unique(source_id, activity_log_id)
);

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
create index activity_curr_data_word_id_idx on activity_log(cast(curr_data ->> 'wordId' as bigint));
create index activity_curr_data_meaning_id_idx on activity_log(cast(curr_data ->> 'meaningId' as bigint));
create index activity_curr_data_lexeme_id_idx on activity_log(cast(curr_data ->> 'lexemeId' as bigint));
