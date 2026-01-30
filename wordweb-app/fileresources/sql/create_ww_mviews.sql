drop materialized view if exists mview_ww_counts;
drop materialized view if exists mview_ww_dataset_word_menu;
drop materialized view if exists mview_ww_new_word_menu;
drop materialized view if exists mview_ww_word_search;
drop materialized view if exists mview_ww_word;
drop materialized view if exists mview_ww_form;
drop materialized view if exists mview_ww_meaning;
drop materialized view if exists mview_ww_lexeme;
drop materialized view if exists mview_ww_colloc_pos_group;
drop materialized view if exists mview_ww_word_etymology;
drop materialized view if exists mview_ww_word_relation;
drop materialized view if exists mview_ww_lexeme_relation;
drop materialized view if exists mview_ww_meaning_relation;
drop materialized view if exists mview_ww_classifier;
drop materialized view if exists mview_ww_dataset;
drop materialized view if exists mview_ww_news_article;
drop materialized view if exists mview_ww_word_suggestion;

drop type if exists type_lang_dataset_publishing;

-- run this once:
-- create extension dblink;
-- SELECT dblink_connect('host=localhost user=ekilex password=3kil3x dbname=ekilex');
-- create extension pg_trgm;
-- create extension fuzzystrmatch;

create type type_lang_dataset_publishing as (
	lang varchar(10),
	dataset_code varchar(10),
	is_ww_unif boolean,
	is_ww_lite boolean,
	is_ww_os boolean
);

create materialized view mview_ww_dataset_word_menu as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_dataset_word_menu') as dataset_word_menu(
	dataset_code varchar(10),
	first_letter char(1),
	word_values text array
);

create materialized view mview_ww_new_word_menu as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_new_word_menu') as new_word_menu(
	word_id bigint,
	value text,
	value_prese text,
	homonym_nr integer,
	reg_year integer,
	word_type_codes varchar(100) array
);

create materialized view mview_ww_word_search as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_search') as word_search(
	sgroup varchar(10),
	word_value text,
	crit text,
	filt_langs varchar(10) array,
	lang_order_by bigint,
	lang_ds_pubs type_lang_dataset_publishing array
);

create materialized view mview_ww_word as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word') as word(
	word_id bigint,
	value text,
	value_prese text,
	value_as_word text,
	lang char(3),
	lang_filt varchar(10),
	lang_order_by bigint,
	homonym_nr integer,
	homonym_exists boolean,
	display_morph_code varchar(100),
	gender_code varchar(100),
	aspect_code varchar(100),
	vocal_form text,
	morph_comment text,
	reg_year integer,
	manual_event_on timestamp,
	last_activity_event_on timestamp,
	word_type_codes varchar(100) array,
	lexeme_variants json,
	meaning_words json,
	definitions json,
	word_eki_recommendations json,
	freq_value numeric(12,7),
	freq_rank bigint,
	forms_exist boolean,
	min_ds_order_by bigint,
	word_type_order_by integer,
	lang_ds_pubs type_lang_dataset_publishing array
);

create materialized view mview_ww_form as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_form') as form(
	word_id bigint,
	word_value text,
	lang char(3),
	vocal_form text,
	morph_comment text,
	paradigm_id bigint,
	paradigm_comment text,
	inflection_type varchar(100),
	inflection_type_nr varchar(100),
	word_class varchar(100),
	form_id bigint,
	value text,
	value_prese text,
	morph_code varchar(100),
	morph_group1 text,
	morph_group2 text,
	morph_group3 text,
	display_level integer,
	display_form varchar(255),
	audio_file varchar(255),
	morph_exists boolean,
	is_questionable boolean,
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
	domain_codes json,
	definitions json,
	meaning_images json,
	meaning_medias json,
	semantic_types text array,
	learner_comments text array,
	meaning_notes json
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
	dataset_name text,
	dataset_type varchar(10),
	value_state_code varchar(100),
	proficiency_level_code varchar(100),
	reliability integer,
	level1 integer,
	level2 integer,
	weight numeric(5,4),
	lexeme_order_by bigint,
	dataset_order_by bigint,
	register_codes varchar(100) array,
	pos_codes varchar(100) array,
	region_codes varchar(100) array,
	deriv_codes varchar(100) array,
	lexeme_notes json,
	grammars json,
	governments json,
	usages json,
	source_links json,
	meaning_words json,
	is_ww_unif boolean,
	is_ww_lite boolean,
	is_ww_os boolean
);

