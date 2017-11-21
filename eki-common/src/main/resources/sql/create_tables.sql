drop table if exists lex_relation_dataset;
drop table if exists lex_relation;
drop table if exists grammar_dataset;--will be removed
drop table if exists grammar;--will be removed
drop table if exists usage_translation;--will be removed
drop table if exists usage;--will be removed
drop table if exists rection;--will be removed
drop table if exists lexeme_freeform;
drop table if exists lexeme_register;
drop table if exists lexeme_pos;
drop table if exists lexeme_deriv;
drop table if exists lexeme_dataset;
drop table if exists lexeme;
drop table if exists definition_freeform;
drop table if exists definition_dataset;
drop table if exists definition;
drop table if exists meaning_freeform;
drop table if exists meaning_domain;
drop table if exists meaning_dataset;
drop table if exists meaning;
drop table if exists form_relation;
drop table if exists form;
drop table if exists paradigm;
drop table if exists word_relation;
drop table if exists word;
drop table if exists source_freeform;
drop table if exists source;
drop table if exists freeform;
drop table if exists word_rel_type_label;
drop table if exists word_rel_type;
drop table if exists form_rel_type_label;
drop table if exists form_rel_type;
drop table if exists lex_rel_type_label;
drop table if exists lex_rel_type;
--drop table if exists meaning_type_label;
drop table if exists meaning_type;
--drop table if exists meaning_state_label;
drop table if exists meaning_state;
--drop table if exists entry_class_label;
drop table if exists entry_class;
drop table if exists deriv_label;
drop table if exists deriv;
drop table if exists morph_label;
drop table if exists morph;
drop table if exists pos_label;
drop table if exists pos;
drop table if exists gender_label;
drop table if exists gender;
drop table if exists register_label;
drop table if exists register;
drop table if exists lexeme_type_label;
drop table if exists lexeme_type;
drop table if exists lexeme_frequency_label; -- removed for now
drop table if exists lexeme_frequency;
drop table if exists domain_label;
drop table if exists domain;
drop table if exists lang_label;
drop table if exists lang;
drop table if exists label_type;
drop table if exists dataset;
drop table if exists lifecycle_log;
drop table if exists eki_user;
drop table if exists rection_type;

create table eki_user
(
  id bigserial primary key,
  name varchar(255) not null,
  password varchar(255) not null,
  created timestamp not null default statement_timestamp(),
  unique(name)
);
alter sequence eki_user_id_seq restart with 10000;

---------------------------------
-- klassifitseeritud andmestik --
---------------------------------

-- klassif. nime liik
create table rection_type
(
  code varchar(10) primary key,
  value text not null
);

-- klassif. nime liik
create table label_type
(
  code varchar(10) primary key,
  value text not null
);

-- keel
create table lang
(
  code char(3) primary key,
  value text not null
);

