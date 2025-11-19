
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

delete
from
	publishing pd
where
	pd.entity_name = 'lexeme'
	and exists (
		select
			1
		from
			lexeme l 
		where
			l.id = pd.entity_id
			and l.dataset_code = 'eki'
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
			and exists (
				select
					1
				from
					publishing p 
				where
					p.entity_name = 'lexeme'
					and p.entity_id = l.id
			)
	)
;

-- #6 --

update dataset set code = 'EK_betoon' where code = 'EELBÜTK';

update
	domain cl
set
	datasets = array_append(cl.datasets, 'EK_betoon')
where
	'EELBÜTK' = any(cl.datasets);

update
	domain cl
set
	datasets = array_remove(cl.datasets, 'EELBÜTK')
where
	'EELBÜTK' = any(cl.datasets);


