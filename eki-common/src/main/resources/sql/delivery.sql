-- upgrade from ver 1.12.0 to 1.13.0

-- ekilexis ühekordselt:
create extension unaccent;

-- wordwebis ühekordselt:
create extension pg_trgm;
create extension fuzzystrmatch;

alter table eki_user add column terms_ver varchar(100) null;
alter table eki_user_profile
  add column preferred_meaning_relation_word_langs char(3) array,
  add column show_lex_meaning_relation_source_lang_words boolean default true,
  add column show_meaning_relation_first_word_only boolean default true,
  add column show_meaning_relation_meaning_id boolean default true,
  add column show_meaning_relation_word_datasets boolean default true;
