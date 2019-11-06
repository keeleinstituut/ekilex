-- 29.10.2019
create index lifecycle_log_event_by_lower_idx on lifecycle_log(lower(event_by));
create index lifecycle_log_event_on_ms_idx on lifecycle_log((date_part('epoch', event_on) * 1000));
create index freeform_complexity_idx on freeform(complexity);

-- 05.11.2019
insert into word_rel_mapping (code1, code2) values ('head', 'ühend');

create table word_freeform
(
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  freeform_id bigint references freeform(id) on delete cascade not null,
  order_by bigserial,
  unique(word_id, freeform_id)
);
alter sequence word_freeform_id_seq restart with 10000;

create index word_freeform_word_id_idx on word_freeform(word_id);
create index word_freeform_freeform_id_idx on word_freeform(freeform_id);