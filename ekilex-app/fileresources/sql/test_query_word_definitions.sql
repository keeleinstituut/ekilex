-- search words and definitions by word prefix
select w.id word_id,
       w.value word,
       w.components,
       w.morph_code word_morph_code,
       wml.value word_morph_value,
       l.id lexeme_id,
       m.id meaning_id,
       d.value definition,
       l.dataset lexeme_dataset
from word w
  inner join morph wm on w.morph_code = wm.code
  inner join morph_label wml on wml.code = wm.code and wml.lang = :labelLang and wml.type = :labelType
  inner join morph_homonym mh on mh.word_id = w.id
  inner join lexeme l on l.morph_homonym_id = mh.id
  inner join meaning m on m.id = l.meaning_id
  left outer join definition d on d.meaning_id = m.id
where w.value like :wordPrefix
order by w.value,
         l.id;
