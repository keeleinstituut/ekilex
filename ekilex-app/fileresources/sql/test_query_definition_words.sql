-- query words of definitions if there are many
select d_w.word,
       d_w.definition
from (select count(w.id) word_count,
             d.id definition_id
      from definition d,
           meaning m,
           morph_homonym mh,
           word w,
           lexeme l
      where d.meaning_id = m.id
      and   mh.word_id = w.id
      and   l.morph_homonym_id = mh.id
      and   l.meaning_id = m.id
      group by d.id) d_w_cnt,
     (select w.value word,
             d.id definition_id,
             d.value definition
      from definition d,
           meaning m,
           morph_homonym mh,
           word w,
           lexeme l
      where d.meaning_id = m.id
      and   mh.word_id = w.id
      and   l.morph_homonym_id = mh.id
      and   l.meaning_id = m.id) d_w
where d_w.definition_id = d_w_cnt.definition_id
and   d_w_cnt.word_count > 1;
