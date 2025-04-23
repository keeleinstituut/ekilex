
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

-- ÕS liitsõnaseosed ??

insert into word_rel_type (code, datasets) values ('ls-esiosaga', '{}');
insert into word_rel_type (code, datasets) values ('ls-järelosaga', '{}');
insert into word_rel_type_label (code, value, lang, type) values ('ls-esiosaga', 'liitsõna esiosaga', 'est', 'descrip');
insert into word_rel_type_label (code, value, lang, type) values ('ls-järelosaga', 'liitsõna järelosaga', 'est', 'descrip');
insert into word_rel_mapping (code1, code2) values ('ls-esiosa', 'ls-esiosaga');
insert into word_rel_mapping (code1, code2) values ('ls-järelosa', 'ls-järelosaga');

