-- upgrade from ver 1.43.* to 1.44.0


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

-- #2 --

delete
from
	lexeme cl
where
	cl.is_word = false
	and cl.is_collocation = true
	and not exists (
		select
			1
		from
			collocation_member cm
		where
			cm.colloc_lexeme_id = cl.id
	)
;

-- #3 --

alter table meaning_media add column title text null;
create index meaning_media_title_idx on meaning_media(title);

-- #4 --

delete from form where morph_code = 'Vlyhi';
delete from morph_label where code = 'Vlyhi';
delete from morph where code = 'Vlyhi';

-- #5 --

update
	lexeme l 
set
	is_word = false
where
	l.dataset_code = 'eki'
	and l.is_word = true
	and l.is_collocation = true
	and not exists (
		select
			1
		from
			definition d 
		where
			d.meaning_id = l.meaning_id
	)
	and not exists (
		select
			1
		from
			lexeme l2
		where 
			l2.meaning_id = l.meaning_id
			and l2.id != l.id
			and l2.dataset_code = l.dataset_code
	)
;

-- #6 --

update dataset set code = 'EK_Betoon' where code = 'EELBÜTK';

update
	domain cl
set
	datasets = array_append(cl.datasets, 'EK_Betoon')
where
	'EELBÜTK' = any(cl.datasets);

update
	domain cl
set
	datasets = array_remove(cl.datasets, 'EELBÜTK')
where
	'EELBÜTK' = any(cl.datasets);


