create type type_value_name_lang as (
	value_id bigint,
	value text,
	name text,
	lang char(3));

alter table dataset add column fed_term_collection_id varchar(100);

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
