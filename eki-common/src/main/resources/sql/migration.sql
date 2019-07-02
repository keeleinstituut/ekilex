alter table dataset add column is_visible boolean default true;
update dataset set is_visible = false where code = 'mab';

alter table eki_user add column recovery_key varchar(60);

-- 20.6.19
create table word_relation_param
(
  id bigserial primary key,
  word_relation_id bigint references word_relation(id) on delete cascade not null,
  name text not null,
  value text not null
);

alter sequence word_relation_param_id_seq restart with 10000;

create index word_relation_param_word_relation_id_idx on word_relation_param(word_relation_id);

alter table word_relation add relation_status varchar(100);

-- 21.06.19

alter table freeform add column complexity varchar(100);
update freeform set complexity = 'DEFAULT';

alter table definition add column complexity varchar(100);
update definition set complexity = 'DEFAULT';
alter table definition alter column complexity set not null;

-- 01.07.19

insert into definition_type_label (code, value, lang, type) values ('määramata', '–', 'est', 'descrip');
insert into definition_type_label (code, value, lang, type) values ('definitsioon', 'definitsioon', 'est', 'descrip');
insert into definition_type_label (code, value, lang, type) values ('selgitus', 'selgitus', 'est', 'descrip');

create index word_word_type_word_id_idx on word_word_type(word_id);
create index word_word_type_idx on word_word_type(word_type_code);
create index freeform_value_text_lower_idx on freeform(lower(value_text));
create index freeform_lang_idx on freeform(lang);
create index freeform_source_link_name_idx on freeform_source_link(name);
create index freeform_source_link_name_lower_idx on freeform_source_link(lower(name));
create index freeform_source_link_value_idx on freeform_source_link(value);
create index freeform_source_link_value_lower_idx on freeform_source_link(lower(value));
create index definition_source_link_name_idx on definition_source_link(name);
create index definition_source_link_name_lower_idx on definition_source_link(lower(name));
create index definition_source_link_value_idx on definition_source_link(value);
create index definition_source_link_value_lower_idx on definition_source_link(lower(value));
create index lexeme_source_link_name_idx on lexeme_source_link(name);
create index lexeme_source_link_name_lower_idx on lexeme_source_link(lower(name));
create index lexeme_source_link_value_idx on lexeme_source_link(value);
create index lexeme_source_link_value_lower_idx on lexeme_source_link(lower(value));
create index definition_lang_idx on definition(lang);
