
-- #1 --

alter table collocation_member add column conjunct_lexeme_id bigint references lexeme(id);
create index collocation_member_conjunct_lexeme_id_idx on collocation_member(conjunct_lexeme_id);

update
	collocation_member 
set
	conjunct_lexeme_id = 2326928
where
	conjunct = 'ja'
;

update
	collocation_member 
set
	conjunct_lexeme_id = 1397467
where
	conjunct = 'või'
;

alter table collocation_member drop column conjunct cascade;

analyze collocation_member;
