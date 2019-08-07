select lc.id lex_colloc_id,
       lc.lexeme_id,
       lc.rel_group_id,
       lc.collocation_id,
       lc.member_form,
       lc.conjunct,
       lc.weight,
       lc.member_order,
       lc.group_order
from lexeme l
  inner join lex_colloc lc on lc.lexeme_id = l.id
where l.dataset_code in (:datasetCodes)
order by l.id,
         lc.group_order,
         lc.member_order
