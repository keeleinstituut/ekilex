select c.collocation_id,
       array_agg(c.word order by c.member_order) member_words
from (select c.id collocation_id,
             f.value word,
             lc.member_order
      from collocation c,
           lex_colloc lc,
           lexeme l,
           paradigm p,
           form f
      where c.value = :collocation
      and   lc.collocation_id = c.id
      and   lc.lexeme_id = l.id
      and   l.word_id = p.word_id
      and   f.paradigm_id = p.id
      and   f.mode in ('WORD', 'UNKNOWN')
      group by c.id,
               f.value,
               lc.member_order) c
group by c.collocation_id
