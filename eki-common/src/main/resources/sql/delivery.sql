-- upgrade from ver 1.12.0 to 1.13.0

alter table eki_user_profile add column terms_ver varchar(100) null;