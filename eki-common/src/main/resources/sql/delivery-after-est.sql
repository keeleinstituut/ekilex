-- ilma keeleta märkustele keele määramine (tekkisid pärast Estermi laadimist)
update freeform ff
set lang = d.lang
from definition_freeform dff,
     definition d
where ff.lang is null
  and ff.type = 'NOTE'
  and ff.id = dff.freeform_id
  and dff.definition_id = d.id;

update freeform ff
set lang = w.lang
from lexeme_freeform lff,
     lexeme l,
     word w
where ff.lang is null
  and ff.type = 'NOTE'
  and ff.id = lff.freeform_id
  and lff.lexeme_id = l.id
  and l.word_id = w.id;

update freeform ff
set lang = case
             when ff.value_text ilike 'note%' then 'eng'
             else 'est'
           end
from meaning_freeform mff
where ff.lang is null
  and ff.type = 'NOTE'
  and ff.id = mff.freeform_id;

