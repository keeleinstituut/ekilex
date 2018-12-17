delete
from freeform ff using meaning_freeform mff
where mff.freeform_id = ff.id
and   exists (select l1.id
              from lexeme l1
              where l1.meaning_id = mff.meaning_id
              and   l1.dataset_code = :dataset)
and   not exists (select l2.id
                  from lexeme l2
                  where l2.meaning_id = mff.meaning_id
                  and   l2.dataset_code != :dataset);
