-- 10.07.19
alter table dataset add column type varchar(10);

-- 15.07.19
alter table dataset_permission add column is_last_chosen boolean default false;

-- 19.7.19
insert into label_type (code, value) values ('iso2', 'iso2');
-- delete from language_label;
-- insert all language_label rows from classifier-main.sql