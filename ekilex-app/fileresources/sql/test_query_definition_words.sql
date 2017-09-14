-- query words of definitions if there are many
select d_w.word,
       d_w.definition
from (select count(l.word_id) word_count,
             d.id definition_id
      from lexeme l,
           meaning m,
           definition d
      where d.meaning_id = m.id
      and   l.meaning_id = m.id
      group by d.id) d_w_cnt,
     (select f.value word,
             d.id definition_id,
             d.value definition
      from word w,
           paradigm p,
           form f,
           meaning m,
           definition d,
           lexeme l
      where f.paradigm_id = p.id
      and   p.word_id = w.id
      and   f.is_word = true
      and   l.word_id = w.id
      and   l.meaning_id = m.id
      and   d.meaning_id = m.id) d_w
where d_w.definition_id = d_w_cnt.definition_id
and   d_w_cnt.word_count > 1;
