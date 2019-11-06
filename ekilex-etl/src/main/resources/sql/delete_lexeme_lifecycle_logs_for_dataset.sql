delete
from lifecycle_log lcl using lexeme_lifecycle_log llcl
where llcl.lifecycle_log_id = lcl.id
  and exists(select l1.id
             from lexeme l1
             where l1.id = llcl.lexeme_id
               and l1.dataset_code = :dataset)
  and not exists(select l2.id
                 from lexeme l2
                 where l2.id = llcl.lexeme_id
                   and l2.dataset_code != :dataset);