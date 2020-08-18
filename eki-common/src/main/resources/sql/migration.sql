alter table lexeme add column is_public boolean default true;
update lexeme set is_public = true where process_state_code = 'avalik';
update lexeme set is_public = false where process_state_code = 'mitteavalik';
update lexeme set is_public = false where process_state_code is null;
alter table lexeme alter column is_public set not null;
alter table freeform alter column is_public set not null;
alter table lexeme drop column process_state_code cascade;
drop table process_state cascade;
create index lexeme_is_public_idx on lexeme(is_public);

update lexeme l
set complexity = l_c.complexity
from (select ls.id lexeme_id,
             (case
                when 'DETAIL' = all (array_agg(lp.complexity)) then 'DETAIL'
                else 'ANY'
              end) complexity
      from lexeme ls,
           lexeme lp
      where ls.type = 'SECONDARY'
        and lp.type = 'PRIMARY'
        and lp.is_public = true
        and lp.word_id = ls.word_id
      group by ls.id) l_c
where l.id = l_c.lexeme_id;