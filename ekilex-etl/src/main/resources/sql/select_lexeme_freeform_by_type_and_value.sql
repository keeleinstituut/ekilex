SELECT
  ff.*
FROM
  LEXEME_FREEFORM lf, FREEFORM ff
WHERE
  lf.id = :lexeme_id
  AND lf.freeform_id = ff.id
  AND ff.type = :type
  AND ff.value_text = :value
