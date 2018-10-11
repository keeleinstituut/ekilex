-- incommon words in dataset 2, compared to dataset 1
select w.id word_id,
       f.value word
from word w,
  paradigm p,
  form f,
  lexeme l1
where f.paradigm_id = p.id
and   p.word_id = w.id
and   f.mode = 'WORD'
and   l1.word_id = w.id
and   l1.dataset_code = :dataset1
and   not exists (select l2.word_id
              from lexeme l2
              where l2.dataset_code = :dataset2
              and   l1.word_id = l2.word_id)
group by w.id,
  f.value
order by f.value;
