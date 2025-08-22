-- detailsuse likvideerimine süsteemist

alter table meaning_note drop column complexity cascade;
alter table meaning_image drop column complexity cascade;
alter table meaning_media drop column complexity cascade;
alter table definition drop column complexity cascade;
alter table lexeme drop column complexity cascade;
alter table grammar drop column complexity cascade;
alter table government drop column complexity cascade;
alter table usage drop column complexity cascade;
alter table lexeme_note drop column complexity cascade;
alter table collocation drop column complexity cascade;

-- ÕS oma klassif väärtuste liik

insert into label_type (code, value) values ('os', 'os');

-- ebastandardsete tühikute asendamine tekstides

update word w
set 
	value = replace(w.value, chr(160), ' '),
	value_prese = replace(w.value_prese, chr(160), ' ')
where
	w.value like '%' || chr(160) || '%'
;

update definition d
set 
	value = replace(d.value, chr(160), ' '),
	value_prese = replace(d.value_prese, chr(160), ' ')
where
	d.value like '%' || chr(160) || '%'
;

update usage u
set 
	value = replace(u.value, chr(160), ' '),
	value_prese = replace(u.value_prese, chr(160), ' ')
where
	u.value like '%' || chr(160) || '%'
;

update source s
set 
	value = replace(s.value, chr(160), ' '),
	value_prese = replace(s.value_prese, chr(160), ' '),
	name = replace(s.name, chr(160), ' ')
where
	s.value like '%' || chr(160) || '%'
;

update freeform f
set 
	value = replace(f.value, chr(160), ' '),
	value_prese = replace(f.value_prese, chr(160), ' ')
where
	f.value like '%' || chr(160) || '%'
;

update lexeme_note n
set 
	value = replace(n.value, chr(160), ' '),
	value_prese = replace(n.value_prese, chr(160), ' ')
where
	n.value like '%' || chr(160) || '%'
;

update meaning_note n
set 
	value = replace(n.value, chr(160), ' '),
	value_prese = replace(n.value_prese, chr(160), ' ')
where
	n.value like '%' || chr(160) || '%'
;

-- trim

update word w
set 
	value = trim(w.value),
	value_prese = trim(w.value_prese)
where
	w.value != trim(w.value)
;

update definition d
set 
	value = trim(d.value),
	value_prese = trim(d.value_prese)
where
	d.value != trim(d.value)
;

update usage u
set 
	value = trim(u.value),
	value_prese = trim(u.value_prese)
where
	u.value != trim(u.value)
;

update source s
set 
	value = trim(s.value),
	value_prese = trim(s.value_prese),
	name = trim(s.name)
where
	s.value != trim(s.value)
;

update freeform f
set 
	value = trim(f.value),
	value_prese = trim(f.value_prese)
where
	f.value != trim(f.value)
;

update lexeme_note n
set 
	value = trim(n.value),
	value_prese = trim(n.value_prese)
where
	n.value != trim(n.value)
;

update meaning_note n
set 
	value = trim(n.value),
	value_prese = trim(n.value_prese)
where
	n.value != trim(n.value)
;

-- ÕS klassif väärtuste loomine

delete
from
	word_type_label
where
	type = 'os'
;

insert into word_type_label 
	(code, value, lang, type)
values
	('l', 'LÜHEND', 'est', 'os'),
	('lz', 'LÜHEND', 'est', 'os'),
	('tn', 'TÄHENIMI', 'est', 'os'),
	('th', 'TÄHIS', 'est', 'os');
	
delete
from
	register_label 
where
	type = 'os'
;

insert into register_label 
	(code, value, lang, type)
values
	('aja', 'AJALOOLINE', 'est', 'os'),
	('am', 'AMETLIK', 'est', 'os'),
	('hlv', 'HALVUSTAV', 'est', 'os'),
	('hrv', 'HARV', 'est', 'os'),
	('kõnek', 'KÕNEKEELNE', 'est', 'os'),
	('lastek', 'LASTEKEELNE', 'est', 'os'),
	('luulek', 'LUULES', 'est', 'os'),
	('mta', 'MITTEAMETLIK', 'est', 'os'),
	('murd', 'MURDES', 'est', 'os'),
	('piib', 'PIIBLIS', 'est', 'os'),
	('rhv', 'RAHVAKEELNE', 'est', 'os'),
	('sti', 'STIILITUNDLIK', 'est', 'os'),
	('unar', 'UNARSÕNA', 'est', 'os'),
	('van', 'VANANENUD', 'est', 'os');

delete
from
	display_morph_label 
where
	type = 'os'
;

insert into display_morph_label 
	(code, value, lang, type)
values
	('pl', 'mitmus', 'est', 'os'),
	('plt', 'mitmus', 'est', 'os'),
	('sgt', 'ainsus', 'est', 'os'),
	('sga', 'ainsus', 'est', 'os'),
	('keskv', 'keskvõrre', 'est', 'os'),
	('üliv', 'ülivõrre', 'est', 'os');

delete
from
	value_state_label 
where
	type = 'os'
;

