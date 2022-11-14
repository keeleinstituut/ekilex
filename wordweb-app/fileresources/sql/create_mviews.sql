drop materialized view if exists mview_ww_counts;
drop materialized view if exists mview_ww_dataset_word_menu;
drop materialized view if exists mview_ww_word_search;
drop materialized view if exists mview_ww_word;
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
drop materialized view if exists mview_ww_word_etym_source_link;
drop materialized view if exists mview_ww_lexeme_source_link;
drop materialized view if exists mview_ww_lexeme_freeform_source_link;
drop materialized view if exists mview_ww_meaning_freeform_source_link;
drop materialized view if exists mview_ww_definition_source_link;

drop type if exists type_meaning_word;
drop type if exists type_freeform;
drop type if exists type_lang_complexity;
drop type if exists type_definition; -- TODO remove later
drop type if exists type_domain;
drop type if exists type_media_file;
drop type if exists type_usage;
drop type if exists type_source_link;
drop type if exists type_colloc_member;
drop type if exists type_word_etym_relation;
drop type if exists type_word_relation;
drop type if exists type_lexeme_relation;
drop type if exists type_meaning_relation;

-- run this once:
-- create extension dblink;
-- SELECT dblink_connect('host=localhost user=ekilex password=3kil3x dbname=ekilex');
-- create extension pg_trgm;
-- create extension fuzzystrmatch;

create type type_lang_complexity as (
				lang varchar(10),
				dataset_code varchar(10),
				lex_complexity varchar(100),
				data_complexity varchar(100));
create type type_domain as (origin varchar(100), code varchar(100));
create type type_media_file as (freeform_id bigint, source_url text, title text, complexity varchar(100));
create type type_source_link as (
				ref_owner varchar(100),
				owner_id bigint,
				source_link_id bigint,
				source_link_type varchar(100),
				name text,
				value text,
				order_by bigint,
				source_id bigint,
				source_props text array);
create type type_usage as (
				usage_id bigint,
				usage text,
				usage_prese text,
				usage_lang char(3),
				complexity varchar(100),
				usage_type_code varchar(100),
				usage_translations text array,
				usage_definitions text array);
create type type_freeform as (
				freeform_id bigint,
				type varchar(100),
				value text,
				lang char(3),
				complexity varchar(100),
				created_by text,
				created_on timestamp,
        modified_by text,
        modified_on timestamp);
create type type_colloc_member as (
				lexeme_id bigint,
				word_id bigint,
				word text,
				form text,
				homonym_nr integer,
				conjunct varchar(100),
				weight numeric(14,4));
create type type_meaning_word as (
				lexeme_id bigint,
				meaning_id bigint,
				mw_lexeme_id bigint,
				mw_lex_complexity varchar(100),
				mw_lex_weight numeric(5,4),
				mw_lex_governments type_freeform array,
				mw_lex_register_codes varchar(100) array,
				mw_lex_value_state_code varchar(100),
				word_id bigint,
				word text,
				word_prese text,
				homonym_nr integer,
				lang char(3),
				aspect_code varchar(100),
				word_type_codes varchar(100) array);
create type type_word_etym_relation as (
				word_etym_rel_id bigint,
				comment text,
				is_questionable boolean,
				is_compound boolean,
				related_word_id bigint);
create type type_word_relation as (
				word_group_id bigint,
				word_rel_type_code varchar(100),
				relation_status varchar(100),
				order_by bigint,
				word_id bigint,
				word text,
				word_prese text,
				homonym_nr integer,
				homonyms_exist boolean,
				lang char(3),
				aspect_code varchar(100),
				word_type_codes varchar(100) array,
				lex_complexities varchar(100) array);
create type type_lexeme_relation as (
				lexeme_id bigint,
				word_id bigint,
				word text,
				word_prese text,
				homonym_nr integer,
				lang char(3),
				word_type_codes varchar(100) array,
				complexity varchar(100),
				lex_rel_type_code varchar(100));
create type type_meaning_relation as (
				meaning_id bigint,
				word_id bigint,
				word text,
				word_prese text,
				homonym_nr integer,
				lang char(3),
				aspect_code varchar(100),
				word_type_codes varchar(100) array,
				complexity varchar(100),
				weight numeric(5,4),
				lex_value_state_codes varchar(100) array,
				lex_register_codes varchar(100) array,
				lex_government_values text array,
				meaning_rel_type_code varchar(100));

create materialized view mview_ww_dataset_word_menu as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_dataset_word_menu') as dataset_word_menu(
	dataset_code varchar(10),
	first_letter char(1),
	words text array
);

create materialized view mview_ww_word_search as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_search') as word_search(
	sgroup varchar(10),
	word text,
	crit text,
	lang_order_by bigint,
	lang_complexities type_lang_complexity array
);

