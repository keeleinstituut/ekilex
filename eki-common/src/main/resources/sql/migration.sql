-- protsessi logi
create table process_log
(
	id bigserial primary key,
	event_by text not null,
	event_on timestamp not null default statement_timestamp(),
	comment text null,
	process_state_code varchar(100) references process_state(code) null,
	dataset_code varchar(10) references dataset(code) not null
);
alter sequence process_log_id_seq restart with 10000;

create table process_log_source_link
(
	id bigserial primary key,
	process_log_id bigint references process_log(id) on delete cascade not null,
	source_id bigint references source(id) on delete cascade not null,
	type varchar(100) not null,
	name text null,
	value text null,
	order_by bigserial
);
alter sequence process_log_source_link_id_seq restart with 10000;

create table word_process_log
(
	id bigserial primary key,
	word_id bigint references word(id) on delete cascade not null,
	process_log_id bigint references process_log(id) on delete cascade not null
);
alter sequence word_process_log_id_seq restart with 10000;

create table meaning_process_log
(
	id bigserial primary key,
	meaning_id bigint references meaning(id) on delete cascade not null,
	process_log_id bigint references process_log(id) on delete cascade not null
);
alter sequence meaning_process_log_id_seq restart with 10000;

create table lexeme_process_log
(
	id bigserial primary key,
	lexeme_id bigint references lexeme(id) on delete cascade not null,
	process_log_id bigint references process_log(id) on delete cascade not null
);
alter sequence lexeme_process_log_id_seq restart with 10000;

create index word_process_log_word_id_idx on word_process_log(word_id);
create index word_process_log_log_id_idx on word_process_log(process_log_id);
create index meaning_process_log_meaning_id_idx on meaning_process_log(meaning_id);
create index meaning_process_log_log_id_idx on meaning_process_log(process_log_id);
create index lexeme_process_log_lexeme_id_idx on lexeme_process_log(lexeme_id);
create index lexeme_process_log_log_id_idx on lexeme_process_log(process_log_id);
create index process_log_source_link_process_log_id_idx on process_log_source_link(process_log_id);
create index process_log_source_link_source_id_idx on process_log_source_link(source_id);