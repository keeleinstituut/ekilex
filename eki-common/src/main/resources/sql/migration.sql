
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
