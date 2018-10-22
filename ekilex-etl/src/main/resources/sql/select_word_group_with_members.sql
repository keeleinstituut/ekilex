select g.id, count(g.id)
from word_group g, word_group_member m
where m.word_id in (:memberIds)
  and m.word_group_id = g.id
  and g.word_rel_type_code = :word_rel_type_code
group by g.id
having count(g.id) = :nrOfMembers
