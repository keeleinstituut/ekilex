delete
from freeform ff
where ff.id in (select cff.freeform_id
                from collocation_freeform cff
                where exists (select lc1.id
                              from lexeme l1,
                                   lex_colloc lc1
                              where lc1.collocation_id = cff.collocation_id
                              and   lc1.lexeme_id = l1.id
                              and   l1.dataset_code = :dataset)
                and   not exists (select lc2.id
                                  from lexeme l2,
                                       lex_colloc lc2
                                  where lc2.collocation_id = cff.collocation_id
                                  and   lc2.lexeme_id = l2.id
                                  and   l2.dataset_code != :dataset));