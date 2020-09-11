alter table lexeme add column is_public boolean default true;
update lexeme set is_public = true where process_state_code = 'avalik';
update lexeme set is_public = false where process_state_code = 'mitteavalik';
update lexeme set is_public = false where process_state_code is null;
alter table lexeme alter column is_public set not null;
alter table freeform alter column is_public set not null;
alter table lexeme drop column process_state_code cascade;
drop table process_state cascade;
create index lexeme_is_public_idx on lexeme(is_public);

-- roheliste ilmikute detailsus sõltuvalt mustade detailsusest
update lexeme l
set complexity = l_c.complexity
from (select ls.id lexeme_id,
             (case
                when 'DETAIL' = all (array_agg(lp.complexity)) then 'DETAIL'
                else 'ANY'
              end) complexity
      from lexeme ls,
           lexeme lp
      where ls.type = 'SECONDARY'
        and lp.type = 'PRIMARY'
        and lp.is_public = true
        and lp.word_id = ls.word_id
      group by ls.id) l_c
where l.id = l_c.lexeme_id;

-- bookmarkide teisendamine ilmiku siltideks
insert into tag select distinct value_text from freeform where type = 'BOOKMARK';

insert into lexeme_tag (lexeme_id, tag_name)
select distinct lff.lexeme_id, ff.value_text
from lexeme_freeform lff,
     freeform ff
where lff.freeform_id = ff.id
  and ff.type = 'BOOKMARK'
  and not exists(select lt.id
                 from lexeme_tag lt
                 where lt.lexeme_id = lff.lexeme_id
                   and lt.tag_name = ff.value_text);

update lifecycle_log lcl
set entity_id = lff.lexeme_id,
    entity_name = 'LEXEME',
    entity_prop = 'TAG'
from lexeme_freeform lff,
     freeform ff
where lff.freeform_id = ff.id
  and ff.type = 'BOOKMARK'
  and lcl.entity_id = ff.id
  and lcl.entry = ff.value_text;

delete from freeform where type = 'BOOKMARK';

-- aspektide sildid sõnaveebis
insert into aspect_label (code, value, lang, type) values ('сов.', 'совершенный вид (что сделать)', 'est', 'wordweb');
insert into aspect_label (code, value, lang, type) values ('несов.', 'несовершенный вид (что делать)', 'est', 'wordweb');
insert into aspect_label (code, value, lang, type) values ('сов. и несов.', 'совершенный и несовершенный', 'est', 'wordweb');

---------------------------------------------
-- lifecycle log to activity log migration --
---------------------------------------------

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

create index lexeme_activity_log_lexeme_id_idx on lexeme_activity_log(lexeme_id);
create index lexeme_activity_log_log_id_idx on lexeme_activity_log(activity_log_id);
create index word_activity_log_word_id_idx on word_activity_log(word_id);
create index word_activity_log_log_id_idx on word_activity_log(activity_log_id);
create index meaning_activity_log_meaning_id_idx on meaning_activity_log(meaning_id);
create index meaning_activity_log_log_id_idx on meaning_activity_log(activity_log_id);
create index source_activity_log_source_id_idx on source_activity_log(source_id);
create index source_activity_log_log_id_idx on source_activity_log(activity_log_id);
create index activity_log_event_on_idx on activity_log(event_on);
create index activity_log_event_on_ms_idx on activity_log((date_part('epoch', event_on) * 1000));
create index activity_log_event_by_idx on activity_log(event_by);
create index activity_log_event_by_lower_idx on activity_log(lower(event_by));
create index activity_log_owner_idx on activity_log(owner_name, owner_id);
create index activity_funct_name_idx on activity_log(funct_name);
create index activity_entity_name_idx on activity_log(entity_name);
create index lifecycle_activity_log_ll_id_idx on lifecycle_activity_log(lifecycle_log_id);
create index lifecycle_activity_log_al_id_idx on lifecycle_activity_log(activity_log_id);

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

-- run this procedure separately ~1hr

do $$
declare
  ll_row lifecycle_log;
  ll_dat record;
  ff_dat record;
  al_id bigint;
  tmp_txt text;
  ll_row_cnt integer;
  ll_row_div integer;
  ll_row_prc integer;
  ll_row_itr integer := 0;
  ll_row_off integer := 0;
  ll_row_lim integer := 100000;
  ll_row_ts text;
