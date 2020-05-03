-- muudab sellised detailsed ilmikud lihtsaks, millel leidub lihtsaid kasutusnäiteid
update lexeme l
set complexity = 'SIMPLE'
where l.dataset_code = 'sss'
  and l.type = 'PRIMARY'
  and l.complexity like 'DETAIL%'
  and exists(select w.id
             from word w
             where w.id = l.word_id
               and w.lang in ('est', 'rus'))
  and exists(select ff.id
             from freeform ff,
                  lexeme_freeform lff
             where lff.lexeme_id = l.id
               and lff.freeform_id = ff.id
               and ff.type = 'USAGE'
               and ff.complexity in ('SIMPLE', 'SIMPLE1'));

update freeform
set value_prese = value_prese || '.jpg'
where type = 'IMAGE_FILE'
  and value_prese not like '%.%';

update freeform
set value_text = value_text || '.jpg'
where type = 'IMAGE_FILE'
  and value_text not like '%.%';

-- basic stuff

drop index eki_user_profile_user_id_idx;
create index eki_user_profile_user_id_idx on eki_user_profile(user_id);

drop index eki_user_profile_recent_dataset_permission_id_idx;
create index eki_user_profile_recent_dataset_permission_id_idx on eki_user_profile(recent_dataset_permission_id);

drop index dataset_perm_dataset_code_idx;
drop index dataset_perm_user_id_idx;

create index dataset_perm_dataset_full_cmplx_idx on dataset_permission(user_id, auth_operation, auth_item, dataset_code, auth_lang);
analyze dataset_permission;

create type type_classifier as (name varchar(100), code varchar(100), value text);