create materialized view mview_ww_word as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word') as word(
	word_id bigint,
	word text,
	word_prese text,
	as_word text,
	lang char(3),
	lang_order_by bigint,
	homonym_nr integer,
	word_type_codes varchar(100) array,
	display_morph_code varchar(100),
	gender_code varchar(100),
	aspect_code varchar(100),
	vocal_form text,
  manual_event_on timestamp,
	last_activity_event_on timestamp,
	lang_complexities type_lang_complexity array,
	meaning_words type_meaning_word array,
	definitions json,
	od_word_recommendations type_freeform array,
	freq_value numeric(12,7),
	freq_rank bigint,
	forms_exist boolean,
	min_ds_order_by bigint,
	word_type_order_by integer
);

create materialized view mview_ww_form as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_form') as form(
	word_id bigint,
	word_class varchar(100),
	word text,
	lang char(3),
	paradigm_id bigint,
	paradigm_comment text,
	inflection_type varchar(100),
  inflection_type_nr varchar(100),
	form_id bigint,
	morph_group1 text,
	morph_group2 text,
	morph_group3 text,
	display_level integer,
	morph_code varchar(100),
	morph_exists boolean,
	is_questionable boolean,
	value text,
	value_prese text,
	components varchar(100) array,
	display_form varchar(255),
	audio_file varchar(255),
	order_by bigint,
	form_freq_value numeric(12,7),
	form_freq_rank bigint,
	form_freq_rank_max bigint,
	morph_freq_value numeric(12,7),
	morph_freq_rank bigint,
	morph_freq_rank_max bigint
);

create materialized view mview_ww_meaning as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_meaning') as meaning(
	meaning_id bigint,
  manual_event_on timestamp,
	last_approve_or_edit_event_on timestamp,
	domain_codes type_domain array,
	image_files type_media_file array,
	media_files type_media_file array,
	systematic_polysemy_patterns text array,
	semantic_types text array,
	learner_comments text array,
	notes type_freeform array,
	definitions json
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
	dataset_type varchar(10),
	dataset_name text,
	value_state_code varchar(100),
	proficiency_level_code varchar(100),
  reliability integer,
	level1 integer,
	level2 integer,
	weight numeric(5,4),
	complexity varchar(100),
	dataset_order_by bigint,
	lexeme_order_by bigint,
	lang_complexities type_lang_complexity array,
	register_codes varchar(100) array,
	pos_codes varchar(100) array,
	region_codes varchar(100) array,
	deriv_codes varchar(100) array,
	meaning_words type_meaning_word array,
	advice_notes text array,
	notes type_freeform array,
	grammars type_freeform array,
	governments type_freeform array,
	usages type_usage array
);

create materialized view mview_ww_collocation as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_collocation') as collocation(
	lexeme_id bigint,
	word_id bigint,
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
	complexity varchar(100)
);

create materialized view mview_ww_word_etymology as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_etymology') as word_etymology(
	word_id bigint,
	word_etym_id bigint,
	word_etym_word_id bigint,
	word_etym_word text,
	word_etym_word_lang char(3),
	word_etym_word_meaning_words text array,
	etymology_type_code varchar(100),
	etymology_year text,
	word_etym_comment text,
	word_etym_is_questionable boolean,
	word_etym_order_by bigint,
	word_etym_relations type_word_etym_relation array
);

create materialized view mview_ww_word_relation as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_relation') as word_relation(
	word_id bigint,
	related_words type_word_relation array,
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

create materialized view mview_ww_word_etym_source_link as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_etym_source_link') as word_etym_source_link(
	word_id bigint,
	source_links type_source_link array
);

create materialized view mview_ww_lexeme_source_link as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_lexeme_source_link') as lexeme_source_link(
	lexeme_id bigint,
	source_links type_source_link array
);

create materialized view mview_ww_lexeme_freeform_source_link as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_lexeme_freeform_source_link') as lexeme_freeform_source_link(
	lexeme_id bigint,
	source_links type_source_link array
);

create materialized view mview_ww_meaning_freeform_source_link as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_meaning_freeform_source_link') as meaning_freeform_source_link(
	meaning_id bigint,
	source_links type_source_link array
);

create materialized view mview_ww_dataset as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_dataset') as dataset(
	code varchar(10),
	type varchar(10),
	name text,
	description text,
	contact text,
  image_url text,
	is_superior boolean,
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
	type varchar(10),
	order_by bigint
);

