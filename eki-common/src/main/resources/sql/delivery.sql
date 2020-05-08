-- upgrade from ver 1.16.0 to 1.17.0

create index collocation_value_idx on collocation(value);
analyze collocation;

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

create index dataset_perm_dataset_full_cmplx_idx on dataset_permission(user_id, auth_operation, auth_item, dataset_code, auth_lang);
analyze dataset_permission;

create type type_classifier as (name varchar(100), code varchar(100), value text);

alter sequence form_order_by_seq owned by form.order_by;

-- vene ilmikute detailsuse 1. muudatus
update lexeme l
set complexity = 'SIMPLE'
where l.dataset_code = 'sss'
  and l.type = 'PRIMARY'
  and l.complexity like 'DETAIL%'
  and exists(select w.id
             from word w
             where w.id = l.word_id
               and w.lang = 'rus')
  and exists(select l2.id
             from lexeme l2
             where l2.meaning_id = l.meaning_id
               and l2.dataset_code = 'sss'
               and l2.type = 'PRIMARY'
               and l2.complexity in ('SIMPLE', 'SIMPLE1')
               and exists(select w2.id
                          from word w2
                          where w2.id = l2.word_id
                            and w2.lang = 'est'));

-- vene ilmikute detailsuse 2. muudatus
update lexeme l
set complexity = 'DETAIL'
where l.dataset_code = 'sss'
  and l.type = 'PRIMARY'
  and l.complexity like 'SIMPLE%'
  and exists(select w.id
             from word w
             where w.id = l.word_id
               and w.lang = 'rus')
  and not exists(select l2.id
                 from lexeme l2
                 where l2.meaning_id = l.meaning_id
                   and l2.dataset_code = 'sss'
                   and l2.type = 'PRIMARY'
                   and l2.complexity in ('SIMPLE', 'SIMPLE1')
                   and exists(select w2.id
                              from word w2
                              where w2.id = l2.word_id
                                and w2.lang = 'est'));

-- qq definitsioonide kustutamine
delete
from definition d using definition_dataset dd
where d.complexity = 'SIMPLE2'
  and d.lang = 'est'
  and dd.definition_id = d.id
  and dd.dataset_code = 'sss'
  and exists(select d2.id
             from definition d2,
                  definition_dataset dd2
             where d2.meaning_id = d.meaning_id
               and d2.id != d.id
               and d2.complexity != 'SIMPLE2'
               and d2.lang = 'est'
               and dd2.definition_id = d2.id
               and dd2.dataset_code = 'sss');
