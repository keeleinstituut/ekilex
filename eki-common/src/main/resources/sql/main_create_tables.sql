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

-- kol rel grupp
create table rel_group
(
  code varchar(100) primary key,
  datasets varchar(10) array not null,
  order_by bigserial
);

create table rel_group_label
(
  code varchar(100) references rel_group(code) on delete cascade not null,
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

-- vabavormi liik
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

---------------------------
-- dünaamiline andmestik --
---------------------------

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
  is_overpower boolean default false,
  is_enabled boolean, 
  review_comment text, 
  created timestamp not null default statement_timestamp(), 
  unique(email)
);
alter sequence eki_user_id_seq restart with 10000;

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

create table dataset_freeform_type (
	id bigserial primary key,
	dataset_code varchar(10) references dataset(code) on update cascade on delete cascade not null,
	freeform_owner varchar(10) not null,
	freeform_type_code varchar(100) references freeform_type(code) on delete cascade not null,
	unique (dataset_code, freeform_owner, freeform_type_code)
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

-- allikas
create table source (
  id bigserial primary key, 
  dataset_code varchar(10) references dataset(code) on update cascade not null,
  type varchar(100) not null, 
  name text not null, 
  value text null, 
  value_prese text null, 
  comment text null, 
  is_public boolean not null default true
);
alter sequence source_id_seq restart with 10000;

-- vabavorm
create table freeform (
  id bigserial primary key, 
  parent_id bigint references freeform(id) on delete cascade null, 
  freeform_type_code varchar(100) not null, 
  value text null, 
  value_prese text null, 
  lang char(3) references language(code) null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence freeform_id_seq restart with 10000;

create table freeform_source_link (
  id bigserial primary key, 
  freeform_id bigint references freeform(id) on delete cascade not null, 
  source_id bigint references source(id) on delete cascade not null, 
  name text null, 
  value text null, 
  order_by bigserial
);
alter sequence freeform_source_link_id_seq restart with 10000;

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
  morph_comment text null,
  reg_year integer,
  manual_event_on timestamp null, 
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

create table word_tag (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  tag_name varchar(100) references tag(name) on delete cascade not null, 
  created_on timestamp not null default statement_timestamp(), 
  unique(word_id, tag_name)
);
alter sequence word_tag_id_seq restart with 10000;

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

create table word_etymology_source_link (
  id bigserial primary key, 
  word_etym_id bigint references word_etymology(id) on delete cascade not null, 
  source_id bigint references source(id) on delete cascade not null, 
  name text null, 
  value text null, 
  order_by bigserial
);
alter sequence word_etymology_source_link_id_seq restart with 10000;

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

create table word_os_homonym_nr (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  homonym_nr integer not null default 1,
  unique(word_id)
);
alter sequence word_os_homonym_nr_id_seq restart with 10000;

create table word_eki_recommendation (
	id bigserial primary key,
	word_id bigint references word(id) on delete cascade not null,
	value text not null, 
	value_prese text not null,
	created_by text null, 
	created_on timestamp null, 
	modified_by text null, 
	modified_on timestamp null
);
alter sequence word_eki_recommendation_id_seq restart with 10000;

create table word_os_usage (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null, 
  value_prese text not null,
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null,
  order_by bigserial
);
alter sequence word_os_usage_id_seq restart with 10000;

create table word_os_morph (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null, 
  value_prese text not null,
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null,
  unique(word_id)
);
alter sequence word_os_morph_id_seq restart with 10000;

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

create table meaning_note (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  meaning_id bigint references meaning(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence meaning_note_id_seq restart with 10000;

create table meaning_note_source_link (
	id bigserial primary key, 
	meaning_note_id bigint references meaning_note(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	name text null, 
	order_by bigserial
);
alter sequence meaning_note_source_link_id_seq restart with 10000;

create table learner_comment (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  meaning_id bigint references meaning(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence learner_comment_id_seq restart with 10000;

create table meaning_image (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  meaning_id bigint references meaning(id) on delete cascade not null, 
  title text null, 
  url text not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence meaning_image_id_seq restart with 10000;

create table meaning_media (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  meaning_id bigint references meaning(id) on delete cascade not null, 
  title text null, 
  url text not null,
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence meaning_media_id_seq restart with 10000;

create table meaning_image_source_link (
	id bigserial primary key, 
	meaning_image_id bigint references meaning_image(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	name text null, 
	order_by bigserial
);
alter sequence meaning_image_source_link_id_seq restart with 10000;

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
  is_public boolean not null default true, 
  order_by bigserial
);
alter sequence definition_id_seq restart with 10000;

create table definition_source_link (
  id bigserial primary key, 
  definition_id bigint references definition(id) on delete cascade not null, 
  source_id bigint references source(id) on delete cascade not null, 
  name text null, 
  value text null, 
  order_by bigserial
);
alter sequence definition_source_link_id_seq restart with 10000;

create table definition_note (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  definition_id bigint references definition(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence definition_note_id_seq restart with 10000;

create table definition_note_source_link (
	id bigserial primary key, 
	definition_note_id bigint references definition_note(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	name text null, 
	order_by bigserial
);
alter sequence definition_note_source_link_id_seq restart with 10000;

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
-- TODO to be removed soon
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
  is_word boolean not null, 
  is_collocation boolean not null, 
  is_public boolean not null default true, 
  weight numeric(5, 4) default 1, 
  reliability integer null, 
  order_by bigserial, 
  unique(
    word_id, meaning_id, dataset_code
  )
);
alter sequence lexeme_id_seq restart with 10000;

create table lexeme_source_link (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  source_id bigint references source(id) on delete cascade not null, 
  name text null, 
  value text null, 
  order_by bigserial
);
alter sequence lexeme_source_link_id_seq restart with 10000;

create table grammar (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence grammar_id_seq restart with 10000;

create table government (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  value text not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence government_id_seq restart with 10000;

create table usage (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  is_public boolean not null default true, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence usage_id_seq restart with 10000;

create table usage_source_link (
	id bigserial primary key, 
	usage_id bigint references usage(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	name text null, 
	order_by bigserial
);
alter sequence usage_source_link_id_seq restart with 10000;

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

create table lexeme_note (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence lexeme_note_id_seq restart with 10000;

create table lexeme_note_source_link (
	id bigserial primary key, 
	lexeme_note_id bigint references lexeme_note(id) on delete cascade not null, 
	source_id bigint references source(id) on delete cascade not null, 
	name text null, 
	order_by bigserial
);
alter sequence lexeme_note_source_link_id_seq restart with 10000;

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
-- TODO to be removed soon
create table lex_colloc_pos_group (
  id bigserial primary key, 
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  pos_group_code varchar(100) references pos_group(code) on delete cascade not null, 
  order_by bigserial
);
alter sequence lex_colloc_pos_group_id_seq restart with 10000;

-- TODO to be removed soon
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
-- TODO to be removed soon
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

create table collocation_member (
	id bigserial primary key,
	colloc_lexeme_id bigint references lexeme(id) on delete cascade not null,
	conjunct_lexeme_id bigint references lexeme(id),
	member_lexeme_id bigint references lexeme(id) not null,
	member_form_id bigint references form(id) not null,
	pos_group_code varchar(100) references pos_group(code),
	rel_group_code varchar(100) references rel_group(code),
	weight numeric(14, 4),
	member_order integer not null,
	group_order integer,
	unique(colloc_lexeme_id, member_lexeme_id)
);
alter sequence collocation_member_id_seq restart with 10000;

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

create table news_article (
	id bigserial primary key,
	created timestamp not null default statement_timestamp(),
	type varchar(100) not null,
	title text not null,
	content text not null,
	lang char(3) references language(code) null
);
alter sequence news_article_id_seq restart with 10000;

create table publishing (
	id bigserial primary key,
	event_by text not null,
	event_on timestamp not null default statement_timestamp(),
	target_name varchar(100) not null,
	entity_name varchar(100) not null,
	entity_id bigint not null,
	unique (target_name, entity_name, entity_id)
);
alter sequence publishing_id_seq restart with 10000;

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
	created timestamp not null default statement_timestamp(), 
	feedback_type varchar(100) null, 
	sender_email text null,
	last_search text null,
	word_value text null,
	description text null
);
alter sequence feedback_log_id_seq restart with 10000;

create table feedback_log_attr (
	id bigserial primary key,
	feedback_log_id bigint references feedback_log(id) on delete cascade not null,
	name text not null,
	value text not null,
	unique(feedback_log_id, name)
);
alter sequence feedback_log_attr_id_seq restart with 10000;

create table feedback_log_comment (
	id bigserial primary key, 
	feedback_log_id bigint references feedback_log(id) on delete cascade not null, 
	created_on timestamp not null default statement_timestamp(),
	comment text, 
	user_name text not null
);
alter sequence feedback_log_comment_id_seq restart with 10000;

create table word_suggestion (
	id bigserial primary key,
	feedback_log_id bigint references feedback_log(id) on delete cascade not null,
	created timestamp not null default statement_timestamp(), 
	word_value text not null,
	definition_value text not null,
	usage_value text not null,
	author_name text not null,
	author_email text not null,
	is_public boolean default false,
	publication_date date
);
alter sequence word_suggestion_id_seq restart with 10000;

create table api_request_count (
	id bigserial primary key,
	auth_name text not null,
	generic_path text not null,
	count bigint not null default 0,
	unique (auth_name, generic_path)
);
alter sequence api_request_count_id_seq restart with 10000;

create table api_error_count (
	id bigserial primary key,
	auth_name text not null,
	generic_path text not null,
	message text not null,
	count bigint not null default 0,
	unique (auth_name, generic_path, message)
);
alter sequence api_error_count_id_seq restart with 10000;

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
