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
drop type if exists type_usage;
create type type_usage as (
				usage_id bigint,
				usage text,
				usage_prese text,
				usage_lang char(3),
				complexity varchar(100),
				usage_type_code varchar(100),
				usage_translations text array,
				usage_definitions text array);

delete from freeform where type in ('OD_LEXEME_RECOMMENDATION', 'OD_USAGE_DEFINITION', 'OD_USAGE_ALTERNATIVE');