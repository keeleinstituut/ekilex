select max(w.homonym_nr) max_homonym_nr
from form f,
     paradigm p,
     word w
where f.value = :word
and   f.mode = :mode
and   f.paradigm_id = p.id
and   p.word_id = w.id
and   w.lang = :lang
and   exists (select wwt.id
              from word_word_type wwt
              where wwt.word_id = w.id
              and   wwt.word_type_code = :wordTypeCode)
