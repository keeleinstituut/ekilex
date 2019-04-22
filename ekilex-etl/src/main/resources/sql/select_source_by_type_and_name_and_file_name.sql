select s.id
from source s
where s.type = :sourceType
and   exists (select sff1.id
              from source_freeform sff1,
                   freeform ff1,
                   source_freeform sff2,
                   freeform ff2,
                   source_freeform sff3,
                   freeform ff3
              where sff1.source_id = s.id
              and   sff2.source_id = sff3.source_id
              and   sff1.source_id = sff2.source_id
              and   sff1.freeform_id = ff1.id
              and   sff2.freeform_id = ff2.id
              and   sff3.freeform_id = ff3.id
              and   ff1.type = :sourcePropertyTypeFileName
              and   ff1.value_text = :sourceFileName
              and   ff2.type = :sourcePropertyTypeName
              and   ff2.value_text = :sourceName
              and   ff3.type = :sourcePropertyTypeExtSourceId
              and   ff3.value_text = :extSourceId)