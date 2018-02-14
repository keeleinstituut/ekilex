SELECT
  com.concept_id, com.content
FROM
  termeki_concepts c
  LEFT JOIN termeki_concept_comments com ON  com.concept_id = c.concept_id
WHERE
  c.termbase_id = :baseId
  AND NOT c.is_deleted
  AND com.creater_id <> 1
