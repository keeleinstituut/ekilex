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

-- uus tegevuslogi kraam

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

