select lc.collocation_id,
       array_agg(lc.word || '|' || lc.word_id || '|' || lc.lexeme_id || '|' || lc.meaning_id order by lc.member_order) member_lexemes
from (select lc.collocation_id,
             (select (array_agg(distinct f.value))[1]
              from paradigm p,
                   form f
              where p.word_id = l.word_id
              and   f.paradigm_id = p.id
              and   f.mode in ('WORD', 'UNKNOWN')
              group by l.word_id) word,
             l.word_id,
             l.id lexeme_id,
             l.meaning_id,
             lc.member_order
      from lex_colloc lc,
           lexeme l
      where lc.collocation_id = :collocationId
      and   lc.lexeme_id = l.id) lc
group by lc.collocation_id
