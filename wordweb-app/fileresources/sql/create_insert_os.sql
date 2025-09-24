drop materialized view if exists mview_od_word_od_morph; -- remove later
drop materialized view if exists mview_od_word_od_usage; -- remove later
drop materialized view if exists mview_od_word_od_usage_idx; -- remove later
drop materialized view if exists mview_od_word_od_recommend; -- remove later
drop materialized view if exists mview_od_word_relation; -- remove later
drop materialized view if exists mview_od_word_relation_idx; -- remove later
drop materialized view if exists mview_od_definition; -- remove later
drop materialized view if exists mview_od_definition_idx; -- remove later
drop materialized view if exists mview_od_lexeme_meaning; -- remove later
drop materialized view if exists mview_od_word; -- remove later

drop table if exists os_word_os_morph;
drop table if exists os_word_os_usage;
drop table if exists os_word_os_usage_idx;
drop table if exists os_word_os_recommend; -- remove later
drop table if exists os_word_os_recommendation; -- remove later
drop table if exists os_word_eki_recommendation;
drop table if exists os_word_relation;
drop table if exists os_word_relation_idx;
drop table if exists os_definition_idx;
drop table if exists os_lexeme_meaning;
drop table if exists os_word;

create table os_word (
	word_id bigint not null,
	value text not null,
	value_prese text not null,
	value_as_word text,
	homonym_nr integer not null,
	homonym_exists boolean not null,
	display_morph_code text,
	word_type_codes varchar(100) array,
	meaning_words json
);

create index os_word_word_id_idx on os_word (word_id);
create index os_word_value_idx on os_word (value);
create index os_word_value_lower_idx on os_word (lower(value));
create index os_word_value_prefix_idx on os_word (value text_pattern_ops);
create index os_word_value_prefix_lower_idx on os_word (lower(value) text_pattern_ops);
create index os_word_value_as_word_lower_idx on os_word (lower(value_as_word));
create index os_word_value_as_word_prefix_idx on os_word (value_as_word text_pattern_ops);
create index os_word_value_as_word_lower_prefix_idx on os_word (lower(value_as_word) text_pattern_ops);

insert into os_word 
select 
	word_id, 
	value, 
	value_prese, 
	value_as_word, 
	homonym_nr, 
	homonym_exists,
	display_morph_code, 
	word_type_codes,
	meaning_words
from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_os_word') as os_word (
	word_id bigint,
	value text,
	value_prese text,
	value_as_word text,
	homonym_nr integer,
	homonym_exists boolean,
	display_morph_code text,
	word_type_codes varchar(100) array,
	meaning_words json
);

create table os_word_os_morph (
	word_id bigint not null,
	word_os_morph_id bigint not null,
	value text not null,
	value_prese text not null
);

create index os_word_os_morph_word_id_idx on os_word_os_morph (word_id);
create index os_word_os_morph_word_os_morph_id_idx on os_word_os_morph (word_os_morph_id);
create index os_word_os_morph_value_idx on os_word_os_morph (value);
create index os_word_os_morph_value_lower_idx on os_word_os_morph (lower(value));
create index os_word_os_morph_value_prefix_idx on os_word_os_morph (value text_pattern_ops);
create index os_word_os_morph_value_prefix_lower_idx on os_word_os_morph (lower(value) text_pattern_ops);

insert into os_word_os_morph 
select 
	word_id,
	word_os_morph_id,
	value,
	value_prese
from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_os_word_os_morph') as os_word_os_morph (
	word_id bigint,
	word_os_morph_id bigint,
	value text,
	value_prese text
);

create table os_word_os_usage (
	word_id bigint not null,
	word_os_usages json not null
);

create index os_word_os_usage_word_id_idx on os_word_os_usage (word_id);

insert into os_word_os_usage 
select 
	word_id,
	word_os_usages
from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_os_word_os_usage') as os_word_os_usage (
	word_id bigint,
	word_os_usages json
);

create table os_word_os_usage_idx (
	word_id bigint not null,
	word_os_usage_id bigint not null,
	value text not null
);

create index os_word_os_usage_idx_word_id_idx on os_word_os_usage_idx (word_id);
create index os_word_os_usage_idx_word_os_usage_id_idx on os_word_os_usage_idx (word_os_usage_id);
create index os_word_os_usage_idx_value_idx on os_word_os_usage_idx (value);
create index os_word_os_usage_idx_value_lower_idx on os_word_os_usage_idx (lower(value));
create index os_word_os_usage_idx_value_prefix_idx on os_word_os_usage_idx (value text_pattern_ops);
create index os_word_os_usage_idx_value_prefix_lower_idx on os_word_os_usage_idx (lower(value) text_pattern_ops);

