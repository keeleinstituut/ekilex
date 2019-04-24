select t.term_id,
       t.concept_id,
       t.term,
       t.lang,
       cs.subject_id,
       lower(tr.subject_name) as domain_code,
       t.gender,
       t.pronunciation,
       t.word_class,
       t.source_id,
       t.create_time,
       t.creater_id,
       t.change_time,
       t.changer_id
from termeki_terms t
  left join termeki_concepts c
         on t.concept_id = c.concept_id
  left join termeki_concept_subjects cs
         on t.concept_id = cs.concept_id
  left join termeki_termbase_subject_translations tr
         on tr.subject_id = cs.subject_id
        and tr.lang = 'et'
  left join termeki_termbase_languages l
         on l.lang = t.lang
where c.termbase_id = :baseId
and   not c.is_deleted
and   not t.is_deleted
and   l.termbase_id = c.termbase_id
order by t.concept_id,
         l.line,
         t.line
