-- for now, no need to specify dataset
delete
from freeform ff using collocation_freeform cff
where cff.freeform_id = ff.id;
