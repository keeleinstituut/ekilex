select lpl.lexeme_id,
       pl.event_by,
       pl.event_on,
       pl.comment,
       pl.process_state_code,
       pl.dataset_code,
       (select count(plsl.id) > 0
        from process_log_source_link plsl
        where plsl.process_log_id = pl.id) source_links_exist
from lexeme l,
     lexeme_process_log lpl,
     process_log pl
where lpl.lexeme_id = l.id
and   lpl.process_log_id = pl.id
and   l.dataset_code in (:datasetCodes)
