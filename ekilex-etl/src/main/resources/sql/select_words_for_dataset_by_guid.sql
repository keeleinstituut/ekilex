select w.id word_id,
       f.value word,
       count(p.id) paradigm_count
from word w,
     paradigm p,
     form f
where p.word_id = w.id
and   f.paradigm_id = p.id
and   f.mode = 'WORD'
and   exists (select wg.id
              from word_guid wg
              where wg.word_id = w.id
              and   wg.dataset_code = :dataset)
group by w.id,
         f.value;
