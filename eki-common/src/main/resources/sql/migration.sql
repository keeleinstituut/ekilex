-- 18.12.2019
alter table lexeme add column weight numeric(5,4) default 1;
alter table word_relation_param alter column value type numeric(5,4) using value::numeric(5,4);

delete from word_relation_param wrp using word_relation_param wrp2
where wrp.word_relation_id = wrp2.word_relation_id
  and wrp.name = wrp2.name
  and wrp.id < wrp2.id;

alter table word_relation_param add unique (word_relation_id, name);

-- 19.12.2019
insert into meaning_rel_type (code, datasets) values ('duplikaadikandidaat', '{}');
insert into meaning_rel_type_label (code, value, lang, type) values ('duplikaadikandidaat', 'duplikaadikandidaat', 'est', 'descrip');
insert into meaning_rel_mapping (code1, code2) values ('duplikaadikandidaat', 'duplikaadikandidaat');

-- 20.12.2019
insert into semantic_type (code, datasets) values ('grupp', '{}');
insert into semantic_type_label (code, value, lang, type) values ('grupp', 'grupp, rühm (nt kari, kamp)', 'est', 'descrip');
insert into semantic_type_label (code, value, lang, type) values ('grupp', 'group', 'eng', 'descrip');

-- 30.12.2019
update lifecycle_log set event_by = 'Ekilex faililaadur' where event_by = 'Ekilex importer';

-- 03.01.2020
-- ekilex:
drop view if exists view_ww_lexeme;
drop view if exists view_ww_word;
drop type if exists type_meaning_word;
-- NB! restore type and views

-- wordweb - recreate types and views

alter table eki_user_profile
add column preferred_biling_candidate_langs char(3) array,
add column preferred_biling_lex_meaning_word_langs char(3) array;

alter table dataset add column is_superior boolean default false;
update dataset set is_superior = true where code = 'sss';

--> kuni siiani testis olemas 21.01.2020