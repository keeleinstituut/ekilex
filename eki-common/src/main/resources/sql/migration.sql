-- fed term lisad
create type type_value_name_lang as (
	value_id bigint,
	value text,
	name text,
	lang char(3));

alter table dataset add column fed_term_collection_id varchar(100);
alter table dataset add column fed_term_domain_id varchar(100);

update dataset set fed_term_domain_id = '56' where code = 'ait';
update dataset set fed_term_domain_id = '3226' where code = 'avt';
update dataset set fed_term_domain_id = '1631' where code = 'aso';
update dataset set fed_term_domain_id = '3611' where code = 'arh';
update dataset set fed_term_domain_id = '2831' where code = 'aos';
update dataset set fed_term_domain_id = '3606' where code = 'bks';
update dataset set fed_term_domain_id = '3606' where code = 'bot';
update dataset set fed_term_domain_id = '2841' where code = 'ett';
update dataset set fed_term_domain_id = '12' where code = 'õtb';
update dataset set fed_term_domain_id = '2841' where code = 'spaa';
update dataset set fed_term_domain_id = '2841' where code = 'tot';
update dataset set fed_term_domain_id = '3236' where code = 'evkit';
update dataset set fed_term_domain_id = '2841' where code = 'vkm';
update dataset set fed_term_domain_id = '2831' where code = 'ehpr';
update dataset set fed_term_domain_id = '36' where code = 'est';
update dataset set fed_term_domain_id = '36' where code = 'est_test';
update dataset set fed_term_domain_id = '6826' where code = 'elteh';
update dataset set fed_term_domain_id = '3606' where code = 'ent';
update dataset set fed_term_domain_id = '3611' where code = 'ety';
update dataset set fed_term_domain_id = '3206' where code = 'cefr';
update dataset set fed_term_domain_id = '3206' where code = 'eõt';
update dataset set fed_term_domain_id = '3226' where code = 'fkm';
update dataset set fed_term_domain_id = '3611' where code = 'fil';
update dataset set fed_term_domain_id = '2831' where code = 'usk';
update dataset set fed_term_domain_id = '3611' where code = 'fon';
update dataset set fed_term_domain_id = '3606' where code = 'gen';
update dataset set fed_term_domain_id = '3606' where code = 'get';
update dataset set fed_term_domain_id = '3606' where code = 'gmt';
update dataset set fed_term_domain_id = '2841' where code = 'GER';
update dataset set fed_term_domain_id = '2841' where code = 'den';
update dataset set fed_term_domain_id = '2831' where code = 'ida';
update dataset set fed_term_domain_id = '3606' where code = 'iht';
update dataset set fed_term_domain_id = '2841' where code = 'imm';
update dataset set fed_term_domain_id = '3606' where code = 'kem';
update dataset set fed_term_domain_id = '3611' where code = 'kkt';
update dataset set fed_term_domain_id = '60' where code = 'kok';
update dataset set fed_term_domain_id = '36' where code = 'kool_KV';
update dataset set fed_term_domain_id = '3606' where code = 'kth';
update dataset set fed_term_domain_id = '6806' where code = 'kto';
update dataset set fed_term_domain_id = '2831' where code = 'LoB';
update dataset set fed_term_domain_id = '3236' where code = 'kfs';
update dataset set fed_term_domain_id = '3606' where code = 'lim';
update dataset set fed_term_domain_id = '5631' where code = 'lkt';
update dataset set fed_term_domain_id = '3606' where code = 'lon';
update dataset set fed_term_domain_id = '5631' where code = 'pre';
update dataset set fed_term_domain_id = '5631' where code = 'lpr';
update dataset set fed_term_domain_id = '2841' where code = 'lko';
update dataset set fed_term_domain_id = '6411' where code = 'mtr';
update dataset set fed_term_domain_id = '2841' where code = 'mef';
update dataset set fed_term_domain_id = '56' where code = 'mes';
update dataset set fed_term_domain_id = '6816' where code = 'met';
update dataset set fed_term_domain_id = '3611' where code = 'mtkl';
update dataset set fed_term_domain_id = '3611' where code = 'mea';
update dataset set fed_term_domain_id = '821' where code = 'mil';
update dataset set fed_term_domain_id = '3206' where code = 'mon';
update dataset set fed_term_domain_id = '2831' where code = 'mut';
update dataset set fed_term_domain_id = '2841' where code = 'mte';
update dataset set fed_term_domain_id = '5211' where code = 'nht';
update dataset set fed_term_domain_id = '2841' where code = 'nems';
update dataset set fed_term_domain_id = '3611' where code = 'oos';
update dataset set fed_term_domain_id = '4021' where code = 'org';
update dataset set fed_term_domain_id = '3606' where code = 'prs';
update dataset set fed_term_domain_id = '2841' where code = 'pot';
update dataset set fed_term_domain_id = '406' where code = 'pol';
update dataset set fed_term_domain_id = '4021' where code = 'p3m';
update dataset set fed_term_domain_id = '2826' where code = 'pur';
update dataset set fed_term_domain_id = '56' where code = 'plt';
update dataset set fed_term_domain_id = '3606' where code = 'rmtk';
update dataset set fed_term_domain_id = '2841' where code = 'rtrv';
update dataset set fed_term_domain_id = '3606' where code = 'rkb';
update dataset set fed_term_domain_id = '3236' where code = 'rob';
update dataset set fed_term_domain_id = '2846' where code = 'plan';
update dataset set fed_term_domain_id = '3611' where code = 'sem';
update dataset set fed_term_domain_id = '2831' where code = 'sarh';
update dataset set fed_term_domain_id = '431' where code = 'sisek';
update dataset set fed_term_domain_id = '2841' where code = 'skt';
update dataset set fed_term_domain_id = '28' where code = 'sup';
update dataset set fed_term_domain_id = '2831' where code = 'tet';
update dataset set fed_term_domain_id = '4806' where code = 'TH';
update dataset set fed_term_domain_id = '20' where code = 'tee';
update dataset set fed_term_domain_id = '6841' where code = 'teks';
update dataset set fed_term_domain_id = '2841' where code = 'terv';
update dataset set fed_term_domain_id = '5606' where code = 'to';
update dataset set fed_term_domain_id = '6411' where code = 'tts';
update dataset set fed_term_domain_id = '6621' where code = 'nuk';
update dataset set fed_term_domain_id = '2831' where code = 'tnpF';
update dataset set fed_term_domain_id = '2831' where code = 'ust';
update dataset set fed_term_domain_id = '2831' where code = 'usu';
update dataset set fed_term_domain_id = '6826' where code = 'valgus';
update dataset set fed_term_domain_id = '5631' where code = 'vlk';
update dataset set fed_term_domain_id = '2826' where code = 'vibu';

-- term kirjete detailsus
update freeform
set complexity = 'DETAIL'
where id in (select f.id
             from freeform f,
                  meaning_freeform mf,
                  lexeme l,
                  dataset d
             where f.type in ('IMAGE_FILE', 'MEDIA_FILE')
               and (f.complexity = 'SIMPLE' or f.complexity is null)
               and mf.freeform_id = f.id
               and l.meaning_id = mf.meaning_id
               and l.dataset_code = d.code
               and d.type = 'TERM');
