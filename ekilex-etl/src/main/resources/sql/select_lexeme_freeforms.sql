select ff.*,
       (select count(ff2.id) > 0
        from freeform ff2
        where ff2.parent_id = ff.id) children_exist
from freeform ff,
     lexeme_freeform lff
where lff.freeform_id = ff.id
and   lff.lexeme_id = :lexemeId
order by ff.order_by
