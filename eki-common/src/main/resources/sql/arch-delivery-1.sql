-- upgrade from ver 1.38.* to 1.39.0 #2

-- ekilexi tegevuslogide arhiivi kolimine

create table activity_log_bulk (
  id bigserial primary key,
  activity_log_id bigint not null,
  owner_id bigint not null, 
  owner_name text not null, 
  entity_id bigint not null, 
  entity_name text not null, 
  prev_data jsonb not null, 
  curr_data jsonb not null
);
alter sequence activity_log_bulk_id_seq restart with 10000;
create index activity_log_bulk_activity_log_id_idx on activity_log_bulk(activity_log_id);
create index activity_log_bulk_owner_id_idx on activity_log_bulk(owner_id);
create index activity_log_bulk_owner_name_idx on activity_log_bulk(owner_name);
create index activity_log_bulk_entity_id_idx on activity_log_bulk(entity_id);
create index activity_log_bulk_entity_name_idx on activity_log_bulk(entity_name);
create index activity_log_bulk_curr_data_word_id_idx on activity_log_bulk(cast(curr_data ->> 'wordId' as bigint));
create index activity_log_bulk_curr_data_meaning_id_idx on activity_log_bulk(cast(curr_data ->> 'meaningId' as bigint));
create index activity_log_bulk_curr_data_lexeme_id_idx on activity_log_bulk(cast(curr_data ->> 'lexemeId' as bigint));

-- NB!!! lisa eelnevalt laiendus "dblink"!!!!

select dblink_connect('host=localhost user=ekilex password=3kil3x dbname=ekilex');

insert into activity_log_bulk (activity_log_id, owner_id, owner_name, entity_id, entity_name, prev_data, curr_data)
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select id, owner_id, owner_name, entity_id, entity_name, prev_data, curr_data from activity_log') as activity_log(
	activity_log_id bigint,
	owner_id bigint,
	owner_name text,
	entity_id bigint,
	entity_name text,
	prev_data jsonb,
	curr_data jsonb
);
analyze activity_log_bulk;