create materialized view mview_ww_colloc_pos_group as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_colloc_pos_group') as colloc_pos_group(
	lexeme_id bigint,
	word_id bigint,
	pos_groups json
);

create materialized view mview_ww_word_etymology as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_etymology') as word_etymology(
	word_id bigint,
	word_etym_id bigint,
	word_etym_word_id bigint,
	word_etym_word_value text,
	word_etym_word_lang char(3),
	etymology_type_code varchar(100),
	etymology_year text,
	word_etym_comment text,
	word_etym_is_questionable boolean,
	word_etym_order_by bigint,
	word_etym_meaning_word_values text array,
	word_etym_relations json,
	source_links json
);

create materialized view mview_ww_word_relation as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_relation') as word_relation(
	word_id bigint,
	related_words json,
	word_group_members json
);

create materialized view mview_ww_lexeme_relation as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_lexeme_relation') as lexeme_relation(
	lexeme_id bigint,
	related_lexemes json
);

create materialized view mview_ww_meaning_relation as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_meaning_relation') as meaning_relation(
	meaning_id bigint,
	related_meanings json
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

create materialized view mview_ww_news_article as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_news_article') as news_article(
	news_article_id bigint,
	created timestamp,
	type varchar(100),
	title text,
	content text,
	lang char(3)
);

create materialized view mview_ww_word_suggestion as
select * from 
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_ww_word_suggestion') as word_suggestion(
	word_suggestion_id bigint,
	created timestamp,
	word_value text,
	definition_value text,
	usage_value text,
	author_name text
);

create materialized view mview_ww_counts as
(select 
	'dsall' as dataset_code,
	w.lang,
	count(w.word_id) word_record_count,
	count(distinct w.value) word_value_count,
	count(distinct l.meaning_id) meaning_record_count
from 
	mview_ww_lexeme l,
	mview_ww_word w
where 
	l.word_id = w.word_id
	and l.dataset_code != 'ety'
group by 
	w.lang
order by 
	w.lang)
union all 
(select
	l.dataset_code,
	w.lang,
	count(w.word_id) word_record_count,
	count(distinct w.value) word_value_count,
	count(distinct l.meaning_id) meaning_record_count
from 
	mview_ww_lexeme l,
	mview_ww_word w
where 
	l.word_id = w.word_id
	and l.dataset_code != 'ety'
group by 
	l.dataset_code,
	w.lang
order by 
	l.dataset_code,
	w.lang);

