select l1.word_id,
       array_agg(distinct l2.word_id order by l2.word_id) sim_word_ids
from lexeme l1,
     lexeme l2,
     word w1,
     word w2,
     paradigm p1,
     paradigm p2,
     form f1,
     form f2
where l1.meaning_id = l2.meaning_id
  and l1.word_id = w1.id
  and l2.word_id = w2.id
  and p1.word_id = w1.id
  and p2.word_id = w2.id
  and f1.paradigm_id = p1.id
  and f2.paradigm_id = p2.id
  and f1.mode = 'WORD'
  and f2.mode = 'WORD'
  and w1.id != w2.id
  and w1.lang = :wordLang
  and w2.lang = :wordLang
  and f1.value = f2.value
  {wordTypeCodesCondition}
group by l1.word_id