select w.*
from word w
where exists (select l.id
              from lexeme l
              where l.word_id = w.id
              and   l.dataset_code = :datasetCode
              --and   l.process_state_code = :processStateCode
              )
order by w.id
