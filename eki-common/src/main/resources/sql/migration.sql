create type type_value_name_lang as (
	value_id bigint,
	value text,
	name text,
	lang char(3));

alter table dataset add column fed_term_collection_id varchar(100);
