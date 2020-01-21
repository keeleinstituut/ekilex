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

update lifecycle_log set event_by = 'Ekilex faililaadur' where event_by = 'Ekilex importer';

-- ekilex:
drop view if exists view_ww_lexeme;
drop view if exists view_ww_word;
drop type if exists type_meaning_word;
-- NB! restore type and views

-- wordweb:
-- NB! recreate types and views

alter table eki_user_profile
add column preferred_biling_candidate_langs char(3) array,
add column preferred_biling_lex_meaning_word_langs char(3) array;

update lexeme
set order_by = l.ev_qq_order_by
from (select l1.id lexeme_id, (array_agg(l2.order_by order by l2.dataset_code))[1] ev_qq_order_by
      from lexeme l1,
           lexeme l2
      where l1.dataset_code = 'sss'
        and l2.dataset_code in ('ev2', 'qq2')
        and l1.word_id = l2.word_id
        and l1.meaning_id = l2.meaning_id
      group by l1.word_id,
               l1.meaning_id,
               l1.id) l
where lexeme.id = l.lexeme_id;

alter table dataset add column is_superior boolean default false;
update dataset set is_superior = true where code = 'sss';