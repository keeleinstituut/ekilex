-- Mitmekeelse sõnakogu kitsamad-laiemad tähendused
insert into definition_type (code, datasets) values ('kitsam/laiem tähendus teises keeles', '{}');

insert into definition_type_label (code, value, lang, type) values ('kitsam/laiem tähendus teises keeles', 'kitsam/laiem tähendus teises keeles', 'est', 'descrip');

insert into meaning_rel_type (code, datasets) values ('kitsam', '{}');
insert into meaning_rel_type (code, datasets) values ('laiem', '{}');

insert into meaning_rel_type_label (code, value, lang, type) values ('kitsam', 'kitsam', 'est', 'descrip');
insert into meaning_rel_type_label (code, value, lang, type) values ('kitsam', 'kitsam', 'est', 'wordweb');
insert into meaning_rel_type_label (code, value, lang, type) values ('laiem', 'laiem', 'est', 'descrip');
insert into meaning_rel_type_label (code, value, lang, type) values ('laiem', 'laiem', 'est', 'wordweb');

insert into meaning_rel_mapping (code1, code2) values ('kitsam', 'laiem');
insert into meaning_rel_mapping (code1, code2) values ('laiem', 'kitsam');

delete from activity_log where funct_name = 'createSynCandidateWordRelation';

-- Aegunud detailsuse tüüpide muutmine
update definition set complexity = 'SIMPLE' where complexity in ('SIMPLE1', 'SIMPLE2');
update definition set complexity = 'DETAIL' where complexity in ('DETAIL1', 'DETAIL2');

update freeform set complexity = 'SIMPLE' where complexity in ('SIMPLE1', 'SIMPLE2');
update freeform set complexity = 'DETAIL' where complexity in ('DETAIL1', 'DETAIL2');

-- Kahepoolsete tähenduste seoste mapping
insert into meaning_rel_mapping (code1, code2) values ('soomõiste', 'liigimõiste');
insert into meaning_rel_mapping (code1, code2) values ('liigimõiste', 'soomõiste');
insert into meaning_rel_mapping (code1, code2) values ('tervikumõiste', 'osamõiste');
insert into meaning_rel_mapping (code1, code2) values ('osamõiste', 'tervikumõiste');
