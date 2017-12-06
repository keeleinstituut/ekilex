SELECT
  s.subject_id,
  s.parent_id,
  s.termbase_id,
  lower(t.subject_name) AS code
FROM
  termeki_termbase_subjects s
  LEFT JOIN termeki_termbase_subject_translations t ON t.subject_id = s.subject_id
WHERE
  s.termbase_id IN (:termbaseIds)
  AND t.lang = :lang
