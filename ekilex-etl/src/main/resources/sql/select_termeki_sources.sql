SELECT
  *
FROM
  termeki_termbase_sources
WHERE
  source_id IN (SELECT distinct source_id FROM termeki_terms WHERE termbase_id = :baseId AND NOT is_deleted)
