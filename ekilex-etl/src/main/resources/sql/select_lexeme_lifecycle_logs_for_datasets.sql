select lll.lexeme_id,
       ll.entity_id,
       ll.entity_name,
       ll.entity_prop,
       ll.event_type,
       ll.event_by,
       ll.event_on,
       ll.recent,
       ll.entry
from lexeme l,
     lexeme_lifecycle_log lll,
     lifecycle_log ll
where lll.lexeme_id = l.id
and   lll.lifecycle_log_id = ll.id
and   l.dataset_code in (:datasetCodes)
order by l.id,
         lll.id,
         ll.id
