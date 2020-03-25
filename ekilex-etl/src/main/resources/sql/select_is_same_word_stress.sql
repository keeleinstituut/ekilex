select count(w1.id) = 1 as is_same_word_stress
from word w1,
     word w2,
     paradigm p1,
     paradigm p2,
     form f1,
     form f2
where w1.id = :wordId1
  and w2.id = :wordId2
  and p1.word_id = w1.id
  and p2.word_id = w2.id
  and f1.paradigm_id = p1.id
  and f2.paradigm_id = p2.id
  and f1.mode = 'WORD'
  and f2.mode = 'WORD'
  and f1.value_prese = f2.value_prese
  and (
    f1.display_form = f2.display_form
    or f1.display_form is null
    or f2.display_form is null
  )