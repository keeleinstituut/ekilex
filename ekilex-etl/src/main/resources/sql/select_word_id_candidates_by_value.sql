select w.id word_id,
       array_remove(array_agg(l.id order by l.order_by), null) lexeme_id_candidates
from word w
  left outer join lexeme l
               on l.word_id = w.id
              and l.dataset_code = :datasetCode
              and l.type = 'PRIMARY'
where exists (select f.id
              from paradigm p,
                   form f
              where p.word_id = w.id
              and   f.paradigm_id = p.id
              and   f.mode in ('WORD', 'UNKNOWN')
              and   f.value = :word)
and   exists (select l.id from lexeme l where l.word_id = w.id)
group by w.id