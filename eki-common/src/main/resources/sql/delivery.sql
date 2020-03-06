-- upgrade from ver 1.14.0 to 1.15.0
alter table eki_user_profile add column preferred_layer_name varchar(100) null;