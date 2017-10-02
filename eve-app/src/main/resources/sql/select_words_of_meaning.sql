select w1w2.word,
       w1w2.word_id,
       w1w2.lexeme_id,
       w1w2.meaning_id,
       w1w2.words,
       w1w2.datasets,
       l.level1,
       l.level2,
       l.level3,
       d.definitions
from (select w1.word,
             w1.word_id,
             w1.lexeme_id,
             w1.meaning_id,
             w1.datasets,
             array_agg(w2.word) words
      from (select f.value word,
                   w.id word_id,
                   l.id lexeme_id,
                   m.id meaning_id,
                   m.datasets
            from form f,
                 paradigm p,
                 word w,
                 lexeme l,
                 meaning m
            where f.id = {0}
            and   f.is_word = true
            and   f.paradigm_id = p.id
            and   p.word_id = w.id
            and   l.word_id = w.id
            and   l.meaning_id = m.id
            order by w.id) w1
        left outer join (select f.value word,
                                w.id word_id,
                                l.id lexeme_id,
                                l.meaning_id
                         from form f,
                              paradigm p,
                              word w,
                              lexeme l
                         where f.id != {0}
                         and   f.is_word = true
                         and   f.paradigm_id = p.id
                         and   p.word_id = w.id
                         and   l.word_id = w.id) w2 on w1.meaning_id = w2.meaning_id
      group by w1.word,
               w1.word_id,
               w1.lexeme_id,
               w1.meaning_id,
               w1.datasets) w1w2
  left outer join (select d.meaning_id,
                          array_agg(d.value) definitions
                   from definition d
                   group by d.meaning_id) d on w1w2.meaning_id = d.meaning_id
  inner join lexeme l on w1w2.lexeme_id = l.id
order by l.level1,
         l.level2,
         l.level3
