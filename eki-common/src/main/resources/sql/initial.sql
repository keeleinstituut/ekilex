-- always run this after full reload

alter sequence eki_user_id_seq restart with 10000;

insert into eki_user (name,email,password,is_admin,is_enabled)
values
('Admin','admin@nowhere.com','$2a$10$YCDhFLqRHPsxLKuCMXhmwOa00pq4PfgXdzMRMu.DTUVFoiADQyldG',true,true),
('Armas Tähetark','armas@nowhere.com','$2a$10$t8A6FTC2n5Q8dfshCKBe5e5vkqDcq722MKdCYwF/I.mQ3X7e6y5IK',false,true),
('Juhan Tuha','juhan.tuha@nowhere.com','$2a$10$2iA/zf7kj274RHffcXb4d.Xc4kjygM5LwIsHqULL9Cu98bqBRQKSa',false,null),
('Mats Kolu','mats.kolu@nowhere.com','$2a$10$c7zTq5ZEia9H6WILvcilJO6PHIClcW6yfRsPuTTMzFcFIuhNBfSRa',false,null),
('Siim Sünonüüm','syno.siim@nowhere.com','$2a$10$YCDhFLqRHPsxLKuCMXhmwOa00pq4PfgXdzMRMu.DTUVFoiADQyldG',false,true);

insert into dataset_permission (dataset_code,user_id,auth_operation,auth_item)
values
('ss1',10001,'CRUD','DATASET'),
('psv',10001,'CRUD','DATASET'),
('ety',10001,'CRUD','DATASET'),
('qq2',10001,'CRUD','DATASET'),
('ev2',10001,'CRUD','DATASET'),
('kol',10001,'CRUD','DATASET'),
('mab',10001,'CRUD','DATASET'),
('eki',10001,'CRUD','DATASET'),
('est',10001,'CRUD','DATASET'),
('mil',10001,'CRUD','DATASET'),
('aia',10001,'CRUD','DATASET'),
('ait',10001,'CRUD','DATASET'),
('avt',10001,'CRUD','DATASET'),
('aso',10001,'CRUD','DATASET'),
('arh',10001,'CRUD','DATASET'),
('aos',10001,'CRUD','DATASET'),
('den',10001,'CRUD','DATASET'),
('eõt',10001,'CRUD','DATASET'),
('ett',10001,'CRUD','DATASET'),
('õtb',10001,'CRUD','DATASET'),
('ent',10001,'CRUD','DATASET'),
('fil',10001,'CRUD','DATASET'),
('fkm',10001,'CRUD','DATASET'),
('fon',10001,'CRUD','DATASET'),
('gen',10001,'CRUD','DATASET'),
('get',10001,'CRUD','DATASET'),
('gmt',10001,'CRUD','DATASET'),
('ida',10001,'CRUD','DATASET'),
('iht',10001,'CRUD','DATASET'),
('imm',10001,'CRUD','DATASET'),
('bks',10001,'CRUD','DATASET'),
('kto',10001,'CRUD','DATASET'),
('kem',10001,'CRUD','DATASET'),
('kkt',10001,'CRUD','DATASET'),
('kok',10001,'CRUD','DATASET'),
('kfs',10001,'CRUD','DATASET'),
('lim',10001,'CRUD','DATASET'),
('lkt',10001,'CRUD','DATASET'),
('lon',10001,'CRUD','DATASET'),
('pre',10001,'CRUD','DATASET'),
('lpr',10001,'CRUD','DATASET'),
('lko',10001,'CRUD','DATASET'),
('les',10001,'CRUD','DATASET'),
('mef',10001,'CRUD','DATASET'),
('mes',10001,'CRUD','DATASET'),
('met',10001,'CRUD','DATASET'),
('mea',10001,'CRUD','DATASET'),
('mon',10001,'CRUD','DATASET'),
('mut',10001,'CRUD','DATASET'),
('mte',10001,'CRUD','DATASET'),
('mtr',10001,'CRUD','DATASET'),
('nht',10001,'CRUD','DATASET'),
('prs',10001,'CRUD','DATASET'),
('plt',10001,'CRUD','DATASET'),
('pot',10001,'CRUD','DATASET'),
('pol',10001,'CRUD','DATASET'),
('pur',10001,'CRUD','DATASET'),
('p3m',10001,'CRUD','DATASET'),
('rkb',10001,'CRUD','DATASET'),
('rob',10001,'CRUD','DATASET'),
('skt',10001,'CRUD','DATASET'),
('tee',10001,'CRUD','DATASET'),
('tts',10001,'CRUD','DATASET'),
('usk',10001,'CRUD','DATASET'),
('ust',10001,'CRUD','DATASET'),
('vlk',10001,'CRUD','DATASET');

