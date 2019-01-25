select max(w.homonym_nr) max_homonym_nr
from form f,
     paradigm p,
     word w
where lower(f.value) = :word
and   f.mode = :mode
and   f.paradigm_id = p.id
and   p.word_id = w.id
and   w.lang = :lang
