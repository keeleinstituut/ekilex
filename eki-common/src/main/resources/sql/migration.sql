
create index activity_entity_name_owner_name_event_on_idx on activity_log(entity_name, owner_name, (date_part('epoch', event_on) * 1000));
