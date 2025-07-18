-- obsolete
select p.*
from paradigm p
where exists (select l.id
              from lexeme l
              where l.word_id = p.word_id
              and   l.dataset_code = :datasetCode
              --and   l.is_public = :publicity
              )
order by p.id
