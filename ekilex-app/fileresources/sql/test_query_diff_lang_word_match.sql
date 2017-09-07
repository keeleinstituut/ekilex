-- TODO test
-- query words in different langs with matching meaning
select
w_1.word word1,
w_2.word word2
from
(select w.value word,
       w.lang,
       m.id meaning_id
from meaning m,
     morph_homonym mh,
     word w,
     lexeme l
where mh.word_id = w.id
and   l.morph_homonym_id = mh.id
and   l.meaning_id = m.id) w_1,
(select w.value word,
       w.lang,
       m.id meaning_id
from meaning m,
     morph_homonym mh,
     word w,
     lexeme l
where mh.word_id = w.id
and   l.morph_homonym_id = mh.id
and   l.meaning_id = m.id) w_2
where
w_1.meaning_id = w_2.meaning_id
and w_1.lang != w_2.lang
and w_1.lang = 'est';