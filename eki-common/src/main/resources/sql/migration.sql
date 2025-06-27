
-- ÕS deffide avaldamise muudatused --

delete
from 
	publishing p
where
	p.target_name = 'ww_od'
	and p.entity_name = 'definition'
	and exists (
		select
			1
		from
			definition d
		where
			d.id = p.entity_id
			and d.lang != 'est'
	)
;

insert into publishing (
	event_by,
	target_name,
	entity_name,
	entity_id
)
select
	'Laadur',
	'ww_od',
	'definition',
	d.id
from 
	definition d
where
	d.is_public = true
	and d.complexity = 'ANY'
	and d.lang = 'est'
	and exists (
		select
			1
		from
			lexeme l
		where 
			l.meaning_id = d.meaning_id 
			and l.dataset_code = 'eki'
	)
	and exists (
		select
			1
		from
			definition_dataset dd
		where 
			dd.definition_id = d.id
			and dd.dataset_code = 'eki'
	)
	and exists (
		select
			1
		from
			lexeme l 
		where
			l.meaning_id = d.meaning_id 
			and exists (
				select
					p.id
				from
					publishing p 
				where
					p.target_name = 'ww_od'
					and p.entity_name = 'lexeme'
					and p.entity_id = l.id
			)
	)
	and not exists (
		select
			p.id
		from
			publishing p 
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'definition'
			and p.entity_id = d.id)
;

delete
from
	publishing p1
where
	p1.target_name in ('ww_unif', 'ww_lite')
	and p1.entity_name = 'definition'
	and exists (
		select
			1
		from
			definition d1
		where
			p1.entity_id = d1.id
			and d1.lang = 'est'
			and d1.is_public = false
			and exists (
				select
					1
				from
					publishing p2
				where
					p2.target_name = 'ww_od'
					and p2.entity_name = 'definition'
					and p2.entity_id = d1.id
			)
			and exists (
				select
					1
				from
					definition d2,
					publishing p3
				where
					p3.target_name in ('ww_unif', 'ww_lite')
					and p3.entity_name = 'definition'
					and p3.entity_id = d2.id
					and d2.lang = 'est'
					and d2.id != d1.id
					and d2.meaning_id = d1.meaning_id
					and d2.lang = 'est'
			)
	)
;

update
	definition d 
set
	is_public = true
where
	d.is_public = false
	and d.lang = 'est'
	and exists (
		select
			1
		from
			definition_dataset dd
		where 
			dd.definition_id = d.id
			and dd.dataset_code = 'eki'
	)
	and exists (
		select
			1
		from
			publishing p 
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'definition'
			and p.entity_id = d.id
	)
;


