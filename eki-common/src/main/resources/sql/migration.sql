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