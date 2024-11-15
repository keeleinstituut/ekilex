-- upgrade from ver 1.38.* to 1.39.0 #1

-- API kasutuse stat

create table api_request_count (
	id bigserial primary key,
	auth_name text not null,
	generic_path text not null,
	count bigint not null default 0,
	unique (auth_name, generic_path)
);

create index api_request_count_auth_name_idx on api_request_count(auth_name);
create index api_request_count_generic_path_idx on api_request_count(generic_path);
create index api_request_count_count_idx on api_request_count(count);

create table api_error_count (
	id bigserial primary key,
	auth_name text not null,
	generic_path text not null,
	message text not null,
	count bigint not null default 0,
	unique (auth_name, generic_path, message)
);

create index api_error_count_auth_name_idx on api_error_count(auth_name);
create index api_error_count_generic_path_idx on api_error_count(generic_path);
create index api_error_count_message_idx on api_error_count(message);
create index api_error_count_count_idx on api_error_count(count);

-- vabavormide struktuuri ühtlustamine

update
	freeform f
set
	value_text = to_char(f.value_date, 'DD.MM.YYYY')
where
	f.freeform_type_code = 'SOURCE_PUBLICATION_YEAR'
	and f.value_date is not null
;

alter table freeform drop column value_date;
alter table freeform drop column value_number;
alter table freeform rename column value_text to value;
alter index freeform_value_text_idx rename to freeform_value_idx;
alter index freeform_value_text_lower_idx rename to freeform_value_lower_idx;

drop type type_mt_lexeme_freeform;
create type type_mt_lexeme_freeform as (
  lexeme_id bigint, 
  freeform_id bigint, 
  freeform_type_code varchar(100), 
  value text, 
  value_prese text, 
  lang char(3), 
  complexity varchar(100), 
  is_public boolean, 
  created_by text, 
  created_on timestamp, 
  modified_by text, 
  modified_on timestamp
);

delete from freeform where value is null;
alter table freeform alter column value set not null;

-- keelendi abstraktsiooni tunnuste kolimine

alter table lexeme add column is_word boolean;
alter table lexeme add column is_collocation boolean;

update lexeme l
set is_word = true,
	is_collocation = false 
where l.dataset_code != 'eki';

update lexeme l
set is_word = w.is_word,
	is_collocation = w.is_collocation
from
(select
	id,
	is_word,
	is_collocation
from
	word) w
where
	l.word_id = w.id
	and l.dataset_code = 'eki';

alter table lexeme alter column is_word set not null;
alter table lexeme alter column is_collocation set not null;
create index lexeme_is_word_idx on lexeme(is_word);
create index lexeme_is_collocation_idx on lexeme(is_collocation);
alter table word drop column is_word cascade;
alter table word drop column is_collocation cascade;
analyze lexeme;

-- peremeheta vormiviidete asendamine kollokatsioonide liikmetes

update
	collocation_member cm
set
	member_form_id = cmfa.suggested_form_id
from
	(
	select
		cm.id colloc_member_id,
		f1.id floating_form_id,
		fa.form_id suggested_form_id,
		fa.form_value,
		fa.word_value
	from
		form f1
	inner join collocation_member cm on
		cm.member_form_id = f1.id
	inner join 
	(
		select
			f.id form_id,
			f.value form_value,
			f.value_prese form_value_prese,
			f.morph_code,
			w.value word_value
		from
			form f,
			paradigm_form pf,
			paradigm p,
			word w
		where
			pf.form_id = f.id
			and pf.paradigm_id = p.id
			and p.word_id = w.id
	) fa on
		fa.form_value = f1.value
		and fa.morph_code = f1.morph_code
		and fa.form_id != f1.id
	where
		not exists (
		select
			1
		from
			paradigm_form pf1
		where
			pf1.form_id = f1.id
	)
	order by
		f1.value
) cmfa
where
	cm.id = cmfa.colloc_member_id
;

-- peremeheta asendamatute vormiviidetega kollokatsioonide liikmete kustutamine

delete
from
	collocation_member cm
where
	cm.id in (
		select
			cm.id
		from
			collocation_member cm
		where
			exists (
				select
					1
				from
					form f1
				where
					f1.id = cm.member_form_id
					and not exists (
						select
							1
						from
							paradigm_form pf1
						where
							pf1.form_id = f1.id
					)
					and not exists (
						select
							1
						from
							form f2,
							paradigm_form pf2
						where
							pf2.form_id = f2.id
							and f2.value = f1.value
							and f2.morph_code = f1.morph_code
							and f2.id != f1.id
				)
			)
	);

-- sõnaveebi tagasiside lihtsustamine

alter table feedback_log alter column sender_name drop not null;
alter table feedback_log alter column sender_email drop not null;

update
	feedback_log
set
	description = "comments"
where
	description is null
	and "comments" is not null;

alter table feedback_log drop column "comments";

update
	feedback_log
set
	feedback_type = 'väline'
where
	feedback_type = 'comment';

update
	feedback_log
set
	feedback_type = 'sõnaveeb'
where
	feedback_type = 'new_word';

update
	feedback_log
set
	feedback_type = 'sõnaveeb'
where
	feedback_type = 'complete';

update
	feedback_log
set
	feedback_type = 'sõnaveeb'
where
	feedback_type = 'simple';
	
-- uus klassifikaatori väärtuse liik

insert into label_type (code, value) values ('comment', 'comment');
