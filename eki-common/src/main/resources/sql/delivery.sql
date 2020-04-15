-- upgrade from ver 1.15.0 to 1.16.0

update word_rel_type_label set value = 'algvõrre' where code = 'posit' and lang = 'est';
update word_rel_type_label set value = 'keskvõrre' where code = 'komp' and lang = 'est';
update word_rel_type_label set value = 'ülivõrre' where code = 'superl' and lang = 'est';

-- kui koli seletus on tähenduse ainuke sss seletus, siis muudab selle detailseks
update definition d
set complexity = 'DETAIL'
where d.complexity = 'DEFAULT'
  and exists(select dd.definition_id
             from definition_dataset dd
             where dd.definition_id = d.id
               and dd.dataset_code = 'sss')
  and not exists(select dd.definition_id
                 from definition_dataset dd
                 where dd.definition_id = d.id
                   and dd.dataset_code != 'sss')
  and not exists(select d2.id
                 from definition d2
                 where d2.meaning_id = d.meaning_id
                   and d2.id != d.id
                   and exists(select dd.definition_id
                              from definition_dataset dd
                              where dd.definition_id = d2.id
                                and dd.dataset_code = 'sss'));

-- kustutab koli seletused
delete
from definition d
where complexity = 'DEFAULT'
  and exists(select dd.definition_id
             from definition_dataset dd
             where dd.definition_id = d.id
               and dd.dataset_code = 'sss')
  and not exists(select dd.definition_id
                 from definition_dataset dd
                 where dd.definition_id = d.id
                   and dd.dataset_code != 'sss');

drop type type_term_meaning_word;
create type type_term_meaning_word as (word_id bigint, word_value text, word_value_prese text, homonym_nr integer, lang char(3), word_type_codes varchar(100) array, prefixoid boolean, suffixoid boolean, "foreign" boolean, matching_word boolean, dataset_codes varchar(10) array);

-- käivitada see skript enne vene rõhkude teisendamise ja homonüümide ühendamise utiliite
update form
set value_prese = replace(value_prese, '<eki-stress>ё</eki-stress>', 'ё')
where value_prese like '%<eki-stress>ё</eki-stress>%';

-- kõigi sõnakogude ülene homonüümi numbrite järjestamise protseduur koos ajutise andmetüübiga
create type temp_word_data_tuple as (word_id bigint, homonym_nr integer);

do $$ 
<<adj_homon_nr_block>>
declare
  ordered_homonym_nrs_str_pattern text := array_to_string(array (select generate_series(1, 100)), '-', '');
  word_row record;
  adj_word_ids temp_word_data_tuple;
  homonym_nr_iter integer;
begin 
  for word_row in
    (select w.word,
           w.lang,
           w.word_ids
    from (select w.word,
                 w.lang,
                 array_agg(row(w.id, w.homonym_nr)::temp_word_data_tuple order by w.ds_order_by, w.homonym_nr, w.id) word_ids,
                 array_to_string(array_agg(w.homonym_nr order by w.ds_order_by, w.homonym_nr), '-', '') homonym_nrs_str
          from (select w.id,
                       (array_agg(distinct f.value))[1] word,
                       w.homonym_nr,
                       w.lang,
                       (select case
                                 when count(l.id) > 0 then 1
                                 else 2
                               end 
                        from lexeme l
                        where l.word_id = w.id
                        and   l.type = 'PRIMARY'
                        and   l.dataset_code = 'sss') ds_order_by
                from word w,
                     paradigm p,
                     form f
                where exists (select l.id
                              from lexeme l
                              where l.word_id = w.id
                              and   l.type = 'PRIMARY')
                and   p.word_id = w.id
                and   f.paradigm_id = p.id
                and   f.mode = 'WORD'
                group by w.id) w
          group by w.word,
                   w.lang) w
    where ordered_homonym_nrs_str_pattern not like w.homonym_nrs_str || '%'
    order by w.lang,
             w.word)
  loop
    homonym_nr_iter := 1;
    foreach adj_word_ids in array word_row.word_ids
    loop 
      if homonym_nr_iter != adj_word_ids.homonym_nr then
        update word set homonym_nr = homonym_nr_iter where id = adj_word_ids.word_id;
      end if;
      homonym_nr_iter := homonym_nr_iter + 1;
    end loop;
  end loop;
end adj_homon_nr_block $$;

drop type temp_word_data_tuple;
-- protseduuri lõpp

-- kustutab ilmikud, mille tähendustel on ainult vene info. Igaöine automaatne kustutaja kustutab 'rippuma' jäänud keelendid ja tähendused
delete
from lexeme l1 using word w1
where l1.dataset_code = 'sss'
  and l1.type = 'PRIMARY'
  and l1.word_id = w1.id
  and w1.lang = 'rus'
  and not exists(select l2.id
                 from lexeme l2,
                      word w2
                 where l2.meaning_id = l1.meaning_id
                   and l2.id != l1.id
                   and l2.dataset_code = 'sss'
                   and l2.type = 'PRIMARY'
                   and l2.word_id = w2.id
                   and w2.lang != 'rus');