select p.*
from paradigm p
where exists (select l.id
              from lexeme l
              where l.word_id = p.word_id
              and   l.dataset_code = :datasetCode
              --and   l.process_state_code = :processStateCode
              )
order by p.id
