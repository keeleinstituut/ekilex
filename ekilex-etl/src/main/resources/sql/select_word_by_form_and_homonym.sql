select w.*
from word w,
     paradigm p,
     form f
where f.value = :word
and   f.is_word = true
and   f.paradigm_id = p.id
and   p.word_id = w.id
and   w.homonym_nr = :homonymNr
