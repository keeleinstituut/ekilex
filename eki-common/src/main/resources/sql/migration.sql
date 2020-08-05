alter table lexeme add column is_public boolean default true;
update lexeme set is_public = true where process_state_code = 'avalik';
update lexeme set is_public = false where process_state_code = 'mitteavalik';
update lexeme set is_public = false where process_state_code is null;
alter table lexeme alter column is_public set not null;
alter table freeform alter column is_public set not null;
alter table lexeme drop column process_state_code cascade;
drop table process_state cascade;
