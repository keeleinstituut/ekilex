delete
from lifecycle_log lcl using word_lifecycle_log wlcl
where wlcl.lifecycle_log_id = lcl.id
  and exists(select l1.id
             from lexeme l1
             where l1.word_id = wlcl.word_id
               and l1.dataset_code = :dataset)
  and not exists(select l2.id
                 from lexeme l2
                 where l2.word_id = wlcl.word_id
                   and l2.dataset_code != :dataset);