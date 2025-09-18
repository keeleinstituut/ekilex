
-- #1 --

create index dataset_is_public_idx on dataset(is_public);

-- #2 --

alter table word_os_recommendation drop column is_public cascade;
alter table word_os_usage drop column is_public cascade;
alter table word_os_morph drop column is_public cascade;

alter table word_os_recommendation drop constraint word_os_recommendation_word_id_key;

insert into word_os_recommendation (
	word_id,
	value,
	value_prese,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	word_id,
	opt_value,
	opt_value_prese,
	created_by,
	created_on,
	modified_by,
	modified_on
from
	word_os_recommendation
where
	opt_value is not null
	and opt_value != ''
order by
	id
;

alter table word_os_recommendation drop column opt_value cascade;
alter table word_os_recommendation drop column opt_value_prese cascade;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'word_os_recommendation',
	wor.id
from
	word_os_recommendation wor
where
	not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'word_os_recommendation'
			and p.entity_id = wor.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_os',
	'word_os_recommendation',
	wor.word_os_recommendation_id
from
	(
	select
		(array_agg(wor.id order by wor.id))[1] word_os_recommendation_id
	from
		word_os_recommendation wor
	group by
		wor.word_id
	order by
		wor.word_id
	) wor
where
	not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_os'
			and p.entity_name = 'word_os_recommendation'
			and p.entity_id = wor.word_os_recommendation_id
	)
;

-- #3 --

delete from 
	usage u
where
	u.is_public = false
	and exists (
		select
			1
		from
			lexeme l 
		where 
			l.id = u.lexeme_id
			and l.dataset_code = 'eki'
	)
;

drop table usage_definition cascade;

-- #4 --

create table word_eki_recommendation (
	id bigserial primary key,
	orig_id bigint,
	word_id bigint references word(id) on delete cascade not null,
	value text not null, 
	value_prese text not null,
	created_by text null, 
	created_on timestamp null, 
	modified_by text null, 
	modified_on timestamp null
);
alter sequence word_eki_recommendation_id_seq restart with 10000;

create index word_eki_recommendation_orig_id_idx on word_eki_recommendation(orig_id);
create index word_eki_recommendation_word_id_idx on word_eki_recommendation(word_id);
create index word_eki_recommendation_value_idx on word_eki_recommendation(value);
create index word_eki_recommendation_value_lower_idx on word_eki_recommendation(lower(value));

insert into word_eki_recommendation (
	orig_id,
	word_id,
	value,
	value_prese,
	created_by,
	created_on,
	modified_by,
	modified_on
)
select
	wor.id,
	wor.word_id,
	wor.value,
	wor.value_prese,
	wor.created_by,
	wor.created_on,
	wor.modified_by,
	wor.modified_on
from
	word_os_recommendation wor
order by
	wor.id
;

drop table word_os_recommendation cascade;

update
	publishing p
set
	entity_id = e.id
from (
	select
		e.id,
		e.orig_id
	from
		word_eki_recommendation e
	order by
		e.id
) e
where
	e.id != e.orig_id
	and p.entity_name = 'word_os_recommendation'
	and p.entity_id = e.orig_id
;

update
	publishing
set
	entity_name = 'word_eki_recommendation'
where
	entity_name = 'word_os_recommendation'
;

update 
	activity_log a
set 
	entity_id = e.id
from 
	word_eki_recommendation e
where
	e.id != e.orig_id
	and a.entity_name = 'WORD_OS_RECOMMENDATION'
	and a.entity_id = e.orig_id
;

update
	activity_log
set
	entity_name = 'WORD_EKI_RECOMMENDATION'
where
	entity_name = 'WORD_OS_RECOMMENDATION'
;

alter table word_eki_recommendation drop column orig_id cascade;



