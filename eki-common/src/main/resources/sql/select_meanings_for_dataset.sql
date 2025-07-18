-- obsolete
select m.*
from meaning m
where exists (select l.id
              from lexeme l
              where l.meaning_id = m.id
              and   l.dataset_code = :datasetCode
              --and   l.is_public = :publicity
              )
order by m.id
