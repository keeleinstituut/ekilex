-- 29.10.2019
create index lifecycle_log_event_by_lower_idx on lifecycle_log(lower(event_by));
create index lifecycle_log_event_on_ms_idx on lifecycle_log((date_part('epoch', event_on) * 1000));
create index freeform_complexity_idx on freeform(complexity);

-- 05.11.2019
insert into word_rel_mapping (code1, code2) values ('head', 'ühend');