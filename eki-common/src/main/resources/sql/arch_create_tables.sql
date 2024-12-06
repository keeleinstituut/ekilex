
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

