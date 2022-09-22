-- keelendi ja tähenduse linkide formaat
update definition set value_prese = replace(value_prese, ' id=', ' data-link-id=') where value_prese like '%eki-link%';
update definition set value_prese = replace(value_prese, ' link-id=', ' data-link-id=') where value_prese like '%eki-link%';
update definition set value_prese = replace(value_prese, ' link-type=', ' data-link-type=') where value_prese like '%eki-link%';
update definition set value_prese = replace(value_prese, ' data-link-type=''meaning_link''', ' data-link-type="meaning"') where value_prese like '%eki-link%';
update definition set value_prese = replace(value_prese, ' data-link-type="meaning_link"', ' data-link-type="meaning"') where value_prese like '%eki-link%';

update freeform set value_prese = replace(value_prese, ' id=', ' data-link-id=') where value_prese like '%eki-link%' and value_prese like '%type="meaning"%';
update freeform set value_prese = replace(value_prese, ' link-type=', ' data-link-type=') where value_prese like '%eki-link%' and value_prese like '%type="meaning"%';
update freeform set value_prese = replace(value_prese, ' id=', ' data-link-id=') where value_prese like '%eki-link%' and value_prese like '%type="word"%';
update freeform set value_prese = replace(value_prese, ' link-type=', ' data-link-type=') where value_prese like '%eki-link%' and value_prese like '%type="word"%';

-- ilmiku ja kasutusnäite ÕS soovituste kustutamine
delete from freeform where type in ('OD_LEXEME_RECOMMENDATION', 'OD_USAGE_DEFINITION', 'OD_USAGE_ALTERNATIVE');
-- recreate ekilex views and types

-- vastete tähenduse seose tüübi mapping
insert into meaning_rel_mapping (code1, code2) values ('sarnane', 'sarnane');

-- igaöise homonüümide ühendaja kolimine baasi funktsioonist java teenusesse
drop function if exists merge_homonyms_to_eki(char(3) array);

-- postgres komplekstüübi siseses tekstimassiivis jutumärkide kodeerimine, et vältida teksti katkemist
create or replace function encode_text(initial_text text)
  returns text
  language plpgsql
  immutable
as $$
declare
  encoded_text text;
begin
  encoded_text = replace(initial_text, '"', 'U+0022');
  return encoded_text;
end $$;

-- tabelivaate agregeerimise minimaalsed andmetüübid
create type type_mt_definition as (
  definition_id bigint,
  definition_type_code varchar(100),
  value text,
  value_prese text,
  lang char(3),
  complexity varchar(100),
  is_public boolean
);

create type type_mt_lexeme as (
  lexeme_id bigint,  
  word_id bigint,
  meaning_id bigint,
  dataset_code varchar(10),
  is_public boolean
);

create type type_mt_word as (
  lexeme_id bigint,
  word_id bigint,
  value text,
  value_prese text,
  lang char(3),
  homonym_nr integer,
  display_morph_code varchar(100),
  gender_code varchar(100),
  aspect_code varchar(100),
  vocal_form text,
  morphophono_form text,
  manual_event_on timestamp
);

create type type_mt_lexeme_freeform as (
  lexeme_id bigint,
  freeform_id bigint,
  "type" varchar(100),
  value_text text,
  value_prese text,
  lang char(3),
  complexity varchar(100),
  is_public boolean,
  created_by text,
  created_on timestamp,
  modified_by text,
  modified_on timestamp
);

-- keelendi ja mõiste sisemärkus
create table word_forum
(
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null,
  value_prese text not null,
  creator_id bigint references eki_user(id) null,
  created_by text null,
  created_on timestamp null,
  modified_by text null,
  modified_on timestamp null,
  order_by bigserial
);
alter sequence word_forum_id_seq restart with 10000;

create table meaning_forum
(
  id bigserial primary key,
  meaning_id bigint references meaning(id) on delete cascade not null,
  value text not null,
  value_prese text not null,
  creator_id bigint references eki_user(id) null,
  created_by text null,
  created_on timestamp null,
  modified_by text null,
  modified_on timestamp null,
  order_by bigserial
);
alter sequence meaning_forum_id_seq restart with 10000;

insert into word_forum(word_id, value, value_prese, created_by, created_on, modified_by, modified_on)
select wff.word_id, ff.value_text, ff.value_prese, ff.created_by, ff.created_on, ff.modified_by, ff.modified_on
from word_freeform wff,
     freeform ff
where wff.freeform_id = ff.id
  and ff.type = 'NOTE'
order by ff.order_by;

update word_forum wf
set creator_id = eki_user.id
from (select u1.name, u1.id
      from eki_user u1
      where not exists(select u2.id
                       from eki_user u2
                       where u2.name = u1.name
                         and u2.id != u1.id
                         and u2.created > u1.created)) eki_user
where wf.created_by = eki_user.name
  and wf.creator_id is null;

delete
from freeform ff
using word_freeform wff
where wff.freeform_id = ff.id
  and ff.type = 'NOTE';

-- keelendi varjamine + avalikkuse indeksid
alter table word add column is_public not null default true;
create index word_is_public_idx on word(is_public);
create index freeform_is_public_idx on freeform(is_public);
create index definition_is_public_idx on definition(is_public);

