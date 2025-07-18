-- kollokatsioonide poolt hõivatud määramata morfo vormide vabastamine

-- TODO kahtlane, ootab
-- insert into morph (code, datasets, order_by) values ('!!', '{}', 999999);
-- insert into morph_label (code, value, lang, type) values ('!!', 'kollide teadmatu', 'est', 'comment');

-- detailsuse likvideerimine süsteemist

alter table meaning_note drop column complexity cascade;
alter table meaning_image drop column complexity cascade;
alter table meaning_media drop column complexity cascade;
alter table definition drop column complexity cascade;
alter table lexeme drop column complexity cascade;
alter table grammar drop column complexity cascade;
alter table government drop column complexity cascade;
alter table usage drop column complexity cascade;
alter table lexeme_note drop column complexity cascade;
alter table collocation drop column complexity cascade;
