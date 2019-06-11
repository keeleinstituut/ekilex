select
  ff.*
from
  lexeme_freeform lf, freeform ff
where
  lf.lexeme_id = :lexeme_id
  and lf.freeform_id = ff.id
  and ff.type = :type
  and ff.value_text = :value
