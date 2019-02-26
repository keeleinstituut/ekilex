insert into lexeme_frequency
(
  lexeme_id,
  source_name,
  rank,
  value
)
select l.id,
       :sourceName,
       :rank,
       :frequency
from lexeme l
where exists (select f.id
              from form f,
                   paradigm p
              where f.value = :wordValue
              and   f.mode = 'WORD'
              and   f.paradigm_id = p.id
              and   p.word_id = l.word_id)
and   not exists (select lp.id
              from lexeme_pos lp
              where lp.lexeme_id = l.id)
and   not exists (select lf.id
                  from lexeme_frequency lf
                  where lf.lexeme_id = l.id
                  and   lf.source_name = :sourceName);
