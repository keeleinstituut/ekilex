select c.*
from collocation c
where exists (select l.id
              from lexeme l,
                   lex_colloc lc
              where lc.collocation_id = c.id
              and   lc.lexeme_id = l.id
              and   l.dataset_code in (:datasetCodes))
order by
c.id
