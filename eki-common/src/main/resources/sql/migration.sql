-- #1 --

delete
from
	word_suggestion
where
	id in (
		select
			ws.id
		from (
			select
				(array_agg(ws.id order by ws.id))[2] id
			from
				word_suggestion ws
			group by
				ws.feedback_log_id 
		) ws
		where
			ws.id is not null
	)
;

delete
from
	feedback_log_comment
where
	id in (
		select
			flc.id
		from (
			select
				(array_agg(flc.id order by flc.id))[2] id
			from
				feedback_log fl,
				feedback_log_comment flc 
			where
				flc.feedback_log_id = fl.id
				and fl.feedback_type = 'WORD_SUGGESTION'
				and flc."comment" like 'Avaldatakse%'
			group by
				fl.id
		) flc
		where
			flc.id is not null
	)
;

-- #2 --

alter table meaning_image add column object_filename text null;
alter table meaning_media add column object_filename text null;

-- #3 --

update 
"language" ln
set datasets = array_cat(ln.datasets, dsl.dataset_codes)
from (
	select
		dsl.lang,
		array_agg(dsl.code) dataset_codes
	from (
		select
			ds.code,
			coalesce(w.lang, 'est') lang
		from
			dataset ds
			left outer join lexeme l on l.dataset_code = ds.code
			left outer join word w on w.id = l.word_id
		where
			not exists (
				select
					1
				from
					(
					select
						unnest(ln.datasets) dataset_code
					from
						"language" ln 
					) ln
				where
					ln.dataset_code = ds.code 
			)
		group by
			ds.code,
			w.lang
		order by
			ds.order_by 
	) dsl
	group by
		dsl.lang
) dsl
where
	dsl.lang = ln.code
;

update 
"language" ln
set datasets = lds.dataset_codes
from (
	select
		lds.lang,
		array_agg(lds.dataset_code order by lds.dataset_code) dataset_codes
	from (
		select
			ln.code lang,
			unnest(ln.datasets) dataset_code
		from
			"language" ln
		group by
			lang,
			dataset_code
		order by
			lang,
			dataset_code
	) lds
	group by
		lds.lang
) lds
where
	lds.lang = ln.code
;

-- #4 --

alter table word_suggestion add column published_word_value text null;

-- Loo uuesti ekilexi baasi vaated (main_create_ww_views.sql)

-- #5 --

create table language_group (
	id bigserial primary key,
	parent_language_group_id bigint references language_group(id) on delete cascade, 
	name text not null,
	unique(name)
);
alter sequence language_group_id_seq restart with 10000;

create index language_group_parent_language_group_id_idx on language_group(parent_language_group_id);
create index language_group_name_idx on language_group(name);

create table language_group_member (
	id bigserial primary key,
	language_group_id bigint references language_group(id) on delete cascade not null, 
	lang char(3) references language(code) not null,
	unique(language_group_id, lang)
);
alter sequence language_group_member_id_seq restart with 10000;

create index language_group_member_language_group_id_idx on language_group_member(language_group_id);
create index language_group_member_lang_idx on language_group_member(lang);



