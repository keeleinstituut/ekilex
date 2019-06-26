alter table dataset add column is_visible boolean default true;
update dataset set is_visible = false where code = 'mab';

alter table eki_user add column recovery_key varchar(60);

-- 20.6.19
create table word_relation_param
(
  id bigserial primary key,
  word_relation_id bigint references word_relation(id) on delete cascade not null,
  name text not null,
  value text not null
);

alter sequence word_relation_param_id_seq restart with 10000;

create index word_relation_param_word_relation_id_idx on word_relation_param(word_relation_id);

alter table word_relation add relation_status varchar(100);

-- 21.06.19

alter table freeform add column complexity varchar(100);
update freeform set complexity = 'DEFAULT';

alter table definition add column complexity varchar(100);
update definition set complexity = 'DEFAULT';
alter table definition alter column complexity set not null;
