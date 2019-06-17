delete
from collocation c
where exists (select lc.id
              from lex_colloc lc,
                   lexeme l
              where lc.collocation_id = c.id
              and   lc.lexeme_id = l.id
              and   l.dataset_code = :dataset)