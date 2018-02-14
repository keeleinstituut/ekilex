SELECT
  d.concept_id, d.lang, d.definition, d.description, d.source_id
FROM termeki_concepts c,
     termeki_definitions d,
     termeki_termbase_languages l
WHERE c.termbase_id = :baseId
      AND NOT c.is_deleted
      AND d.concept_id = c.concept_id
      AND d.is_deleted = 0
      AND COALESCE(definition, '') != ''
      AND l.termbase_id = c.termbase_id
      AND l.lang = d.lang
ORDER BY d.concept_id, l.line, d.line