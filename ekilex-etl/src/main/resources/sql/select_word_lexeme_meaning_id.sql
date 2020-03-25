select l.id lexeme_id,
       l.complexity,
       l.word_id,
       (select distinct f.value
        from paradigm p,
             form f
        where p.word_id = w.id
        and   f.paradigm_id = p.id
        and   f.mode = 'WORD') word,
       w.lang,
       w.homonym_nr,
       l.order_by,
       l.meaning_id,
       l.dataset_code
from lexeme l,
     word w
where w.id = :wordId
and   l.word_id = w.id