create materialized view mview_ww_counts as
(select 'dsall' as dataset_code,
       w.lang,
       count(w.word_id) word_record_count,
       count(distinct w.word) word_value_count,
       count(distinct l.meaning_id) meaning_record_count
from mview_ww_lexeme l,
     mview_ww_word w
where l.word_id = w.word_id
and   l.dataset_code != 'ety'
group by w.lang
order by w.lang)
union all 
(select l.dataset_code,
       w.lang,
       count(w.word_id) word_record_count,
       count(distinct w.word) word_value_count,
       count(distinct l.meaning_id) meaning_record_count
from mview_ww_lexeme l,
     mview_ww_word w
where l.word_id = w.word_id
and   l.dataset_code != 'ety'
group by l.dataset_code,
         w.lang
order by l.dataset_code,
         w.lang);

create index mview_ww_dataset_word_menu_dataset_fletter_idx on mview_ww_dataset_word_menu (dataset_code, first_letter);
create index mview_ww_word_search_sgroup_idx on mview_ww_word_search (sgroup);
create index mview_ww_word_search_crit_idx on mview_ww_word_search (crit);
create index mview_ww_word_search_crit_prefix_idx on mview_ww_word_search (crit text_pattern_ops);
create index mview_ww_word_search_crit_tri_idx on mview_ww_word_search using gin(crit gin_trgm_ops);
create index mview_ww_word_word_id_idx on mview_ww_word (word_id);
create index mview_ww_word_value_idx on mview_ww_word (word);
create index mview_ww_word_value_lower_idx on mview_ww_word (lower(word));
create index mview_ww_word_value_prefix_idx on mview_ww_word (word text_pattern_ops);
create index mview_ww_word_value_lower_prefix_idx on mview_ww_word (lower(word) text_pattern_ops);
create index mview_ww_word_as_value_lower_idx on mview_ww_word (lower(as_word));
create index mview_ww_word_as_value_prefix_idx on mview_ww_word (as_word text_pattern_ops);
create index mview_ww_word_as_value_lower_prefix_idx on mview_ww_word (lower(as_word) text_pattern_ops);
create index mview_ww_word_lang_idx on mview_ww_word (lang);
create index mview_ww_form_word_id_idx on mview_ww_form (word_id);
create index mview_ww_form_word_idx on mview_ww_form (word);
create index mview_ww_form_word_lower_idx on mview_ww_form (lower(word));
create index mview_ww_form_paradigm_id_idx on mview_ww_form (paradigm_id);
create index mview_ww_form_value_idx on mview_ww_form (value);
create index mview_ww_form_value_lower_idx on mview_ww_form (lower(value));
create index mview_ww_form_lang_idx on mview_ww_form (lang);
create index mview_ww_form_display_level_idx on mview_ww_form (display_level);
create index mview_ww_meaning_meaning_id_idx on mview_ww_meaning (meaning_id);
create index mview_ww_lexeme_lexeme_id_idx on mview_ww_lexeme (lexeme_id);
create index mview_ww_lexeme_word_id_idx on mview_ww_lexeme (word_id);
create index mview_ww_lexeme_meaning_id_idx on mview_ww_lexeme (meaning_id);
create index mview_ww_lexeme_dataset_type_idx on mview_ww_lexeme (dataset_type);
create index mview_ww_lexeme_dataset_code_idx on mview_ww_lexeme (dataset_code);
create index mview_ww_lexeme_complexity_idx on mview_ww_lexeme (complexity);
create index mview_ww_collocation_lexeme_id_idx on mview_ww_collocation (lexeme_id);
create index mview_ww_collocation_word_id_idx on mview_ww_collocation (word_id);
create index mview_ww_collocation_complexity_idx on mview_ww_collocation (complexity);
create index mview_ww_word_etymology_word_id_idx on mview_ww_word_etymology (word_id);
create index mview_ww_word_relation_word_id_idx on mview_ww_word_relation (word_id);
create index mview_ww_lexeme_relation_lexeme_id_idx on mview_ww_lexeme_relation (lexeme_id);
create index mview_ww_meaning_relation_meaning_id_idx on mview_ww_meaning_relation (meaning_id);
create index mview_ww_word_etym_source_link_word_id_idx on mview_ww_word_etym_source_link (word_id);
create index mview_ww_lexeme_source_link_word_id_idx on mview_ww_lexeme_source_link (lexeme_id);
create index mview_ww_lexeme_freeform_source_link_word_id_idx on mview_ww_lexeme_freeform_source_link (lexeme_id);
create index mview_ww_meaning_freeform_source_link_word_id_idx on mview_ww_meaning_freeform_source_link (meaning_id);
create index mview_ww_classifier_name_code_lang_type_idx on mview_ww_classifier (name, code, lang, type);
create index mview_ww_classifier_name_origin_code_lang_type_idx on mview_ww_classifier (name, origin, code, lang, type);
create index mview_ww_counts_dataset_code_idx on mview_ww_counts (dataset_code);
create index mview_ww_counts_lang_idx on mview_ww_counts (lang);
