select w.id
from word w
where not exists (select l.id from lexeme l where l.word_id = w.id)
and   not exists (select wg.id
                  from word_guid wg
                  where wg.word_id = w.id
                  and   wg.dataset_code = 'mab');
