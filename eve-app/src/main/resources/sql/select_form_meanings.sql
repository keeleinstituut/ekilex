select f.value word,
       w.id word_id,
       l.id lexeme_id,
       l.level1,
       l.level2,
       l.level3,
       l.meaning_id,
       m.datasets,
       array_agg(d.value order by d.id) definitions
from form f,
     paradigm p,
     word w,
     lexeme l,
     meaning m
  left outer join definition d on d.meaning_id = m.id
where f.id = {0}
and   f.is_word = true
and   f.paradigm_id = p.id
and   p.word_id = w.id
and   l.word_id = w.id
and   l.meaning_id = m.id
group by f.id,
         w.id,
         l.id,
         m.id
order by w.id,
         l.level1,
         l.level2,
         l.level3
