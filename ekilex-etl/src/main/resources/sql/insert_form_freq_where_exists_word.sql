insert into form_frequency
(
  source_name,
  word_value,
  morph_code,
  form_value,
  rank,
  value
)
select
  :sourceName,
  :wordValue,
  :morphCode,
  :formValue,
  :rank,
  :frequency 
where exists (select f.id
              from form f
              where f.mode = 'WORD'
              and   f.value = :wordValue);
