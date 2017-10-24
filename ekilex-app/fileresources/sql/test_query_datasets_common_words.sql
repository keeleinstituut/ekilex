-- common words in dataset 2, compared to dataset 1
select w.id word_id,
       f.value word
from word w,
  paradigm p,
  form f,
  lexeme l1,
  lexeme_dataset ld1
where f.paradigm_id = p.id
      and   p.word_id = w.id
      and   f.is_word = true
      and   l1.word_id = w.id
      and   ld1.lexeme_id = l1.id
      and   ld1.dataset_code = :dataset1
      and   exists (select l2.word_id
                    from lexeme l2,
                      lexeme_dataset ld2
                    where ld2.dataset_code = :dataset2
                          and   l1.word_id = l2.word_id
                          and   ld2.lexeme_id = l2.id)
group by w.id,
  f.value
order by f.value;
