---------------------------------------------
-- lifecycle log to activity log migration --
---------------------------------------------

create type type_activity_log_diff as (
  op varchar(100),
  path text,
  value text
);

create table activity_log
(
  id bigserial primary key,
  event_by text not null,
  event_on timestamp not null default statement_timestamp(),  
  funct_name text not null,
  owner_id bigint not null,
  owner_name text not null,
  entity_id bigint not null,
  entity_name text not null,
  prev_data jsonb not null,
  curr_data jsonb not null,
  prev_diffs type_activity_log_diff array not null,
  curr_diffs type_activity_log_diff array not null
);
alter sequence activity_log_id_seq restart with 10000;

create table lexeme_activity_log
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  activity_log_id bigint references activity_log(id) on delete cascade not null,
  unique(lexeme_id, activity_log_id)
);
alter sequence lexeme_activity_log_id_seq restart with 10000;

create table word_activity_log
(
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  activity_log_id bigint references activity_log(id) on delete cascade not null,
  unique(word_id, activity_log_id)
);
alter sequence word_activity_log_id_seq restart with 10000;

create table meaning_activity_log
(
  id bigserial primary key,
  meaning_id bigint references meaning(id) on delete cascade not null,
  activity_log_id bigint references activity_log(id) on delete cascade not null,
  unique(meaning_id, activity_log_id)
);
alter sequence meaning_activity_log_id_seq restart with 10000;

create table source_activity_log
(
  id bigserial primary key,
  source_id bigint references source(id) on delete cascade not null,
  activity_log_id bigint references activity_log(id) on delete cascade not null,
  unique(source_id, activity_log_id)
);
alter sequence source_activity_log_id_seq restart with 10000;

create table lifecycle_activity_log
(
id bigserial primary key,
lifecycle_log_id bigint references lifecycle_log(id) on delete cascade not null,
activity_log_id bigint references activity_log(id) on delete cascade not null,
unique(lifecycle_log_id,activity_log_id)
);
alter sequence lifecycle_activity_log_id_seq restart with 10000;

create index lifecycle_log_entity_id_idx on lifecycle_log(entity_id);
create index lifecycle_log_entity_name_idx on lifecycle_log(entity_name);

create type wlm_id as (
  lexeme_id bigint,
  word_id bigint,
  meaning_id bigint
);

create or replace function conv_event_type_to_op(event_type varchar(100))
returns varchar(100)
language plpgsql 
as 
$$
begin
  if event_type = 'CREATE' then
    return 'add';
  elsif event_type = 'UPDATE' then
    return 'replace';
  elsif event_type = 'DELETE' then
    return 'remove';
  else 
    return 'replace';
  end if;
end $$;

create or replace function create_activity_log_same_owner_and_entity(ll_row lifecycle_log, diff_path text)
returns bigint
language plpgsql
as
$$
declare
  op varchar(100);
  al_id bigint;
begin
  if ll_row.entity_name not in ('LEXEME', 'WORD', 'MEANING', 'SOURCE') then
    raise exception 'illegal owner %', ll_row.entity_name;
  end if;
  op := conv_event_type_to_op(ll_row.event_type);
  insert into activity_log (event_by, event_on, funct_name, owner_id, owner_name, entity_id, entity_name, prev_data, curr_data, prev_diffs, curr_diffs)
  values  (ll_row.event_by, ll_row.event_on, lower(ll_row.event_type), ll_row.entity_id, ll_row.entity_name, ll_row.entity_id, ll_row.entity_name, '{"data" : "n/a"}', '{"data" : "n/a"}', array[row('-', diff_path, ll_row.recent)::type_activity_log_diff], array[row(op, diff_path, ll_row.entry)::type_activity_log_diff])
  returning id into al_id;
  insert into lifecycle_activity_log (lifecycle_log_id, activity_log_id) values (ll_row.id, al_id);
  return al_id;
end $$;

create or replace function create_activity_log_unknown_entity(ll_row lifecycle_log, entity_name text, diff_path text)
returns bigint
language plpgsql
as
$$
declare
  op varchar(100);
  al_id bigint;
begin
  if ll_row.entity_name not in ('LEXEME', 'WORD', 'MEANING', 'SOURCE') then
    raise exception 'illegal owner %', ll_row.entity_name;
  end if;
  op := conv_event_type_to_op(ll_row.event_type);
  insert into activity_log (event_by, event_on, funct_name, owner_id, owner_name, entity_id, entity_name, prev_data, curr_data, prev_diffs, curr_diffs)
  values  (ll_row.event_by, ll_row.event_on, lower(ll_row.event_type), ll_row.entity_id, ll_row.entity_name, -1, entity_name, '{"data" : "n/a"}', '{"data" : "n/a"}', array[row('-', diff_path, ll_row.recent)::type_activity_log_diff], array[row(op, diff_path, ll_row.entry)::type_activity_log_diff])
  returning id into al_id;
  insert into lifecycle_activity_log (lifecycle_log_id, activity_log_id) values (ll_row.id, al_id);
  return al_id;
end $$;

create or replace function create_activity_log_for_owner_and_unknown_entity(owner_id bigint, owner_name text, entity_name text, ll_row lifecycle_log, diff_path text)
returns bigint
language plpgsql
as
$$
declare
  op varchar(100);
  al_id bigint;
begin
  op := conv_event_type_to_op(ll_row.event_type);
  insert into activity_log (event_by, event_on, funct_name, owner_id, owner_name, entity_id, entity_name, prev_data, curr_data, prev_diffs, curr_diffs)
  values  (ll_row.event_by, ll_row.event_on, lower(ll_row.event_type), owner_id, owner_name, -1, entity_name, '{"data" : "n/a"}', '{"data" : "n/a"}', array[row('-', diff_path, ll_row.recent)::type_activity_log_diff], array[row(op, diff_path, ll_row.entry)::type_activity_log_diff])
  returning id into al_id;
  insert into lifecycle_activity_log (lifecycle_log_id, activity_log_id) values (ll_row.id, al_id);
  return al_id;
