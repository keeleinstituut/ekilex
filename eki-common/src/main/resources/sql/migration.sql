
-- API kasutuse stat

create table api_request_count (
	id bigserial primary key,
	auth_name text not null,
	generic_path text not null,
	count bigint not null default 0,
	unique (auth_name, generic_path)
);

create index api_request_count_auth_name_idx on api_request_count(auth_name);
create index api_request_count_generic_path_idx on api_request_count(generic_path);
create index api_request_count_count_idx on api_request_count(count);

create table api_error_count (
	id bigserial primary key,
	auth_name text not null,
	generic_path text not null,
	message text not null,
	count bigint not null default 0,
	unique (auth_name, generic_path, message)
);

create index api_error_count_auth_name_idx on api_error_count(auth_name);
create index api_error_count_generic_path_idx on api_error_count(generic_path);
create index api_error_count_message_idx on api_error_count(message);
create index api_error_count_count_idx on api_error_count(count);

-- vabavormide struktuuri ühtlustamine

update
	freeform f
set
	value_text = to_char(f.value_date, 'DD.MM.YYYY')
where
	f.freeform_type_code = 'SOURCE_PUBLICATION_YEAR'
	and f.value_date is not null
;

alter table freeform drop column value_date;
alter table freeform drop column value_number;
alter table freeform rename column value_text to value;
alter index public.freeform_value_text_idx rename to freeform_value_idx;
alter index public.freeform_value_text_lower_idx rename to freeform_value_lower_idx;

drop type type_mt_lexeme_freeform;
create type type_mt_lexeme_freeform as (
  lexeme_id bigint, 
  freeform_id bigint, 
  freeform_type_code varchar(100), 
  value text, 
  value_prese text, 
  lang char(3), 
  complexity varchar(100), 
  is_public boolean, 
  created_by text, 
  created_on timestamp, 
  modified_by text, 
  modified_on timestamp
);

delete from freeform where value is null;
alter table freeform alter column value set not null;
