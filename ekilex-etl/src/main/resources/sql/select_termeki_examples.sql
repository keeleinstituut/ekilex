select e.*,
       t1.lang
from termeki_term_examples e
  left join termeki_terms t1
         on t1.term_id = e.term_id
where exists (select t2.term_id
              from termeki_terms t2
              where t2.term_id = e.term_id
              and   t2.termbase_id = :baseId)

