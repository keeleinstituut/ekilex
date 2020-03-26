-- kustuta qqst pärit seletused
delete
from definition d
where d.complexity = 'SIMPLE2'
  and exists(select dd.definition_id
             from definition_dataset dd
             where dd.definition_id = d.id
               and dd.dataset_code = 'sss')
  and not exists(select dd.definition_id
                 from definition_dataset dd
                 where dd.definition_id = d.id
                   and dd.dataset_code != 'sss');