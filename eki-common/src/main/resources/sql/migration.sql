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
  original_freeform_id bigint references freeform(id) on delete cascade, -- to be dropped later
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
  original_freeform_id bigint references freeform(id) on delete cascade, -- to be dropped later
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
  original_freeform_id bigint references freeform(id) on delete cascade, -- to be dropped later
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
  original_freeform_id bigint references freeform(id) on delete cascade, -- to be dropped later
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
  original_freeform_id bigint references freeform(id) on delete cascade, -- to be dropped later
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
  original_freeform_id bigint references freeform(id) on delete cascade, -- to be dropped later
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
  original_freeform_id bigint references freeform(id) on delete cascade, -- to be dropped later
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
