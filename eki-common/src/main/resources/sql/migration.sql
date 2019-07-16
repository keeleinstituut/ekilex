-- 10.07.19
alter table dataset add column type varchar(10);

-- 15.07.19
alter table dataset_permission add column is_last_chosen boolean default false;