insert into value_state_label 
	(code, value, lang, type)
values
	('eelistatud', 'EELISTATUD', 'est', 'os'),
	('mööndav', 'MÖÖNDAV', 'est', 'os'),
	('endine', 'ENDINE', 'est', 'os');

-- ÕS struktuuride ümbernimetamine

create table word_os_recommendation (
	id bigserial primary key,
	orig_id bigint,
	word_id bigint references word(id) on delete cascade not null,
	value text not null, 
	value_prese text not null,
	opt_value text,
	opt_value_prese text,
	is_public boolean default true not null, 
	created_by text null, 
	created_on timestamp null, 
	modified_by text null, 
	modified_on timestamp null,
	unique(word_id)
);
alter sequence word_os_recommendation_id_seq restart with 10000;

create table word_os_usage (
	id bigserial primary key,
	orig_id bigint,
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
alter sequence word_os_usage_id_seq restart with 10000;

create table word_os_morph (
	id bigserial primary key,
	orig_id bigint,
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
alter sequence word_os_morph_id_seq restart with 10000;

create index word_os_recommendation_orig_id_idx on word_os_recommendation(orig_id);
create index word_os_recommendation_word_id_idx on word_os_recommendation(word_id);
create index word_os_recommendation_value_idx on word_os_recommendation(value);
create index word_os_recommendation_value_lower_idx on word_os_recommendation(lower(value));
create index word_os_recommendation_opt_value_idx on word_os_recommendation(opt_value);
create index word_os_recommendation_opt_value_lower_idx on word_os_recommendation(lower(opt_value));
create index word_os_usage_orig_id_idx on word_os_usage(orig_id);
create index word_os_usage_word_id_idx on word_os_usage(word_id);
create index word_os_usage_value_idx on word_os_usage(value);
create index word_os_usage_value_lower_idx on word_os_usage(lower(value));
create index word_os_morph_orig_id_idx on word_os_morph(orig_id);
create index word_os_morph_word_id_idx on word_os_morph(word_id);
create index word_os_morph_value_idx on word_os_morph(value);
create index word_os_morph_value_lower_idx on word_os_morph(lower(value));

insert into word_os_recommendation (
	orig_id,
	word_id,
	value, 
	value_prese,
	opt_value,
	opt_value_prese,
	is_public, 
	created_by, 
	created_on, 
	modified_by, 
	modified_on
)
select
	id,
	word_id,
	value, 
	value_prese,
	opt_value,
	opt_value_prese,
	is_public, 
	created_by, 
	created_on, 
	modified_by, 
	modified_on
from
	word_od_recommendation
order by
	id
;

insert into word_os_usage (
	orig_id,
	word_id,
	value, 
	value_prese,
	is_public, 
	created_by, 
	created_on, 
	modified_by, 
	modified_on,
	order_by
)
select
	id,
	word_id,
	value, 
	value_prese,
	is_public, 
	created_by, 
	created_on, 
	modified_by, 
	modified_on,
	order_by
from
	word_od_usage
order by
	id
;

insert into word_os_morph (
	orig_id,
	word_id,
	value, 
	value_prese,
	is_public, 
	created_by, 
	created_on, 
	modified_by, 
	modified_on
)
select
	id,
	word_id,
	value, 
	value_prese,
	is_public, 
	created_by, 
	created_on, 
	modified_by, 
	modified_on
from
	word_od_morph
order by
	id
;

drop table word_od_recommendation cascade;
drop table word_od_usage cascade;
drop table word_od_morph cascade;

update publishing 
set target_name = 'ww_os' 
where target_name = 'ww_od';

update 
	activity_log a
set 
	entity_id = e.id
from 
	word_os_recommendation e
where
	e.id != e.orig_id
	and a.entity_name = 'WORD_OD_RECOMMENDATION'
	and a.entity_id = e.orig_id
;

update 
	activity_log a
set 
	entity_id = e.id
from 
	word_os_usage e
where
	e.id != e.orig_id
	and a.entity_name = 'WORD_OD_USAGE'
	and a.entity_id = e.orig_id
;

update 
	activity_log a
set 
	entity_id = e.id
from 
	word_os_morph e
where
	e.id != e.orig_id
	and a.entity_name = 'WORD_OD_MORPH'
	and a.entity_id = e.orig_id
;

update activity_log 
set entity_name = 'WORD_OS_RECOMMENDATION'
where entity_name = 'WORD_OD_RECOMMENDATION'
;

update activity_log 
set entity_name = 'WORD_OS_USAGE'
where entity_name = 'WORD_OD_USAGE'
;

update activity_log 
set entity_name = 'WORD_OS_MORPH'
where entity_name = 'WORD_OD_MORPH'
;

alter table word_os_recommendation drop column orig_id cascade;
alter table word_os_usage drop column orig_id cascade;
alter table word_os_morph drop column orig_id cascade;

analyze word;
analyze lexeme;
analyze meaning;
analyze definition;
analyze word_os_recommendation;
analyze word_os_usage;
analyze word_os_morph;
analyze publishing;


