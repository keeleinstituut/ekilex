drop materialized view if exists mview_ww_word;
drop materialized view if exists mview_ww_as_word;
drop materialized view if exists mview_ww_form;
drop materialized view if exists mview_ww_meaning;
drop materialized view if exists mview_ww_lexeme;
drop materialized view if exists mview_ww_collocation;
drop materialized view if exists mview_ww_word_etymology;
drop materialized view if exists mview_ww_classifier;
drop materialized view if exists mview_ww_dataset;
drop materialized view if exists mview_ww_word_relation;
drop materialized view if exists mview_ww_lexeme_relation;
drop materialized view if exists mview_ww_meaning_relation;
drop type if exists type_word;
drop type if exists type_definition;
drop type if exists type_domain;
drop type if exists type_usage;
drop type if exists type_colloc_member;
drop type if exists type_word_etym;
drop type if exists type_word_relation;
drop type if exists type_lexeme_relation;
drop type if exists type_meaning_relation;

-- run this once:
-- CREATE EXTENSION dblink;
-- SELECT dblink_connect('host=localhost user=ekilex password=3kil3x dbname=ekilex');

create type type_word as (lexeme_id bigint, meaning_id bigint, value text, lang char(3), word_type_codes varchar(100) array, dataset_code varchar(10));
create type type_definition as (lexeme_id bigint, meaning_id bigint, value text, value_prese text, lang char(3), dataset_code varchar(10));
create type type_domain as (origin varchar(100), code varchar(100));
create type type_usage as (usage text, usage_lang char(3), usage_type_code varchar(100), usage_translations text array, usage_definitions text array, usage_authors text array);
create type type_colloc_member as (lexeme_id bigint, word_id bigint, word text, form text, homonym_nr integer, word_exists boolean, conjunct varchar(100), weight numeric(14,4));
create type type_word_etym as (word_id bigint, etym_word_id bigint, etym_word text, etym_word_lang char(3), etym_year text, etym_meaning_words text array, etym_word_sources text array, comments text array, is_questionable boolean, is_compound boolean);
create type type_word_relation as (word_id bigint, word text, word_lang char(3), word_type_codes varchar(100) array, dataset_codes varchar(10) array, word_rel_type_code varchar(100));
create type type_lexeme_relation as (lexeme_id bigint, word_id bigint, word text, word_lang char(3), lex_rel_type_code varchar(100));
create type type_meaning_relation as (meaning_id bigint, lexeme_id bigint, word_id bigint, word text, word_lang char(3), meaning_rel_type_code varchar(100));

create materialized view mview_ww_word as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word') as word(
	word_id bigint,
	word text,
	homonym_nr integer,
	word_class varchar(100),
	lang char(3),
	word_type_codes varchar(100) array,
	morph_code varchar(100),
	display_morph_code varchar(100),
	aspect_code varchar(100),
	etymology_year text,
	etymology_type_code varchar(100),
	dataset_codes varchar(100) array,
	meaning_count integer,
	meaning_words type_word array,
	definitions type_definition array
);

create materialized view mview_ww_as_word as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_as_word') as as_word(
	word_id bigint,
	word text,
	as_word text
);

create materialized view mview_ww_form as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_form') as form(
	word_id bigint,
	word text,
	lang char(3),
	dataset_codes varchar(100) array,
	paradigm_id bigint,
	inflection_type varchar(100),
	form_id bigint,
	mode varchar(100),
	morph_group1 text,
	morph_group2 text,
	morph_group3 text,
	display_level integer,
	morph_code varchar(100),
	morph_exists boolean,
	form text,
	components varchar(100) array,
	display_form varchar(255),
	vocal_form varchar(255),
	sound_file varchar(255),
	order_by integer
);

create materialized view mview_ww_meaning as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_meaning') as meaning(
	meaning_id bigint,
	domain_codes type_domain array,
	image_files text array,
	systematic_polysemy_patterns text array,
	semantic_types text array,
	learner_comments text array,
	definitions type_definition array
);

create materialized view mview_ww_lexeme as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_lexeme') as lexeme(
	lexeme_id bigint,
	word_id bigint,
	meaning_id bigint,
	dataset_code varchar(10),
	ds_order_by bigint,
	level1 integer,
	level2 integer,
	level3 integer,
	lex_order_by bigint,
	register_codes varchar(100) array,
	pos_codes varchar(100) array,
	deriv_codes varchar(100) array,
	advice_notes text array,
	public_notes text array,
	grammars text array,
	governments text array,
	usages type_usage array
);

