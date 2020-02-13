select l.*
from lexeme l
where l.dataset_code = :datasetCode
--and   l.process_state_code = :processStateCode
order by l.id