create index mview_ww_dataset_word_menu_dataset_fletter_idx on mview_ww_dataset_word_menu (dataset_code, first_letter);
create index mview_ww_new_word_menu_word_id_idx on mview_ww_new_word_menu (word_id);
create index mview_ww_new_word_menu_reg_year_idx on mview_ww_new_word_menu (reg_year);
create index mview_ww_word_search_sgroup_idx on mview_ww_word_search (sgroup);
create index mview_ww_word_search_crit_idx on mview_ww_word_search (crit);
create index mview_ww_word_search_crit_prefix_idx on mview_ww_word_search (crit text_pattern_ops);
create index mview_ww_word_search_crit_tri_idx on mview_ww_word_search using gin(crit gin_trgm_ops);
create index mview_ww_word_search_filt_langs_idx on mview_ww_word_search using gin(filt_langs);
create index mview_ww_word_search_lang_ds_pubs_idx on mview_ww_word_search using gin(lang_ds_pubs);
create index mview_ww_word_word_id_idx on mview_ww_word (word_id);
create index mview_ww_word_value_idx on mview_ww_word (value);
create index mview_ww_word_value_lower_idx on mview_ww_word (lower(value));
create index mview_ww_word_value_prefix_idx on mview_ww_word (value text_pattern_ops);
create index mview_ww_word_value_lower_prefix_idx on mview_ww_word (lower(value) text_pattern_ops);
create index mview_ww_word_value_as_word_lower_idx on mview_ww_word (lower(value_as_word));
create index mview_ww_word_value_as_word_prefix_idx on mview_ww_word (value_as_word text_pattern_ops);
create index mview_ww_word_value_as_word_lower_prefix_idx on mview_ww_word (lower(value_as_word) text_pattern_ops);
create index mview_ww_word_lang_idx on mview_ww_word (lang);
create index mview_ww_word_lang_filt_idx on mview_ww_word (lang_filt);
create index mview_ww_word_lang_ds_pubs_idx on mview_ww_word using gin(lang_ds_pubs);
create index mview_ww_form_word_id_idx on mview_ww_form (word_id);
create index mview_ww_form_word_value_idx on mview_ww_form (word_value);
create index mview_ww_form_word_value_lower_idx on mview_ww_form (lower(word_value));
create index mview_ww_form_paradigm_id_idx on mview_ww_form (paradigm_id);
create index mview_ww_form_value_idx on mview_ww_form (value);
create index mview_ww_form_value_lower_idx on mview_ww_form (lower(value));
create index mview_ww_form_lang_idx on mview_ww_form (lang);
create index mview_ww_form_display_level_idx on mview_ww_form (display_level);
create index mview_ww_form_is_questionable_idx on mview_ww_form (is_questionable);
create index mview_ww_meaning_meaning_id_idx on mview_ww_meaning (meaning_id);
create index mview_ww_lexeme_lexeme_id_idx on mview_ww_lexeme (lexeme_id);
create index mview_ww_lexeme_word_id_idx on mview_ww_lexeme (word_id);
create index mview_ww_lexeme_meaning_id_idx on mview_ww_lexeme (meaning_id);
create index mview_ww_lexeme_dataset_type_idx on mview_ww_lexeme (dataset_type);
create index mview_ww_lexeme_dataset_code_idx on mview_ww_lexeme (dataset_code);
create index mview_ww_lexeme_is_ww_unif_idx on mview_ww_lexeme (is_ww_unif);
create index mview_ww_lexeme_is_ww_lite_idx on mview_ww_lexeme (is_ww_lite);
create index mview_ww_lexeme_is_ww_os_idx on mview_ww_lexeme (is_ww_os);
create index mview_ww_colloc_pos_group_lexeme_id_idx on mview_ww_colloc_pos_group (lexeme_id);
create index mview_ww_colloc_pos_group_word_id_idx on mview_ww_colloc_pos_group (word_id);
create index mview_ww_word_etymology_word_id_idx on mview_ww_word_etymology (word_id);
create index mview_ww_word_relation_word_id_idx on mview_ww_word_relation (word_id);
create index mview_ww_lexeme_relation_lexeme_id_idx on mview_ww_lexeme_relation (lexeme_id);
create index mview_ww_meaning_relation_meaning_id_idx on mview_ww_meaning_relation (meaning_id);
create index mview_ww_classifier_name_code_lang_type_idx on mview_ww_classifier (name, code, lang, type);
create index mview_ww_classifier_name_origin_code_lang_type_idx on mview_ww_classifier (name, origin, code, lang, type);
create index mview_ww_counts_dataset_code_idx on mview_ww_counts (dataset_code);
create index mview_ww_counts_lang_idx on mview_ww_counts (lang);
create index mview_ww_news_article_created_idx on mview_ww_news_article (created);
create index mview_ww_news_article_type_idx on mview_ww_news_article (type);
create index mview_ww_news_article_lang_idx on mview_ww_news_article (lang);
create index mview_ww_word_suggestion_created_idx on mview_ww_word_suggestion (created);

