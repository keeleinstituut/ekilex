select w.id
from word w
where exists (select g1.id
              from word_guid g1
              where g1.word_id = w.id
              and   g1.dataset_code = :dataset)
and   not exists (select g2.id
                  from word_guid g2
                  where g2.word_id = w.id
                  and   g2.dataset_code != :dataset);