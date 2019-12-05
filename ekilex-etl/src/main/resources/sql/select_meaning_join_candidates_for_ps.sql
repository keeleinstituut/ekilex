-- matching ss and ps meanings
select w_ss.word_id,
       (select distinct f.value
        from paradigm p,
             form f
        where p.word_id = w_ss.word_id
        and   f.paradigm_id = p.id
        and   f.mode = 'WORD') word,
       m_ss.lexeme_id ss_lexeme_id,
       m_ss.meaning_id ss_meaning_id,
       m_ps.lexeme_id comp_lexeme_id,
       m_ps.meaning_id comp_meaning_id
from (select l1.id lexeme_id,
             l1.meaning_id,
             l1.word_id
      from word w1,
           lexeme l1
      where w1.lang = 'est'
      and   l1.word_id = w1.id
      and   l1.dataset_code = :compoundDatasetCode
      and   exists (select d1.id
                    from definition d1
                    where d1.meaning_id = l1.meaning_id
                    and   d1.complexity = 'DETAIL1')) m_ss,
     (select l1.id lexeme_id,
             l1.meaning_id,
             l1.word_id
      from word w1,
           lexeme l1
      where w1.lang = 'est'
      and   l1.word_id = w1.id
      and   l1.dataset_code = :compoundDatasetCode
      and   exists (select d1.id
                    from definition d1
                    where d1.meaning_id = l1.meaning_id
                    and   d1.complexity = 'SIMPLE1')
      and   not exists (select d1.id
                        from definition d1
                        where d1.meaning_id = l1.meaning_id
                        and   d1.complexity = 'DETAIL1')) m_ps,
     (select w1.id word_id,
             count(l1.meaning_id) m_cnt
      from word w1,
           lexeme l1
      where w1.lang = 'est'
      and   l1.word_id = w1.id
      and   l1.dataset_code = :compoundDatasetCode
      and   exists (select d1.id
                    from definition d1
                    where d1.meaning_id = l1.meaning_id
                    and   d1.complexity = 'DETAIL1')
      group by w1.id) w_ss,
     (select w1.id word_id,
             count(l1.meaning_id) m_cnt
      from word w1,
           lexeme l1
      where w1.lang = 'est'
      and   l1.word_id = w1.id
      and   l1.dataset_code = :compoundDatasetCode
      and   exists (select d1.id
                    from definition d1
                    where d1.meaning_id = l1.meaning_id
                    and   d1.complexity = 'SIMPLE1')
      and   not exists (select d1.id
                        from definition d1
                        where d1.meaning_id = l1.meaning_id
                        and   d1.complexity = 'DETAIL1')
      group by w1.id) w_ps
where m_ss.word_id = m_ps.word_id
and   w_ss.word_id = w_ps.word_id
and   m_ss.word_id = w_ss.word_id
and   w_ss.m_cnt = 1
and   w_ps.m_cnt = 1
order by word
