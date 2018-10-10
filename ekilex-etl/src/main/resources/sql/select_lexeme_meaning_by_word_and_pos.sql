select l.id lexeme_id,
       m.id meaning_id
from lexeme l,
     meaning m
where l.meaning_id = m.id
and   l.word_id = :wordId
and   l.dataset_code = :dataset
and   exists (select lp.id
              from lexeme_pos lp
              where lp.lexeme_id = l.id
              and   lp.pos_code = :posCode)
order by l.id
