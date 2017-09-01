drop table if exists lex_relation;
drop table if exists grammar;
drop table if exists usage;
drop table if exists rection;
drop table if exists lexeme_domain;
drop table if exists lexeme_register;
drop table if exists lexeme_pos;
drop table if exists lexeme;
drop table if exists definition;
drop table if exists meaning;
drop table if exists form;
drop table if exists paradigm;
drop table if exists declination;
drop table if exists word;
drop table if exists lex_rel_type_label;
drop table if exists lex_rel_type;
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
drop table if exists domain_label;
drop table if exists domain;
drop table if exists lang_label;
drop table if exists lang;
drop table if exists label_type;
drop table if exists dataset;
drop table if exists eki_user;

create table eki_user
(
  id bigserial primary key,
  name varchar(255) not null,
  password varchar(255) not null,
  created timestamp not null default statement_timestamp(),
  unique(name)
);

---------------------------------
-- klassifitseeritud andmestik --
---------------------------------

-- klassif. nime liik
create table label_type
(
  code char(10) primary key,
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
  type char(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- valdkond
create table domain
(
  code varchar(100) not null,
  origin varchar(100) not null,
  parent_code varchar(100) null,
  parent_origin varchar(100) null,
  dataset char(10) array not null,
  primary key (code, origin),
  foreign key (parent_code, parent_origin) references domain (code, origin)
);

create table domain_label
(
  code varchar(100) not null,
  origin varchar(100) not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type char(10) references label_type(code) not null,
  foreign key (code, origin) references domain (code, origin),
  unique(code, origin, lang, type)
);

-- register
create table register
(
  code varchar(100) primary key,
  dataset char(10) array not null
);

create table register_label
(
  code varchar(100) references register(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type char(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- sugu
create table gender
(
  code varchar(100) primary key,
  dataset char(10) array not null
);

create table gender_label
(
  code varchar(100) references gender(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type char(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- sõnaliik
create table pos
(
  code varchar(100) primary key,
  dataset char(10) array not null
);

create table pos_label
(
  code varchar(100) references pos(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type char(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- vormi märgend
create table morph
(
  code varchar(100) primary key,
  dataset char(10) array not null
);

create table morph_label
(
  code varchar(100) references morph(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type char(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- tuletuskood
create table deriv
(
  code varchar(100) primary key,
  dataset char(10) array not null
);

create table deriv_label
(
  code varchar(100) references deriv(code) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type char(10) references label_type(code) not null,
  unique(code, lang, type)
);

-- seose liik
create table lex_rel_type
(
  id bigserial primary key,
  dataset char(10) array not null
);

create table lex_rel_type_label
(
  lex_rel_type_id bigint references lex_rel_type(id) on delete cascade not null,
  value text not null,
  lang char(3) references lang(code) not null,
  type char(10) references label_type(code) not null,
  unique(lex_rel_type_id, lang, type)
);

---------------------------
-- dünaamiline andmestik --
---------------------------

-- sõnakogu
create table dataset
(
  code char(10) primary key,
  name text not null
);

-- keelend
create table word
(
  id bigserial primary key,
  value varchar(255) null,
  display_form varchar(255) null,
  components varchar(100) array null,
  lang char(3) references lang(code) null,
  morph_code varchar(100) references morph(code) null,
  dataset char(10) array not null
);

-- muutmisviis
create table declination
(
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null
);

-- paradigma
create table paradigm
(
  id bigserial primary key,
  declination_id bigint references declination(id) on delete cascade not null,
  example text not null
);

-- vorm
create table form
(
  id bigserial primary key,
  paradigm_id bigint references paradigm(id) on delete cascade not null,
  morph_code varchar(100) references morph(code) not null,
  value text not null,
  dataset char(10) array not null
);

-- mõiste/tähendus
create table meaning
(
  id bigserial primary key,
  dataset char(10) array not null
);

-- sõnastus/seletus/definitsioon
create table definition
(
  id bigserial primary key,
  meaning_id bigint references meaning(id) not null,
  value text not null,
  dataset char(10) array not null
);

-- ilmik
create table lexeme
(
  id bigserial primary key,
  declination_id bigint references declination(id) not null,
  meaning_id bigint references meaning(id) not null,
  order_by varchar(100) not null default '',
  dataset char(10) array not null,
  unique(declination_id, meaning_id)
);

create table lexeme_domain
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  domain_code varchar(100) not null,
  domain_origin varchar(100) not null,
  foreign key (domain_code, domain_origin) references domain (code, origin),
  unique(lexeme_id, domain_code, domain_origin)
);

create table lexeme_register
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  register_code varchar(100) references register(code) not null,
  unique(lexeme_id, register_code)
);

create table lexeme_pos
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  pos_code varchar(100) references pos(code) not null,
  unique(lexeme_id, pos_code)
);

create table lexeme_deriv
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  deriv_code varchar(100) references deriv(code) not null,
  unique(lexeme_id, deriv_code)
);

-- rektsioon
create table rection
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  value text not null,
  dataset char(10) array not null
);

-- kasutusnäide/kontekst
create table usage
(
  id bigserial primary key,
  rection_id bigint references rection(id) on delete cascade not null,
  value text not null,
  dataset char(10) array not null
);

-- gramm.kasutusinfo
create table grammar
(
  id bigserial primary key,
  lexeme_id bigint references lexeme(id) on delete cascade not null,
  value text not null,
  dataset char(10) array not null
);

-- seos
create table lex_relation
(
  id bigserial primary key,
  lexeme1_id bigint references lexeme(id) on delete cascade not null,
  lexeme2_id bigint references lexeme(id) on delete cascade not null,
  lex_rel_type_id bigint references lex_rel_type(id) on delete cascade not null,
  dataset char(10) array not null,
  unique(lexeme1_id, lexeme2_id, lex_rel_type_id)
);

