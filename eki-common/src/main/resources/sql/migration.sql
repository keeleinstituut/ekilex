
-- ÕS kasutusnäited ja lühimorfo

create table word_od_usage (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null, 
  value_prese text not null,
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null,
  order_by bigserial
);
alter sequence word_od_usage_id_seq restart with 10000;

create index word_od_usage_word_id_idx on word_od_usage(word_id);
create index word_od_usage_value_idx on word_od_usage(value);
create index word_od_usage_value_lower_idx on word_od_usage(lower(value));

create table word_od_morph (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null, 
  value_prese text not null,
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null,
  unique(word_id)
);
alter sequence word_od_morph_id_seq restart with 10000;

create index word_od_morph_word_id_idx on word_od_morph(word_id);
create index word_od_morph_value_idx on word_od_morph(value);
create index word_od_morph_value_lower_idx on word_od_morph(lower(value));

-- ÕS liitsõnaseosed

insert into word_rel_type (code, datasets) values ('ls-esiosaga', '{}');
insert into word_rel_type (code, datasets) values ('ls-järelosaga', '{}');
insert into word_rel_type_label (code, value, lang, type) values ('ls-esiosaga', 'Liitsõna esiosaga', 'est', 'descrip');
insert into word_rel_type_label (code, value, lang, type) values ('ls-järelosaga', 'Liitsõna järelosaga', 'est', 'descrip');
insert into word_rel_type_label (code, value, lang, type) values ('ls-esiosaga', 'Liitsõna esiosaga', 'est', 'wordweb');
insert into word_rel_type_label (code, value, lang, type) values ('ls-järelosaga', 'Liitsõna järelosaga', 'est', 'wordweb');
insert into word_rel_mapping (code1, code2) values ('ls-esiosa', 'ls-esiosaga');
insert into word_rel_mapping (code1, code2) values ('ls-järelosa', 'ls-järelosaga');

-- keelendite registreerimine

alter table word add column reg_year integer;
create index word_reg_year_idx on word(reg_year);

update word w
set reg_year = rw.reg_year
from (
	select
		l.word_id,
		right(lr.register_code, 4)::int reg_year
	from
		lexeme l,
		lexeme_register lr
	where
		lr.lexeme_id = l.id
		and lr.register_code like 'uus%'
	group by
		l.word_id,
		lr.register_code
) rw
where
	w.id = rw.word_id
;

insert into register (code, datasets) values ('uus', '{}');
insert into register_label (code, value, lang, type) values ('uus', 'uus', 'est', 'descrip');
insert into register_label (code, value, lang, type) values ('uus', 'uus', 'est', 'wordweb');
insert into register_label (code, value, lang, type) values ('uus', 'new', 'eng', 'descrip');
insert into register_label (code, value, lang, type) values ('uus', 'new', 'eng', 'wordweb');
insert into register_label (code, value, lang, type) values ('uus', 'новый', 'rus', 'descrip');
insert into register_label (code, value, lang, type) values ('uus', 'новый', 'rus', 'wordweb');

insert into lexeme_register (lexeme_id, register_code)
select
	l.id,
	'uus'
from
	lexeme l
where
	exists (
		select
			1
		from
			lexeme_register lr
		where
			lr.lexeme_id = l.id
			and lr.register_code like 'uus20%'
	)
;

delete from lexeme_register where register_code like 'uus20%';
delete from register where code like 'uus20%';




