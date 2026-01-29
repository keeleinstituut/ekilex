-- #1 --

alter table value_state drop column datasets cascade;
alter table government_type drop column datasets cascade;
alter table register drop column datasets cascade;
alter table semantic_type drop column datasets cascade;
alter table word_type drop column datasets cascade;
alter table aspect drop column datasets cascade;
alter table gender drop column datasets cascade;
alter table pos drop column datasets cascade;
alter table pos_group drop column datasets cascade;
alter table rel_group drop column datasets cascade;
alter table morph drop column datasets cascade;
alter table display_morph drop column datasets cascade;
alter table deriv drop column datasets cascade;
alter table lex_rel_type drop column datasets cascade;
alter table word_rel_type drop column datasets cascade;
alter table meaning_rel_type drop column datasets cascade;
alter table usage_type drop column datasets cascade;
alter table etymology_type drop column datasets cascade;
alter table definition_type drop column datasets cascade;
alter table region drop column datasets cascade;
alter table proficiency_level drop column datasets cascade;
alter table freeform_type drop column datasets cascade;

-- #2 --

create table variant_type (
	code varchar(100) primary key,
	order_by bigserial
);

create table variant_type_label (
	code varchar(100) references variant_type(code) on delete cascade not null,
	value text not null,
	lang char(3) references language(code) not null,
	type varchar(10) references label_type(code) not null,
	unique(code, lang, type)
);

insert into variant_type (code) values ('latinisatsioon');
insert into variant_type (code) values ('eesti transkriptsioon');
insert into variant_type (code) values ('inglise transkriptsioon');
insert into variant_type (code) values ('vana kirjaviis');

insert into variant_type_label (code, value, lang, type) values ('latinisatsioon', 'latinisatsioon', 'est', 'descrip');
insert into variant_type_label (code, value, lang, type) values ('eesti transkriptsioon', 'eesti transkriptsioon', 'est', 'descrip');
insert into variant_type_label (code, value, lang, type) values ('inglise transkriptsioon', 'inglise transkriptsioon', 'est', 'descrip');
insert into variant_type_label (code, value, lang, type) values ('vana kirjaviis', 'vana kirjaviis', 'est', 'descrip');

create table lexeme_variant (
	id bigserial primary key,
	lexeme_id bigint references lexeme(id) on delete cascade not null,
	variant_lexeme_id bigint references lexeme(id) on delete cascade not null,
	variant_type_code varchar(100) references variant_type(code) on delete cascade,
	order_by bigserial,
	unique(lexeme_id, variant_lexeme_id)
);
alter sequence lexeme_variant_id_seq restart with 10000;

create index lexeme_variant_lexeme_id_idx on lexeme_variant(lexeme_id);
create index lexeme_variant_variant_lexeme_id_idx on lexeme_variant(variant_lexeme_id);
create index lexeme_variant_variant_type_code_idx on lexeme_variant(variant_type_code);

-- #3 --

drop type type_term_meaning_word cascade;




