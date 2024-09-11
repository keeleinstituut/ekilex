-- vabavormide kolimine olemiteks

----------
-- drop --
----------

drop table if exists definition_note_source_link cascade;
drop table if exists definition_note cascade;
drop table if exists meaning_image_source_link cascade;
drop table if exists meaning_image cascade;
drop table if exists meaning_note_source_link cascade;
drop table if exists meaning_note cascade;
drop table if exists lexeme_note_source_link cascade;
drop table if exists lexeme_note cascade;
drop table if exists usage_definition cascade;
drop table if exists usage_translation cascade;
drop table if exists usage_source_link cascade;
drop table if exists usage cascade;

------------
-- create --
------------

create table usage (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  complexity varchar(100) not null, 
  is_public boolean not null default true, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence usage_id_seq restart with 10000;

create index usage_original_freeform_id_idx on usage(original_freeform_id);
create index usage_lexeme_id_idx on usage(lexeme_id);
create index usage_lang_idx on usage(lang);
create index usage_complexity_idx on usage(complexity);
create index usage_is_public_idx on usage(is_public);
create index usage_fts_idx on usage using gin(to_tsvector('simple', value));

create table usage_source_link (
	id bigserial primary key, 
	usage_id bigint references usage(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	type varchar(100) not null, 
	name text null, 
	order_by bigserial
);
alter sequence usage_source_link_id_seq restart with 10000;

create index usage_source_link_usage_id_idx on usage_source_link(usage_id);
create index usage_source_link_source_id_idx on usage_source_link(source_id);
create index usage_source_link_name_idx on usage_source_link(name);
create index usage_source_link_name_lower_idx on usage_source_link(lower(name));

create table usage_translation (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  usage_id bigint references usage(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence usage_translation_id_seq restart with 10000;

create index usage_translation_original_freeform_id_idx on usage_translation(original_freeform_id);
create index usage_translation_usage_id_idx on usage_translation(usage_id);
create index usage_translation_lang_idx on usage_translation(lang);

create table usage_definition (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  usage_id bigint references usage(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence usage_definition_id_seq restart with 10000;

create index usage_definition_original_freeform_id_idx on usage_definition(original_freeform_id);
create index usage_definition_usage_id_idx on usage_definition(usage_id);
create index usage_definition_lang_idx on usage_definition(lang);

create table lexeme_note (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  complexity varchar(100) not null, 
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence lexeme_note_id_seq restart with 10000;

create index lexeme_note_original_freeform_id_idx on lexeme_note(original_freeform_id);
create index lexeme_note_lexeme_id_idx on lexeme_note(lexeme_id);
create index lexeme_note_lang_idx on lexeme_note(lang);
create index lexeme_note_complexity_idx on lexeme_note(complexity);
create index lexeme_note_is_public_idx on lexeme_note(is_public);
create index lexeme_note_fts_idx on lexeme_note using gin(to_tsvector('simple', value));

create table lexeme_note_source_link (
	id bigserial primary key, 
	lexeme_note_id bigint references lexeme_note(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	type varchar(100) not null, 
	name text null, 
	order_by bigserial
);
alter sequence lexeme_note_source_link_id_seq restart with 10000;

create index lexeme_note_source_link_lexeme_note_id_idx on lexeme_note_source_link(lexeme_note_id);
create index lexeme_note_source_link_source_id_idx on lexeme_note_source_link(source_id);
create index lexeme_note_source_link_name_idx on lexeme_note_source_link(name);
create index lexeme_note_source_link_name_lower_idx on lexeme_note_source_link(lower(name));

create table meaning_note (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  meaning_id bigint references meaning(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  complexity varchar(100) not null, 
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence meaning_note_id_seq restart with 10000;

create index meaning_note_original_freeform_id_idx on meaning_note(original_freeform_id);
create index meaning_note_meaning_id_idx on meaning_note(meaning_id);
create index meaning_note_lang_idx on meaning_note(lang);
create index meaning_note_complexity_idx on meaning_note(complexity);
create index meaning_note_is_public_idx on meaning_note(is_public);
create index meaning_note_fts_idx on meaning_note using gin(to_tsvector('simple', value));

create table meaning_note_source_link (
	id bigserial primary key, 
	meaning_note_id bigint references meaning_note(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	type varchar(100) not null, 
	name text null, 
	order_by bigserial
);
alter sequence meaning_note_source_link_id_seq restart with 10000;

create index meaning_note_source_link_meaning_note_id_idx on meaning_note_source_link(meaning_note_id);
create index meaning_note_source_link_source_id_idx on meaning_note_source_link(source_id);
create index meaning_note_source_link_name_idx on meaning_note_source_link(name);
create index meaning_note_source_link_name_lower_idx on meaning_note_source_link(lower(name));

create table meaning_image (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  meaning_id bigint references meaning(id) on delete cascade not null, 
  title text null, 
  url text not null, 
  complexity varchar(100) not null, 
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence meaning_image_id_seq restart with 10000;

create index meaning_image_original_freeform_id_idx on meaning_image(original_freeform_id);
create index meaning_image_meaning_id_idx on meaning_image(meaning_id);
create index meaning_image_lang_idx on meaning_image(title);
create index meaning_image_complexity_idx on meaning_image(complexity);
create index meaning_image_is_public_idx on meaning_image(is_public);

create table meaning_image_source_link (
	id bigserial primary key, 
	meaning_image_id bigint references meaning_image(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	type varchar(100) not null, 
	name text null, 
	order_by bigserial
);
alter sequence meaning_image_source_link_id_seq restart with 10000;

create index meaning_image_source_link_meaning_image_id_idx on meaning_image_source_link(meaning_image_id);
create index meaning_image_source_link_source_id_idx on meaning_image_source_link(source_id);
create index meaning_image_source_link_name_idx on meaning_image_source_link(name);
create index meaning_image_source_link_name_lower_idx on meaning_image_source_link(lower(name));

create table definition_note (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  definition_id bigint references definition(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  complexity varchar(100) not null, 
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence definition_note_id_seq restart with 10000;

create index definition_note_original_freeform_id_idx on definition_note(original_freeform_id);
create index definition_note_definition_id_idx on definition_note(definition_id);
create index definition_note_lang_idx on definition_note(lang);
create index definition_note_complexity_idx on definition_note(complexity);
create index definition_note_is_public_idx on definition_note(is_public);
create index definition_note_fts_idx on definition_note using gin(to_tsvector('simple', value));

create table definition_note_source_link (
	id bigserial primary key, 
	definition_note_id bigint references definition_note(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	type varchar(100) not null, 
	name text null, 
	order_by bigserial
);
alter sequence definition_note_source_link_id_seq restart with 10000;

create index definition_note_source_link_definition_note_id_idx on definition_note_source_link(definition_note_id);
create index definition_note_source_link_source_id_idx on definition_note_source_link(source_id);
create index definition_note_source_link_name_idx on definition_note_source_link(name);
create index definition_note_source_link_name_lower_idx on definition_note_source_link(lower(name));

------------
-- insert --
------------

insert into usage (
	original_freeform_id,
	lexeme_id,
	value,
	value_prese,
	lang,
	complexity,
	is_public,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	lf.lexeme_id,
	f.value_text,
	f.value_prese,
	coalesce(f.lang, 'est'),
	f.complexity,
	f.is_public,
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	lexeme_freeform lf,
	freeform f
where
	lf.freeform_id = f.id
	and f.type = 'USAGE'
order by f.order_by;

insert into usage_source_link (
	usage_id,
	source_id,
	type,
	name)
select
	u.id,
	fsl.source_id,
	fsl.type,
	fsl.name
from
	usage u,
	freeform_source_link fsl
where
	fsl.freeform_id = u.original_freeform_id
order by u.order_by, fsl.order_by;

insert into usage_translation (
	original_freeform_id,
	usage_id,
	value,
	value_prese,
	lang,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	u.id,
	f.value_text,
	f.value_prese,
	coalesce(f.lang, 'rus'),
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	usage u,
	freeform f
where
	f.parent_id = u.original_freeform_id
	and f.type = 'USAGE_TRANSLATION'
order by u.order_by, f.order_by;

insert into usage_definition (
	original_freeform_id,
	usage_id,
	value,
	value_prese,
	lang,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	u.id,
	f.value_text,
	f.value_prese,
	coalesce(f.lang, 'est'),
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	usage u,
	freeform f
where
	f.parent_id = u.original_freeform_id
	and f.type = 'USAGE_DEFINITION'
order by u.order_by, f.order_by;

insert into lexeme_note (
	original_freeform_id,
	lexeme_id,
	value,
	value_prese,
	lang,
	complexity,
	is_public,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	lf.lexeme_id,
	f.value_text,
	f.value_prese,
	f.lang,
	coalesce(f.complexity, 'DETAIL'),
	f.is_public,
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	lexeme_freeform lf,
	freeform f
where
	lf.freeform_id = f.id
	and f.type = 'NOTE'
	and f.value_text is not null
order by f.order_by;

insert into lexeme_note_source_link (
	lexeme_note_id,
	source_id,
	type,
	name)
select
	ln.id,
	fsl.source_id,
	fsl.type,
	fsl.name
from
	lexeme_note ln,
	freeform_source_link fsl
where
	fsl.freeform_id = ln.original_freeform_id
order by ln.order_by, fsl.order_by;

insert into meaning_note (
	original_freeform_id,
	meaning_id,
	value,
	value_prese,
	lang,
	complexity,
	is_public,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	mf.meaning_id,
	f.value_text,
	coalesce(f.value_prese, f.value_text),
	f.lang,
	coalesce(f.complexity, 'DETAIL'),
	f.is_public,
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	meaning_freeform mf,
	freeform f
where
	mf.freeform_id = f.id
	and f.type = 'NOTE'
	and f.value_text is not null
order by f.order_by;

insert into meaning_note_source_link (
	meaning_note_id,
	source_id,
	type,
	name)
select
	mn.id,
	fsl.source_id,
	fsl.type,
	fsl.name
from
	meaning_note mn,
	freeform_source_link fsl
where
	fsl.freeform_id = mn.original_freeform_id
order by mn.order_by, fsl.order_by;

insert into meaning_image (
	original_freeform_id,
	meaning_id,
	title,
	url,
	complexity,
	is_public,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f1.id,
	mf.meaning_id,
	f2.value_text,
	f1.value_text,
	f1.complexity,
	f1.is_public,
	f1.created_by,
	f1.created_on,
	f1.modified_by,
	f1.modified_on
from
	freeform f1
	inner join meaning_freeform mf on mf.freeform_id = f1.id
	left outer join freeform f2 on f2.parent_id = f1.id and f2.type = 'IMAGE_TITLE'
where
	f1.type = 'IMAGE_FILE'
	and f1.value_text is not null
order by f1.order_by;

insert into meaning_image_source_link (
	meaning_image_id,
	source_id,
	type,
	name)
select
	mi.id,
	fsl.source_id,
	fsl.type,
	fsl.name
from
	meaning_image mi,
	freeform_source_link fsl
where
	fsl.freeform_id = mi.original_freeform_id
order by mi.order_by, fsl.order_by;

insert into definition_note (
	original_freeform_id,
	definition_id,
	value,
	value_prese,
	lang,
	complexity,
	is_public,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	df.definition_id,
	f.value_text,
	f.value_prese,
	f.lang,
	coalesce(f.complexity, 'DETAIL'),
	f.is_public,
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	definition_freeform df,
	freeform f
where
	df.freeform_id = f.id
	and f.type = 'NOTE'
	and f.value_text is not null
order by f.order_by;

insert into definition_note_source_link (
	definition_note_id,
	source_id,
	type,
	name)
select
	dn.id,
	fsl.source_id,
	fsl.type,
	fsl.name
from
	definition_note dn,
	freeform_source_link fsl
where
	fsl.freeform_id = dn.original_freeform_id
order by dn.order_by, fsl.order_by;

update freeform f set type = 'MEANING_IMAGE' where f.type = 'IMAGE_FILE' and exists (select 1 from meaning_freeform mf where mf.freeform_id = f.id);
alter table lexeme_source_link drop column if exists value;
alter table definition_source_link drop column if exists value;

-- kasutamata väljad vabavormides

alter table freeform drop column value_array cascade;
alter table freeform drop column classif_code cascade;
alter table freeform drop column classif_name cascade;

-- freeform.value_date ? (USAGE, SOURCE_PUBLICATION_YEAR)
-- freeform.value_number ? (NOTE, IMAGE_FILE, MEANING_IMAGE)

-- muuda ilma selge päritolufailita kasutamata allikate sõnakogu tegutsejate õiguste järgi

-- skript #1

update "source" su
set dataset_code = s.sugg_dataset_code
from (
	select
		s.id,
		case
			when array_length(s.event_by_perm_dataset_codes, 1) = 1 then s.event_by_perm_dataset_codes[1]
			when 'eki' = any(s.event_by_perm_dataset_codes) then 'eki'
			when (s.event_by = 'Esta Prangel (API)' and s.name like '%§%') then 'RTtermleg'
			when array_length(s.event_by_perm_dataset_codes, 1) > 1 then 'kce'
			else 'kce'
		end sugg_dataset_code
	from
	(
		select
			s.*,
			(
				select
					array_agg(distinct dp.dataset_code)
				from
					eki_user eu,
					dataset_permission dp
				where
					eu.name = s.event_by
					and dp.user_id = eu.id
					and dp.auth_operation in ('OWN', 'CRUD')
					and dp.dataset_code not in ('kce', 'vrk')
				group by
					eu.name
			) event_by_perm_dataset_codes
		from
			(
			select
				s.*,
				(
					select
						f.value_text
					from
						source_freeform sf,
						freeform f
					where
						sf.source_id = s.id
						and sf.freeform_id = f.id
						and f."type" = 'SOURCE_FILE'
					limit 1
				) source_file,
				(
					select
						al.event_by
					from
						source_activity_log sal,
						activity_log al
					where
						sal.source_id = s.id
						and sal.activity_log_id = al.id
					order by
						al.id
					limit 1
				) event_by
			from
				"source" s
			where
				not exists (
					select
						xsl.id
					from
						freeform_source_link xsl
					where
						xsl.source_id = s.id
				)
				and not exists (
					select
						xsl.id
					from
						word_etymology_source_link xsl
					where
						xsl.source_id = s.id
				)
				and not exists (
					select
						xsl.id
					from
						meaning_note_source_link xsl
					where
						xsl.source_id = s.id
				)
				and not exists (
					select
						xsl.id
					from
						meaning_image_source_link xsl
					where
						xsl.source_id = s.id
				)
				and not exists (
					select
						xsl.id
					from
						definition_source_link xsl
					where
						xsl.source_id = s.id
				)
				and not exists (
					select
						xsl.id
					from
						definition_note_source_link xsl
					where
						xsl.source_id = s.id
				)
				and not exists (
					select
						xsl.id
					from
						lexeme_source_link xsl
					where
						xsl.source_id = s.id
				)
				and not exists (
					select
						xsl.id
					from
						usage_source_link xsl
					where
						xsl.source_id = s.id
				)
				and not exists (
					select
						xsl.id
					from
						lexeme_note_source_link xsl
					where
						xsl.source_id = s.id
				)
		) s
		where
			s.source_file is null
			and s.dataset_code = 'esterm'
		order by s.id
	) s
) s
where su.id = s.id
;

-- skript #2

update "source" su
set dataset_code = s.sugg_dataset_code
from (
	select
		s.*,
		case 
			when s.source_file = 'ev2' then 'eki'
			when s.source_file = 'ss1' then 'eki'
			when s.source_file = 'aia' then 'ait'
			when s.source_file = 'Raamatukogusõnastik' then 'rara'
			when s.source_file = 'mtfsrc-kem_lisa-1593400016.xml' then 'kem'
			when s.source_file = 'militerm-yld.xm' then 'mil'
			when s.source_file = 'militerm-aap.xml' then 'mil'
			when s.source_file = 'esterm.xml' then 'esterm'
			else 'kce'
		end sugg_dataset_code
	from
	(
		select
			s.*,
			(
				select
					f.value_text
				from
					source_freeform sf,
					freeform f
				where
					sf.source_id = s.id
					and sf.freeform_id = f.id
					and f."type" = 'SOURCE_FILE'
				limit 1
			) source_file
		from
			"source" s
		where
			not exists (
				select
					xsl.id
				from
					freeform_source_link xsl
				where
					xsl.source_id = s.id
			)
			and not exists (
				select
					xsl.id
				from
					word_etymology_source_link xsl
				where
					xsl.source_id = s.id
			)
			and not exists (
				select
					xsl.id
				from
					meaning_note_source_link xsl
				where
					xsl.source_id = s.id
			)
			and not exists (
				select
					xsl.id
				from
					meaning_image_source_link xsl
				where
					xsl.source_id = s.id
			)
			and not exists (
				select
					xsl.id
				from
					definition_source_link xsl
				where
					xsl.source_id = s.id
			)
			and not exists (
				select
					xsl.id
				from
					definition_note_source_link xsl
				where
					xsl.source_id = s.id
			)
			and not exists (
				select
					xsl.id
				from
					lexeme_source_link xsl
				where
					xsl.source_id = s.id
			)
			and not exists (
				select
					xsl.id
				from
					usage_source_link xsl
				where
					xsl.source_id = s.id
			)
			and not exists (
				select
					xsl.id
				from
					lexeme_note_source_link xsl
				where
					xsl.source_id = s.id
			)
		) s
	where
		s.source_file is not null
		and s.dataset_code = 'esterm'
	order by
		s.id
) s
where su.id = s.id
;

-- ms nähtamatute realõppude trim

update word set value = trim(value, chr(160)) where value != trim(value, chr(160));
update word set value_prese = trim(value_prese, chr(160)) where value_prese != trim(value_prese, chr(160));
update word set morphophono_form = trim(morphophono_form, chr(160)) where morphophono_form != trim(morphophono_form, chr(160));

-- lekseemi märkuste eemaldamine

delete
from
	lexeme_note ln
where
	ln.value like 'Grammatiline sugu:%'
	and exists (
		select
			1
		from
			lexeme l
		where
			l.id = ln.lexeme_id
			and l.dataset_code in ('has', 'har', 'usu')
	);

-- morfoloogia kommentaari väli

alter table word add column morph_comment text;

-- vabavormi liik klassifikaatoriks

create table freeform_type (
	code varchar(100) primary key,
	datasets varchar(10) array not null,
	order_by bigserial
);

create table freeform_type_label (
	code varchar(100) references freeform_type(code) on delete cascade not null,
	value text not null,
	lang char(3) references language(code) not null,
	type varchar(10) references label_type(code) not null,
	unique(code, lang, type)
);

insert into freeform_type (code, datasets) values ('CONCEPT_ID', '{}');
insert into freeform_type (code, datasets) values ('DESCRIBER', '{}');
insert into freeform_type (code, datasets) values ('DESCRIBING_YEAR', '{}');
insert into freeform_type (code, datasets) values ('EXTERNAL_SOURCE_ID', '{}');
insert into freeform_type (code, datasets) values ('FAMILY', '{}');
insert into freeform_type (code, datasets) values ('GENUS', '{}');
insert into freeform_type (code, datasets) values ('GOVERNMENT', '{}');
insert into freeform_type (code, datasets) values ('GOVERNMENT_OPTIONAL', '{}');
insert into freeform_type (code, datasets) values ('GOVERNMENT_PLACEMENT', '{}');
insert into freeform_type (code, datasets) values ('GOVERNMENT_VARIANT', '{}');
insert into freeform_type (code, datasets) values ('GRAMMAR', '{}');
insert into freeform_type (code, datasets) values ('IMAGE_FILE', '{}');
insert into freeform_type (code, datasets) values ('IMAGE_TITLE', '{}');
insert into freeform_type (code, datasets) values ('LEARNER_COMMENT', '{}');
insert into freeform_type (code, datasets) values ('MEANING_IMAGE', '{}');
insert into freeform_type (code, datasets) values ('MEDIA_FILE', '{}');
insert into freeform_type (code, datasets) values ('NOTE', '{}');
insert into freeform_type (code, datasets) values ('OD_WORD_RECOMMENDATION', '{}');
insert into freeform_type (code, datasets) values ('SEMANTIC_TYPE', '{}');
insert into freeform_type (code, datasets) values ('SEMANTIC_TYPE_GROUP', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_ARTICLE_AUTHOR', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_ARTICLE_TITLE', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_AUTHOR', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_CELEX', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_EXPLANATION', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_FILE', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_ISBN', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_ISSN', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_NAME', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_PUBLICATION_NAME', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_PUBLICATION_PLACE', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_PUBLICATION_YEAR', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_PUBLISHER', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_RT', '{}');
insert into freeform_type (code, datasets) values ('SOURCE_WWW', '{}');
insert into freeform_type (code, datasets) values ('SYSTEMATIC_POLYSEMY_PATTERN', '{}');
insert into freeform_type (code, datasets) values ('USAGE', '{}');
insert into freeform_type (code, datasets) values ('USAGE_DEFINITION', '{}');
insert into freeform_type (code, datasets) values ('USAGE_TRANSLATION', '{}');

drop index freeform_type_idx cascade;

alter table freeform rename column "type" to freeform_type_code;
alter table freeform add constraint freeform_type_code_fkey foreign key (freeform_type_code) references freeform_type (code) on update cascade;
create index freeform_type_code_idx on freeform (freeform_type_code);

drop type if exists type_mt_lexeme_freeform;

create type type_mt_lexeme_freeform as (
	lexeme_id bigint,
	freeform_id bigint,
	freeform_type_code varchar(100),
	value_text text,
	value_prese text,
	lang char(3),
	complexity varchar(100),
	is_public boolean,
	created_by text,
	created_on timestamp,
	modified_by text,
	modified_on timestamp
);

create table dataset_freeform_type (
	id bigserial primary key,
	dataset_code varchar(10) references dataset(code) on update cascade on delete cascade not null,
	freeform_owner varchar(10) not null,
	freeform_type_code varchar(100) references freeform_type(code) on delete cascade not null,
	unique (dataset_code, freeform_owner, freeform_type_code)
);

create index dataset_freeform_type_dataset_code_idx on dataset_freeform_type(dataset_code);
create index dataset_freeform_type_freeform_owner_idx on dataset_freeform_type(freeform_owner);

