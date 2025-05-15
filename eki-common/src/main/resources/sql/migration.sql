
-- ÕS kasutusnäited ja lühimorfo --

create table word_od_usage (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null, 
  value_prese text not null,
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null,
  order_by bigserial
);
alter sequence word_od_usage_id_seq restart with 10000;

create index word_od_usage_word_id_idx on word_od_usage(word_id);
create index word_od_usage_value_idx on word_od_usage(value);
create index word_od_usage_value_lower_idx on word_od_usage(lower(value));

create table word_od_morph (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null, 
  value_prese text not null,
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null,
  unique(word_id)
);
alter sequence word_od_morph_id_seq restart with 10000;

create index word_od_morph_word_id_idx on word_od_morph(word_id);
create index word_od_morph_value_idx on word_od_morph(value);
create index word_od_morph_value_lower_idx on word_od_morph(lower(value));

-- ÕS liitsõnaseosed --

insert into word_rel_type (code, datasets) values ('ls-esiosaga', '{}');
insert into word_rel_type (code, datasets) values ('ls-järelosaga', '{}');
insert into word_rel_type_label (code, value, lang, type) values ('ls-esiosaga', 'Liitsõna esiosaga', 'est', 'descrip');
insert into word_rel_type_label (code, value, lang, type) values ('ls-järelosaga', 'Liitsõna järelosaga', 'est', 'descrip');
insert into word_rel_type_label (code, value, lang, type) values ('ls-esiosaga', 'Liitsõna esiosaga', 'est', 'wordweb');
insert into word_rel_type_label (code, value, lang, type) values ('ls-järelosaga', 'Liitsõna järelosaga', 'est', 'wordweb');
insert into word_rel_mapping (code1, code2) values ('ls-esiosa', 'ls-esiosaga');
insert into word_rel_mapping (code1, code2) values ('ls-järelosa', 'ls-järelosaga');

-- keelendite registreerimine --

alter table word add column reg_year integer;
create index word_reg_year_idx on word(reg_year);

update word w
set reg_year = rw.reg_year
from (
	select
		l.word_id,
		right(lr.register_code, 4)::int reg_year
	from
		lexeme l,
		lexeme_register lr
	where
		lr.lexeme_id = l.id
		and lr.register_code like 'uus%'
	group by
		l.word_id,
		lr.register_code
) rw
where
	w.id = rw.word_id
;

insert into register (code, datasets) values ('uus', '{}');
insert into register_label (code, value, lang, type) values ('uus', 'uus', 'est', 'descrip');
insert into register_label (code, value, lang, type) values ('uus', 'uus', 'est', 'wordweb');
insert into register_label (code, value, lang, type) values ('uus', 'new', 'eng', 'descrip');
insert into register_label (code, value, lang, type) values ('uus', 'new', 'eng', 'wordweb');
insert into register_label (code, value, lang, type) values ('uus', 'новый', 'rus', 'descrip');
insert into register_label (code, value, lang, type) values ('uus', 'новый', 'rus', 'wordweb');

insert into lexeme_register (lexeme_id, register_code)
select
	l.id,
	'uus'
from
	lexeme l
where
	exists (
		select
			1
		from
			lexeme_register lr
		where
			lr.lexeme_id = l.id
			and lr.register_code like 'uus20%'
	)
;

delete from lexeme_register where register_code like 'uus20%';
delete from register where code like 'uus20%';

-- kollokatsioonide duplikaatide kustutamine --

alter table collocation_member
drop constraint collocation_member_colloc_lexeme_id_fkey,
add constraint collocation_member_colloc_lexeme_id_fkey foreign key (colloc_lexeme_id) references lexeme (id) on delete cascade;

-- !! 30 min !! --
delete
from
	lexeme l
