SELECT
  t.concept_id, t.term, t.lang, cs.subject_id, lower(tr.subject_name) AS domain_code, t.gender, t.pronunciation, t.word_class, t.source_id
FROM termeki_terms t
  LEFT JOIN termeki_concepts c ON t.concept_id = c.concept_id
  LEFT JOIN termeki_concept_subjects cs ON t.concept_id = cs.concept_id
  LEFT JOIN termeki_termbase_subject_translations tr ON tr.subject_id = cs.subject_id AND tr.lang = 'et'
WHERE
  c.termbase_id = :baseId
  AND NOT c.is_deleted
  AND NOT t.is_deleted