end $$;

create or replace function create_activity_log_for_owner_and_entity(owner_id bigint, owner_name text, ll_row lifecycle_log, diff_path text)
returns bigint
language plpgsql
as
$$
declare
  op varchar(100);
  al_id bigint;
begin
  op := conv_event_type_to_op(ll_row.event_type);
  insert into activity_log (event_by, event_on, funct_name, owner_id, owner_name, entity_id, entity_name, prev_data, curr_data, prev_diffs, curr_diffs)
  values  (ll_row.event_by, ll_row.event_on, lower(ll_row.event_type), owner_id, owner_name, ll_row.entity_id, ll_row.entity_name, '{"data" : "n/a"}', '{"data" : "n/a"}', array[row('-', diff_path, ll_row.recent)::type_activity_log_diff], array[row(op, diff_path, ll_row.entry)::type_activity_log_diff])
  returning id into al_id;
  insert into lifecycle_activity_log (lifecycle_log_id, activity_log_id) values (ll_row.id, al_id);
  return al_id;
end $$;

create or replace function create_lexeme_activity_log(lexeme_id bigint, activity_log_id bigint)
returns bigint
language plpgsql
as
$$
declare
  lal_id bigint;
begin
  if lexeme_id is null then
    raise exception 'missing lexeme_id';
  end if;
  insert into lexeme_activity_log (lexeme_id, activity_log_id) select lexeme_id, activity_log_id where exists (select l.id from lexeme l where l.id = lexeme_id) returning id into lal_id;
  return lal_id;
end $$;

create or replace function create_word_activity_log(owner_word_id bigint, owner_activity_log_id bigint)
returns bigint
language plpgsql
as
$$
declare
  wal_id bigint;
begin
  if owner_word_id is null then
    raise exception 'missing owner_word_id';
  end if;
  insert into word_activity_log
  (
    word_id,
    activity_log_id
  )
  select owner_word_id,
         owner_activity_log_id
         where 
           exists (
             select w.id from word w where w.id = owner_word_id)
           and not exists (
             select wal.id
             from word_activity_log wal
             where wal.word_id = owner_word_id
             and   wal.activity_log_id = owner_activity_log_id) returning id into wal_id;
  return wal_id;
end $$;

create or replace function create_meaning_activity_log(owner_meaning_id bigint, owner_activity_log_id bigint)
returns bigint
language plpgsql
as
$$
declare
  mal_id bigint;
begin
  if owner_meaning_id is null then
    raise exception 'missing owner_meaning_id';
  end if;
  insert into meaning_activity_log
  (
    meaning_id,
    activity_log_id
  )
  select owner_meaning_id,
         owner_activity_log_id 
         where exists (
           select m.id from meaning m where m.id = owner_meaning_id)
         and not exists (
           select mal.id
           from meaning_activity_log mal
           where mal.meaning_id = owner_meaning_id
           and   mal.activity_log_id = owner_activity_log_id) returning id into mal_id;
  return mal_id;
end $$;

create or replace function create_source_activity_log(source_id bigint, activity_log_id bigint)
returns bigint
language plpgsql
as
$$
declare
  sal_id bigint;
begin
  if source_id is null then
    raise exception 'missing source_id';
  end if;
  insert into source_activity_log (source_id, activity_log_id) select source_id, activity_log_id where exists (select s.id from source s where s.id = source_id) returning id into sal_id;
  return sal_id;
end $$;

create or replace function create_lexeme_activity_log_and_bindings(owner_lexeme_id bigint, activity_log_id bigint)
returns void
language plpgsql
as
$$
declare
  wlm_id_row wlm_id;
begin
  perform create_lexeme_activity_log(owner_lexeme_id, activity_log_id);
  for wlm_id_row in
    (select l.id lexeme_id, l.word_id, l.meaning_id from lexeme l where l.id = owner_lexeme_id)
  loop
    perform create_word_activity_log(wlm_id_row.word_id, activity_log_id);
    perform create_meaning_activity_log(wlm_id_row.meaning_id, activity_log_id);
  end loop;
end $$;

create or replace function create_word_activity_log_and_bindings(owner_word_id bigint, activity_log_id bigint)
returns void
language plpgsql
as
$$
declare
  wlm_id_row wlm_id;
begin
  perform create_word_activity_log(owner_word_id, activity_log_id);
  for wlm_id_row in
    (select l.id lexeme_id, l.word_id, l.meaning_id from lexeme l where l.word_id = owner_word_id and l.type = 'PRIMARY')
  loop
    perform create_lexeme_activity_log(wlm_id_row.lexeme_id, activity_log_id);
    perform create_meaning_activity_log(wlm_id_row.meaning_id, activity_log_id);
  end loop;
end $$;

create or replace function create_meaning_activity_log_and_bindings(owner_meaning_id bigint, activity_log_id bigint)
returns void
language plpgsql
as
$$
declare
  wlm_id_row wlm_id;
begin
  perform create_meaning_activity_log(owner_meaning_id, activity_log_id);
  for wlm_id_row in
    (select l.id lexeme_id, l.word_id, l.meaning_id from lexeme l where l.meaning_id = owner_meaning_id and l.type = 'PRIMARY')
  loop
    perform create_lexeme_activity_log(wlm_id_row.lexeme_id, activity_log_id);
    perform create_word_activity_log(wlm_id_row.word_id, activity_log_id);
  end loop;
end $$;

