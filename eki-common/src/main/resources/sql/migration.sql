
------------------------------------------------
-- osasünode tähendusseosteks konverteerimine --
------------------------------------------------

-- eeldused
insert into meaning_rel_type (code, datasets) values ('sarnane', '{}');
insert into meaning_rel_mapping (code1, code2) values ('sarnane', 'sarnane');
insert into meaning_rel_type_label (code, value, lang, type)  values ('sarnane', 'sarnane', 'est', 'descrip');
insert into tag (name) values ('süno migra');
alter table meaning_relation add column weight numeric(5,4);

-- konverteerimise protseduur tuleb siia...


