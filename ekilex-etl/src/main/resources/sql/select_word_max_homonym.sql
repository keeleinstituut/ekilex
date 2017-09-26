select case
         when we.word_exists = true then wh.max_homonym_nr
         else 0
       end max_homonym_nr
from (select max(w.homonym_nr) max_homonym_nr
      from form f,
           paradigm p,
           word w
      where f.value = :word
      and   f.is_word = true
      and   f.paradigm_id = p.id
      and   p.word_id = w.id
      and   w.lang = :lang) wh,
     (select (count(f.id) > 0) word_exists
      from form f,
           paradigm p,
           word w
      where f.value = :word
      and   f.is_word = true
      and   f.paradigm_id = p.id
      and   p.word_id = w.id
      and   w.lang = :lang) we
