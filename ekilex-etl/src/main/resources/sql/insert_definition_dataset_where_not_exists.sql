insert into definition_dataset
(
  definition_id,
  dataset_code
)
select d.id,
       :datasetCode
from definition d
where exists (select m.id
              from lexeme l,
                   meaning m
              where l.meaning_id = m.id
              and   d.meaning_id = m.id
              and   l.dataset_code = :datasetCode)
and   not exists (select dds.definition_id
                  from definition_dataset dds
                  where dds.definition_id = d.id
                  and   dds.dataset_code = :datasetCode)
