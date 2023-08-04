-- recreate types and views

-- Kasutusnäidete allikaviidete puuduvate väärtuste taastamine
update freeform_source_link ffsl
set value = s.source_name
from (select sff.source_id,
             array_to_string(array_agg(distinct snff.value_text), ', ', '*') source_name
      from source_freeform sff,
           freeform snff
      where sff.freeform_id = snff.id
        and snff.type = 'SOURCE_NAME'
      group by sff.source_id) s
where ffsl.value is null
  and ffsl.source_id = s.source_id;