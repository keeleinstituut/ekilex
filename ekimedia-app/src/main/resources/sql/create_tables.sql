drop table if exists media_file;

create table media_file
(
	id bigserial primary key,
	origin varchar(100) not null,
	created timestamp not null default statement_timestamp(),
	original_filename text not null,
	object_filename varchar(100) not null,
	filename_ext varchar(100) not null,
	unique(object_filename)
);
alter sequence media_file_id_seq restart with 10000;

create index media_file_origin_idx on media_file(origin);
create index media_file_created_idx on media_file(created);
create index media_file_object_filename_idx on media_file(object_filename);

