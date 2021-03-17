-- search words and definitions by word prefix
select w.id word_id,
       w.value word,
       l.id lexeme_id,
       m.id meaning_id,
       d.value definition,
       l.dataset_code lexeme_dataset
from word w
  inner join lexeme l on l.word_id = w.id
  inner join meaning m on m.id = l.meaning_id
  left outer join definition d on d.meaning_id = m.id
where w.value like :wordPrefix
order by w.value,
         l.id;
