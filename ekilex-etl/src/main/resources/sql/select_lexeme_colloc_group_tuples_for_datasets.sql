select lpgr.lexeme_id,
       lpgr.id pos_group_id,
       lpgr.pos_group_code,
       lpgr.order_by pos_group_order_by,
       lrgr.id rel_group_id,
       lrgr.name rel_group_name,
       lrgr.frequency rel_group_frequency,
       lrgr.score rel_group_score,
       lrgr.order_by rel_group_order_by
from lexeme l
  inner join lex_colloc_pos_group lpgr on lpgr.lexeme_id = l.id
  inner join lex_colloc_rel_group lrgr on lrgr.pos_group_id = lpgr.id
where l.dataset_code in (:datasetCodes)
order by l.id,
         lpgr.order_by,
         lrgr.order_by
