-- kustutab sellised keelendid, millel primary ilmikud puuduvad
do $$
declare
  w_id word.id%type;
  deleted_words_counter integer := 0;
begin
  for w_id in
    (select w.id
     from word w
     where exists(select l.id
                  from lexeme l
                  where l.word_id = w.id
                    and l.type = 'SECONDARY')
       and not exists(select l.id
                      from lexeme l
                      where l.word_id = w.id
                        and l.type = 'PRIMARY'))
    loop
      delete from lifecycle_log lcl using word_lifecycle_log wlcl where wlcl.id = wlcl.lifecycle_log_id and wlcl.word_id = w_id;
      delete from process_log pl using word_process_log wpl where wpl.id = wpl.process_log_id and wpl.word_id = w_id;
      delete from freeform ff using word_freeform wff where ff.id = wff.freeform_id and wff.word_id = w_id;
      delete from lexeme where word_id = w_id;
      delete from word where id = w_id;
      deleted_words_counter := deleted_words_counter + 1;
    end loop;
  raise notice '% words deleted', deleted_words_counter;
end $$;

alter table definition add column is_public boolean default true;
alter table freeform add column is_public boolean default true;

-- kõrvaldab erinevad reavahetused, tabulaatori, topelttühikud definitsioonidest ja kõigist vabavormidest
update definition
   set value_prese = trim(regexp_replace(regexp_replace(value_prese, '\r|\n|\t', ' ', 'g'), '\s+', ' ', 'g')),
   value = trim(regexp_replace(regexp_replace(value, '\r|\n|\t', ' ', 'g'), '\s+', ' ', 'g'))
   where value_prese != trim(regexp_replace(regexp_replace(value_prese, '\r|\n|\t', ' ', 'g'), '\s+', ' ', 'g'));

update freeform
   set value_prese = trim(regexp_replace(regexp_replace(value_prese, '\r|\n|\t', ' ', 'g'), '\s+', ' ', 'g')),
   value_text = trim(regexp_replace(regexp_replace(value_text, '\r|\n|\t', ' ', 'g'), '\s+', ' ', 'g'))
   where value_prese != trim(regexp_replace(regexp_replace(value_prese, '\r|\n|\t', ' ', 'g'), '\s+', ' ', 'g'));

-- väärtusolekute jrk

update value_state set order_by = 5 where code = 'väldi';
update value_state set order_by = 4 where code = 'endine';

-- uus termini db tüübi struktuur

drop type type_term_meaning_word;
create type type_term_meaning_word as (
				word_id bigint,
				word_value text,
				word_value_prese text,
				homonym_nr integer,
				lang char(3),
				word_type_codes varchar(100) array,
				prefixoid boolean,
				suffixoid boolean,
				"foreign" boolean,
				matching_word boolean,
				most_preferred boolean,
				least_preferred boolean,
				is_public boolean,
				dataset_codes varchar(10) array);

-- ilma keeleta märkustele keele määramine
update freeform ff
set lang = d.lang
from definition_freeform dff,
     definition d
where ff.lang is null
  and ff.type = 'NOTE'
  and ff.id = dff.freeform_id
  and dff.definition_id = d.id;

update freeform ff
set lang = w.lang
from lexeme_freeform lff,
     lexeme l,
     word w
where ff.lang is null
  and ff.type = 'NOTE'
  and ff.id = lff.freeform_id
  and lff.lexeme_id = l.id
  and l.word_id = w.id;

update freeform ff
set lang = case
             when ff.value_text ilike 'note%' then 'eng'
             else 'est'
           end
from meaning_freeform mff
where ff.lang is null
  and ff.type = 'NOTE'
  and ff.id = mff.freeform_id;

-- detailsuste muudatused
insert into definition_type (code, datasets) values ('lühivihje', '{}');

update definition d
set complexity = 'ANY'
where d.complexity = 'SIMPLE'
  and not exists(select d2.id
                 from definition d2
                 where d2.meaning_id = d.meaning_id
                   and d2.id != d.id
                   and d2.complexity in ('DETAIL', 'ANY'));

update definition
set complexity = 'ANY',
    definition_type_code = 'lühivihje',
    is_public = false
where complexity in ('SIMPLE2', 'DETAIL2');

update freeform ff
set complexity = 'SIMPLE'
where ff.type = 'IMAGE_FILE'
  and exists(select l.id
             from lexeme l,
                  meaning_freeform mff
             where mff.freeform_id = ff.id
               and l.meaning_id = mff.meaning_id
               and l.dataset_code = 'sss');

