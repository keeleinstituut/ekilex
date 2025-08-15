-- kollokatsioonide poolt hõivatud määramata morfo vormide vabastamine

-- TODO kahtlane, ootab
-- insert into morph (code, datasets, order_by) values ('!!', '{}', 999999);
-- insert into morph_label (code, value, lang, type) values ('!!', 'kollide teadmatu', 'est', 'comment');

-- detailsuse likvideerimine süsteemist

alter table meaning_note drop column complexity cascade;
alter table meaning_image drop column complexity cascade;
alter table meaning_media drop column complexity cascade;
alter table definition drop column complexity cascade;
alter table lexeme drop column complexity cascade;
alter table grammar drop column complexity cascade;
alter table government drop column complexity cascade;
alter table usage drop column complexity cascade;
alter table lexeme_note drop column complexity cascade;
alter table collocation drop column complexity cascade;

-- ÕS oma klassif väärtuste liik

insert into label_type (code, value) values ('od', 'od');

-- ÕS soovituse lisavälja otsing

create index word_od_recommendation_opt_value_idx on word_od_recommendation(opt_value);
create index word_od_recommendation_opt_value_lower_idx on word_od_recommendation(lower(opt_value));

-- ebastandardsete tühikute asendamine tekstides

update word w
set 
	value = replace(w.value, chr(160), ' '),
	value_prese = replace(w.value_prese, chr(160), ' ')
where
	w.value like '%' || chr(160) || '%'
;

update definition d
set 
	value = replace(d.value, chr(160), ' '),
	value_prese = replace(d.value_prese, chr(160), ' ')
where
	d.value like '%' || chr(160) || '%'
;

update usage u
set 
	value = replace(u.value, chr(160), ' '),
	value_prese = replace(u.value_prese, chr(160), ' ')
where
	u.value like '%' || chr(160) || '%'
;

update source s
set 
	value = replace(s.value, chr(160), ' '),
	value_prese = replace(s.value_prese, chr(160), ' ')
where
	s.value like '%' || chr(160) || '%'
;

update freeform f
set 
	value = replace(f.value, chr(160), ' '),
	value_prese = replace(f.value_prese, chr(160), ' ')
where
	f.value like '%' || chr(160) || '%'
;

update lexeme_note n
set 
	value = replace(n.value, chr(160), ' '),
	value_prese = replace(n.value_prese, chr(160), ' ')
where
	n.value like '%' || chr(160) || '%'
;

update meaning_note n
set 
	value = replace(n.value, chr(160), ' '),
	value_prese = replace(n.value_prese, chr(160), ' ')
where
	n.value like '%' || chr(160) || '%'
;

-- ÕS klassif väärtuste loomine

delete
from
	word_type_label
where
	type = 'od'
;

insert into word_type_label 
	(code, value, lang, type)
values
	('l', 'LÜHEND', 'est', 'od'),
	('lz', 'LÜHEND', 'est', 'od'),
	('tn', 'TÄHENIMI', 'est', 'od'),
	('th', 'TÄHIS', 'est', 'od');
	
delete
from
	register_label 
where
	type = 'od'
;

insert into register_label 
	(code, value, lang, type)
values
	('aja', 'AJALOOLINE', 'est', 'od'),
	('am', 'AMETLIK', 'est', 'od'),
	('hlv', 'HALVUSTAV', 'est', 'od'),
	('hrv', 'HARV', 'est', 'od'),
	('kõnek', 'KÕNEKEELNE', 'est', 'od'),
	('lastek', 'LASTEKEELNE', 'est', 'od'),
	('luulek', 'LUULES', 'est', 'od'),
	('mta', 'MITTEAMETLIK', 'est', 'od'),
	('murd', 'MURDES', 'est', 'od'),
	('piib', 'PIIBLIS', 'est', 'od'),
	('rhv', 'RAHVAKEELNE', 'est', 'od'),
	('sti', 'STIILITUNDLIK', 'est', 'od'),
	('unar', 'UNARSÕNA', 'est', 'od'),
	('van', 'VANANENUD', 'est', 'od');

delete
from
	display_morph_label 
where
	type = 'od'
;

insert into display_morph_label 
	(code, value, lang, type)
values
	('pl', 'mitmus', 'est', 'od'),
	('plt', 'mitmus', 'est', 'od'),
	('sgt', 'ainsus', 'est', 'od'),
	('sga', 'ainsus', 'est', 'od'),
	('keskv', 'keskvõrre', 'est', 'od'),
	('üliv', 'ülivõrre', 'est', 'od');

delete
from
	value_state_label 
where
	type = 'od'
;

insert into value_state_label 
	(code, value, lang, type)
values
	('eelistatud', 'EELISTATUD', 'est', 'od'),
	('mööndav', 'MÖÖNDAV', 'est', 'od'),
	('endine', 'ENDINE', 'est', 'od');



