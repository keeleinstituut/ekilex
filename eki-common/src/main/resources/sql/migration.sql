-- always run this after full import
insert into eki_user_profile (user_id) (select eki_user.id from eki_user);

-- 14.08.2019
alter table eki_user_profile add column selected_datasets varchar(10) array;
alter table eki_user_profile rename selected_datasets to preferred_datasets;