update definition set complexity = 'DETAIL' where complexity = 'DEFAULT';
update freeform set complexity = 'DETAIL' where type = 'USAGE' and complexity = 'DEFAULT';
update freeform set complexity = 'ANY', is_public = false where type = 'USAGE' and complexity = 'DETAIL2';
update freeform set complexity = 'ANY', is_public = false where type = 'USAGE' and complexity = 'SIMPLE2';
update freeform set complexity = 'ANY' where type = 'GOVERNMENT';
update freeform set complexity = 'DETAIL' where type = 'GRAMMAR';
update freeform set complexity = 'SIMPLE' where type = 'LEARNER_COMMENT';
update freeform set complexity = 'DETAIL' where type = 'OD_LEXEME_RECOMMENDATION';
update freeform set complexity = 'DETAIL' where type = 'OD_WORD_RECOMMENDATION';
update freeform set complexity = 'DETAIL' where type = 'BOOKMARK';
update freeform set complexity = 'DETAIL' where type = 'PUBLIC_NOTE' and complexity = 'DEFAULT';
update collocation set complexity = 'ANY' where complexity = 'SIMPLE';

--rõhu märgenduse eemaldamine ё tähelt
update freeform
set value_prese = replace(value_prese, '<eki-stress>ё</eki-stress>', 'ё')
where value_prese like '%<eki-stress>ё</eki-stress>%';

update lexeme set complexity = 'DETAIL' where complexity = 'DEFAULT';
update lexeme set complexity = 'DETAIL' where complexity = 'SIMPLE' and dataset_code = 'rmtk';
update lexeme set complexity = 'ANY' where complexity = 'SIMPLE';

--kokkulangevate definitsioonide kustutamine
delete
from definition d
where d.complexity = 'ANY'
  and d.definition_type_code = 'lühivihje'
  and d.is_public = false
  and exists(select d2.id
             from definition d2
             where d2.value_prese = d.value_prese
               and d2.complexity in ('DETAIL', 'DETAIL1')
               and d2.meaning_id = d.meaning_id);

update freeform ff set complexity = 'DETAIL' from meaning_freeform mff where ff.complexity is null and ff.type = 'PUBLIC_NOTE' and ff.id = mff.freeform_id;
update freeform ff set complexity = 'DETAIL' from lexeme_freeform lff where ff.complexity is null and ff.type = 'PUBLIC_NOTE' and ff.id = lff.freeform_id;

-- ilmikute sildid
create table tag
(
  name varchar(100) primary key,
  set_automatically boolean default false,
  order_by bigserial
);

create table lexeme_tag
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  tag_name varchar(100) references tag(name) on delete cascade not null,
  created_on timestamp not null default statement_timestamp(),
  unique(lexeme_id, tag_name)
);
alter sequence lexeme_tag_id_seq restart with 10000;

create index lexeme_tag_lexeme_id_idx on lexeme_tag(lexeme_id);
create index lexeme_tag_tag_name_idx on lexeme_tag(tag_name);

insert into tag select distinct process_state_code from lexeme where process_state_code != 'avalik';
insert into lexeme_tag (lexeme_id, tag_name) select l.id, l.process_state_code from lexeme l where l.process_state_code != 'avalik';
insert into process_state (code, datasets) values ('mitteavalik', '{}');
update lexeme set process_state_code = 'mitteavalik' where process_state_code != 'avalik';
alter table eki_user_profile add column preferred_tag_names varchar(100) array;
alter table eki_user_profile add column active_tag_name varchar(100) references tag(name);
alter table eki_user add column api_key varchar(100) null;
alter table eki_user add column is_api_crud boolean default false;
create index eki_user_email_idx on eki_user(email);
create index eki_user_api_key_idx on eki_user(api_key);

update freeform set type = 'NOTE' where type = 'PUBLIC_NOTE';
update lifecycle_log set entity_prop = 'NOTE' where entity_prop = 'PUBLIC_NOTE';

-- kolib teksti kujul olevad protsessi logid keelendi, tähenduse ja ilmiku märkuste vabavormidesse ning elutsükli logidesse
do $$
declare
  pl_row record;
  ff_id freeform.id%type;
  lcl_id lifecycle_log.id%type;
  moved_word_logs_counter integer := 0;
  moved_meaning_logs_counter integer := 0;
  moved_mil_lexeme_logs_counter integer := 0;
