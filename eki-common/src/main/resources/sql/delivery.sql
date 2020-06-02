-- upgrade from ver 1.17.0 to 1.18.0

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
  and ff.type = 'PUBLIC_NOTE'
  and ff.id = dff.freeform_id
  and dff.definition_id = d.id;

update freeform ff
set lang = w.lang
from lexeme_freeform lff,
     lexeme l,
     word w
where ff.lang is null
  and ff.type = 'PUBLIC_NOTE'
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
  and ff.type = 'PUBLIC_NOTE'
  and ff.id = mff.freeform_id;