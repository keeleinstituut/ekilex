select l.word_id,
       l.meaning_id,
       array_agg(l.id order by d.order_by) lexeme_ids
from lexeme l,
     dataset d
where l.dataset_code = d.code
      and l.dataset_code in (:datasetCodes)
      and l.process_state_code = :processState
group by l.word_id,
         l.meaning_id
order by l.word_id,
         l.meaning_id