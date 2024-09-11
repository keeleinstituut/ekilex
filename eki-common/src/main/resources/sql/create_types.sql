create type type_term_meaning_word as (
  word_id bigint, 
  word_value text, 
  word_value_prese text, 
  homonym_nr integer, 
  lang char(3), 
  word_type_codes varchar(100) array, 
  prefixoid boolean, 
  suffixoid boolean, 
  "foreign" boolean, 
  matching_word boolean, 
  most_preferred boolean, 
  least_preferred boolean, 
  is_public boolean, 
  dataset_codes varchar(10) array
);

create type type_word_rel_param as (
  name text, 
  value numeric(5, 4)
);

create type type_word_rel_meaning as (
  meaning_id bigint, 
  lexeme_id bigint, 
  definition_values text array, 
  usage_values text array, 
  lex_register_codes varchar(100) array, 
  lex_pos_codes varchar(100) array
);

create type type_classifier as (
  name varchar(100), 
  code varchar(100), 
  value text
);

create type type_activity_log_diff as (
  op varchar(100), 
  path text, 
  value text
);

create type type_value_name_lang as (
  value_id bigint, 
  value text, 
  name text, 
  lang char(3)
);

create type type_mt_definition as (
  definition_id bigint, 
  definition_type_code varchar(100), 
  value text, 
  value_prese text, 
  lang char(3), 
  complexity varchar(100), 
  is_public boolean
);

create type type_mt_lexeme as (
  lexeme_id bigint, 
  word_id bigint, 
  meaning_id bigint, 
  dataset_code varchar(10), 
  is_public boolean
);

create type type_mt_word as (
  lexeme_id bigint, 
  word_id bigint, 
  value text, 
  value_prese text, 
  lang char(3), 
  homonym_nr integer, 
  display_morph_code varchar(100), 
  gender_code varchar(100), 
  aspect_code varchar(100), 
  vocal_form text, 
  morphophono_form text, 
  manual_event_on timestamp
);

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
