select s.id
from source s
where s.type = :sourceType
and   s.ext_source_id = :extSourceId
and   exists (select sff1.id
              from source_freeform sff1,
                   freeform ff1,
                   source_freeform sff2,
                   freeform ff2
              where sff1.source_id = s.id
              and   sff1.source_id = sff2.source_id
              and   sff1.freeform_id = ff1.id
              and   sff2.freeform_id = ff2.id
              and   ff1.type = :sourcePropertyTypeFileName
              and   ff1.value_text = :sourceFileName
              and   ff2.type = :sourcePropertyTypeName
              and   ff2.value_text = :sourceName)