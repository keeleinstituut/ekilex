SELECT
  d.concept_id, d.lang, d.definition, d.description, d.source_id
FROM termeki_concepts c,
  termeki_definitions d
WHERE c.termbase_id = :baseId
      AND   NOT c.is_deleted
      AND   d.concept_id = c.concept_id
      AND   d.is_deleted = 0
      AND   COALESCE(definition, '') != ''
ORDER BY d.concept_id, d.is_preferred desc, d.line