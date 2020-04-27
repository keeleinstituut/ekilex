select lcpg.lexeme_id,
       lcpg.id pos_group_id,
       lcpg.pos_group_code,
       lcrg.id rel_group_id,
       lcrg.name rel_group_name
from lex_colloc_pos_group lcpg,
     lex_colloc_rel_group lcrg
where lcpg.lexeme_id = :lexemeId
and   lcrg.pos_group_id = lcpg.id
order by lcpg.id,
         lcrg.id
