-- incommon words in dataset 2, compared to dataset 1
select w.id word_id,
       f.value word
from word w,
     paradigm p,
     form f,
     lexeme l1
where f.paradigm_id = p.id
and   p.word_id = w.id
and   f.is_word = true
and   l1.word_id = w.id
and   :dataset1 = all (l1.datasets)
and   not exists (select l2.word_id
                  from lexeme l2
                  where :dataset2 = all (l2.datasets)
                  and   l1.word_id = l2.word_id)
group by w.id,
         f.value
order by f.value;
