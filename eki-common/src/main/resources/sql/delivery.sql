-- upgrade from ver 1.9.0 to 1.10.0

create index lifecycle_log_event_by_lower_idx on lifecycle_log(lower(event_by));
create index lifecycle_log_event_on_ms_idx on lifecycle_log((date_part('epoch', event_on) * 1000));
create index freeform_complexity_idx on freeform(complexity);

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

drop view view_ww_form;--NB!
drop view view_ww_lexeme;--NB!
drop view view_ww_word;--NB!

create sequence form_order_by_seq;
alter table form alter column order_by type bigint;
alter table form alter column order_by set default nextval('form_order_by_seq');

drop type type_usage;
create type type_usage as (usage text, usage_prese text, usage_lang char(3), complexity varchar(100), usage_type_code varchar(100), usage_translations text array, usage_definitions text array, od_usage_definitions text array, od_usage_alternatives text array, usage_authors text array);

--NB! restore the view_ww_form in create_views.sql
--NB! restore the view_ww_lexeme in create_views.sql
--NB! restore the view_ww_word in create_views.sql

update lifecycle_log lfcl
set entity_name = 'MEANING',
    entity_prop = 'DOMAIN',
    event_type  = 'ORDER_BY',
    entity_id   = md.meaning_id
from meaning_domain md
where md.id = lfcl.entity_id
  and lfcl.entity_name = 'MEANING_DOMAIN'
  and lfcl.entity_prop = 'ORDER_BY';

alter table lexeme alter column process_state_code set not null;

create table layer_state
(
	id bigserial primary key,
	lexeme_id bigint references lexeme(id) on delete cascade not null,
	layer_name varchar(100) not null,
	process_state_code varchar(100) references process_state(code) not null,
	unique(lexeme_id, layer_name)
);
alter sequence layer_state_id_seq restart with 10000;

create index layer_state_lexeme_id_idx on layer_state(lexeme_id);
create index layer_state_layer_name_idx on layer_state(layer_name);

create index domain_code_origin_idx on domain(code, origin);
create index domain_parent_code_origin_idx on domain(parent_code, parent_origin);
create index domain_label_code_origin_idx on domain_label(code, origin);
create index meaning_domain_code_origin_idx on meaning_domain(domain_code, domain_origin);

update lifecycle_log lcl
set entity_name = 'LEXEME',
	entity_prop = 'SOURCE_LINK',
	entity_id   = llcl.lexeme_id
from lexeme_lifecycle_log llcl
where lcl.id = llcl.lifecycle_log_id
  and lcl.entity_name = 'LEXEME_SOURCE_LINK'
  and lcl.entity_prop = 'VALUE';

update lifecycle_log lcl
set entity_name = 'DEFINITION',
	entity_prop = 'SOURCE_LINK',
	entity_id   = dsl.definition_id
from definition_source_link dsl
where lcl.entity_id = dsl.id
  and entity_name = 'DEFINITION_SOURCE_LINK'
  and lcl.entity_prop = 'VALUE';

update lifecycle_log lcl
set entity_name = 'LEXEME',
	entity_prop = 'PUBLIC_NOTE',
	entity_id   = llcl.lexeme_id
from lexeme_lifecycle_log llcl
where lcl.id = llcl.lifecycle_log_id
  and lcl.entity_name = 'LEXEME_PUBLIC_NOTE'
  and lcl.entity_prop = 'VALUE';

update lifecycle_log lcl
set entity_name = 'MEANING',
	entity_prop = 'PUBLIC_NOTE',
	entity_id   = mlcl.meaning_id
from meaning_lifecycle_log mlcl
where lcl.id = mlcl.lifecycle_log_id
  and lcl.entity_name = 'MEANING_PUBLIC_NOTE'
  and lcl.entity_prop = 'VALUE';

update lifecycle_log lcl
set entity_name = 'DEFINITION',
	entity_prop = 'PUBLIC_NOTE',
	entity_id   = df.definition_id
from definition_freeform df
where lcl.entity_id = df.freeform_id
  and lcl.entity_name = 'DEFINITION_PUBLIC_NOTE'
  and lcl.entity_prop = 'VALUE';

