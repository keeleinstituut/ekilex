-- Mitmekeelse sõnakogu kitsamad-laiemad tähendused
insert into definition_type (code, datasets) values ('kitsam/laiem tähendus teises keeles', '{}');

insert into meaning_rel_type (code, datasets) values ('kitsam', '{}');
insert into meaning_rel_type (code, datasets) values ('laiem', '{}');

insert into meaning_rel_mapping (code1, code2) values ('kitsam', 'laiem');
insert into meaning_rel_mapping (code1, code2) values ('laiem', 'kitsam');

delete from activity_log where funct_name = 'createSynCandidateWordRelation';

-- Aegunud detailsuse tüüpide muutmine
update definition set complexity = 'SIMPLE' where complexity in ('SIMPLE1', 'SIMPLE2');
update definition set complexity = 'DETAIL' where complexity in ('DETAIL1', 'DETAIL2');

update freeform set complexity = 'SIMPLE' where complexity in ('SIMPLE1', 'SIMPLE2');
update freeform set complexity = 'DETAIL' where complexity in ('DETAIL1', 'DETAIL2');
