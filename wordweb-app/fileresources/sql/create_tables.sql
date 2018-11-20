drop table if exists lexical_decision_result;
drop table if exists lexical_decision_data;
drop table if exists similarity_judgement_result;
drop table if exists similarity_judgement_data;

create table lexical_decision_data 
(
  id bigserial primary key,
  word text not null,
  lang char(3) not null,
  is_word boolean not null
);

create table lexical_decision_result
(
  id bigserial primary key,
  data_id bigint references lexical_decision_data(id) not null,
  remote_addr text not null,
  session_id text not null,
  answer boolean not null,
  delay bigint not null,
  created timestamp not null default statement_timestamp()
);

-- load lexical decision data
insert into lexical_decision_data (word, lang, is_word)
select * from
dblink(
  'host=localhost user=ekilex password=3kil3x dbname=ekilex',
  'select * from view_ww_lexical_decision_data') as lexical_decision_data(
  word text,
  lang char(3),
  is_word boolean
);

create table similarity_judgement_data
(
  id bigserial primary key,
  meaning_id bigint not null,
  word text not null,
  lang char(3) not null,
  dataset_code varchar(10) not null
);

create table similarity_judgement_result
(
  id bigserial primary key,
  game_key text not null,
  pair11_data_id bigint references similarity_judgement_data(id) not null,
  pair12_data_id bigint references similarity_judgement_data(id) not null,
  pair21_data_id bigint references similarity_judgement_data(id) not null,
  pair22_data_id bigint references similarity_judgement_data(id) not null,
  remote_addr text not null,
  session_id text not null,
  answer_pair1 boolean not null,
  answer_pair2 boolean not null,
  delay bigint not null,
  created timestamp not null default statement_timestamp()
);

-- load similarity judgement data
insert into similarity_judgement_data (meaning_id, word, lang, dataset_code)
select * from
dblink(
  'host=localhost user=ekilex password=3kil3x dbname=ekilex',
  'select * from view_ww_similarity_judgement_data') as similarity_judgement_data(
  meaning_id bigint,
  word text,
  lang char(3),
  dataset_code varchar(10)
);

create index lexical_decision_data_lang_idx on lexical_decision_data (lang);
create index lexical_decision_result_data_id_idx on lexical_decision_result (data_id);
create index similarity_judgement_data_meaning_id_idx on similarity_judgement_data (meaning_id);
create index similarity_judgement_data_word_idx on similarity_judgement_data (word);
create index similarity_judgement_data_lang_idx on similarity_judgement_data (lang);
create index similarity_judgement_data_dataset_code_idx on similarity_judgement_data (dataset_code);
create index similarity_judgement_result_game_key_idx on similarity_judgement_result (game_key);
create index similarity_judgement_result_pair11_data_id_idx on similarity_judgement_result (pair11_data_id);
create index similarity_judgement_result_pair12_data_id_idx on similarity_judgement_result (pair12_data_id);
create index similarity_judgement_result_pair21_data_id_idx on similarity_judgement_result (pair21_data_id);
create index similarity_judgement_result_pair22_data_id_idx on similarity_judgement_result (pair22_data_id);


