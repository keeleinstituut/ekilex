delete
from freeform ff using lexeme_freeform lff
where lff.freeform_id = ff.id
and   exists (select l1.id
              from lexeme l1
              where l1.id = lff.lexeme_id
              and   l1.dataset_code = :dataset)
and   not exists (select l2.id
                  from lexeme l2
                  where l2.id = lff.lexeme_id
                  and   l2.dataset_code != :dataset);
