delete
from freeform ff using definition_freeform dff
where dff.freeform_id = ff.id
and   exists (select dd1.definition_id
              from definition_dataset dd1
              where dd1.definition_id = dff.definition_id
              and   dd1.dataset_code = :dataset)
and   not exists (select dd2.definition_id
                  from definition_dataset dd2
                  where dd2.definition_id = dff.definition_id
                  and   dd2.dataset_code != :dataset);
