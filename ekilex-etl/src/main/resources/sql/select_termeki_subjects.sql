SELECT s.subject_id,
  s.parent_id,
  t.lang,
  t.subject_name
FROM termeki_termbase_subjects s
  LEFT JOIN termeki_termbase_subject_translations t ON s.subject_id = t.subject_id
WHERE s.termbase_id = :baseId
ORDER BY s.subject_id;
