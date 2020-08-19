alter table lexeme add column is_public boolean default true;
update lexeme set is_public = true where process_state_code = 'avalik';
update lexeme set is_public = false where process_state_code = 'mitteavalik';
update lexeme set is_public = false where process_state_code is null;
alter table lexeme alter column is_public set not null;
alter table freeform alter column is_public set not null;
alter table lexeme drop column process_state_code cascade;
drop table process_state cascade;
create index lexeme_is_public_idx on lexeme(is_public);

-- roheliste ilmikute detailsus sõltuvalt mustade detailsusest
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

-- bookmarkide teisendamine ilmiku siltideks
insert into tag select distinct value_text from freeform where type = 'BOOKMARK';

insert into lexeme_tag (lexeme_id, tag_name)
select distinct lff.lexeme_id, ff.value_text
from lexeme_freeform lff,
     freeform ff
where lff.freeform_id = ff.id
  and ff.type = 'BOOKMARK'
  and not exists(select lt.id
                 from lexeme_tag lt
                 where lt.lexeme_id = lff.lexeme_id
                   and lt.tag_name = ff.value_text);

update lifecycle_log lcl
set entity_id = lff.lexeme_id,
    entity_name = 'LEXEME',
    entity_prop = 'TAG'
from lexeme_freeform lff,
     freeform ff
where lff.freeform_id = ff.id
  and ff.type = 'BOOKMARK'
  and lcl.entity_id = ff.id
  and lcl.entry = ff.value_text;

delete from freeform where type = 'BOOKMARK';