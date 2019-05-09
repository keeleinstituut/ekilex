select distinct f.id
from form f
  join paradigm p on p.id = f.paradigm_id
  join word w on w.id = p.word_id
  join lexeme l on l.word_id = w.id
where lower (f.value) = :word
and   f.mode = 'WORD'
and   f.audio_file is null
and   (exists (select lp.id
               from lexeme_pos lp
               where lp.lexeme_id = l.id
               and   lp.pos_code = 'prop')
       or 
       exists (select wt.id
               from word_word_type wt
               where wt.word_id = w.id
               and   wt.word_type_code = 'z'))