where
	exists (
		select
			1
		from
			(
			select
				 (array_agg(cw1.colloc_lexeme_id order by cw1.colloc_lexeme_id))[1] orig_colloc_lexeme_id,
				 array_agg(cw1.colloc_lexeme_id order by cw1.colloc_lexeme_id) colloc_lexeme_ids
			from
				(
				select
					cl.id colloc_lexeme_id,
					cw.id colloc_word_id,
					cw.value,
					cl.complexity,
					(
					select
						array_agg(cm.member_lexeme_id order by cm.member_order)
					from
						collocation_member cm
					where
						cm.colloc_lexeme_id = cl.id
					) member_lexeme_ids,
					(
					select
						array_agg(cm.member_form_id order by cm.member_order)
					from
						collocation_member cm
					where
						cm.colloc_lexeme_id = cl.id
					) member_form_ids,
					(
					select
						coalesce(array_agg(u.value order by u.value), '{}')
					from 
						usage u
					where
						u.lexeme_id = cl.id
					) usage_values,
					(
					select
						coalesce(array_agg(d.value order by d.value), '{}')
					from
						definition d 
					where
						d.meaning_id = cl.meaning_id 
						and exists (
							select
								1
							from
								definition_dataset dd 
							where
								dd.definition_id = d.id
								and dd.dataset_code = 'eki'
						)
					) definition_values
				from
					word cw,
					lexeme cl
				where
					cl.word_id = cw.id
					and cl.is_collocation = true
					and cl.is_word = false
				) cw1
			where
				exists (
					select
						1
					from (
						select
							cl.id colloc_lexeme_id,
							cw.value,
							cl.complexity,
							(
							select
								array_agg(cm.member_lexeme_id order by cm.member_order)
							from
								collocation_member cm
							where
								cm.colloc_lexeme_id = cl.id
							) member_lexeme_ids,
							(
							select
								array_agg(cm.member_form_id order by cm.member_order)
							from
								collocation_member cm
							where
								cm.colloc_lexeme_id = cl.id
							) member_form_ids,
							(
							select
								coalesce(array_agg(u.value order by u.value), '{}')
							from 
								usage u
							where
								u.lexeme_id = cl.id
							) usage_values,
							(
							select
								coalesce(array_agg(d.value order by d.value), '{}')
							from
								definition d 
							where
								d.meaning_id = cl.meaning_id 
								and exists (
									select
										1
									from
										definition_dataset dd 
									where
										dd.definition_id = d.id
										and dd.dataset_code = 'eki'
								)
							) definition_values
						from
							word cw,
							lexeme cl
						where
							cl.word_id = cw.id
							and cl.is_collocation = true
							and cl.is_word = false
						) cw2
					where
						cw2.value = cw1.value
						and cw2.complexity = cw1.complexity
						and cw2.member_lexeme_ids = cw1.member_lexeme_ids
						and cw2.member_form_ids = cw1.member_form_ids
						and cw2.usage_values = cw1.usage_values
						and cw2.definition_values = cw1.definition_values
						and cw2.colloc_lexeme_id != cw1.colloc_lexeme_id
				)
			group by
				cw1.value,
				cw1.member_lexeme_ids,
				cw1.member_form_ids,
				cw1.usage_values,
				cw1.definition_values
			) dl
		where
			l.id = any (dl.colloc_lexeme_ids)
			and l.id != dl.orig_colloc_lexeme_id
	)
;

delete
from 
	word w 
where
	not exists (
		select
			1
		from
			lexeme l 
		where
			l.word_id = w.id
	)
;

-- publitseerimine --

create table publishing (
	id bigserial primary key,
	event_by text not null,
	event_on timestamp not null default statement_timestamp(),
	target_name varchar(100) not null,
	entity_name varchar(100) not null,
	entity_id bigint not null,
	unique (target_name, entity_name, entity_id)
);
alter sequence publishing_id_seq restart with 10000;

create index publishing_event_by_idx on publishing(event_by);
create index publishing_event_on_idx on publishing(event_on);
create index publishing_target_name_idx on publishing(target_name);
create index publishing_entity_name_idx on publishing(entity_name);
create index publishing_entity_id_idx on publishing(entity_id);

-- detailsuse kolimine publitseerimisse --



insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'definition',
	d.id
from
	definition d
where
	d.complexity in ('ANY', 'DETAIL')
	and d.is_public = true
	and exists (
		select
			1
		from
			definition_dataset dd
		where
			dd.definition_id = d.id
			and dd.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'definition'
			and p.entity_id = d.id
	)
; -- 151977

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'definition',
	d.id
from
	definition d
where
	d.complexity in ('ANY', 'SIMPLE')
	and d.is_public = true
	and exists (
		select
			1
		from
			lexeme l
		where
			l.meaning_id = d.meaning_id 
			and l.complexity in ('ANY', 'SIMPLE')
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
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'definition'
			and p.entity_id = d.id
	)
; -- 7904

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_od',
	'definition',
	d.id
from
	definition d
where
	d.complexity = 'ANY'
	and d.is_public = false
	and exists (
		select
			1
		from
			definition_dataset dd
		where
			dd.definition_id = d.id
			and dd.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'definition'
			and p.entity_id = d.id
	)
; -- 39214




