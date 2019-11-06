delete
from lifecycle_log lcl using meaning_lifecycle_log mlcl
where mlcl.lifecycle_log_id = lcl.id
  and exists(select l1.id
             from lexeme l1
             where l1.meaning_id = mlcl.meaning_id
               and l1.dataset_code = :dataset)
  and not exists(select l2.id
                 from lexeme l2
                 where l2.meaning_id = mlcl.meaning_id
                   and l2.dataset_code != :dataset);
