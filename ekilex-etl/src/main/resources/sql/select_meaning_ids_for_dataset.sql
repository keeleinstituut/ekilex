select m.id
from meaning m
where exists (select l1.id
              from lexeme l1
              where l1.meaning_id = m.id
              and   l1.dataset_code = :dataset)
and   not exists (select l2.id
                  from lexeme l2
                  where l2.meaning_id = m.id
                  and   l2.dataset_code != :dataset);