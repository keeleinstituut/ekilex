-- upgrade from ver 1.15.0 to 1.16.0
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