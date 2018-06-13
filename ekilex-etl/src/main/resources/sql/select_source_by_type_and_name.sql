select s.id
from source s,
     source_freeform sff,
     freeform ff
where sff.source_id = s.id
and   sff.freeform_id = ff.id
and   s.type = :sourceType
and   s.concept = :conceptId
and   ff.type = :sourceAttrType
and   ff.value_text = :sourceName