begin
  alter sequence activity_log_id_seq restart with 10000;
  alter sequence lexeme_activity_log_id_seq restart with 10000;
  alter sequence word_activity_log_id_seq restart with 10000;
  alter sequence meaning_activity_log_id_seq restart with 10000;
  alter sequence source_activity_log_id_seq restart with 10000;
  select count(id) from lifecycle_log into ll_row_cnt;
  ll_row_div := ll_row_cnt / 100;
  raise info 'iterating over % lifecycle log records', ll_row_cnt;
  while ll_row_off < ll_row_cnt
  loop
    ll_row_itr := ll_row_off;
    for ll_row in
      (select * from lifecycle_log order by id offset ll_row_off limit ll_row_lim)
    loop
      select
      (select lll.lexeme_id from lexeme_lifecycle_log lll where lll.lifecycle_log_id = ll_row.id) lexeme_id,
      (select wll.word_id from word_lifecycle_log wll where wll.lifecycle_log_id = ll_row.id) word_id,
      (select mll.meaning_id from meaning_lifecycle_log mll where mll.lifecycle_log_id = ll_row.id) meaning_id,
      (select sll.source_id from source_lifecycle_log sll where sll.lifecycle_log_id = ll_row.id) source_id
      into ll_dat;
      ff_dat := null;
      tmp_txt := null;
      ll_row_itr := ll_row_itr + 1;
      if ll_row_itr % ll_row_div = 0 then
        ll_row_prc := round((ll_row_itr * 100) / ll_row_cnt);
        ll_row_ts := timeofday();
        raise info '[%] % perc. (row % / %)', ll_row_ts, ll_row_prc, ll_row_itr, ll_row_cnt;
      end if;
      if ll_row.entity_prop = 'ASPECT' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/aspectCode');
        perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
      elsif ll_row.entity_prop = 'COMPLEXITY' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/complexity');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'DATASET' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/datasetCode');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'DERIV' then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/derivs');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'DOMAIN' then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/domains');
        perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
      elsif ll_row.entity_prop = 'FREEFORM_SOURCE_LINK' then
        if ll_row.entity_name = 'LEXEME' then
          select distinct ff.type
          from lifecycle_log ll,
               lexeme_freeform lff,
               freeform ff,
               freeform_source_link fsl
          where ll.entity_name = ll_row.entity_name
          and   ll.entity_prop = ll_row.entity_prop
          and   lff.lexeme_id = ll.entity_id
          and   lff.freeform_id = ff.id
          and   fsl.freeform_id = ff.id
          and   ll.entity_id = ll_row.entity_id into tmp_txt;
          if tmp_txt = 'NOTE' then
            al_id := create_activity_log_unknown_entity(ll_row, 'LEXEME_NOTE_SOURCE_LINK', '/lexemeNoteLangGroups/notes/sourceLinks');
            perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
          elsif tmp_txt is not null then
            raise exception 'Unsupported freeform type % for the owner %', tmp_txt, ll_row.entity_name;
          end if;
        elsif ll_row.entity_name = 'MEANING' then
          select distinct ff.type
          from lifecycle_log ll,
               meaning_freeform mff,
               freeform ff,
               freeform_source_link fsl
          where ll.entity_name = ll_row.entity_name
          and   ll.entity_prop = ll_row.entity_prop
          and   mff.meaning_id = ll.entity_id
          and   mff.freeform_id = ff.id
          and   fsl.freeform_id = ff.id
          and   ll.entity_id = ll_row.entity_id into tmp_txt;
          if tmp_txt = 'NOTE' then
            al_id := create_activity_log_unknown_entity(ll_row, 'MEANING_NOTE_SOURCE_LINK', '/meaningNoteLangGroups/notes/sourceLinks');
            perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
          elsif tmp_txt is not null then
            raise exception 'Unsupported freeform type % for the owner %', tmp_txt, ll_row.entity_name;
          end if;
        elsif ll_row.entity_name = 'DEFINITION' then
          select distinct ff.type
          from lifecycle_log ll,
               definition_freeform dff,
               freeform ff,
               freeform_source_link fsl
          where ll.entity_name = ll_row.entity_name
          and   ll.entity_prop = ll_row.entity_prop
          and   dff.definition_id = ll.entity_id
          and   dff.freeform_id = ff.id
          and   fsl.freeform_id = ff.id
          and   ll.entity_id = ll_row.entity_id into tmp_txt;
          if tmp_txt = 'NOTE' then
            al_id := create_activity_log_for_owner_and_unknown_entity(ll_dat.meaning_id, 'MEANING', 'DEFINITION_NOTE_SOURCE_LINK', ll_row, '/definitions/notes/sourceLinks/value');
            perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
          elsif tmp_txt is not null then
            raise exception 'Unsupported freeform type % for the owner %', tmp_txt, ll_row.entity_name;
          end if;
        end if;
      elsif ll_row.entity_prop = 'FREQUENCY_GROUP' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/lexemeFrequencyGroupCode');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'GENDER' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/genderCode');
        perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
      elsif ll_row.entity_prop = 'ID' then
        if ll_row.event_type = 'ORDER_BY' then
          if ll_row.entity_name = 'LEXEME' then
            al_id := create_activity_log_same_owner_and_entity(ll_row, '/orderBy');
            perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
          elsif ll_row.entity_name = 'LEXEME_RELATION' then
            al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/lexemeRelations/orderBy');
            perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
          elsif ll_row.entity_name = 'GOVERNMENT' then
            al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/governments/orderBy');
            perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
          elsif ll_row.entity_name = 'USAGE' then
            al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/usages/orderBy');
            perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
          elsif ll_row.entity_name = 'DEFINITION' then
            al_id := create_activity_log_for_owner_and_entity(ll_dat.meaning_id, 'MEANING', ll_row, '/definitions/orderBy');
            perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
          elsif ll_row.entity_name = 'WORD_RELATION' then
            al_id := create_activity_log_for_owner_and_entity(ll_dat.word_id, 'WORD', ll_row, '/relations/orderBy');
            perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
          end if;
        elsif ll_row.event_type = 'CREATE' then
          if ll_row.entity_name = 'WORD' then
            select f.value from form f, paradigm p where p.word_id = ll_row.entity_id and f.paradigm_id = p.id and f.mode = 'WORD' limit 1 into tmp_txt;
            if tmp_txt is not null then
              ll_row.entry := tmp_txt;
              al_id := create_activity_log_same_owner_and_entity(ll_row, '/wordValue');
              perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
            end if;
          elsif ll_row.entity_name = 'WORD_ETYMOLOGY' then
            -- dont bother
          elsif ll_row.entity_name = 'MEANING' then
            al_id := create_activity_log_same_owner_and_entity(ll_row, '/meaningId');
            perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
          end if;
        end if;
      elsif ll_row.entity_prop = 'IMAGE' then
        al_id := create_activity_log_unknown_entity(ll_row, 'IMAGE_FILE', '/images');
        perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
      elsif ll_row.entity_prop = 'LANG' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/lang');
        perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
      elsif ll_row.entity_prop = 'LEVEL' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/levels');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'MEANING_WORD' then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/meaningWords/*');
        if ll_dat.lexeme_id is null then
          perform create_lexeme_activity_log_and_bindings(ll_row.entity_id, al_id);
        else
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        end if;
      elsif ll_row.entity_prop = 'MORPH_CODE' then
        al_id := create_activity_log_unknown_entity(ll_row, 'FORM', '/paradigms/forms/morphCode');
        perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
      elsif ll_row.entity_prop = 'NOTE' then
        if ll_row.entity_name = 'LEXEME' then
          al_id := create_activity_log_unknown_entity(ll_row, 'LEXEME_NOTE', '/lexemeNoteLangGroups/notes/valuePrese');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'WORD' then
          al_id := create_activity_log_unknown_entity(ll_row, 'WORD_NOTE', '/notes/valuePrese');
          perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
        elsif ll_row.entity_name = 'MEANING' then
          al_id := create_activity_log_unknown_entity(ll_row, 'MEANING_NOTE', '/meaningNoteLangGroups/notes/valuePrese');
          perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
        elsif ll_row.entity_name = 'DEFINITION' then
          al_id := create_activity_log_for_owner_and_unknown_entity(ll_dat.meaning_id, 'MEANING', 'DEFINITION_NOTE', ll_row, '/definitions/notes/valuePrese');
          perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
        elsif ll_row.entity_name = 'SOURCE' then
          al_id := create_activity_log_unknown_entity(ll_row, 'SOURCE_NOTE', '/sourceProperties/valueText');
          perform create_source_activity_log(ll_dat.source_id, al_id);
        end if;
      elsif ll_row.entity_prop = 'OD_ALTERNATIVE' then
        al_id := create_activity_log_for_owner_and_unknown_entity(ll_dat.lexeme_id, 'LEXEME', 'OD_USAGE_ALTERNATIVE', ll_row, '/usages/odAlternatives/value');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'OD_DEFINITION' then
        al_id := create_activity_log_for_owner_and_unknown_entity(ll_dat.lexeme_id, 'LEXEME', 'OD_USAGE_DEFINITION', ll_row, '/usages/odDefinitions/value');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'OD_RECOMMENDATION' then
        if ll_row.entity_name = 'LEXEME' then
          al_id := create_activity_log_unknown_entity(ll_row, 'OD_LEXEME_RECOMMENDATION', '/odLexemeRecommendations/valuePrese');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'WORD' then
          al_id := create_activity_log_unknown_entity(ll_row, 'OD_WORD_RECOMMENDATION', '/odWordRecommendations/valuePrese');
          perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
        end if;
      elsif ll_row.entity_prop = 'ORDER_BY' then
        ll_row.event_type := ll_row.entity_prop;
        if ll_row.entity_name = 'LEXEME' then
          al_id := create_activity_log_same_owner_and_entity(ll_row, '/orderBy');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'DEFINITION' then
          al_id := create_activity_log_for_owner_and_unknown_entity(ll_dat.meaning_id, 'MEANING', 'DEFINITION', ll_row, '/definitions/orderBy');
          perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
        end if;
      elsif ll_row.entity_prop = 'POS' then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/pos');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'PROCESS_STATE' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/lexemeProcessStateCode');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'REGION' then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/regions');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'REGISTER' then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/registers');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'SEMANTIC_TYPE' then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/semanticTypes/code');
        perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
      elsif ll_row.entity_prop = 'SOURCE_LINK' then
        if ll_row.entity_name = 'LEXEME' then
          al_id := create_activity_log_unknown_entity(ll_row, 'LEXEME_SOURCE_LINK', '/sourceLinks/value');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'USAGE' then
          if ll_dat.lexeme_id is not null then
            al_id := create_activity_log_for_owner_and_unknown_entity(ll_dat.lexeme_id, 'LEXEME', 'USAGE_SOURCE_LINK', ll_row, '/usages/sourceLinks/value');
            perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
          elsif ll_dat.meaning_id is not null then
            -- completely bonkers case
            ll_row.entity_name := 'MEANING_NOTE_SOURCE_LINK';
            al_id := create_activity_log_for_owner_and_entity(ll_dat.meaning_id, 'MEANING', ll_row, '/meaningNoteLangGroups/notes/sourceLinks');
            perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
          end if;
        elsif ll_row.entity_name = 'DEFINITION' then
          al_id := create_activity_log_for_owner_and_unknown_entity(ll_dat.meaning_id, 'MEANING', 'DEFINITION_SOURCE_LINK', ll_row, '/definitions/sourceLinks/value');
          perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
        end if;
      elsif ll_row.entity_prop = 'SOURCE_TYPE' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/type');
        perform create_source_activity_log(ll_dat.source_id, al_id);
      elsif ll_row.entity_prop in (
            'SOURCE_NAME', 'SOURCE_ARTICLE_AUTHOR', 'SOURCE_ARTICLE_TITLE', 'SOURCE_AUTHOR',
            'SOURCE_CELEX', 'SOURCE_EXPLANATION', 'SOURCE_FILE','SOURCE_ISBN', 'SOURCE_ISSN',
            'SOURCE_PUBLICATION_NAME', 'SOURCE_PUBLICATION_PLACE', 'SOURCE_PUBLICATION_YEAR',
            'SOURCE_PUBLISHER', 'SOURCE_RT', 'SOURCE_WWW', 'EXTERNAL_SOURCE_ID') then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/sourceProperties/valueText');
        perform create_source_activity_log(ll_dat.source_id, al_id);
      elsif ll_row.entity_prop = 'STATUS' then
        if ll_row.entity_name = 'WORD_RELATION' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.word_id, 'WORD', ll_row, '/relations/relationStatus');
          perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
        end if;
      elsif ll_row.entity_prop = 'TAG' then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/tags');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'VALUE' then
        if ll_row.entity_name = 'ATTRIBUTE_FREEFORM' then
          -- unable to extract all
          select ff.type,
                 lff.lexeme_id,
                 mff.meaning_id,
                 plff.lexeme_id parent_lexeme_id,
                 pmff.meaning_id parent_meaning_id
          from freeform ff
            left outer join lexeme_freeform lff on lff.freeform_id = ff.id
            left outer join meaning_freeform mff on mff.freeform_id = ff.id
            left outer join lexeme_freeform plff on plff.freeform_id = ff.parent_id
            left outer join meaning_freeform pmff on pmff.freeform_id = ff.parent_id
          where ff.id = ll_row.entity_id
          into ff_dat;
          if ff_dat.type in ('GOVERNMENT', 'GRAMMAR', 'USAGE') then
            ll_row.entity_name := ff_dat.type;
            al_id := create_activity_log_for_owner_and_entity(ff_dat.lexeme_id, 'LEXEME', ll_row, '/*');
            perform create_lexeme_activity_log_and_bindings(ff_dat.lexeme_id, al_id);
          elsif ff_dat.type in ('GOVERNMENT_PLACEMENT', 'USAGE_TRANSLATION') then
            ll_row.entity_name := ff_dat.type;
            al_id := create_activity_log_for_owner_and_entity(ff_dat.parent_lexeme_id, 'LEXEME', ll_row, '/*');
            perform create_lexeme_activity_log_and_bindings(ff_dat.parent_lexeme_id, al_id);
          elsif ff_dat.type in ('CONCEPT_ID', 'DESCRIBER', 'DESCRIBING_YEAR', 'FAMILY', 'GENUS', 'IMAGE_FILE', 'SEMANTIC_TYPE', 'SOURCE_FILE', 'SYSTEMATIC_POLYSEMY_PATTERN') then
            ll_row.entity_name := ff_dat.type;
            al_id := create_activity_log_for_owner_and_entity(ff_dat.meaning_id, 'MEANING', ll_row, '/*');
            perform create_meaning_activity_log_and_bindings(ff_dat.meaning_id, al_id);
          elsif ff_dat.type = 'SEMANTIC_TYPE_GROUP' then
            ll_row.entity_name := ff_dat.type;
            al_id := create_activity_log_for_owner_and_entity(ff_dat.parent_meaning_id, 'MEANING', ll_row, '/*');
            perform create_meaning_activity_log_and_bindings(ff_dat.parent_meaning_id, al_id);
          elsif ff_dat.type = 'NOTE' then
            if ff_dat.lexeme_id is not null then
              ll_row.entity_name := 'LEXEME_NOTE';
              al_id := create_activity_log_for_owner_and_entity(ff_dat.lexeme_id, 'LEXEME', ll_row, '/*');
              perform create_lexeme_activity_log_and_bindings(ff_dat.lexeme_id, al_id);
            elsif ff_dat.meaning_id is not null then
              ll_row.entity_name := 'MEANING_NOTE';
              al_id := create_activity_log_for_owner_and_entity(ff_dat.meaning_id, 'MEANING', ll_row, '/*');
              perform create_meaning_activity_log_and_bindings(ff_dat.meaning_id, al_id);
            end if;
          end if;
        elsif ll_row.entity_name = 'DEFINITION' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.meaning_id, 'MEANING', ll_row, '/definitions/value');
          perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
        elsif ll_row.entity_name = 'DEFINITION_PUBLIC_NOTE' then
          -- dont bother
        elsif ll_row.entity_name = 'DEFINITION_SOURCE_LINK' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.meaning_id, 'MEANING', ll_row, '/definitions/sourceLinks/value');
          perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
        elsif ll_row.entity_name = 'GOVERNMENT' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/governments/valuePrese');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'GRAMMAR' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/grammars/valuePrese');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'LEARNER_COMMENT' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.meaning_id, 'MEANING', ll_row, '/learnerComments/valuePrese');
          perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
        elsif ll_row.entity_name = 'LEXEME' then
          al_id := create_activity_log_same_owner_and_entity(ll_row, '/*');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'LEXEME_PUBLIC_NOTE' then
          al_id := create_activity_log_for_owner_and_unknown_entity(ll_dat.lexeme_id, 'LEXEME', 'NOTE', ll_row, '/lexemeNoteLangGroups/notes/valuePrese');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'LEXEME_RELATION' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/lexemeRelations/*');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'LEXEME_SOURCE_LINK' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/sourceLinks/value');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'MEANING' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.meaning_id, 'MEANING', ll_row, '/*');
          perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
        elsif ll_row.entity_name = 'MEANING_RELATION' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.meaning_id, 'MEANING', ll_row, '/relations/*');
          perform create_meaning_activity_log_and_bindings(ll_dat.meaning_id, al_id);
        elsif ll_row.entity_name = 'SOURCE' then
          al_id := create_activity_log_same_owner_and_entity(ll_row, '/*');
          perform create_source_activity_log(ll_dat.source_id, al_id);
        elsif ll_row.entity_name = 'USAGE' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/usages/value');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'USAGE_DEFINITION' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/usages/definitions/value');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'USAGE_TRANSLATION' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.lexeme_id, 'LEXEME', ll_row, '/usages/translations/value');
          perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
        elsif ll_row.entity_name = 'WORD' then
          al_id := create_activity_log_same_owner_and_entity(ll_row, '/*');
          perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
        elsif ll_row.entity_name = 'WORD_RELATION' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.word_id, 'WORD', ll_row, '/relations/*');
          perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
        elsif ll_row.entity_name = 'WORD_RELATION_GROUP_MEMBER' then
          al_id := create_activity_log_for_owner_and_entity(ll_dat.word_id, 'WORD', ll_row, '/groups/members/*');
          perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
        end if;
      elsif ll_row.entity_prop = 'VALUE_STATE' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/lexemeValueStateCode');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'VOCAL_FORM' then
        al_id := create_activity_log_unknown_entity(ll_row, 'FORM', '/paradigms/forms/vocalForm');
        perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
      elsif ll_row.entity_prop = 'WEIGHT' then
        al_id := create_activity_log_same_owner_and_entity(ll_row, '/weight');
        perform create_lexeme_activity_log_and_bindings(ll_dat.lexeme_id, al_id);
      elsif ll_row.entity_prop = 'WORD_TYPE' then
        al_id := create_activity_log_unknown_entity(ll_row, ll_row.entity_prop, '/wordTypeCodes');
        perform create_word_activity_log_and_bindings(ll_dat.word_id, al_id);
      end if;
    end loop;
    ll_row_off := ll_row_off + ll_row_lim;
  end loop;
  raise info 'All done!';
end $$;
commit;

drop function create_lexeme_activity_log(bigint, bigint);
drop function create_word_activity_log(bigint, bigint);
drop function create_meaning_activity_log(bigint, bigint);
drop function create_source_activity_log(bigint, bigint);
drop function create_lexeme_activity_log_and_bindings(bigint, bigint);
drop function create_word_activity_log_and_bindings(bigint, bigint);
drop function create_meaning_activity_log_and_bindings(bigint, bigint);
drop function create_activity_log_same_owner_and_entity(lifecycle_log, text);
drop function create_activity_log_unknown_entity(lifecycle_log, text, text);
drop function create_activity_log_for_owner_and_unknown_entity(bigint, text, text, lifecycle_log, text);
drop function create_activity_log_for_owner_and_entity(bigint, text, lifecycle_log, text);
drop function conv_event_type_to_op(varchar(100));
drop type wlm_id;

commit;
