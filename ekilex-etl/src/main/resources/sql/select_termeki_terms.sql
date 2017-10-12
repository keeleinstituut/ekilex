SELECT
  t.concept_id, t.term, t.lang, cs.subject_id
FROM termeki_terms t
  left join termeki_concepts c on t.concept_id = c.concept_id
  left join termeki_concept_subjects cs on t.concept_id = cs.concept_id
WHERE
  c.termbase_id = :baseId
  AND NOT c.is_deleted
  AND NOT t.is_deleted