update lifecycle_log lcl
set entity_id = wlcl.word_id
from word_lifecycle_log wlcl
where lcl.id = wlcl.lifecycle_log_id
  and lcl.entity_name = 'WORD'
  and lcl.entity_prop = 'WORD_TYPE';

update lifecycle_log lcl
set entity_id = wlcl.word_id
from word_lifecycle_log wlcl
where lcl.id = wlcl.lifecycle_log_id
  and lcl.entity_name = 'WORD'
  and lcl.entity_prop = 'OD_RECOMMENDATION';

update lifecycle_log lcl
set entity_id = llcl.lexeme_id
from lexeme_lifecycle_log llcl
where lcl.id = llcl.lifecycle_log_id
  and lcl.entity_name = 'LEXEME'
  and lcl.entity_prop = 'POS';

update lifecycle_log lcl
set entity_id = llcl.lexeme_id
from lexeme_lifecycle_log llcl
where lcl.id = llcl.lifecycle_log_id
  and lcl.entity_name = 'LEXEME'
  and lcl.entity_prop = 'DERIV';

update lifecycle_log lcl
set entity_id = llcl.lexeme_id
from lexeme_lifecycle_log llcl
where lcl.id = llcl.lifecycle_log_id
  and lcl.entity_name = 'LEXEME'
  and lcl.entity_prop = 'REGISTER';

update lifecycle_log lcl
set entity_id = llcl.lexeme_id
from lexeme_lifecycle_log llcl
where lcl.id = llcl.lifecycle_log_id
  and lcl.entity_name = 'LEXEME'
  and lcl.entity_prop = 'REGION';

update lifecycle_log lcl
set entity_id = llcl.lexeme_id
from lexeme_lifecycle_log llcl
where lcl.id = llcl.lifecycle_log_id
  and lcl.entity_name = 'LEXEME'
  and lcl.entity_prop = 'OD_RECOMMENDATION';

update lifecycle_log lcl
set entity_id = llcl.lexeme_id
from lexeme_lifecycle_log llcl
where lcl.id = llcl.lifecycle_log_id
  and lcl.entity_name = 'LEXEME'
  and lcl.entity_prop = 'FREEFORM_SOURCE_LINK';

update lifecycle_log lcl
set entity_id = mlcl.meaning_id
from meaning_lifecycle_log mlcl
where lcl.id = mlcl.lifecycle_log_id
  and lcl.entity_name = 'MEANING'
  and lcl.entity_prop = 'DOMAIN';

update lifecycle_log lcl
set entity_id = mlcl.meaning_id
from meaning_lifecycle_log mlcl
where lcl.id = mlcl.lifecycle_log_id
  and lcl.entity_name = 'MEANING'
  and lcl.entity_prop = 'IMAGE_TITLE';

update lifecycle_log lcl
set entity_id = mlcl.meaning_id
from meaning_lifecycle_log mlcl
where lcl.id = mlcl.lifecycle_log_id
  and lcl.entity_name = 'MEANING'
  and lcl.entity_prop = 'IMAGE_TITLE';

update lifecycle_log lcl
set entity_id = mlcl.meaning_id
from meaning_lifecycle_log mlcl
where lcl.id = mlcl.lifecycle_log_id
  and lcl.entity_name = 'MEANING'
  and lcl.entity_prop = 'IMAGE';

update lifecycle_log lcl
set entity_id = mlcl.meaning_id
from meaning_lifecycle_log mlcl
where lcl.id = mlcl.lifecycle_log_id
  and lcl.entity_name = 'MEANING'
  and lcl.entity_prop = 'SEMANTIC_TYPE';

update lifecycle_log lcl
set entity_id = mlcl.meaning_id
from meaning_lifecycle_log mlcl
where lcl.id = mlcl.lifecycle_log_id
  and lcl.entity_name = 'MEANING'
  and lcl.entity_prop = 'FREEFORM_SOURCE_LINK';

update lifecycle_log lcl
set entity_name = 'USAGE',
	entity_prop = 'SOURCE_LINK'
where entity_name = 'FREEFORM_SOURCE_LINK'
  and entity_prop = 'VALUE';

alter table process_log add column layer_name varchar(100) null;