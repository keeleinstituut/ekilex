select l.id, l.level1, l.level2
from lexeme l
where word_id = :wordId
  and dataset_code = :datasetCode
  and type = :lexemeType
order by l.level1, l.level2, l.id