-- 14.08.2019
alter table eki_user_profile add column selected_datasets varchar(10) array;
alter table eki_user_profile rename selected_datasets to preferred_datasets;