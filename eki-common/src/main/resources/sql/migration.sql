-- 29.10.2019
create index lifecycle_log_event_by_lower_idx on lifecycle_log(lower(event_by));
create index lifecycle_log_event_on_ms_idx on lifecycle_log((date_part('epoch', event_on) * 1000));
create index freeform_complexity_idx on freeform(complexity);

-- 05.11.2019
insert into word_rel_mapping (code1, code2) values ('head', 'ühend');

create table word_freeform
(
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  freeform_id bigint references freeform(id) on delete cascade not null,
  order_by bigserial,
  unique(word_id, freeform_id)
);
alter sequence word_freeform_id_seq restart with 10000;

create index word_freeform_word_id_idx on word_freeform(word_id);
create index word_freeform_freeform_id_idx on word_freeform(freeform_id);

do $$
declare
  lex_rel constant lex_rel_type.code%type := 'pyh';
  opposite_lex_rel constant lex_rel_type.code%type := 'head';
  word_rel constant word_rel_type.code%type := 'ühend';
  opposite_word_rel constant word_rel_type.code%type := 'head';
  rel_moved_counter integer := 0;
  opposite_rel_moved_counter integer := 0;
  word1_id word.id%type;
  word2_id word.id%type;
  lex_rel_row lex_relation%rowtype;
  opposite_lex_rel_id lex_relation.id%type;
begin
  for lex_rel_row in
    select * from lex_relation where lex_rel_type_code = lex_rel
    loop
      select lexeme.word_id into word1_id from lexeme where id = lex_rel_row.lexeme1_id;
      select lexeme.word_id into word2_id from lexeme where id = lex_rel_row.lexeme2_id;

      insert into word_relation (word1_id, word2_id, word_rel_type_code) values (word1_id, word2_id, word_rel) on conflict do nothing;
      delete from lex_relation where id = lex_rel_row.id;
      rel_moved_counter := rel_moved_counter + 1;

      select id into opposite_lex_rel_id from lex_relation where lexeme1_id = lex_rel_row.lexeme2_id and lexeme2_id = lex_rel_row.lexeme1_id and lex_rel_type_code = opposite_lex_rel;
      if opposite_lex_rel_id is not null then
        insert into word_relation (word1_id, word2_id, word_rel_type_code) values (word2_id, word1_id, opposite_word_rel) on conflict do nothing;
        delete from lex_relation where id = opposite_lex_rel_id;
        opposite_rel_moved_counter := opposite_rel_moved_counter + 1;
      end if;
    end loop;
  RAISE notice '% lexeme relations moved to word relations', rel_moved_counter;
  RAISE notice '% opposite lexeme relations moved to opposite word relations', opposite_rel_moved_counter;
end $$;

delete from lex_rel_mapping where code1 = 'pyh';
delete from lex_rel_mapping where code2 = 'pyh';
delete from lex_rel_type where code = 'pyh';

-- 13.11.2019

drop view view_ww_form;--NB!

create sequence form_order_by_seq;
alter table form alter column order_by type bigint;
alter table form alter column order_by set default nextval('form_order_by_seq');

--NB! restore the view_ww_form in create_views.sql

-- 21.11.2019
drop view view_ww_lexeme;--NB!
drop view view_ww_word;--NB!

drop type type_usage;
create type type_usage as (usage text, usage_prese text, usage_lang char(3), complexity varchar(100), usage_type_code varchar(100), usage_translations text array, usage_definitions text array, od_usage_definitions text array, od_usage_alternatives text array, usage_authors text array);

--NB! restore the view_ww_lexeme in create_views.sql
--NB! restore the view_ww_word in create_views.sql

-- 22.11.2019
update lifecycle_log lfcl
set entity_name = 'MEANING',
    entity_prop = 'DOMAIN',
    event_type  = 'ORDER_BY',
    entity_id   = md.meaning_id
from meaning_domain md
where md.id = lfcl.entity_id
  and lfcl.entity_name = 'MEANING_DOMAIN'
  and lfcl.entity_prop = 'ORDER_BY';

-- 25.11.2019
update freeform set type = 'OD_WORD_RECOMMENDATION' where type = 'OD_WORD_SUGGESTION';
update freeform set type = 'OD_LEXEME_RECOMMENDATION' where type = 'OD_LEXEME_SUGGESTION';
update freeform set type = 'OD_USAGE_ALTERNATIVE' where type = 'OD_USAGE_VERSION';
update lifecycle_log set entity_prop = 'OD_RECOMMENDATION' where entity_prop = 'OD_SUGGESTION';
update lifecycle_log set entity_prop = 'OD_ALTERNATIVE' where entity_prop = 'OD_VERSION';

drop view view_ww_lexeme;--NB!
drop view view_ww_word;--NB!

drop type type_usage;
create type type_usage as (usage text, usage_prese text, usage_lang char(3), complexity varchar(100), usage_type_code varchar(100), usage_translations text array, usage_definitions text array, od_usage_definitions text array, od_usage_alternatives text array, usage_authors text array);

--NB! restore the view_ww_lexeme in create_views.sql
--NB! restore the view_ww_word in create_views.sql

--> kuni siiani testis olemas 25.11.2019