insert into eki_user_profile (user_id) (select eki_user.id from eki_user);

insert into meaning_semantic_type (meaning_id, semantic_type_code)
(select distinct on (ff.value, meaning_id) mff.meaning_id, ff.value
from meaning_freeform mff, freeform ff
where mff.freeform_id = ff.id
  and ff.type = 'SEMANTIC_TYPE'
  and ff.value != '');

insert into word_rel_mapping (code1, code2) values ('posit', 'komp');
insert into word_rel_mapping (code1, code2) values ('posit', 'superl');
insert into word_rel_mapping (code1, code2) values ('deriv_base', 'deriv');
insert into word_rel_mapping (code1, code2) values ('komp', 'posit');
insert into word_rel_mapping (code1, code2) values ('komp', 'superl');
insert into word_rel_mapping (code1, code2) values ('superl', 'posit');
insert into word_rel_mapping (code1, code2) values ('superl', 'komp');
insert into word_rel_mapping (code1, code2) values ('deriv', 'deriv_base');
insert into word_rel_mapping (code1, code2) values ('ühend', 'head');
insert into word_rel_mapping (code1, code2) values ('head', 'ühend');
insert into word_rel_mapping (code1, code2) values ('raw', 'raw');
insert into lex_rel_mapping (code1, code2) values ('comp', 'head');
insert into lex_rel_mapping (code1, code2) values ('head', 'comp');
insert into lex_rel_mapping (code1, code2) values ('head', 'vor');
insert into lex_rel_mapping (code1, code2) values ('head', 'yvr');
insert into lex_rel_mapping (code1, code2) values ('head', 'pyh');
insert into lex_rel_mapping (code1, code2) values ('head', 'yhvt');
insert into lex_rel_mapping (code1, code2) values ('head', 'lyh');
insert into lex_rel_mapping (code1, code2) values ('head', 'sub_word');
insert into lex_rel_mapping (code1, code2) values ('vor', 'head');
insert into lex_rel_mapping (code1, code2) values ('yvr', 'head');
insert into lex_rel_mapping (code1, code2) values ('pyh', 'head');
insert into lex_rel_mapping (code1, code2) values ('yhvt', 'head');
insert into lex_rel_mapping (code1, code2) values ('tvt:vrd', 'tvt:vrd');
insert into lex_rel_mapping (code1, code2) values ('tvt:vt ka', 'tvt:vt ka');
insert into lex_rel_mapping (code1, code2) values ('yvt:vrd', 'yvt:vrd');
insert into lex_rel_mapping (code1, code2) values ('yvt:vt ka', 'yvt:vt ka');
insert into lex_rel_mapping (code1, code2) values ('yvt:NB', 'yvt:NB');
insert into lex_rel_mapping (code1, code2) values ('lyh', 'head');
insert into lex_rel_mapping (code1, code2) values ('sub_word', 'head');
insert into meaning_rel_mapping (code1, code2) values ('antonüüm', 'antonüüm');
insert into meaning_rel_mapping (code1, code2) values ('kaashüponüüm', 'kaashüponüüm');
insert into meaning_rel_mapping (code1, code2) values ('määramata', 'määramata');
insert into meaning_rel_mapping (code1, code2) values ('soomõiste', 'liigimõiste');
insert into meaning_rel_mapping (code1, code2) values ('liigimõiste', 'soomõiste');
insert into meaning_rel_mapping (code1, code2) values ('tervikumõiste', 'osamõiste');
insert into meaning_rel_mapping (code1, code2) values ('osamõiste', 'tervikumõiste');
insert into meaning_rel_mapping (code1, code2) values ('kaasalluv mõiste', 'kaasalluv mõiste');
insert into meaning_rel_mapping (code1, code2) values ('seotud mõiste', 'seotud mõiste');
insert into meaning_rel_mapping (code1, code2) values ('põhjus', 'tagajärg');
insert into meaning_rel_mapping (code1, code2) values ('tagajärg', 'põhjus');
insert into meaning_rel_mapping (code1, code2) values ('eelnev', 'järgnev');
insert into meaning_rel_mapping (code1, code2) values ('järgnev', 'eelnev');
insert into meaning_rel_mapping (code1, code2) values ('vastand', 'vastand');
insert into meaning_rel_mapping (code1, code2) values ('vahend', 'eesmärk');
insert into meaning_rel_mapping (code1, code2) values ('eesmärk', 'vahend');
insert into meaning_rel_mapping (code1, code2) values ('tegevus', 'tegija');
insert into meaning_rel_mapping (code1, code2) values ('tegija', 'tegevus');
insert into meaning_rel_mapping (code1, code2) values ('dimensioon', 'mõõtühik');
insert into meaning_rel_mapping (code1, code2) values ('mõõtühik', 'dimensioon');
insert into meaning_rel_mapping (code1, code2) values ('tootja', 'toode');
insert into meaning_rel_mapping (code1, code2) values ('toode', 'tootja');
insert into meaning_rel_mapping (code1, code2) values ('ülemmõiste', 'alammõiste');
insert into meaning_rel_mapping (code1, code2) values ('alammõiste', 'ülemmõiste');
insert into meaning_rel_mapping (code1, code2) values ('üldmõiste', 'ainikmõiste');
insert into meaning_rel_mapping (code1, code2) values ('ainikmõiste', 'üldmõiste');
insert into meaning_rel_mapping (code1, code2) values ('duplikaadikandidaat', 'duplikaadikandidaat');

