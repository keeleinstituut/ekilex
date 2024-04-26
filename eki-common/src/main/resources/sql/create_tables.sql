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
  "type" varchar(100), 
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

create table terms_of_use (
  id bigserial primary key, 
  version varchar(100), 
  value text not null, 
  is_active boolean default false not null, 
  unique(version)
);
alter sequence terms_of_use_id_seq restart with 10000;

create table eki_user (
  id bigserial primary key, 
  name text not null, 
  email text not null, 
  password text not null, 
  terms_ver varchar(100) null references terms_of_use(version), 
  activation_key varchar(60) null, 
  recovery_key varchar(60) null, 
  api_key varchar(100) null, 
  is_api_crud boolean default false, 
  is_admin boolean default false, 
  is_master boolean default false, 
  is_enabled boolean, 
  review_comment text, 
  created timestamp not null default statement_timestamp(), 
  unique(email)
);
alter sequence eki_user_id_seq restart with 10000;

---------------------------------
-- klassifitseeritud andmestik --
---------------------------------

-- klassif. nime liik
create table label_type (
  code varchar(10) primary key, 
  value text not null
);

-- keel
create table language (
  code char(3) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table language_label (
  code char(3) references language(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- valdkond
create table domain (
  code varchar(100) not null, 
  origin varchar(100) not null, 
  parent_code varchar(100) null, 
  parent_origin varchar(100) null, 
  datasets varchar(10) array not null, 
  order_by bigserial, 
  primary key (code, origin), 
  foreign key (parent_code, parent_origin) references domain (code, origin)
);

create table domain_label (
  code varchar(100) not null, 
  origin varchar(100) not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  foreign key (code, origin) references domain (code, origin), 
  unique(code, origin, lang, type)
);

create table value_state (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table value_state_label (
  code varchar(100) references value_state(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- klassif. rektsiooni tüüp
create table government_type (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table government_type_label (
  code varchar(100) references government_type(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- register
create table register (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table register_label (
  code varchar(100) references register(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- semantiline tüüp
create table semantic_type (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table semantic_type_label (
  code varchar(100) references semantic_type(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- sõnasort
create table word_type (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table word_type_label (
  code varchar(100) references word_type(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- aspekt
create table aspect (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table aspect_label (
  code varchar(100) references aspect(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- sugu
create table gender (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table gender_label (
  code varchar(100) references gender(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- sõnaliik
create table pos (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table pos_label (
  code varchar(100) references pos(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- kol pos grupp
create table pos_group (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table pos_group_label (
  code varchar(100) references pos_group(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- vormi märgend
create table morph (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table morph_label (
  code varchar(100) references morph(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- kuvatav vormi märgend
create table display_morph (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table display_morph_label (
  code varchar(100) references display_morph(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- tuletuskood
create table deriv (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table deriv_label (
  code varchar(100) references deriv(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

-- ilmiku seose liik
create table lex_rel_type (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table lex_rel_type_label (
  code varchar(100) references lex_rel_type(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

create table lex_rel_mapping (
  code1 varchar(100) references lex_rel_type(code) on delete cascade not null, 
  code2 varchar(100) references lex_rel_type(code) on delete cascade not null, 
  unique(code1, code2)
);

-- keelendi seose liik
create table word_rel_type (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table word_rel_type_label (
  code varchar(100) references word_rel_type(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

create table word_rel_mapping (
  code1 varchar(100) references word_rel_type(code) on delete cascade not null, 
  code2 varchar(100) references word_rel_type(code) on delete cascade not null, 
  unique(code1, code2)
);

-- tähenduse seose liik
create table meaning_rel_type (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table meaning_rel_type_label (
  code varchar(100) references meaning_rel_type(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

create table meaning_rel_mapping (
  code1 varchar(100) references meaning_rel_type(code) on delete cascade not null, 
  code2 varchar(100) references meaning_rel_type(code) on delete cascade not null, 
  unique(code1, code2)
);

create table usage_type (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table usage_type_label (
  code varchar(100) references usage_type(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

create table etymology_type (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table definition_type (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table definition_type_label (
  code varchar(100) references definition_type(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

create table region (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table tag (
  name varchar(100) primary key, 
  set_automatically boolean default false not null, 
  remove_to_complete boolean default true not null, 
  type varchar(10) not null, 
  order_by bigserial
);

-- keeletase
create table proficiency_level (
  code varchar(100) primary key, 
  datasets varchar(10) array not null, 
  order_by bigserial
);

create table proficiency_level_label (
  code varchar(100) references proficiency_level(code) on delete cascade not null, 
  value text not null, 
  lang char(3) references language(code) not null, 
  type varchar(10) references label_type(code) not null, 
  unique(code, lang, type)
);

---------------------------
-- dünaamiline andmestik --
---------------------------

-- sõnakogu
create table dataset (
  code varchar(10) primary key, 
  type varchar(10) not null, 
  name text not null, 
  description text, 
  contact text, 
  image_url text, 
  fed_term_collection_id varchar(100), 
  fed_term_domain_id varchar(100), 
  is_visible boolean default true, 
  is_public boolean default true, 
  is_superior boolean default false, 
  order_by bigserial
);

create table dataset_permission (
  id bigserial primary key, 
  dataset_code varchar(10) references dataset(code) on update cascade on delete cascade not null, 
  user_id bigint references eki_user(id) on delete cascade not null, 
  auth_operation varchar(100) not null, 
  auth_item varchar(100) not null, 
  auth_lang char(3) references language(code) null, 
  unique(
    dataset_code, user_id, auth_operation, 
    auth_item, auth_lang
  )
);
alter sequence dataset_permission_id_seq restart with 10000;

-- kasutaja õiguste taotlus
create table eki_user_application (
  id bigserial primary key, 
  user_id bigint references eki_user(id) on delete cascade not null, 
  dataset_code varchar(10) references dataset(code) on update cascade on delete cascade not null, 
  auth_operation varchar(100) not null, 
  lang char(3) references language(code) null, 
  comment text null, 
  status varchar(10) not null, 
  created timestamp not null default statement_timestamp()
);
alter sequence eki_user_application_id_seq restart with 10000;

-- kasutaja profiil
create table eki_user_profile (
  id bigserial primary key, 
  user_id bigint references eki_user(id) on delete cascade not null, 
  recent_dataset_permission_id bigint references dataset_permission(id), 
  preferred_datasets varchar(10) array, 
  preferred_tag_names varchar(100) array, 
  active_tag_name varchar(100) references tag(name), 
  preferred_part_syn_candidate_langs char(3) array, 
  preferred_syn_lex_meaning_word_langs char(3) array, 
  preferred_full_syn_candidate_lang char(3) references language(code) null, 
  preferred_full_syn_candidate_dataset_code varchar(10) references dataset(code) null, 
  preferred_meaning_relation_word_langs char(3) array, 
  show_lex_meaning_relation_source_lang_words boolean default true, 
  show_meaning_relation_first_word_only boolean default true, 
  show_meaning_relation_meaning_id boolean default true, 
  show_meaning_relation_word_datasets boolean default true, 
  is_approve_meaning_enabled boolean default false
);
alter sequence eki_user_profile_id_seq restart with 10000;

-- vabavorm
create table freeform (
  id bigserial primary key, 
  parent_id bigint references freeform(id) on delete cascade null, 
  type varchar(100) not null, 
  value_text text null, 
  value_prese text null, 
  value_date timestamp null, 
  value_number numeric(14, 4) null, 
  value_array text array null, 
  classif_name text null, 
  classif_code varchar(100) null, 
  lang char(3) references language(code) null, 
  complexity varchar(100) null, 
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence freeform_id_seq restart with 10000;

-- allikas
create table source (
  id bigserial primary key, 
  type varchar(100) not null, 
  name text not null, 
  value text null, 
  value_prese text null, 
  comment text null, 
  is_public boolean not null default true
);
alter sequence source_id_seq restart with 10000;

-- allika vabavorm
create table source_freeform (
  id bigserial primary key, 
  source_id bigint references source(id) on delete cascade not null, 
  freeform_id bigint references freeform(id) on delete cascade not null, 
  unique(source_id, freeform_id)
);
alter sequence source_freeform_id_seq restart with 10000;

-- keelend (morfoloogiline homonüüm)
create table word (
  id bigserial primary key, 
  value text not null, 
  value_prese text not null, 
  value_as_word text null, 
  lang char(3) references language(code) null, 
  homonym_nr integer default 1, 
  display_morph_code varchar(100) references display_morph(code) null, 
  gender_code varchar(100) references gender(code) null, 
  aspect_code varchar(100) references aspect(code) null, 
  vocal_form text null, 
  morphophono_form text null, 
  manual_event_on timestamp null, 
  is_word boolean not null, 
  is_collocation boolean not null, 
  is_public boolean not null default true
);
alter sequence word_id_seq restart with 10000;

create table word_word_type (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  word_type_code varchar(100) references word_type(code) not null, 
  order_by bigserial, 
  unique(word_id, word_type_code)
);
alter sequence word_word_type_id_seq restart with 10000;

create table word_guid (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  guid varchar(100) not null, 
  dataset_code varchar(10) references dataset(code) on update cascade on delete cascade not null, 
  unique(word_id, guid, dataset_code)
);
alter sequence word_guid_id_seq restart with 10000;

-- keelendi seos
create table word_relation (
  id bigserial primary key, 
  word1_id bigint references word(id) on delete cascade not null, 
  word2_id bigint references word(id) on delete cascade not null, 
  word_rel_type_code varchar(100) references word_rel_type(code), 
  relation_status varchar(100), 
  order_by bigserial, 
  unique(
    word1_id, word2_id, word_rel_type_code
  )
);
alter sequence word_relation_id_seq restart with 10000;

create table word_relation_param (
  id bigserial primary key, 
  word_relation_id bigint references word_relation(id) on delete cascade not null, 
  name text not null, 
  value numeric(5, 4) not null, 
  unique (word_relation_id, name)
);
alter sequence word_relation_param_id_seq restart with 10000;

-- keelendi sari
create table word_group (
  id bigserial primary key, 
  word_rel_type_code varchar(100) references word_rel_type(code) on delete cascade not null
);
alter sequence word_group_id_seq restart with 10000;

create table word_group_member (
  id bigserial primary key, 
  word_group_id bigint references word_group(id) on delete cascade not null, 
  word_id bigint references word(id) on delete cascade not null, 
  order_by bigserial, 
  unique(word_group_id, word_id)
);
alter sequence word_group_member_id_seq restart with 10000;

create table word_etymology (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  etymology_type_code varchar(100) references etymology_type(code), 
  etymology_year text null, 
  comment text null, 
  comment_prese text null, 
  is_questionable boolean not null default false, 
  order_by bigserial
);
alter sequence word_etymology_id_seq restart with 10000;

create table word_etymology_relation (
  id bigserial primary key, 
  word_etym_id bigint references word_etymology(id) on delete cascade not null, 
  related_word_id bigint references word(id) on delete cascade not null, 
  comment text null, 
  comment_prese text null, 
  is_questionable boolean not null default false, 
  is_compound boolean not null default false, 
  order_by bigserial, 
  unique(word_etym_id, related_word_id)
);
alter sequence word_etymology_relation_id_seq restart with 10000;

-- keelendi vabavorm
create table word_freeform (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  freeform_id bigint references freeform(id) on delete cascade not null, 
  order_by bigserial, 
  unique(word_id, freeform_id)
);
alter sequence word_freeform_id_seq restart with 10000;

-- keelendi sisemärkus
create table word_forum (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  creator_id bigint references eki_user(id) null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence word_forum_id_seq restart with 10000;

-- paradigma
create table paradigm (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  word_class varchar(100) null, 
  comment text null, 
  inflection_type_nr varchar(100), 
  inflection_type varchar(100), 
  is_secondary boolean default false
);
alter sequence paradigm_id_seq restart with 10000;

-- vorm
create table form (
  id bigserial primary key, 
  value text not null, 
  value_prese text not null,
  morph_code varchar(100) references morph(code) not null
);
alter sequence form_id_seq restart with 10000;

create table paradigm_form (
  id bigserial primary key, 
  paradigm_id bigint references paradigm(id) on delete cascade not null, 
  form_id bigint references form(id) on delete cascade not null,
  morph_group1 text null, 
  morph_group2 text null, 
  morph_group3 text null, 
  display_level integer not null default 1, 
  display_form varchar(255) null,
  audio_file varchar(255) null,
  morph_exists boolean not null, 
  is_questionable boolean not null default false, 
  order_by bigserial
);
alter sequence paradigm_form_id_seq restart with 10000;

-- tähendus
create table meaning (
  id bigserial primary key, manual_event_on timestamp null
);
alter sequence meaning_id_seq restart with 10000;

create table meaning_nr (
  id bigserial primary key, 
  meaning_id bigint references meaning(id) on delete cascade not null, 
  mnr varchar(100) not null, 
  dataset_code varchar(10) references dataset(code) on update cascade on delete cascade not null, 
  unique(meaning_id, mnr, dataset_code)
);
alter sequence meaning_nr_id_seq restart with 10000;

-- tähenduse seos
create table meaning_relation (
  id bigserial primary key, 
  meaning1_id bigint references meaning(id) on delete cascade not null, 
  meaning2_id bigint references meaning(id) on delete cascade not null, 
  meaning_rel_type_code varchar(100) references meaning_rel_type(code) on delete cascade not null, 
  weight numeric(5, 4), 
  order_by bigserial, 
  unique(
    meaning1_id, meaning2_id, meaning_rel_type_code
  )
);
alter sequence meaning_relation_id_seq restart with 10000;

create table meaning_domain (
  id bigserial primary key, 
  meaning_id bigint references meaning(id) on delete cascade not null, 
  domain_origin varchar(100) not null, 
  domain_code varchar(100) not null, 
  order_by bigserial, 
  foreign key (domain_code, domain_origin) references domain (code, origin), 
  unique(
    meaning_id, domain_code, domain_origin
  )
);
alter sequence meaning_domain_id_seq restart with 10000;

create table meaning_semantic_type (
  id bigserial primary key, 
  meaning_id bigint references meaning(id) on delete cascade not null, 
  semantic_type_code varchar(100) references semantic_type(code) not null, 
  order_by bigserial, 
  unique(meaning_id, semantic_type_code)
);
alter sequence meaning_semantic_type_id_seq restart with 10000;

-- tähenduse vabavorm
create table meaning_freeform (
  id bigserial primary key, 
  meaning_id bigint references meaning(id) on delete cascade not null, 
  freeform_id bigint references freeform(id) on delete cascade not null, 
  unique(meaning_id, freeform_id)
);
alter sequence meaning_freeform_id_seq restart with 10000;

-- mõiste sisemärkus
create table meaning_forum (
  id bigserial primary key, 
  meaning_id bigint references meaning(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  creator_id bigint references eki_user(id) null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence meaning_forum_id_seq restart with 10000;

create table meaning_tag (
  id bigserial primary key, 
  meaning_id bigint references meaning(id) on delete cascade not null, 
  tag_name varchar(100) references tag(name) on delete cascade not null, 
  created_on timestamp not null default statement_timestamp(), 
  unique(meaning_id, tag_name)
);
alter sequence meaning_tag_id_seq restart with 10000;

-- seletus
create table definition (
  id bigserial primary key, 
  meaning_id bigint references meaning(id) on delete cascade not null, 
  definition_type_code varchar(100) references definition_type(code) not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  complexity varchar(100) not null, 
  is_public boolean not null default true, 
  order_by bigserial
);
alter sequence definition_id_seq restart with 10000;

create table definition_dataset (
  definition_id bigint references definition(id) on delete cascade not null, 
  dataset_code varchar(10) references dataset(code) on update cascade on delete cascade not null, 
  primary key (definition_id, dataset_code)
);

-- seletuse vabavorm
create table definition_freeform (
  id bigserial primary key, 
  definition_id bigint references definition(id) on delete cascade not null, 
  freeform_id bigint references freeform(id) on delete cascade not null, 
  unique(definition_id, freeform_id)
);
alter sequence definition_freeform_id_seq restart with 10000;

-- kollokatsioon
create table collocation (
  id bigserial primary key, 
  value text not null, 
  definition text, 
  frequency numeric(14, 4), 
  score numeric(14, 4), 
  usages text array, 
  complexity varchar(100) not null
);
alter sequence collocation_id_seq restart with 10000;

-- kollokatsiooni vabavorm
create table collocation_freeform (
  id bigserial primary key, 
  collocation_id bigint references collocation(id) on delete cascade not null, 
  freeform_id bigint references freeform(id) on delete cascade not null, 
  unique(collocation_id, freeform_id)
);
alter sequence collocation_freeform_id_seq restart with 10000;

-- ilmik
create table lexeme (
  id bigserial primary key, 
  word_id bigint references word(id) not null, 
  meaning_id bigint references meaning(id) not null, 
  dataset_code varchar(10) references dataset(code) on update cascade not null, 
  level1 integer default 0 not null, 
  level2 integer default 0 not null, 
  value_state_code varchar(100) references value_state(code) null, 
  proficiency_level_code varchar(100) references proficiency_level(code) null, 
  is_public boolean not null default true, 
  complexity varchar(100) not null, 
  weight numeric(5, 4) default 1, 
  reliability integer null, 
  order_by bigserial, 
  unique(
    word_id, meaning_id, dataset_code
  )
);
alter sequence lexeme_id_seq restart with 10000;

create table lexeme_tag (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  tag_name varchar(100) references tag(name) on delete cascade not null, 
  created_on timestamp not null default statement_timestamp(), 
  unique(lexeme_id, tag_name)
);
alter sequence lexeme_tag_id_seq restart with 10000;

create table lexeme_register (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  register_code varchar(100) references register(code) not null, 
  order_by bigserial, 
  unique(lexeme_id, register_code)
);
alter sequence lexeme_register_id_seq restart with 10000;

create table lexeme_pos (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  pos_code varchar(100) references pos(code) not null, 
  order_by bigserial, 
  unique(lexeme_id, pos_code)
);
alter sequence lexeme_pos_id_seq restart with 10000;

create table lexeme_deriv (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  deriv_code varchar(100) references deriv(code) not null, 
  order_by bigserial, 
  unique(lexeme_id, deriv_code)
);
alter sequence lexeme_deriv_id_seq restart with 10000;

create table lexeme_region (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  region_code varchar(100) references region(code) not null, 
  order_by bigserial, 
  unique(lexeme_id, region_code)
);
alter sequence lexeme_region_id_seq restart with 10000;

-- ilmiku vabavorm
create table lexeme_freeform (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  freeform_id bigint references freeform(id) on delete cascade not null, 
  order_by bigserial, 
  unique(lexeme_id, freeform_id)
);
alter sequence lexeme_freeform_id_seq restart with 10000;

-- ilmiku seos
create table lex_relation (
  id bigserial primary key, 
  lexeme1_id bigint references lexeme(id) on delete cascade not null, 
  lexeme2_id bigint references lexeme(id) on delete cascade not null, 
  lex_rel_type_code varchar(100) references lex_rel_type(code) on delete cascade not null, 
  order_by bigserial, 
  unique(
    lexeme1_id, lexeme2_id, lex_rel_type_code
  )
);
alter sequence lex_relation_id_seq restart with 10000;

-- ilmiku kollokatsiooni grupid
create table lex_colloc_pos_group (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  pos_group_code varchar(100) references pos_group(code) on delete cascade not null, 
  order_by bigserial
);
alter sequence lex_colloc_pos_group_id_seq restart with 10000;

create table lex_colloc_rel_group (
  id bigserial primary key, 
  pos_group_id bigint references lex_colloc_pos_group(id) on delete cascade not null, 
  name text not null, 
  frequency numeric(14, 4), 
  score numeric(14, 4), 
  order_by bigserial
);
alter sequence lex_colloc_rel_group_id_seq restart with 10000;

-- ilmiku kollokatsioon
create table lex_colloc (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  rel_group_id bigint references lex_colloc_rel_group(id) on delete cascade null, 
  collocation_id bigint references collocation(id) on delete cascade not null, 
  member_form text not null, 
  conjunct varchar(100) null, 
  weight numeric(14, 4), 
  member_order integer not null, 
  group_order integer, 
  unique(lexeme_id, collocation_id)
);
alter sequence lex_colloc_id_seq restart with 10000;

-- allikaviited
create table freeform_source_link (
  id bigserial primary key, 
  freeform_id bigint references freeform(id) on delete cascade not null, 
  source_id bigint references source(id) on delete cascade not null, 
  type varchar(100) not null, 
  name text null, 
  value text null, 
  order_by bigserial
);
alter sequence freeform_source_link_id_seq restart with 10000;

create table definition_source_link (
  id bigserial primary key, 
  definition_id bigint references definition(id) on delete cascade not null, 
  source_id bigint references source(id) on delete cascade not null, 
  type varchar(100) not null, 
  name text null, 
  value text null, 
  order_by bigserial
);
alter sequence definition_source_link_id_seq restart with 10000;

create table lexeme_source_link (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  source_id bigint references source(id) on delete cascade not null, 
  type varchar(100) not null, 
  name text null, 
  value text null, 
  order_by bigserial
);
alter sequence lexeme_source_link_id_seq restart with 10000;

create table word_etymology_source_link (
  id bigserial primary key, 
  word_etym_id bigint references word_etymology(id) on delete cascade not null, 
  source_id bigint references source(id) on delete cascade not null, 
  type varchar(100) not null, 
  name text null, 
  value text null, 
  order_by bigserial
);
alter sequence word_etymology_source_link_id_seq restart with 10000;

create table freq_corp (
  id bigserial primary key, 
  name text not null, 
  corp_date date, 
  is_public boolean not null, 
  unique(name)
);
alter sequence freq_corp_id_seq restart with 10000;

create table form_freq (
  id bigserial primary key, 
  freq_corp_id bigint references freq_corp(id) on delete cascade not null, 
  form_id bigint references form(id) on delete cascade not null, 
  value numeric(12, 7) not null, 
  rank bigint not null, 
  unique(freq_corp_id, form_id)
);
alter sequence form_freq_id_seq restart with 10000;

create table morph_freq (
  id bigserial primary key, 
  freq_corp_id bigint references freq_corp(id) on delete cascade not null, 
  morph_code varchar(100) references morph(code) not null, 
  value numeric(12, 7) not null, 
  rank bigint not null, 
  unique(freq_corp_id, morph_code)
);
alter sequence morph_freq_id_seq restart with 10000;

create table word_freq (
  id bigserial primary key, 
  freq_corp_id bigint references freq_corp(id) on delete cascade not null, 
  word_id bigint references word(id) on delete cascade not null, 
  value numeric(12, 7) not null, 
  rank bigint not null, 
  unique(freq_corp_id, word_id)
);
alter sequence word_freq_id_seq restart with 10000;

create table data_request (
  id bigserial primary key, 
  user_id bigint references eki_user(id) on delete cascade not null, 
  request_key varchar(60) not null, 
  content text not null, 
  accessed timestamp, 
  created timestamp not null default statement_timestamp()
);
alter sequence data_request_id_seq restart with 10000;

create table activity_log (
  id bigserial primary key, 
  event_by text not null, 
  event_on timestamp not null default statement_timestamp(), 
  dataset_code varchar(10) null, 
  funct_name text not null, 
  owner_id bigint not null, 
  owner_name text not null, 
  entity_id bigint not null, 
  entity_name text not null, 
  prev_data jsonb not null, 
  curr_data jsonb not null, 
  prev_diffs type_activity_log_diff array not null, 
  curr_diffs type_activity_log_diff array not null
);
alter sequence activity_log_id_seq restart with 10000;

create table lexeme_activity_log (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null, 
  unique(lexeme_id, activity_log_id)
);
alter sequence lexeme_activity_log_id_seq restart with 10000;

create table word_activity_log (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null, 
  unique(word_id, activity_log_id)
);
alter sequence word_activity_log_id_seq restart with 10000;

create table word_last_activity_log (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null, 
  unique(word_id)
);
alter sequence word_last_activity_log_id_seq restart with 10000;

create table meaning_activity_log (
  id bigserial primary key, 
  meaning_id bigint references meaning(id) on delete cascade not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null, 
  unique(meaning_id, activity_log_id)
);
alter sequence meaning_activity_log_id_seq restart with 10000;

create table meaning_last_activity_log (
  id bigserial primary key, 
  meaning_id bigint references meaning(id) on delete cascade not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null, 
  type varchar(100) not null, 
  unique(meaning_id, type)
);
alter sequence meaning_last_activity_log_id_seq restart with 10000;

create table source_activity_log (
  id bigserial primary key, 
  source_id bigint references source(id) on delete cascade not null, 
  activity_log_id bigint references activity_log(id) on delete cascade not null, 
  unique(source_id, activity_log_id)
);
alter sequence source_activity_log_id_seq restart with 10000;

create table game_nonword (
  id bigserial primary key, 
  word text not null, 
  lang char(3) references language(code) not null, 
  unique(word, lang)
);

create table feedback_log (
  id bigserial primary key, 
  feedback_type varchar(100) not null, 
  sender_name text not null, 
  sender_email text not null, 
  created_on timestamp not null default statement_timestamp(), 
  description text null, 
  word text null, 
  definition text null, 
  definition_source text null, 
  domain text null, 
  comments text null, 
  usage text null, 
  usage_source text null, 
  other_info text null, 
  company text null, 
  last_search text null
);
alter sequence feedback_log_id_seq restart with 10000;

create table feedback_log_comment (
  id bigserial primary key, 
  feedback_log_id bigint references feedback_log(id) on delete cascade not null, 
  comment text, 
  user_name text not null, 
  created_on timestamp not null default statement_timestamp()
);
alter sequence feedback_log_comment_id_seq restart with 10000;

create table temp_ds_import_pk_map (
  id bigserial primary key, 
  import_code varchar(100) not null, 
  created_on timestamp not null default statement_timestamp(), 
  table_name text not null, 
  source_pk bigint not null, 
  target_pk bigint not null
);
alter sequence temp_ds_import_pk_map_id_seq restart with 10000;

create table temp_ds_import_queue (
  id bigserial primary key, 
  import_code varchar(100) not null, 
  created_on timestamp not null default statement_timestamp(), 
  table_name text not null, 
  content text not null
);
alter sequence temp_ds_import_queue_id_seq restart with 10000;

--- indexes
create index eki_user_email_idx on eki_user(email);
create index eki_user_api_key_idx on eki_user(api_key);
create index eki_user_profile_user_id_idx on eki_user_profile(user_id);
create index eki_user_profile_recent_dataset_permission_id_idx on eki_user_profile(recent_dataset_permission_id);
create index dataset_code_idx on dataset(code);
create index dataset_type_idx on dataset(type);
create index dataset_perm_user_id_idx on dataset_permission(user_id);
create index dataset_perm_dataset_code_idx on dataset_permission(dataset_code);
create index dataset_perm_dataset_full_cmplx_idx on dataset_permission(user_id, auth_operation, auth_item, dataset_code, auth_lang);
create index form_value_idx on form(value);
create index form_value_lower_idx on form(lower(value));
create index form_value_lower_prefix_idx on form (lower(value) text_pattern_ops);
create index form_morph_code_idx on form(morph_code);
create index paradigm_word_id_idx on paradigm(word_id);
create index paradigm_form_paradigm_id_idx on paradigm_form(paradigm_id);
create index paradigm_form_form_id_idx on paradigm_form(form_id);
create index paradigm_form_display_form_idx on paradigm_form(display_form);
create index paradigm_form_display_level_idx on paradigm_form(display_level);
create index word_homonym_nr_idx on word(homonym_nr);
create index word_lang_idx on word(lang);
create index word_value_idx on word(value);
create index word_value_lower_idx on word(lower(value));
create index word_value_lower_prefix_idx on word(lower(value) text_pattern_ops);
create index word_value_as_word_idx on word(value_as_word);
create index word_value_as_word_lower_idx on word(lower(value_as_word));
create index word_value_as_word_lower_prefix_idx on word(lower(value_as_word) text_pattern_ops);
create index word_manual_event_on_idx on word(manual_event_on);
create index word_morphophono_form_idx on word(morphophono_form);
create index word_morphophono_form_lower_idx on word(lower(morphophono_form));
create index word_is_word_idx on word(is_word);
create index word_is_collocation_idx on word(is_collocation);
create index word_is_public_idx on word(is_public);
create index word_etym_word_id_idx on word_etymology(word_id);
create index word_etym_etym_type_code_idx on word_etymology(etymology_type_code);
create index word_etym_rel_word_etym_id_idx on word_etymology_relation(word_etym_id);
create index word_etym_rel_rel_word_id_idx on word_etymology_relation(related_word_id);
create index word_guid_word_id_idx on word_guid(word_id);
create index word_guid_dataset_code_idx on word_guid(dataset_code);
create index word_guid_guid_idx on word_guid(guid);
create index word_group_member_group_id_idx on word_group_member(word_group_id);
create index word_group_member_word_id_idx on word_group_member(word_id);
create index word_word_type_word_id_idx on word_word_type(word_id);
create index word_word_type_idx on word_word_type(word_type_code);
create index meaning_manual_event_on_idx on meaning(manual_event_on);
create index meaning_nr_meaning_id_idx on meaning_nr(meaning_id);
create index meaning_nr_dataset_code_idx on meaning_nr(dataset_code);
create index meaning_nr_mnr_idx on meaning_nr(mnr);
create index lexeme_word_id_idx on lexeme(word_id);
create index lexeme_meaning_id_idx on lexeme(meaning_id);
create index lexeme_dataset_code_idx on lexeme(dataset_code);
create index lexeme_value_state_code_idx on lexeme(value_state_code);
create index lexeme_proficiency_level_code_idx on lexeme(proficiency_level_code);
create index lexeme_is_public_idx on lexeme(is_public);
create index lexeme_complexity_idx on lexeme(complexity);
create index lexeme_tag_lexeme_id_idx on lexeme_tag(lexeme_id);
create index lexeme_tag_tag_name_idx on lexeme_tag(tag_name);
create index lexeme_tag_tag_name_lower_idx on lexeme_tag(lower(tag_name));
create index definition_meaning_id_idx on definition(meaning_id);
create index definition_lang_idx on definition(lang);
create index definition_complexity_idx on definition(complexity);
create index definition_is_public_idx on definition(is_public);
create index meaning_relation_meaning1_id_idx on meaning_relation(meaning1_id);
create index meaning_relation_meaning2_id_idx on meaning_relation(meaning2_id);
create index meaning_rel_mapping_code1_idx on meaning_rel_mapping(code1);
create index meaning_rel_mapping_code2_idx on meaning_rel_mapping(code2);
create index meaning_tag_meaning_id_idx on meaning_tag(meaning_id);
create index meaning_tag_tag_name_idx on meaning_tag(tag_name);
create index meaning_tag_tag_name_lower_idx on meaning_tag(lower(tag_name));
create index lex_relation_lexeme1_id_idx on lex_relation(lexeme1_id);
create index lex_relation_lexeme2_id_idx on lex_relation(lexeme2_id);
create index lex_rel_mapping_code1_idx on lex_rel_mapping(code1);
create index lex_rel_mapping_code2_idx on lex_rel_mapping(code2);
create index word_relation_word1_id_idx on word_relation(word1_id);
create index word_relation_word2_id_idx on word_relation(word2_id);
create index word_relation_word_rel_type_code_idx on word_relation(word_rel_type_code);
create index word_relation_param_word_relation_id_idx on word_relation_param(word_relation_id);
create index word_rel_mapping_code1_idx on word_rel_mapping(code1);
create index word_rel_mapping_code2_idx on word_rel_mapping(code2);
create index freeform_parent_id_idx on freeform(parent_id);
create index freeform_value_text_idx on freeform(value_text);
create index freeform_value_text_lower_idx on freeform(lower(value_text));
create index freeform_type_idx on freeform(type);
create index freeform_lang_idx on freeform(lang);
create index freeform_complexity_idx on freeform(complexity);
create index freeform_is_public_idx on freeform(is_public);
create index source_type_idx on source(type);
create index source_name_idx on source(name);
create index source_name_lower_idx on source(lower(name));
create index source_name_lower_prefix_idx on source(lower(name) text_pattern_ops);
create index source_value_idx on source(value);
create index source_value_lower_idx on source(lower(value));
create index source_value_lower_prefix_idx on source(lower(value) text_pattern_ops);
create index source_freeform_source_id_idx on source_freeform(source_id);
create index source_freeform_freeform_id_idx on source_freeform(freeform_id);
create index meaning_freeform_meaning_id_idx on meaning_freeform(meaning_id);
create index meaning_freeform_freeform_id_idx on meaning_freeform(freeform_id);
create index lexeme_freeform_lexeme_id_idx on lexeme_freeform(lexeme_id);
create index lexeme_freeform_freeform_id_idx on lexeme_freeform(freeform_id);
create index word_freeform_word_id_idx on word_freeform(word_id);
create index word_freeform_freeform_id_idx on word_freeform(freeform_id);
create index definition_freeform_definition_id_idx on definition_freeform(definition_id);
create index definition_freeform_freeform_id_idx on definition_freeform(freeform_id);
create index collocation_freeform_collocation_id_idx on collocation_freeform(collocation_id);
create index collocation_freeform_freeform_id_idx on collocation_freeform(freeform_id);
create index freeform_source_link_freeform_id_idx on freeform_source_link(freeform_id);
create index freeform_source_link_source_id_idx on freeform_source_link(source_id);
create index freeform_source_link_name_idx on freeform_source_link(name);
create index freeform_source_link_name_lower_idx on freeform_source_link(lower(name));
create index freeform_source_link_value_idx on freeform_source_link(value);
create index freeform_source_link_value_lower_idx on freeform_source_link(lower(value));
create index definition_source_link_definition_id_idx on definition_source_link(definition_id);
create index definition_source_link_source_id_idx on definition_source_link(source_id);
create index definition_source_link_name_idx on definition_source_link(name);
create index definition_source_link_name_lower_idx on definition_source_link(lower(name));
create index definition_source_link_value_idx on definition_source_link(value);
create index definition_source_link_value_lower_idx on definition_source_link(lower(value));
create index lexeme_source_link_lexeme_id_idx on lexeme_source_link(lexeme_id);
create index lexeme_source_link_source_id_idx on lexeme_source_link(source_id);
create index lexeme_source_link_name_idx on lexeme_source_link(name);
create index lexeme_source_link_name_lower_idx on lexeme_source_link(lower(name));
create index lexeme_source_link_value_idx on lexeme_source_link(value);
create index lexeme_source_link_value_lower_idx on lexeme_source_link(lower(value));
create index word_etym_source_link_word_etym_id_idx on word_etymology_source_link(word_etym_id);
create index word_etym_source_link_source_id_idx on word_etymology_source_link(source_id);
create index lex_colloc_pos_group_lexeme_id_idx on lex_colloc_pos_group(lexeme_id);
create index lex_colloc_rel_group_pos_group_id_idx on lex_colloc_rel_group(pos_group_id);
create index lex_colloc_lexeme_id_idx on lex_colloc(lexeme_id);
create index lex_colloc_rel_group_id_idx on lex_colloc(rel_group_id);
create index lex_colloc_collocation_id_idx on lex_colloc(collocation_id);
create index collocation_value_idx on collocation(value);
create index lexeme_register_lexeme_id_idx on lexeme_register(lexeme_id);
create index lexeme_pos_lexeme_id_idx on lexeme_pos(lexeme_id);
create index lexeme_pos_pos_code_idx on lexeme_pos(pos_code);
create index lexeme_deriv_lexeme_id_idx on lexeme_deriv(lexeme_id);
create index lexeme_region_lexeme_id_idx on lexeme_region(lexeme_id);
create index meaning_domain_meaning_id_idx on meaning_domain(meaning_id);
create index meaning_semantic_type_meaning_id_idx on meaning_semantic_type(meaning_id);
create index form_freq_corp_id_idx on form_freq(freq_corp_id);
create index form_freq_form_id_idx on form_freq(form_id);
create index form_freq_value_id_idx on form_freq(value);
create index form_freq_rank_id_idx on form_freq(rank);
create index morph_freq_corp_id_idx on morph_freq(freq_corp_id);
create index morph_freq_morph_code_idx on morph_freq(morph_code);
create index morph_freq_value_id_idx on morph_freq(value);
create index morph_freq_rank_id_idx on morph_freq(rank);
create index word_freq_corp_id_idx on word_freq(freq_corp_id);
create index word_freq_word_id_idx on word_freq(word_id);
create index word_freq_value_id_idx on word_freq(value);
create index word_freq_rank_id_idx on word_freq(rank);
create index data_request_user_id_idx on data_request(user_id);
create index lexeme_activity_log_lexeme_id_idx on lexeme_activity_log(lexeme_id);
create index lexeme_activity_log_log_id_idx on lexeme_activity_log(activity_log_id);
create index word_activity_log_word_id_idx on word_activity_log(word_id);
create index word_activity_log_log_id_idx on word_activity_log(activity_log_id);
create index word_last_activity_log_word_id_idx on word_last_activity_log(word_id);
create index word_last_activity_log_log_id_idx on word_last_activity_log(activity_log_id);
create index meaning_activity_log_meaning_id_idx on meaning_activity_log(meaning_id);
create index meaning_activity_log_log_id_idx on meaning_activity_log(activity_log_id);
create index meaning_last_activity_log_meaning_id_idx on meaning_last_activity_log(meaning_id);
create index meaning_last_activity_log_log_id_idx on meaning_last_activity_log(activity_log_id);
create index source_activity_log_source_id_idx on source_activity_log(source_id);
create index source_activity_log_log_id_idx on source_activity_log(activity_log_id);
create index activity_log_event_on_idx on activity_log(event_on);
create index activity_log_event_on_desc_idx on activity_log(event_on desc);
create index activity_log_event_on_ms_idx on activity_log((date_part('epoch', event_on) * 1000));
create index activity_log_event_on_desc_ms_idx on activity_log((date_part('epoch', event_on) * 1000) desc);
create index activity_log_event_by_idx on activity_log(event_by);
create index activity_log_event_by_lower_idx on activity_log(lower(event_by));
create index activity_log_owner_idx on activity_log(owner_name, owner_id);
create index activity_log_owner_name_idx on activity_log(owner_name);
create index activity_log_dataset_code_idx on activity_log(dataset_code);
create index activity_funct_name_idx on activity_log(funct_name);
create index activity_entity_name_idx on activity_log(entity_name);
create index activity_entity_name_owner_name_event_on_idx on activity_log(entity_name, owner_name, (date_part('epoch', event_on) * 1000));
create index activity_entity_id_idx on activity_log(entity_id);
create index activity_curr_data_word_id_idx on activity_log(cast(curr_data ->> 'wordId' as bigint));
create index activity_curr_data_meaning_id_idx on activity_log(cast(curr_data ->> 'meaningId' as bigint));
create index activity_curr_data_lexeme_id_idx on activity_log(cast(curr_data ->> 'lexemeId' as bigint));
create index feedback_log_comment_log_id_idx on feedback_log_comment(feedback_log_id);
create index temp_ds_import_pk_map_import_code_idx on temp_ds_import_pk_map(import_code);
create index temp_ds_import_pk_map_table_name_idx on temp_ds_import_pk_map(table_name);
create index temp_ds_import_pk_map_source_pk_idx on temp_ds_import_pk_map(source_pk);
create index temp_ds_import_pk_map_target_pk_idx on temp_ds_import_pk_map(target_pk);
create index temp_ds_import_queue_import_code_idx on temp_ds_import_queue(import_code);
create index temp_ds_import_queue_table_name_idx on temp_ds_import_queue(table_name);
create index domain_code_origin_idx on domain(code, origin);
create index domain_parent_code_origin_idx on domain(parent_code, parent_origin);
create index domain_label_code_origin_idx on domain_label(code, origin);
create index meaning_domain_code_origin_idx on meaning_domain(domain_code, domain_origin);
create index definition_fts_idx on definition using gin(to_tsvector('simple', value));
create index freeform_fts_idx on freeform using gin(to_tsvector('simple', value_text));
create index form_fts_idx on form using gin(to_tsvector('simple', value));