create materialized view mview_ww_collocation as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_collocation') as collocation(
	lexeme_id bigint,
	word_id bigint,
	dataset_code varchar(10),
	level1 integer,
	level2 integer,
	level3 integer,
	pos_group_id bigint,
	pos_group_code varchar(100),
	pos_group_order_by bigint,
	rel_group_id bigint,
	rel_group_name text,
	rel_group_order_by bigint,
	colloc_group_order integer,
	colloc_id bigint,
	colloc_value text,
	colloc_definition text,
	colloc_usages text array,
	colloc_members type_colloc_member array,
	target_context varchar(100)
);

create materialized view mview_ww_word_etymology as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_etymology') as word_etymology(
	word_id bigint,
	word_sources text array,
	etym_lineup type_word_etym array
);

create materialized view mview_ww_word_relation as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_relation') as word_relation(
	word_id bigint,
	related_words type_word_relation array,
	word_group_id bigint,
	word_rel_type_code varchar(100),
	word_group_members type_word_relation array
);

create materialized view mview_ww_lexeme_relation as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_lexeme_relation') as lexeme_relation(
	lexeme_id bigint,
	related_lexemes type_lexeme_relation array
);

create materialized view mview_ww_meaning_relation as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_meaning_relation') as meaning_relation(
	meaning_id bigint,
	related_meanings type_meaning_relation array
);

create materialized view mview_ww_dataset as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_dataset') as dataset(
	code varchar(10),
	name text,
	description text,
	lang char(3),
	order_by bigint
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
	lang char(3),
	order_by bigint
);

create index mview_ww_word_word_id_idx on mview_ww_word (word_id);
create index mview_ww_word_value_idx on mview_ww_word (word);
create index mview_ww_word_value_lower_idx on mview_ww_word (lower(word));
create index mview_ww_word_value_prefix_idx on mview_ww_word (word text_pattern_ops);
create index mview_ww_word_value_lower_prefix_idx on mview_ww_word (lower(word) text_pattern_ops);
create index mview_ww_word_lang_idx on mview_ww_word (lang);
create index mview_ww_as_word_word_id_idx on mview_ww_as_word (word_id);
create index mview_ww_as_word_value_idx on mview_ww_as_word (as_word);
create index mview_ww_as_word_value_lower_idx on mview_ww_as_word (lower(as_word));
create index mview_ww_as_word_value_prefix_idx on mview_ww_as_word (as_word text_pattern_ops);
create index mview_ww_as_word_value_lower_prefix_idx on mview_ww_as_word (lower(as_word) text_pattern_ops);
create index mview_ww_form_word_id_idx on mview_ww_form (word_id);
create index mview_ww_form_word_idx on mview_ww_form (word);
create index mview_ww_form_word_lower_idx on mview_ww_form (lower(word));
create index mview_ww_form_value_idx on mview_ww_form (form);
create index mview_ww_form_value_lower_idx on mview_ww_form (lower(form));
create index mview_ww_form_mode_idx on mview_ww_form (mode);
create index mview_ww_form_lang_idx on mview_ww_form (lang);
create index mview_ww_form_display_level_idx on mview_ww_form (display_level);
create index mview_ww_meaning_meaning_id_idx on mview_ww_meaning (meaning_id);
create index mview_ww_lexeme_lexeme_id_idx on mview_ww_lexeme (lexeme_id);
create index mview_ww_lexeme_word_id_idx on mview_ww_lexeme (word_id);
create index mview_ww_lexeme_meaning_id_idx on mview_ww_lexeme (meaning_id);
create index mview_ww_collocation_lexeme_id_idx on mview_ww_collocation (lexeme_id);
create index mview_ww_collocation_word_id_idx on mview_ww_collocation (word_id);
create index mview_ww_collocation_dataset_code_idx on mview_ww_collocation (dataset_code);
create index mview_ww_collocation_target_context_idx on mview_ww_collocation (target_context);
create index mview_ww_word_etymology_word_id_idx on mview_ww_word_etymology (word_id);
create index mview_ww_word_relation_word_id_idx on mview_ww_word_relation (word_id);
create index mview_ww_lexeme_relation_lexeme_id_idx on mview_ww_lexeme_relation (lexeme_id);
create index mview_ww_meaning_relation_meaning_id_idx on mview_ww_meaning_relation (meaning_id);
create index mview_ww_classifier_name_code_lang_idx on mview_ww_classifier (name, code, lang);
create index mview_ww_classifier_name_origin_code_lang_idx on mview_ww_classifier (name, origin, code, lang);
