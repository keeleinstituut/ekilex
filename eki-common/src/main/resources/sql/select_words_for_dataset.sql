-- obsolete
select w.*
from word w
where exists (select l.id
              from lexeme l
              where l.word_id = w.id
              and   l.dataset_code = :datasetCode
              --and   l.is_public = :publicity
              )
order by w.id
