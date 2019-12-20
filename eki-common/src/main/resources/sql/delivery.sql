-- upgrade from ver 1.10.0 to 1.11.0

alter table lexeme add column weight numeric(5,4) default 1;
alter table word_relation_param alter column value type numeric(5,4) using value::numeric(5,4);

delete from word_relation_param wrp using word_relation_param wrp2
where wrp.word_relation_id = wrp2.word_relation_id
  and wrp.name = wrp2.name
  and wrp.id < wrp2.id;

alter table word_relation_param add unique (word_relation_id, name);

insert into meaning_rel_type (code, datasets) values ('duplikaadikandidaat', '{}');
insert into meaning_rel_type_label (code, value, lang, type) values ('duplikaadikandidaat', 'duplikaadikandidaat', 'est', 'descrip');
insert into meaning_rel_mapping (code1, code2) values ('duplikaadikandidaat', 'duplikaadikandidaat');

insert into semantic_type (code, datasets) values ('grupp', '{}');
insert into semantic_type_label (code, value, lang, type) values ('grupp', 'grupp, rühm (nt kari, kamp)', 'est', 'descrip');
insert into semantic_type_label (code, value, lang, type) values ('grupp', 'group', 'eng', 'descrip');