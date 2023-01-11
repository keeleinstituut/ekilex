-- Mitmekeelse sõnakogu kitsamad-laiemad tähendused
insert into definition_type (code, datasets) values ('kitsam/laiem tähendus teises keeles', '{}');

insert into meaning_rel_type (code, datasets) values ('kitsam', '{}');
insert into meaning_rel_type (code, datasets) values ('laiem', '{}');

insert into meaning_rel_mapping (code1, code2) values ('kitsam', 'laiem');
insert into meaning_rel_mapping (code1, code2) values ('laiem', 'kitsam');
