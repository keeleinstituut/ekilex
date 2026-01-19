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
