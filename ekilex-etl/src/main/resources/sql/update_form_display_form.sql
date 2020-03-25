update form f1
set display_form = f2.display_form
from form f2,
     paradigm p1,
     paradigm p2,
     word w1,
     word w2
where w1.id = :wordId1
  and w2.id = :wordId2
  and p1.word_id = w1.id
  and p2.word_id = w2.id
  and f1.paradigm_id = p1.id
  and f2.paradigm_id = p2.id
  and f1.mode = 'WORD'
  and f2.mode = 'WORD'
  and f1.display_form is null
  and f2.display_form is not null