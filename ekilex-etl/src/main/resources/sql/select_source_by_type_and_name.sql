select s.id
from source s
where s.type = :sourceType
and   s.ext_source_id = :extSourceId
and   exists (select sff.id
              from source_freeform sff,
                   freeform ff
              where sff.source_id = s.id
              and   sff.freeform_id = ff.id
              and   ff.type = :sourcePropertyTypeName
              and   ff.value_text = :sourceName)
