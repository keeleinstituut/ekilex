-- always run this after full import
insert into eki_user_profile (user_id) (select eki_user.id from eki_user);
