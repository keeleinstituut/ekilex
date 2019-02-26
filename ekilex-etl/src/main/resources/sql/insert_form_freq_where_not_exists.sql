insert into form_frequency
(
  form_id,
  source_name,
  rank,
  value
)
select f1.id,
       :sourceName,
       :rank,
       :frequency
from form f1,
     form f2
where f1.paradigm_id = f2.paradigm_id
and   f1.morph_code = :morphCode
and   f1.value = :formValue
and   f2.mode = 'WORD'
and   f2.value = :wordValue
and   not exists (select ff.id
                  from form_frequency ff
                  where ff.form_id = f1.id
                  and   ff.source_name = :sourceName);