create table lang_label
(
  code char(3) references lang(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- valdkond
create table domain
(
  code varchar(100) not null,
  origin varchar(100) not null,
  parent_code varchar(100) null,
  parent_origin varchar(100) null,
  datasets varchar(10) array not null,
  primary key (code, origin),
  foreign key (parent_code, parent_origin) references domain (code, origin)
);

create table domain_label
(
  code varchar(100) not null,
  origin varchar(100) not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  foreign key (code, origin) references domain (code, origin),
  unique(code, origin, lang, type)
);

-- register
create table register
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

create table register_label
(
  code varchar(100) references register(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- ilmiku tüüp
create table lexeme_type
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

create table lexeme_type_label
(
  code varchar(100) references lexeme_type(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- ilmiku sagedusrühm
create table lexeme_frequency
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

-- sugu
create table gender
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

create table gender_label
(
  code varchar(100) references gender(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- sõnaliik
create table pos
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

create table pos_label
(
  code varchar(100) references pos(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- vormi märgend
create table morph
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

create table morph_label
(
  code varchar(100) references morph(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- tuletuskood
create table deriv
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

create table deriv_label
(
  code varchar(100) references deriv(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- entry class?
create table entry_class
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);
-- missing entry_class_label

-- tähenduse liik
create table meaning_type
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);
-- missing meaning_type_label

-- tähenduse staatus
create table meaning_state
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);
-- missing meaning_state_label

-- seose liik
create table lex_rel_type
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

create table lex_rel_type_label
(
  code varchar(100) references lex_rel_type(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- vormi seose liik
create table form_rel_type
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

create table form_rel_type_label
(
  code varchar(100) references form_rel_type(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- ilmiku seose liik
create table word_rel_type
(
  code varchar(100) primary key,
  datasets varchar(10) array not null
);

create table word_rel_type_label
(
  code varchar(100) references word_rel_type(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

---------------------------
-- dünaamiline andmestik --
---------------------------

create table lifecycle_log
(
  id bigserial primary key,
  owner_id bigint not null,
  owner_name varchar(100) not null,
  type varchar(100) not null,
  event_by varchar(100) null,
  event_on timestamp null
);
alter sequence lifecycle_log_id_seq restart with 10000;

-- sõnakogu
create table dataset
(
  code varchar(10) primary key,
  name text not null
);

-- vabavorm
create table freeform
(
  id bigserial primary key,
  parent_id bigint references freeform(id) on delete cascade null,
  type varchar(100) not null,
  value_text text null,
  value_date timestamp null,
  value_number numeric(14, 4) null,
  value_array text array null,
  classif_name text null,
  classif_code varchar(100) null,
  lang char(3) references lang(code) null
);
alter sequence freeform_id_seq restart with 10000;

-- allikas
create table source
(
  id bigserial primary key,
  concept varchar(100) not null,
  created_on timestamp null,
  created_by varchar(100) null,
  modified_on timestamp null,
  modified_by varchar(100) null,
  entry_class_code varchar(100) references entry_class(code) null,
  type varchar(100) null
);
alter sequence source_id_seq restart with 10000;

-- allika vabavorm
create table source_freeform
(
  id bigserial primary key,
  source_id bigint references source(id) on delete cascade not null,
  freeform_id bigint references freeform(id) on delete cascade not null,
  unique(source_id, freeform_id)
);
alter sequence source_freeform_id_seq restart with 10000;

-- keelend (morfoloogiline homonüüm)
create table word
(
  id bigserial primary key,
  lang char(3) references lang(code) null,
  morph_code varchar(100) references morph(code) null,
  homonym_nr integer default 1,
  display_pos varchar(100)
);
alter sequence word_id_seq restart with 10000;

-- keelendi seos
create table word_relation
(
  id bigserial primary key,
  word1_id bigint references word(id) on delete cascade not null,
  word2_id bigint references word(id) on delete cascade not null,
  word_rel_type_code varchar(100) references word_rel_type(code),
  unique(word1_id, word2_id, word_rel_type_code)
);
alter sequence word_relation_id_seq restart with 10000;

-- paradigma
create table paradigm
(
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  example text null,
  inflection_type_nr varchar(100),
  is_secondary boolean default false
);
alter sequence paradigm_id_seq restart with 10000;

-- vorm
create table form
(
  id bigserial primary key,
  paradigm_id bigint references paradigm(id) on delete cascade not null,
  morph_code varchar(100) references morph(code) not null,
  value text not null,
  components varchar(100) array null,
  display_form varchar(255) null,
  vocal_form varchar(255) null,
  is_word boolean default false
);
alter sequence form_id_seq restart with 10000;

-- vormi seos
create table form_relation
(
  id bigserial primary key,
  form1_id bigint references form(id) on delete cascade not null,
  form2_id bigint references form(id) on delete cascade not null,
  form_rel_type_code varchar(100) references form_rel_type(code),
  unique(form1_id, form2_id, form_rel_type_code)
);
alter sequence form_relation_id_seq restart with 10000;

-- tähendus
create table meaning
(
  id bigserial primary key,
  created_on timestamp null,
  created_by varchar(100) null,
  modified_on timestamp null,
  modified_by varchar(100) null,
  entry_class_code varchar(100) references entry_class(code) null,
  state_code varchar(100) references meaning_state(code) null,
  type_code varchar(100) references meaning_type(code) null
);
alter sequence meaning_id_seq restart with 10000;

create table meaning_dataset
(
  meaning_id bigint REFERENCES meaning(id) on delete CASCADE not null,
  dataset_code varchar(10) references dataset(code) not null,
  primary key (meaning_id, dataset_code)
);

create table meaning_domain
(
  id bigserial primary key,
  meaning_id bigint references meaning(id) on delete cascade not null,
  domain_code varchar(100) not null,
  domain_origin varchar(100) not null,
  foreign key (domain_code, domain_origin) references domain (code, origin),
  unique(meaning_id, domain_code, domain_origin)
);
alter sequence meaning_domain_id_seq restart with 10000;

-- tähenduse vabavorm
create table meaning_freeform
(
  id bigserial primary key,
  meaning_id bigint references meaning(id) on delete cascade not null,
  freeform_id bigint references freeform(id) on delete cascade not null,
  unique(meaning_id, freeform_id)
);
alter sequence meaning_freeform_id_seq restart with 10000;

-- seletus
create table definition
(
  id bigserial primary key,
  meaning_id bigint references meaning(id) not null,
  value text not null,
  lang char(3) references lang(code) not null
);
alter sequence definition_id_seq restart with 10000;

create table definition_dataset
(
  definition_id bigint REFERENCES definition(id) on delete CASCADE not null,
  dataset_code varchar(10) references dataset(code) not null,
  primary key (definition_id, dataset_code)
);

-- seletuse vabavorm
create table definition_freeform
(
  id bigserial primary key,
  definition_id bigint references definition(id) on delete cascade not null,
  freeform_id bigint references freeform(id) on delete cascade not null,
  unique(definition_id, freeform_id)
);
alter sequence definition_freeform_id_seq restart with 10000;

-- ilmik
create table lexeme
(
  id bigserial primary key,
  word_id bigint references word(id) not null,
  meaning_id bigint references meaning(id) not null,
  created_on timestamp null,
  created_by varchar(100) null,
  modified_on timestamp null,
  modified_by varchar(100) null,
  type_code varchar(100) references lexeme_type(code) null,
  frequency_group varchar(100) references lexeme_frequency(code) null,
  level1 integer default 0,
  level2 integer default 0,
  level3 integer default 0,
  unique(word_id, meaning_id)
);
alter sequence lexeme_id_seq restart with 10000;

create table lexeme_dataset
(
  lexeme_id bigint REFERENCES lexeme(id) on delete CASCADE not null,
  dataset_code varchar(10) references dataset(code) not null,
  primary key (lexeme_id, dataset_code)
);

create table lexeme_register
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  register_code varchar(100) references register(code) not null,
  unique(lexeme_id, register_code)
);
alter sequence lexeme_register_id_seq restart with 10000;

create table lexeme_pos
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  pos_code varchar(100) references pos(code) not null,
  unique(lexeme_id, pos_code)
);
alter sequence lexeme_pos_id_seq restart with 10000;

create table lexeme_deriv
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  deriv_code varchar(100) references deriv(code) not null,
  unique(lexeme_id, deriv_code)
);
alter sequence lexeme_deriv_id_seq restart with 10000;

-- ilmiku vabavorm
create table lexeme_freeform
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  freeform_id bigint references freeform(id) on delete cascade not null,
  unique(lexeme_id, freeform_id)
);
alter sequence lexeme_freeform_id_seq restart with 10000;

-- seos
create table lex_relation
(
  id bigserial primary key,
  lexeme1_id bigint references lexeme(id) on delete cascade not null,
  lexeme2_id bigint references lexeme(id) on delete cascade not null,
  lex_rel_type_code varchar(100) references lex_rel_type(code) on delete cascade not null,
  unique(lexeme1_id, lexeme2_id, lex_rel_type_code)
);
alter sequence lex_relation_id_seq restart with 10000;

create table lex_relation_dataset
(
  lex_relation_id bigint REFERENCES lex_relation(id) on delete CASCADE not null,
  dataset_code varchar(10) references dataset(code) not null,
  primary key (lex_relation_id, dataset_code)
);

--- indexes

create index form_value_idx on form(value);
create index form_paradigm_id_idx on form(paradigm_id);
create index paradigm_word_id_idx on paradigm(word_id);
create index word_homonym_nr_idx on word(homonym_nr);
create index word_lang_idx on word(lang);
create index lexeme_word_id_idx on lexeme(word_id);
create index lexeme_meaning_id_idx on lexeme(meaning_id);
create index definition_meaning_id_idx on definition(meaning_id);
create index lex_relation_lexeme1_id_idx on lex_relation(lexeme1_id);
create index lex_relation_lexeme2_id_idx on lex_relation(lexeme2_id);
create index word_relation_word1_id_idx on word_relation(word1_id);
create index word_relation_word2_id_idx on word_relation(word2_id);
create index freeform_parent_id_idx on freeform(parent_id);
create index freeform_value_text_idx on freeform(value_text);
create index source_freeform_source_id_idx on source_freeform(source_id);
create index source_freeform_freeform_id_idx on source_freeform(freeform_id);
create index meaning_freeform_meaning_id_idx on meaning_freeform(meaning_id);
create index meaning_freeform_freeform_id_idx on meaning_freeform(freeform_id);
create index definition_freeform_definition_id_idx on definition_freeform(definition_id);
create index definition_freeform_freeform_id_idx on definition_freeform(freeform_id);
