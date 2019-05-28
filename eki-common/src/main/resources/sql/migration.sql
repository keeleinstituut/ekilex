alter table dataset add column is_visible boolean default true;
update dataset set is_visible = false where code = 'mab';

alter table eki_user add column recovery_key varchar(60);