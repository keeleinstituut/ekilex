select
  w.*
from
  word w
where
  exists (
      select
        f.id
      from
        paradigm p,
        form f,
        lexeme l
      where
        f.value = :word
        and f.is_word = true
        and f.paradigm_id = p.id
        and p.word_id = w.id
        and l.word_id = w.id
        and l.dataset_code = :dataset)
