select lex1.word_id, array_agg(distinct lex1.dataset_code) dataset_codes
from lexeme lex1, lexeme lex2
where lex1.level1 = lex2.level1
  and lex1.level2 = lex2.level2
  and lex1.word_id = lex2.word_id
  and lex1.dataset_code = lex2.dataset_code
  and lex1.type = lex2.type
  and lex1.id != lex2.id
  and lex1.dataset_code not in (:ignoredDatasetCodes)
  and lex1.type = :lexemeType
group by lex1.word_id