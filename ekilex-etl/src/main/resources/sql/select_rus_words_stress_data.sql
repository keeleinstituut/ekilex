select f.id form_id, f.value, f.value_prese, f.display_form
from form f, paradigm p, word w
where f.mode = 'WORD'
  and f.value_prese != f.display_form
  and f.display_form like '%"%'
  and f.paradigm_id = p.id
  and p.word_id = w.id
  and w.lang = 'rus'
order by f.value