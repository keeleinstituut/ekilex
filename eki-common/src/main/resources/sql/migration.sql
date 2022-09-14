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
