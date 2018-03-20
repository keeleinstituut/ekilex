drop materialized view if exists mview_ww_word cascade;
drop materialized view if exists mview_ww_form cascade;
drop materialized view if exists mview_ww_meaning cascade;
drop materialized view if exists mview_ww_classifier cascade;
drop type if exists type_definition;
drop type if exists type_domain;

-- run this once:
-- CREATE EXTENSION dblink;
-- SELECT dblink_connect('host=localhost user=ekilex password=3kil3x dbname=ekilex');

create type type_definition as (value text, lang char(3));

create materialized view mview_ww_word as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word') as word(
	word_id bigint,
	word text,
	homonym_nr integer,
	lang char(3),
	morph_code varchar(100),
	display_morph_code varchar(100),
	dataset_codes varchar(100) array,
	meaning_count integer,
	meaning_words text array,
	definitions type_definition array
);

create materialized view mview_ww_form as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_form') as form(
	word_id bigint,
	paradigm_id bigint,
	form_id bigint,
	form text,
	morph_code varchar(100),
	components varchar(100) array,
	display_form varchar(255),
	vocal_form varchar(255),
	sound_file varchar(255),
	is_word boolean
);

create type type_domain as (origin varchar(100), code varchar(100));

create materialized view mview_ww_meaning as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_meaning') as meaning(
	word_id bigint,
	meaning_id bigint,
	lexeme_id bigint,
	definition_id bigint,
	dataset_code varchar(10),
	level1 integer,
	level2 integer,
	level3 integer,
	lexeme_type_code varchar(100),
	register_codes varchar(100) array,
	pos_codes varchar(100) array,
	deriv_codes varchar(100) array,
	domain_codes type_domain array,
	definition text,
	definition_lang char(3)
);

create materialized view mview_ww_classifier as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_classifier') as classifier(
	name text,
	origin text,
	code varchar(100),
	value text,
	lang char(3)
);

create index mview_ww_word_word_id_idx on mview_ww_word (word_id);
create index mview_ww_word_lang_idx on mview_ww_word (lang);
create index mview_ww_form_word_id_idx on mview_ww_form (word_id);
create index mview_ww_form_value_idx on mview_ww_form (form);
create index mview_ww_form_value_lower_idx on mview_ww_form (lower(form));
create index mview_ww_meaning_word_id_idx on mview_ww_meaning (word_id);
create index mview_ww_meaning_meaning_id_idx on mview_ww_meaning (meaning_id);
create index mview_ww_meaning_lexeme_id_idx on mview_ww_meaning (lexeme_id);
create index mview_ww_meaning_definition_lang_idx on mview_ww_meaning (definition_lang);
create index mview_ww_classifier_name_code_lang_idx on mview_ww_classifier (name, code, lang);
create index mview_ww_classifier_name_origin_code_lang_idx on mview_ww_classifier (name, origin, code, lang);
