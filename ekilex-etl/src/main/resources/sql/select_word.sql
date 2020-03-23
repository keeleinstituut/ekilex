select w.id word_id,
       (array_agg(distinct f.value))[1] word
from word w,
     paradigm p,
     form f
where p.word_id = w.id
and   f.paradigm_id = p.id
and   f.mode = 'WORD'
and   w.id = :wordId
group by w.id
