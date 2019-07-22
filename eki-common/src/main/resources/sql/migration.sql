-- 10.07.19
alter table dataset add column type varchar(10);
-- FIXME must be not null!!

-- 15.07.19
alter table dataset_permission add column is_last_chosen boolean default false;
-- FIXME use eki_user_profile instead

-- 19.7.19
insert into label_type (code, value) values ('iso2', 'iso2');
-- delete from language_label;
-- insert all language_label rows from classifier-main.sql

-- 22.07.19
update dataset set type = 'TERM' where type is null;
alter table dataset alter column type set not null;
