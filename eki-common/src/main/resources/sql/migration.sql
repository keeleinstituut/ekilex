-- #1 --

alter table feedback_log rename column created_on to created;
create index feedback_log_created_idx on feedback_log(created);
create index feedback_log_feedback_type_idx on feedback_log(feedback_type);
create index feedback_log_sender_email_idx on feedback_log(sender_email);
create index feedback_log_sender_email_lower_idx on feedback_log(lower(sender_email));
create index feedback_log_attr_name_idx on feedback_log_attr(name);

create table word_suggestion (
	id bigserial primary key,
	feedback_log_id bigint references feedback_log(id) on delete cascade not null,
	created timestamp not null default statement_timestamp(), 
	word_value text not null,
	definition_value text not null,
	author_name text not null,
	author_email text not null,
	is_public boolean default false,
	publication_time timestamp
);
alter sequence word_suggestion_id_seq restart with 10000;

create index word_suggestion_feedback_log_id_idx on word_suggestion(feedback_log_id);
create index word_suggestion_created_idx on word_suggestion(created);
create index word_suggestion_word_value_idx on word_suggestion(word_value);
create index word_suggestion_word_value_lower_idx on word_suggestion(lower(word_value));
create index word_suggestion_author_name_idx on word_suggestion(author_name);
create index word_suggestion_author_name_lower_idx on word_suggestion(lower(author_name));
create index word_suggestion_is_public_idx on word_suggestion(is_public);
create index word_suggestion_publication_time_idx on word_suggestion(publication_time);

-- #2 --

update 
	feedback_log 
set 
	feedback_type = 'WW'
where
	feedback_type = 'sõnaveeb'
;

update 
	feedback_log 
set 
	feedback_type = 'EXT'
where
	feedback_type = 'väline'
;

update 
	feedback_log 
set 
	feedback_type = 'OS'
where
	feedback_type = 'ÕS'
;