insert into os_word_os_usage_idx 
select 
	word_id,
	word_os_usage_id,
	value
from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_os_word_os_usage_idx') as os_word_os_usage_idx (
	word_id bigint,
	word_os_usage_id bigint,
	value text
);

create table os_word_eki_recommendation (
	word_id bigint not null,
	word_eki_recommendations json not null
);

create index os_word_eki_recommendation_word_id_idx on os_word_eki_recommendation (word_id);

insert into os_word_eki_recommendation 
select 
	word_id,
	word_eki_recommendations
from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_os_word_eki_recommendation') as os_word_eki_recommendation (
	word_id bigint,
	word_eki_recommendations json
);

create table os_lexeme_meaning (
	word_id bigint not null,
	lexeme_meanings json not null
);

create index os_lexeme_meaning_word_id_idx on os_lexeme_meaning (word_id);

insert into os_lexeme_meaning 
select 
	word_id,
	lexeme_meanings
from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_os_lexeme_meaning') as os_lexeme_meaning (
	word_id bigint,
	lexeme_meanings json
);

create table os_definition_idx (
	word_id bigint not null,
	meaning_id bigint not null,
	definition_id bigint not null,
	value text not null
);

create index os_definition_idx_word_id_idx on os_definition_idx (word_id);
create index os_definition_idx_meaning_id_idx on os_definition_idx (meaning_id);
create index os_definition_idx_definition_id_idx on os_definition_idx (definition_id);
create index os_definition_idx_value_idx on os_definition_idx (value);
create index os_definition_idx_value_lower_idx on os_definition_idx (lower(value));
create index os_definition_idx_value_prefix_idx on os_definition_idx (value text_pattern_ops);
create index os_definition_idx_value_prefix_lower_idx on os_definition_idx (lower(value) text_pattern_ops);

insert into os_definition_idx 
select 
	word_id,
	meaning_id,
	definition_id,
	value
from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_os_definition_idx') as os_definition_idx (
	word_id bigint,
	meaning_id bigint,
	definition_id bigint,
	value text
);

create table os_word_relation (
	word_id bigint not null,
	word_relation_groups json not null
);

create index os_word_relation_word_id_idx on os_word_relation (word_id);

insert into os_word_relation 
select 
	word_id,
	word_relation_groups
from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_os_word_relation') as os_word_relation (
	word_id bigint,
	word_relation_groups json
);

create table os_word_relation_idx (
	word_id bigint not null,
	word_relation_id bigint not null,
	word_rel_type_code varchar(100) not null, -- unindexed
	order_by bigint not null,
	related_word_id bigint not null,
	value text not null,
	value_as_word text
);

create index os_word_relation_idx_word_id_idx on os_word_relation_idx (word_id);
create index os_word_relation_idx_word_relation_id_idx on os_word_relation_idx (word_relation_id);
create index os_word_relation_idx_related_word_id_idx on os_word_relation_idx (related_word_id);
create index os_word_relation_idx_value_idx on os_word_relation_idx (value);
create index os_word_relation_idx_value_lower_idx on os_word_relation_idx (lower(value));
create index os_word_relation_idx_value_prefix_idx on os_word_relation_idx (value text_pattern_ops);
create index os_word_relation_idx_value_prefix_lower_idx on os_word_relation_idx (lower(value) text_pattern_ops);
create index os_word_relation_idx_value_as_word_lower_idx on os_word_relation_idx (lower(value_as_word));
create index os_word_relation_idx_value_as_word_prefix_idx on os_word_relation_idx (value_as_word text_pattern_ops);
create index os_word_relation_idx_value_as_word_lower_prefix_idx on os_word_relation_idx (lower(value_as_word) text_pattern_ops);

insert into os_word_relation_idx 
select 
	word_id,
	word_relation_id,
	word_rel_type_code,
	order_by,
	related_word_id,
	value,
	value_as_word
from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_os_word_relation_idx') as os_word_relation_idx (
	word_id bigint,
	word_relation_id bigint,
	word_rel_type_code varchar(100),
	order_by bigint,
	related_word_id bigint,
	value text,
	value_as_word text
);
