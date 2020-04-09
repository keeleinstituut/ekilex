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

-- ekilex:
drop type type_term_meaning_word;
create type type_term_meaning_word as (word_id bigint, word_value text, word_value_prese text, homonym_nr integer, lang char(3), word_type_codes varchar(100) array, prefixoid boolean, suffixoid boolean, "foreign" boolean, matching_word boolean, dataset_codes varchar(10) array);

update form
set value_prese = replace(value_prese, '<eki-stress>ё</eki-stress>', 'ё')
where value_prese like '%<eki-stress>ё</eki-stress>%';