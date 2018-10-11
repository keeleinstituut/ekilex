-- query words in different langs with matching meaning
select w_1.word word1,
       w_2.word word2
from (select w.id word_id,
             f.value word,
             w.lang,
             m.id meaning_id
      from word w,
           paradigm p,
           form f,
           meaning m,
           lexeme l
      where f.paradigm_id = p.id
      and   p.word_id = w.id
      and   f.mode = 'WORD'
      and   l.word_id = w.id
      and   l.meaning_id = m.id) w_1,
     (select f.value word,
             w.lang,
             m.id meaning_id
      from word w,
           paradigm p,
           form f,
           meaning m,
           lexeme l
      where f.paradigm_id = p.id
      and   p.word_id = w.id
      and   f.mode = 'WORD'
      and   l.word_id = w.id
      and   l.meaning_id = m.id) w_2
where w_1.meaning_id = w_2.meaning_id
and   w_1.lang != w_2.lang
and   w_1.lang = :lang1;
