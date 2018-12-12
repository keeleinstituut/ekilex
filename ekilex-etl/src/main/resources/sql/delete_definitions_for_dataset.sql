delete
from definition d
where exists (select dd1.definition_id
              from definition_dataset dd1
              where dd1.definition_id = d.id
              and   dd1.dataset_code = :dataset)
and   not exists (select dd2.definition_id
                  from definition_dataset dd2
                  where dd2.definition_id = d.id
                  and   dd2.dataset_code != :dataset);