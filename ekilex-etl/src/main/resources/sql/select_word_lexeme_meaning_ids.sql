select l.word_id,
       l.meaning_id,
       array_agg(l.id) lexeme_ids
from lexeme l
where l.dataset_code in (:datasetCodes)
      and l.process_state_code = :processState
group by l.word_id,
         l.meaning_id
