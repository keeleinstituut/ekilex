-- upgrade from ver 1.10.0 to 1.11.0

-- at ekilex, drop all types and views for wordweb 
drop view if exists view_ww_word_search;
drop view if exists view_ww_word;
drop view if exists view_ww_as_word;--remove later
drop view if exists view_ww_form;
drop view if exists view_ww_meaning;
drop view if exists view_ww_lexeme;
drop view if exists view_ww_collocation;
drop view if exists view_ww_classifier;
drop view if exists view_ww_dataset;
drop view if exists view_ww_word_etymology;
drop view if exists view_ww_word_relation;
drop view if exists view_ww_lexeme_relation;
drop view if exists view_ww_meaning_relation;
drop view if exists view_ww_lexical_decision_data;
drop view if exists view_ww_similarity_judgement_data;
drop type if exists type_public_note;
drop type if exists type_meaning_word;
drop type if exists type_grammar;
drop type if exists type_government;
drop type if exists type_lang_complexity;
drop type if exists type_word;--remove later
drop type if exists type_definition;
drop type if exists type_domain;
drop type if exists type_usage;
drop type if exists type_source_link;
drop type if exists type_colloc_member;
drop type if exists type_word_etym_relation;
drop type if exists type_word_relation;
drop type if exists type_lexeme_relation;
drop type if exists type_meaning_relation;

alter table lexeme add column weight numeric(5,4) default 1;
update lexeme set weight = 0.89 where type = 'SECONDARY';
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

alter table eki_user_profile
add column preferred_biling_candidate_langs char(3) array,
add column preferred_biling_lex_meaning_word_langs char(3) array;

-- only pre meaning sum:
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

-- only pre meaning sum:
update lexeme l
   set order_by = nextval('lexeme_order_by_seq')
from (select l.id
      from lexeme l,
           word w
      where l.complexity = 'SIMPLE'
      and   l.word_id = w.id
      and   w.lang = 'rus'
      order by l.order_by) lqq
where l.id = lqq.id;

alter table dataset add column is_superior boolean default false;

-- recreate types and views:
-- at ekilex run create_views.sql 
-- at wordweb run create_mviews.sql  
