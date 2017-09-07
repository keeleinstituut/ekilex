-- incommon words in dataset 2, compared to dataset 1
select w_ds.word_id,
       w_ds.word
from (select w.id word_id,
             w.value word
      from word w,
           morph_homonym mh,
           lexeme l
      where mh.word_id = w.id
      and   l.morph_homonym_id = mh.id
      and   :dataset1 = all (l.dataset)
      group by w.id
      order by w.value) w_ds
where not exists (select w.id word_id,
                         w.value word
                  from word w,
                       morph_homonym mh,
                       lexeme l
                  where mh.word_id = w.id
                  and   l.morph_homonym_id = mh.id
                  and   :dataset2 = all (l.dataset)
                  and   w.value = w_ds.word);