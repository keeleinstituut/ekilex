-- search words and definitions by word prefix
select w.word_id,
       w.word,
       w.components,
       w.morph_code word_morph_code,
       wml.value word_morph_value,
       l.id lexeme_id,
       m.id meaning_id,
       d.value definition,
       ld.dataset_code lexeme_dataset
from (select w.id word_id,
             f.value word,
             f.components,
             f.morph_code
      from word w,
           paradigm p,
           form f
      where f.paradigm_id = p.id
      and   p.word_id = w.id
      and   f.is_word = true) w
  inner join morph wm on w.morph_code = wm.code
  inner join morph_label wml on wml.code = wm.code and wml.lang = :labelLang and wml.type = :labelType
  inner join lexeme l on l.word_id = w.word_id
  inner join lexeme_dataset ld on ld.lexeme_id = l.id
  inner join meaning m on m.id = l.meaning_id
  left outer join definition d on d.meaning_id = m.id
where w.word like :wordPrefix
order by w.word,
         l.id;
