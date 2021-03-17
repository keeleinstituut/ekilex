-- query words relations
select w_r.word,
       case
         when w_r.rel_exists = true then array_agg(w_r.relation)
         else null
       end relations
from (select w1.word word,
             w1.lexeme1_id,
             w2.word || ' (' || lrtl.value || ')' relation,
             (select w2.word is not null) rel_exists
      from (select w.id word_id,
                   w.value word,
                   l.id lexeme1_id
            from word w,
                 lexeme l
            where l.word_id = w.id
            order by w.id) w1
        left outer join lex_relation lr on lr.lexeme1_id = w1.lexeme1_id
        left outer join (select w.id word_id,
                                w.value word,
                                l.id lexeme2_id
                         from word w,
                              lexeme l
                         where l.word_id = w.id) w2 on lr.lexeme2_id = w2.lexeme2_id
        left outer join lex_rel_type_label lrtl on lrtl.code = lr.lex_rel_type_code and lrtl.lang = :defaultLabelLang and lrtl.type = :defaultLabelType
      order by w1.word) w_r
group by w_r.word,
         w_r.rel_exists
order by w_r.word;
