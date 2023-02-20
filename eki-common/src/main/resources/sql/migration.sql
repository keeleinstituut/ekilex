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

-- Sõnakogu õiguste taotlemine
alter table eki_user_application rename to eki_user_application_deprecated;
alter sequence eki_user_application_id_seq rename to eki_user_application_deprecated_id_seq;

create table eki_user_application
(
  id bigserial primary key,
  user_id bigint references eki_user(id) on delete cascade not null,
  dataset_code varchar(10) references dataset(code) on update cascade on delete cascade not null,
  auth_operation varchar(100) not null,
  lang char(3) references language(code) null,
  comment text null,
  status varchar(10) not null,
  created timestamp not null default statement_timestamp()
);
alter sequence eki_user_application_id_seq restart with 10000;

insert into eki_user_application(user_id, dataset_code, auth_operation, lang, comment, status)
select euad.user_id, unnest(euad.datasets), 'CRUD', null, euad.comment, 'NEW'
from eki_user_application_deprecated euad
where euad.is_reviewed = false
order by euad.created;

drop table eki_user_application_deprecated;

-- Osasüno kaal
update meaning_relation set weight = 0 where meaning_rel_type_code = 'sarnane' and weight is null;