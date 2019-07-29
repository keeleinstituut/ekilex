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

alter table dataset_permission drop column if exists is_last_chosen;

create table eki_user_profile
(
	id bigserial primary key,
	user_id bigint references eki_user(id) on delete cascade not null,
	recent_dataset_permission_id bigint references dataset_permission(id)
);
alter sequence eki_user_profile_id_seq restart with 10000;

create index eki_user_profile_user_id_idx on eki_user(id);
create index eki_user_profile_recent_dataset_permission_id_idx on dataset_permission(id);

insert into eki_user_profile (user_id) (select eki_user.id from eki_user);

-- 25.07.19
alter table lexeme add column complexity varchar(100) null;
update lexeme set complexity = 'SIMPLE' where dataset_code = 'psv';
update lexeme set complexity = 'DETAIL' where dataset_code in ('ss1', 'qq2', 'ev2');
update lexeme set complexity = 'DEFAULT' where complexity is null;
alter table lexeme alter column complexity set not null;

-- 29.07.19
alter table process_log add column comment_prese text null;