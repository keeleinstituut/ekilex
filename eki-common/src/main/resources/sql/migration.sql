
create index dataset_is_public_idx on dataset(is_public);

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

