-- always run this after full reload

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
('sss',10001,'CRUD','DATASET'),
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
(select distinct on (ff.value_text, meaning_id) mff.meaning_id, ff.value_text
from meaning_freeform mff, freeform ff
where mff.freeform_id = ff.id
  and ff.type = 'SEMANTIC_TYPE');
