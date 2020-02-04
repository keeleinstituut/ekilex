-- 04.02.2020
alter table eki_user_profile add column terms_ver varchar(100) null;

-- ekilexis ühekordselt:
create extension unaccent;

-- wordwebis ühekordselt:
create extension pg_trgm;
create extension fuzzystrmatch;

-- kuni siiani testis 04.02.2020

