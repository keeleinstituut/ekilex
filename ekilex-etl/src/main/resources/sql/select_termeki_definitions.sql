SELECT
  d.concept_id, d.lang, d.definition, d.description
FROM termeki_concepts c,
  termeki_definitions d
WHERE c.termbase_id = :baseId
      AND   NOT c.is_deleted
      AND   d.concept_id = c.concept_id
      AND   d.is_deleted = 0
      AND   COALESCE(definition, '') != ''