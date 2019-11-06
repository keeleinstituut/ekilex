select ff.*
from freeform ff,
     word_freeform wff
where wff.freeform_id = ff.id
  and wff.word_id = :wordId