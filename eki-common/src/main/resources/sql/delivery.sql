-- upgrade from ver 1.11.0 to 1.12.0

update dataset set is_superior = true where code = 'sss';

alter table eki_user add column is_master boolean default false;