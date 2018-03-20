SELECT
  e.*, t.lang
FROM
  termeki_term_examples e
  LEFT JOIN termeki_terms t ON t.term_id = e.term_id
WHERE
  e.term_id IN (SELECT term_id FROM termeki_terms WHERE termbase_id = :baseId)
