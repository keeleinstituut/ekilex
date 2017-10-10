SELECT
  t.concept_id, t.term, t.lang
FROM
  termeki_concepts c,
  termeki_terms t
WHERE
  c.termbase_id = :baseId
  AND NOT c.is_deleted
  AND t.concept_id = c.concept_id
  AND NOT t.is_deleted