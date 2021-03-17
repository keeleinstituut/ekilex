-- incommon words in dataset 2, compared to dataset 1
select w.id word_id,
       w.value word
from word w,
     lexeme l1
where l1.word_id = w.id
and   l1.dataset_code = :dataset1
and   not exists (select l2.word_id
              from lexeme l2
              where l2.dataset_code = :dataset2
              and   l1.word_id = l2.word_id)
group by w.id,
         w.value
order by w.value;
