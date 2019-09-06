-- always run this after full import
insert into eki_user_profile (user_id) (select eki_user.id from eki_user);

-- 02.09.2019
alter table lexeme add column type varchar(50);
update lexeme set type = 'PRIMARY';