begin
  for pl_row in
    (select wpl.word_id, wpl.process_log_id, pl.event_by, pl.event_on, pl.comment, pl.comment_prese
    from word_process_log wpl, process_log pl
    where pl.id = wpl.process_log_id
    order by pl.id desc)
  loop
    insert into freeform (type, value_text, value_prese, lang, complexity, is_public)
    values ('NOTE', pl_row.comment, pl_row.comment_prese, 'est', 'DETAIL', false)
    returning id into ff_id;

    insert into lifecycle_log (entity_id, entity_name, entity_prop, event_type, event_by, event_on, entry)
    values (pl_row.word_id, 'WORD', 'NOTE', 'CREATE', pl_row.event_by, pl_row.event_on, pl_row.comment_prese)
    returning id into lcl_id;

    insert into word_freeform (word_id, freeform_id) values (pl_row.word_id, ff_id);
    insert into word_lifecycle_log (word_id, lifecycle_log_id) values (pl_row.word_id, lcl_id);
    delete from process_log where id = pl_row.process_log_id;
    moved_word_logs_counter := moved_word_logs_counter + 1;
  end loop;
  raise notice '% keelendi protsessi logi kolitud vabavormidesse ning elutsükli logidesse', moved_word_logs_counter;

  for pl_row in
    (select mpl.meaning_id, mpl.process_log_id, pl.event_by, pl.event_on, pl.comment, pl.comment_prese
     from meaning_process_log mpl, process_log pl
     where pl.id = mpl.process_log_id
     order by pl.id desc)
    loop
      insert into freeform (type, value_text, value_prese, lang, complexity, is_public)
      values ('NOTE', pl_row.comment, pl_row.comment_prese, 'est', 'DETAIL', false)
      returning id into ff_id;

      insert into lifecycle_log (entity_id, entity_name, entity_prop, event_type, event_by, event_on, entry)
      values (pl_row.meaning_id, 'MEANING', 'NOTE', 'CREATE', pl_row.event_by, pl_row.event_on, pl_row.comment_prese)
      returning id into lcl_id;

      insert into meaning_freeform (meaning_id, freeform_id) values (pl_row.meaning_id, ff_id);
      insert into meaning_lifecycle_log (meaning_id, lifecycle_log_id) values (pl_row.meaning_id, lcl_id);
      delete from process_log where id = pl_row.process_log_id;
      moved_meaning_logs_counter := moved_meaning_logs_counter + 1;
    end loop;
  raise notice '% tähenduse protsessi logi kolitud vabavormidesse ning elutsükli logidesse', moved_meaning_logs_counter;

  for pl_row in
    (select lpl.lexeme_id, lpl.process_log_id, pl.event_by, pl.event_on, pl.comment, pl.comment_prese
     from lexeme_process_log lpl, process_log pl
     where pl.id = lpl.process_log_id
       and pl.dataset_code = 'mil'
       and pl.process_state_code is null
     order by pl.id desc)
    loop
      insert into freeform (type, value_text, value_prese, lang, complexity, is_public)
      values ('NOTE', pl_row.comment, pl_row.comment_prese, 'est', 'DETAIL', false)
      returning id into ff_id;

      insert into lifecycle_log (entity_id, entity_name, entity_prop, event_type, event_by, event_on, entry)
      values (pl_row.lexeme_id, 'LEXEME', 'NOTE', 'CREATE', pl_row.event_by, pl_row.event_on, pl_row.comment_prese)
      returning id into lcl_id;

      insert into lexeme_freeform (lexeme_id, freeform_id) values (pl_row.lexeme_id, ff_id);
      insert into lexeme_lifecycle_log (lexeme_id, lifecycle_log_id) values (pl_row.lexeme_id, lcl_id);
      delete from process_log where id = pl_row.process_log_id;
      moved_mil_lexeme_logs_counter := moved_mil_lexeme_logs_counter + 1;
    end loop;
  raise notice '% militermi ilmiku protsessi logi kolitud vabavormidesse ning elutsükli logidesse', moved_mil_lexeme_logs_counter;
end $$;

drop table word_process_log;
drop table meaning_process_log;

alter table eki_user_profile drop column preferred_layer_name;

-- kolib süno kihis märgitud 'valmis' olekud ilmiku siltidesse
insert into tag (name) values ('süno valmis');
insert into lexeme_tag (lexeme_id, tag_name) select ls.lexeme_id, 'süno valmis' from layer_state ls where ls.layer_name = 'SYN' and ls.process_state_code = 'valmis';
drop table layer_state;

-- kolib ilmiku haldusoleku muutuse logid ilmiku elutsükli logidesse
do $$
declare
  pl_row record;
  lcl_id lifecycle_log.id%type;
  entry_text text;
  moved_logs_counter integer := 0;
begin
  for pl_row in
    (select lpl.lexeme_id, lpl.process_log_id, pl.event_by, pl.event_on, pl.comment, pl.process_state_code, pl.layer_name
    from lexeme_process_log lpl, process_log pl
    where pl.id = lpl.process_log_id
    order by pl.id desc)
  loop
    if pl_row.process_state_code is not null then
      if pl_row.layer_name = 'SYN' then
        entry_text = 'süno ' || pl_row.process_state_code;
      else
        entry_text = pl_row.process_state_code;
      end if;

      insert into lifecycle_log (entity_id, entity_name, entity_prop, event_type, event_by, event_on, entry, recent)
      values (pl_row.lexeme_id, 'LEXEME', 'PROCESS_STATE', 'UPDATE', pl_row.event_by, pl_row.event_on, entry_text, pl_row.comment)
      returning id into lcl_id;

      insert into lexeme_lifecycle_log (lexeme_id, lifecycle_log_id) values (pl_row.lexeme_id, lcl_id);
      delete from process_log where id = pl_row.process_log_id;
      moved_logs_counter := moved_logs_counter + 1;
    end if;
  end loop;
  raise notice '% ilmiku haldusoleku logi kolitud elutsükli logidesse', moved_logs_counter;
end $$;

drop table lexeme_process_log;
drop table process_log_source_link;
drop table process_log;

delete from process_state where code not in ('avalik', 'mitteavalik');
update process_state set datasets = '{}';

alter table tag add column remove_to_complete boolean default true not null;