do $$
declare
  lex_rel constant lex_rel_type.code%type := 'pyh';
  opposite_lex_rel constant lex_rel_type.code%type := 'head';
  word_rel constant word_rel_type.code%type := 'ühend';
  opposite_word_rel constant word_rel_type.code%type := 'head';
  rel_moved_counter integer := 0;
  opposite_rel_moved_counter integer := 0;
  word1_id word.id%type;
  word2_id word.id%type;
  lex_rel_row lex_relation%rowtype;
  opposite_lex_rel_id lex_relation.id%type;
begin
  for lex_rel_row in
    select * from lex_relation where lex_rel_type_code = lex_rel
    loop
      select lexeme.word_id into word1_id from lexeme where id = lex_rel_row.lexeme1_id;
      select lexeme.word_id into word2_id from lexeme where id = lex_rel_row.lexeme2_id;

      insert into word_relation (word1_id, word2_id, word_rel_type_code) values (word1_id, word2_id, word_rel) on conflict do nothing;
      delete from lex_relation where id = lex_rel_row.id;
      rel_moved_counter := rel_moved_counter + 1;

      select id into opposite_lex_rel_id from lex_relation where lexeme1_id = lex_rel_row.lexeme2_id and lexeme2_id = lex_rel_row.lexeme1_id and lex_rel_type_code = opposite_lex_rel;
      if opposite_lex_rel_id is not null then
        insert into word_relation (word1_id, word2_id, word_rel_type_code) values (word2_id, word1_id, opposite_word_rel) on conflict do nothing;
        delete from lex_relation where id = opposite_lex_rel_id;
        opposite_rel_moved_counter := opposite_rel_moved_counter + 1;
      end if;
    end loop;
  RAISE notice '% lexeme relations moved to word relations', rel_moved_counter;
  RAISE notice '% opposite lexeme relations moved to opposite word relations', opposite_rel_moved_counter;
end $$;

delete from lex_rel_mapping where code1 = 'pyh';
delete from lex_rel_mapping where code2 = 'pyh';
delete from lex_rel_type where code = 'pyh';


-- only pre meaning sum:
update lexeme
set order_by = l.ev_qq_order_by
from (select l1.id lexeme_id, (array_agg(l2.order_by order by l2.dataset_code))[1] ev_qq_order_by
      from lexeme l1,
           lexeme l2
      where l1.dataset_code = 'eki'
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

