-- always run this after full import
insert into eki_user_profile (user_id) (select eki_user.id from eki_user);

-- 02.09.2019
alter table lexeme add column type varchar(50);

-- this can be executed after the type introducing implementation is finished
update lexeme set type = 'PRIMARY' where type is null;

-- TODO change lexeme.type not nullable