-- obsolete
select l.*
from lexeme l
where l.dataset_code = :datasetCode
--and   l.is_public = :publicity
order by l.id
