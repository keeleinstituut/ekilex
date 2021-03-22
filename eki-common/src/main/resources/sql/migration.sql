-- mõiste viimane muutmise ja kinnitamise kuupäev 
alter table meaning_last_activity_log add column type varchar(100);
update meaning_last_activity_log set type = 'EDIT';
alter table meaning_last_activity_log alter column type set not null;
alter table meaning_last_activity_log drop constraint meaning_last_activity_log_meaning_id_key;
alter table meaning_last_activity_log add constraint meaning_last_activity_log_meaning_id_type_key unique (meaning_id, type);

-- otsevaste seos
insert into lex_rel_type (code, datasets) values ('otse', '{}');
insert into lex_rel_type_label (code, value, lang, type) values ('otse', 'otsevaste', 'est', 'descrip');
insert into lex_rel_type_label (code, value, lang, type) values ('otse', 'otsevaste', 'est', 'wordweb');
insert into lex_rel_mapping (code1, code2) values ('otse', 'otse');

