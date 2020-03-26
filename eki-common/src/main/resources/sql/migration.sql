create index collocation_value_idx on collocation(value);
analyze collocation;

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
