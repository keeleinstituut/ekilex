-- migratsioon ver 1.40.* -> 1.41.0, osa #1

-- ÕS kasutusnäited ja lühimorfo --

create table word_od_usage (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null, 
  value_prese text not null,
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null,
  order_by bigserial
);
alter sequence word_od_usage_id_seq restart with 10000;

create index word_od_usage_word_id_idx on word_od_usage(word_id);
create index word_od_usage_value_idx on word_od_usage(value);
create index word_od_usage_value_lower_idx on word_od_usage(lower(value));

create table word_od_morph (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null, 
  value_prese text not null,
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null,
  unique(word_id)
);
alter sequence word_od_morph_id_seq restart with 10000;

create index word_od_morph_word_id_idx on word_od_morph(word_id);
create index word_od_morph_value_idx on word_od_morph(value);
create index word_od_morph_value_lower_idx on word_od_morph(lower(value));

-- keelendite registreerimine --

alter table word add column reg_year integer;
create index word_reg_year_idx on word(reg_year);

-- kollokatsioonide duplikaatide kustutamine --

alter table collocation_member
drop constraint collocation_member_colloc_lexeme_id_fkey,
add constraint collocation_member_colloc_lexeme_id_fkey foreign key (colloc_lexeme_id) references lexeme (id) on delete cascade;

-- vabavormidest kolimine --

create table grammar (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  value text not null, 
  value_prese text not null, 
  lang char(3) references language(code) not null, 
  complexity varchar(100) not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence grammar_id_seq restart with 10000;

create index grammar_original_freeform_id_idx on grammar(original_freeform_id);
create index grammar_lexeme_id_idx on grammar(lexeme_id);
create index grammar_value_idx on grammar(value);
create index grammar_value_lower_idx on grammar(lower(value));
create index grammar_lang_idx on grammar(lang);
create index grammar_complexity_idx on grammar(complexity);
create index grammar_fts_idx on grammar using gin(to_tsvector('simple', value));

create table government (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  lexeme_id bigint references lexeme(id) on delete cascade not null, 
  value text not null, 
  complexity varchar(100) not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence government_id_seq restart with 10000;

create index government_original_freeform_id_idx on government(original_freeform_id);
create index government_lexeme_id_idx on government(lexeme_id);
create index government_value_idx on government(value);
create index government_value_lower_idx on government(lower(value));
create index government_complexity_idx on government(complexity);
create index government_fts_idx on government using gin(to_tsvector('simple', value));

create table meaning_media (
  id bigserial primary key, 
  original_freeform_id bigint, -- to be dropped later
  meaning_id bigint references meaning(id) on delete cascade not null, 
  url text not null,
  complexity varchar(100) not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null, 
  order_by bigserial
);
alter sequence meaning_media_id_seq restart with 10000;

create index meaning_media_original_freeform_id_idx on meaning_media(original_freeform_id);
create index meaning_media_meaning_id_idx on meaning_media(meaning_id);
create index meaning_media_complexity_idx on meaning_media(complexity);

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

create index learner_comment_original_freeform_id_idx on learner_comment(original_freeform_id);
create index learner_comment_meaning_id_idx on learner_comment(meaning_id);
create index learner_comment_value_idx on learner_comment(value);
create index learner_comment_value_lower_idx on learner_comment(lower(value));
create index learner_comment_fts_idx on learner_comment using gin(to_tsvector('simple', value));

-- publitseerimine --

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

create index publishing_event_by_idx on publishing(event_by);
create index publishing_event_on_idx on publishing(event_on);
create index publishing_target_name_idx on publishing(target_name);
create index publishing_entity_name_idx on publishing(entity_name);
create index publishing_entity_id_idx on publishing(entity_id);

-- ÕS liitsõnaseosed --

insert into word_rel_type (code, datasets) values ('ls-esiosaga', '{}');
insert into word_rel_type (code, datasets) values ('ls-järelosaga', '{}');
insert into word_rel_type_label (code, value, lang, type) values ('ls-esiosaga', 'Liitsõna esiosaga', 'est', 'descrip');
insert into word_rel_type_label (code, value, lang, type) values ('ls-järelosaga', 'Liitsõna järelosaga', 'est', 'descrip');
insert into word_rel_type_label (code, value, lang, type) values ('ls-esiosaga', 'Liitsõna esiosaga', 'est', 'wordweb');
insert into word_rel_type_label (code, value, lang, type) values ('ls-järelosaga', 'Liitsõna järelosaga', 'est', 'wordweb');
insert into word_rel_mapping (code1, code2) values ('ls-esiosa', 'ls-esiosaga');
insert into word_rel_mapping (code1, code2) values ('ls-järelosa', 'ls-järelosaga');
