drop materialized view if exists mview_od_word_od_morph;
drop materialized view if exists mview_od_word_od_usage;
drop materialized view if exists mview_od_word_od_usage_idx;
drop materialized view if exists mview_od_word_od_recommend;
drop materialized view if exists mview_od_word_relation;
drop materialized view if exists mview_od_word_relation_idx;
drop materialized view if exists mview_od_definition_idx;
drop materialized view if exists mview_od_lexeme_meaning;
drop materialized view if exists mview_od_word;

create materialized view mview_od_word as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_od_word') as od_word(
	word_id bigint,
	value text,
	value_prese text,
	value_as_word text,
	homonym_nr integer,
	vocal_form text,
	word_type_codes varchar(100) array
);

create materialized view mview_od_word_od_morph as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_od_word_od_morph') as od_word_od_morph(
	word_id bigint,
	word_od_morph_id bigint,
	value text,
	value_prese text
);

create materialized view mview_od_word_od_usage as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_od_word_od_usage') as od_word_od_usage(
	word_id bigint,
	word_od_usages json
);

create materialized view mview_od_word_od_usage_idx as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_od_word_od_usage_idx') as od_word_od_usage_idx(
	word_id bigint,
	word_od_usage_id bigint,
	value text
);

create materialized view mview_od_word_od_recommend as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_od_word_od_recommend') as od_word_od_recommend(
	word_id bigint,
	word_od_recommend_id bigint,
	value text,
	value_prese text,
	opt_value text, -- unindexed
	opt_value_prese text
);

create materialized view mview_od_lexeme_meaning as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_od_lexeme_meaning') as od_lexeme_meaning(
	word_id bigint,
	lexeme_meanings json
);

create materialized view mview_od_definition_idx as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_od_definition_idx') as od_definition(
	word_id bigint,
	meaning_id bigint,
	definition_id bigint,
	value text
);

create materialized view mview_od_word_relation as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_od_word_relation') as od_word_relation(
	word_id bigint,
	word_relation_groups json
);

create materialized view mview_od_word_relation_idx as
select * from
dblink(
	'host=localhost user=ekilex password=3kil3x dbname=ekilex',
	'select * from view_od_word_relation_idx') as od_word_relation_idx(
	word_id bigint,
	word_relation_id bigint,
	word_rel_type_code varchar(100), -- unindexed
	related_word_id bigint,
	value text,
	value_as_word text
);

create index mview_od_word_word_id_idx on mview_od_word (word_id);
create index mview_od_word_value_idx on mview_od_word (value);
create index mview_od_word_value_lower_idx on mview_od_word (lower(value));
create index mview_od_word_value_prefix_idx on mview_od_word (value text_pattern_ops);
create index mview_od_word_value_prefix_lower_idx on mview_od_word (lower(value) text_pattern_ops);
create index mview_od_word_value_as_word_lower_idx on mview_od_word (lower(value_as_word));
create index mview_od_word_value_as_word_prefix_idx on mview_od_word (value_as_word text_pattern_ops);
create index mview_od_word_value_as_word_lower_prefix_idx on mview_od_word (lower(value_as_word) text_pattern_ops);

create index mview_od_word_od_morph_word_id_idx on mview_od_word_od_morph (word_id);
create index mview_od_word_od_morph_word_od_morph_id_idx on mview_od_word_od_morph (word_od_morph_id);
create index mview_od_word_od_morph_value_idx on mview_od_word_od_morph (value);
create index mview_od_word_od_morph_value_lower_idx on mview_od_word_od_morph (lower(value));
create index mview_od_word_od_morph_value_prefix_idx on mview_od_word_od_morph (value text_pattern_ops);
create index mview_od_word_od_morph_value_prefix_lower_idx on mview_od_word_od_morph (lower(value) text_pattern_ops);

create index mview_od_word_od_usage_word_id_idx on mview_od_word_od_usage (word_id);

create index mview_od_word_od_usage_idx_word_id_idx on mview_od_word_od_usage_idx (word_id);
create index mview_od_word_od_usage_idx_word_od_usage_id_idx on mview_od_word_od_usage_idx (word_od_usage_id);
create index mview_od_word_od_usage_idx_value_idx on mview_od_word_od_usage_idx (value);
create index mview_od_word_od_usage_idx_value_lower_idx on mview_od_word_od_usage_idx (lower(value));
create index mview_od_word_od_usage_idx_value_prefix_idx on mview_od_word_od_usage_idx (value text_pattern_ops);
create index mview_od_word_od_usage_idx_value_prefix_lower_idx on mview_od_word_od_usage_idx (lower(value) text_pattern_ops);

create index mview_od_word_od_recommend_word_id_idx on mview_od_word_od_recommend (word_id);
create index mview_od_word_od_recommend_word_od_recommend_id_idx on mview_od_word_od_recommend (word_od_recommend_id);
create index mview_od_word_od_recommend_value_idx on mview_od_word_od_recommend (value);
create index mview_od_word_od_recommend_value_lower_idx on mview_od_word_od_recommend (lower(value));
create index mview_od_word_od_recommend_value_prefix_idx on mview_od_word_od_recommend (value text_pattern_ops);
create index mview_od_word_od_recommend_value_prefix_lower_idx on mview_od_word_od_recommend (lower(value) text_pattern_ops);

create index mview_od_lexeme_meaning_word_id_idx on mview_od_lexeme_meaning (word_id);

create index mview_od_definition_idx_word_id_idx on mview_od_definition_idx (word_id);
create index mview_od_definition_idx_meaning_id_idx on mview_od_definition_idx (meaning_id);
create index mview_od_definition_idx_definition_id_idx on mview_od_definition_idx (definition_id);
create index mview_od_definition_idx_value_idx on mview_od_definition_idx (value);
create index mview_od_definition_idx_value_lower_idx on mview_od_definition_idx (lower(value));
create index mview_od_definition_idx_value_prefix_idx on mview_od_definition_idx (value text_pattern_ops);
create index mview_od_definition_idx_value_prefix_lower_idx on mview_od_definition_idx (lower(value) text_pattern_ops);

create index mview_od_word_relation_word_id_idx on mview_od_word_relation (word_id);

create index mview_od_word_relation_idx_word_id_idx on mview_od_word_relation_idx (word_id);
create index mview_od_word_relation_idx_word_relation_id_idx on mview_od_word_relation_idx (word_relation_id);
create index mview_od_word_relation_idx_related_word_id_idx on mview_od_word_relation_idx (related_word_id);
create index mview_od_word_relation_idx_value_idx on mview_od_word_relation_idx (value);
create index mview_od_word_relation_idx_value_lower_idx on mview_od_word_relation_idx (lower(value));
create index mview_od_word_relation_idx_value_prefix_idx on mview_od_word_relation_idx (value text_pattern_ops);
create index mview_od_word_relation_idx_value_prefix_lower_idx on mview_od_word_relation_idx (lower(value) text_pattern_ops);
create index mview_od_word_relation_idx_value_as_word_lower_idx on mview_od_word_relation_idx (lower(value_as_word));
create index mview_od_word_relation_idx_value_as_word_prefix_idx on mview_od_word_relation_idx (value_as_word text_pattern_ops);
create index mview_od_word_relation_idx_value_as_word_lower_prefix_idx on mview_od_word_relation_idx (lower(value_as_word) text_pattern_ops);

