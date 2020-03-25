select w1.id word_id, array_agg(distinct w2.id) sim_word_ids
from word w1,
     word w2,
     paradigm p1,
     paradigm p2,
     form f1,
     form f2
where w1.lang = :wordLang
  and w2.lang = :wordLang
  and p1.word_id = w1.id
  and p2.word_id = w2.id
  and f1.paradigm_id = p1.id
  and f2.paradigm_id = p2.id
  and f1.mode = 'WORD'
  and f2.mode = 'WORD'
  and f1.value = f2.value
  and f1.value not in (:excludedWordValues)
  and w1.id != w2.id
  {wordTypeCodesCondition}
group by w1.id